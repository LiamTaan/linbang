package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.prioritypoolrecord.PriorityPoolRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.prioritypoolrecord.PriorityPoolRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PriorityPoolServiceImpl implements PriorityPoolService {

    @Resource
    private PriorityPoolRecordMapper priorityPoolRecordMapper;
    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

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
        MerchantInfoDO merchant = merchantInfoMapper.selectById(merchantId);
        if (merchant == null) {
            return;
        }
        boolean eligible = isEligible(merchant.getUserId());
        PriorityPoolRecordDO current = priorityPoolRecordMapper.selectCurrentByMerchantId(merchantId);
        if (eligible) {
            if (current != null && Objects.equals(current.getStatus(), "IN_POOL")) {
                return;
            }
            expireCurrent(current, "RECOMPUTE_REPLACED");
            priorityPoolRecordMapper.insert(PriorityPoolRecordDO.builder()
                    .merchantId(merchantId)
                    .userId(merchant.getUserId())
                    .status("IN_POOL")
                    .reasonCode("GOOD_REVIEW_15")
                    .reasonRemark("最近15条生效被评记录全部4-5星")
                    .currentFlag(true)
                    .effectiveTime(LocalDateTime.now())
                    .build());
            messagePushDispatchService.dispatchSingleIdempotent("lb_priority_pool_entered", "优先池入池通知",
                    "PRIORITY_POOL", merchantId, merchant.getUserId(), "优先池重算入池通知",
                    "lb_priority_pool_entered:" + merchantId);
            return;
        }
        if (current != null && Objects.equals(current.getStatus(), "IN_POOL")) {
            expireCurrent(current, "BAD_REVIEW_OR_COMPLAINT");
            priorityPoolRecordMapper.insert(PriorityPoolRecordDO.builder()
                    .merchantId(merchantId)
                    .userId(merchant.getUserId())
                    .status("OUT_POOL")
                    .reasonCode("BAD_REVIEW_OR_COMPLAINT")
                    .reasonRemark("最近评价链出现中差评")
                    .currentFlag(true)
                    .effectiveTime(LocalDateTime.now())
                    .build());
            messagePushDispatchService.dispatchSingleIdempotent("lb_priority_pool_exited", "优先池出池通知",
                    "PRIORITY_POOL", merchantId, merchant.getUserId(), "优先池重算出池通知",
                    "lb_priority_pool_exited:" + merchantId + ":" + LocalDateTime.now().toLocalDate());
        }
    }

    @Override
    public void recomputeAllPriorityPool() {
        merchantInfoMapper.selectList(new LambdaQueryWrapperX<MerchantInfoDO>()
                        .eq(MerchantInfoDO::getStatus, "ENABLE"))
                .forEach(item -> recomputeMerchantPriorityPool(item.getId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void freezeCurrent(Long merchantId, String reasonRemark) {
        MerchantInfoDO merchant = merchantInfoMapper.selectById(merchantId);
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
        priorityPoolRecordMapper.updateById(PriorityPoolRecordDO.builder()
                .id(current.getId())
                .currentFlag(false)
                .expireTime(LocalDateTime.now())
                .reasonCode(reasonCode)
                .build());
    }
}
