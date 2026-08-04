package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord.PriorityPoolRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.prioritypoolrecord.PriorityPoolRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PriorityPoolServiceImpl implements PriorityPoolService {

    private static final int RECOMPUTE_BATCH_SIZE = 200;
    private static final int RECOMPUTE_TRANSACTION_BATCH_SIZE = 25;

    private final ThreadLocal<Map<Long, Boolean>> batchEligibilityCache = new ThreadLocal<>();

    @Resource
    private PriorityPoolRecordMapper priorityPoolRecordMapper;
    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private PlatformTransactionManager transactionManager;

    @Override
    public boolean isInPriorityPool(Long merchantId) {
        if (merchantId == null) {
            return false;
        }
        PriorityPoolRecordDO record = priorityPoolRecordMapper.selectCurrentByMerchantId(merchantId);
        return record != null && Objects.equals(record.getStatus(), "IN_POOL");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recomputeMerchantPriorityPool(Long merchantId) {
        if (merchantId == null) {
            return;
        }
        // 锁定服务商主记录，串行化同一服务商的重算、冻结和解冻，避免产生多个当前记录。
        MerchantInfoDO merchant = merchantInfoMapper.selectByIdForUpdate(merchantId);
        if (merchant == null) {
            return;
        }
        PriorityPoolRecordDO current = priorityPoolRecordMapper.selectCurrentByMerchantId(merchantId);
        // 人工冻结只能由显式解冻操作解除，定时重算不得覆盖人工风控状态。
        if (current != null && Objects.equals(current.getStatus(), "FROZEN")) {
            return;
        }
        boolean merchantEnabled = Objects.equals(merchant.getStatus(), "ENABLE");
        boolean eligible = merchantEnabled && isEligible(merchant.getUserId());
        if (eligible) {
            if (current != null && Objects.equals(current.getStatus(), "IN_POOL")) {
                return;
            }
            expireCurrent(current, "RECOMPUTE_REPLACED");
            PriorityPoolRecordDO next = PriorityPoolRecordDO.builder()
                    .merchantId(merchantId)
                    .userId(merchant.getUserId())
                    .status("IN_POOL")
                    .reasonCode("GOOD_REVIEW_15")
                    .reasonRemark("最近15条生效被评记录全部4-5星")
                    .currentFlag(true)
                    .effectiveTime(LocalDateTime.now())
                    .build();
            priorityPoolRecordMapper.insert(next);
            messagePushDispatchService.dispatchSingleIdempotent("lb_priority_pool_entered", "优先池入池通知",
                    "PRIORITY_POOL", merchantId, merchant.getUserId(), "优先池重算入池通知",
                    "lb_priority_pool_entered:" + merchantId + ":" + next.getId());
            return;
        }
        if (current != null && Objects.equals(current.getStatus(), "IN_POOL")) {
            String reasonCode = merchantEnabled ? "BAD_REVIEW_OR_COMPLAINT" : "MERCHANT_DISABLED";
            expireCurrent(current, reasonCode);
            PriorityPoolRecordDO next = PriorityPoolRecordDO.builder()
                    .merchantId(merchantId)
                    .userId(merchant.getUserId())
                    .status("OUT_POOL")
                    .reasonCode(reasonCode)
                    .reasonRemark(merchantEnabled ? "最近评价链出现中差评" : "服务商已停用")
                    .currentFlag(true)
                    .effectiveTime(LocalDateTime.now())
                    .build();
            priorityPoolRecordMapper.insert(next);
            messagePushDispatchService.dispatchSingleIdempotent("lb_priority_pool_exited", "优先池出池通知",
                    "PRIORITY_POOL", merchantId, merchant.getUserId(), "优先池重算出池通知",
                    "lb_priority_pool_exited:" + merchantId + ":" + next.getId());
        }
    }

    @Override
    public void recomputeAllPriorityPool() {
        long lastId = 0L;
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        while (true) {
            List<MerchantInfoDO> merchants = merchantInfoMapper.selectList(new LambdaQueryWrapperX<MerchantInfoDO>()
                    .gt(MerchantInfoDO::getId, lastId)
                    .eq(MerchantInfoDO::getStatus, "ENABLE")
                    .orderByAsc(MerchantInfoDO::getId)
                    .last("LIMIT " + RECOMPUTE_BATCH_SIZE));
            if (merchants.isEmpty()) {
                return;
            }
            List<Long> userIds = merchants.stream().map(MerchantInfoDO::getUserId)
                    .filter(Objects::nonNull).distinct().collect(Collectors.toList());
            Map<Long, Boolean> eligibilityMap = new HashMap<>();
            userIds.forEach(userId -> eligibilityMap.put(userId, Boolean.FALSE));
            if (!userIds.isEmpty()) {
                reviewCommentMapper.selectPriorityEligibleUserIds(
                                TenantContextHolder.getRequiredTenantId(), userIds)
                        .forEach(userId -> eligibilityMap.put(userId, Boolean.TRUE));
            }
            batchEligibilityCache.set(eligibilityMap);
            try {
                for (int offset = 0; offset < merchants.size(); offset += RECOMPUTE_TRANSACTION_BATCH_SIZE) {
                    final List<MerchantInfoDO> transactionBatch = merchants.subList(offset,
                            Math.min(offset + RECOMPUTE_TRANSACTION_BATCH_SIZE, merchants.size()));
                    transactionTemplate.executeWithoutResult(status -> transactionBatch
                            .forEach(merchant -> recomputeMerchantPriorityPool(merchant.getId())));
                }
            } finally {
                batchEligibilityCache.remove();
            }
            lastId = merchants.get(merchants.size() - 1).getId();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeCurrent(Long merchantId, String reasonRemark) {
        MerchantInfoDO merchant = merchantInfoMapper.selectByIdForUpdate(merchantId);
        if (merchant == null) {
            return;
        }
        PriorityPoolRecordDO current = priorityPoolRecordMapper.selectCurrentByMerchantId(merchantId);
        expireCurrent(current, "MANUAL_FREEZE");
        priorityPoolRecordMapper.insert(PriorityPoolRecordDO.builder()
                .merchantId(merchantId)
                .userId(merchant.getUserId())
                .status("FROZEN")
                .reasonCode("MANUAL_FREEZE")
                .reasonRemark(reasonRemark)
                .currentFlag(true)
                .effectiveTime(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfreezeByRecompute(Long merchantId) {
        if (merchantId == null || merchantInfoMapper.selectByIdForUpdate(merchantId) == null) {
            return;
        }
        PriorityPoolRecordDO current = priorityPoolRecordMapper.selectCurrentByMerchantId(merchantId);
        if (current != null && Objects.equals(current.getStatus(), "FROZEN")) {
            expireCurrent(current, "MANUAL_UNFREEZE");
        }
        recomputeMerchantPriorityPool(merchantId);
    }

    private boolean isEligible(Long merchantUserId) {
        if (merchantUserId == null) {
            return false;
        }
        Map<Long, Boolean> eligibilityMap = batchEligibilityCache.get();
        if (eligibilityMap != null && eligibilityMap.containsKey(merchantUserId)) {
            return Boolean.TRUE.equals(eligibilityMap.get(merchantUserId));
        }
        List<ReviewCommentDO> reviews = reviewCommentMapper.selectList(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getToUserId, merchantUserId)
                .eq(ReviewCommentDO::getStatus, "ENABLE")
                .orderByDesc(ReviewCommentDO::getCreateTime, ReviewCommentDO::getId)
                .last("LIMIT 15"));
        if (reviews.size() < 15) {
            return false;
        }
        return reviews.stream().allMatch(item -> item.getStarLevel() != null && item.getStarLevel() >= 4);
    }

    private void expireCurrent(PriorityPoolRecordDO current, String reasonCode) {
        if (current == null) {
            return;
        }
        priorityPoolRecordMapper.update(null, new LambdaUpdateWrapper<PriorityPoolRecordDO>()
                .eq(PriorityPoolRecordDO::getId, current.getId())
                .eq(PriorityPoolRecordDO::getCurrentFlag, true)
                .set(PriorityPoolRecordDO::getCurrentFlag, false)
                .set(PriorityPoolRecordDO::getExpireTime, LocalDateTime.now())
                .set(PriorityPoolRecordDO::getReasonCode, reasonCode));
    }
}
