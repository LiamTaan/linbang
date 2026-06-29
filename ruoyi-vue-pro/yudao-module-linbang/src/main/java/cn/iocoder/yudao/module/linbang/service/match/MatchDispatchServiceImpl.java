package cn.iocoder.yudao.module.linbang.service.match;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchpushbatch.MatchPushBatchDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantdispatchsetting.MerchantDispatchSettingDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.matchpushbatch.MatchPushBatchMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord.OrderMatchRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.service.app.pay.AutoFlowRefundService;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MatchDispatchServiceImpl implements MatchDispatchService {

    @Resource
    private MatchStrategyService matchStrategyService;
    @Resource
    private MatchPushBatchMapper matchPushBatchMapper;
    @Resource
    private OrderMatchRecordMapper orderMatchRecordMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantDispatchSettingService merchantDispatchSettingService;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private PriorityPoolService priorityPoolService;
    @Resource
    private ShowcaseRewardService showcaseRewardService;
    @Resource
    private AmapLocationService amapLocationService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private AutoFlowRefundService autoFlowRefundService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startInitialDispatch(Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null || !Objects.equals(order.getStatus(), "PENDING_ACCEPT")) {
            return;
        }
        OrderUnitDO unit = orderUnitMapper.selectOne(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, orderId)
                .eq(OrderUnitDO::getStatus, "PENDING_ACCEPT")
                .eq(OrderUnitDO::getIsLocked, false)
                .isNull(OrderUnitDO::getMerchantId)
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId)
                .last("LIMIT 1"));
        if (unit == null) {
            return;
        }
        createStageBatch(order, unit, 1, "ORDER_PAID");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processDispatchTick() {
        for (MatchPushBatchDO batch : matchPushBatchMapper.selectExpiredActiveBatches(LocalDateTime.now())) {
            OrderUnitDO unit = orderUnitMapper.selectById(batch.getUnitId());
            if (unit == null) {
                continue;
            }
            if (unit.getMerchantId() != null || !Objects.equals(unit.getStatus(), "PENDING_ACCEPT")) {
                markBatchStatus(batch.getId(), "ACCEPTED");
                continue;
            }
            List<MatchStrategyService.StageRule> rules = matchStrategyService.getStageRules();
            if (batch.getStageNo() < rules.size()) {
                markBatchStatus(batch.getId(), "EXPIRED");
                OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
                createStageBatch(order, unit, batch.getStageNo() + 1, "SCHEDULE");
                continue;
            }
            markBatchStatus(batch.getId(), "FLOWED");
            flowUnit(unit);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAccepted(Long unitId, Long acceptedMerchantId) {
        List<OrderMatchRecordDO> records = orderMatchRecordMapper.selectListByUnitId(unitId);
        for (OrderMatchRecordDO record : records) {
            orderMatchRecordMapper.updateById(OrderMatchRecordDO.builder()
                    .id(record.getId())
                    .status(Objects.equals(record.getMerchantId(), acceptedMerchantId) ? "ACCEPTED" : "CLOSED")
                    .finalResult(Objects.equals(record.getMerchantId(), acceptedMerchantId) ? "ACCEPTED" : "LOST")
                    .build());
        }
        List<MatchPushBatchDO> batches = matchPushBatchMapper.selectListByUnitId(unitId);
        for (MatchPushBatchDO batch : batches) {
            markBatchStatus(batch.getId(), "ACCEPTED");
        }
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unitId)
                .dispatchStatus("ACCEPTED")
                .build());
    }

    private void createStageBatch(OrderInfoDO order, OrderUnitDO unit, int stageNo, String triggerType) {
        if (matchPushBatchMapper.selectByUnitIdAndStageNo(unit.getId(), stageNo) != null) {
            return;
        }
        MatchStrategyService.StageRule stageRule = matchStrategyService.getStageRules().stream()
                .filter(item -> Objects.equals(item.getStageNo(), stageNo))
                .findFirst()
                .orElse(null);
        if (stageRule == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiredAt = now.plusSeconds(stageRule.getDurationSeconds());
        matchPushBatchMapper.insert(MatchPushBatchDO.builder()
                .orderId(order.getId())
                .unitId(unit.getId())
                .stageNo(stageNo)
                .pushBatchNo(stageNo)
                .radiusStartKm(stageRule.getRadiusStartKm())
                .radiusEndKm(stageRule.getRadiusEndKm())
                .plannedAt(now)
                .expiredAt(expiredAt)
                .status("PUSHING")
                .triggerType(triggerType)
                .build());
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .dispatchStatus("PUSHING")
                .currentBatchNo(stageNo)
                .acceptDeadlineTime(now.plusMinutes(5))
                .build());
        pushToMerchants(order, unit, stageNo, stageRule, expiredAt);
    }

    private void pushToMerchants(OrderInfoDO order, OrderUnitDO unit, int stageNo,
                                 MatchStrategyService.StageRule stageRule, LocalDateTime expiredAt) {
        List<MerchantCandidate> candidates = buildCandidates(order, stageRule);
        if (CollUtil.isEmpty(candidates)) {
            return;
        }
        List<MessagePushDispatchTarget> targets = new ArrayList<>();
        for (MerchantCandidate candidate : candidates) {
            if (orderMatchRecordMapper.selectByUnitIdAndMerchantIdAndStageNo(unit.getId(), candidate.getMerchant().getId(), stageNo) != null) {
                continue;
            }
            orderMatchRecordMapper.insert(OrderMatchRecordDO.builder()
                    .orderId(order.getId())
                    .unitId(unit.getId())
                    .merchantId(candidate.getMerchant().getId())
                    .matchRuleCode("DEFAULT_STAGE_" + stageNo)
                    .matchScore(candidate.getScore())
                    .distanceKm(candidate.getDistanceKm())
                    .pushTime(LocalDateTime.now())
                    .stageNo(stageNo)
                    .pushBatchNo(stageNo)
                    .priorityLayer(candidate.getPriorityLayer())
                    .priorityPoolFlag(candidate.isPriorityPoolFlag())
                    .categoryMatchLevel(candidate.getCategoryMatchLevel())
                    .acceptDeadlineTime(unit.getAcceptDeadlineTime())
                    .expiredTime(expiredAt)
                    .status("PUSHED")
                    .finalResult("WAITING")
                    .tenantId(0L)
                    .build());
            String dedupeKey = "lb_match_pushed:" + candidate.getMerchant().getUserId() + ":" + unit.getId() + ":" + stageNo;
            targets.add(new MessagePushDispatchTarget(candidate.getMerchant().getUserId(), unit.getId(), dedupeKey));
            messagePushDispatchService.dispatchSingleIdempotent("lb_grab_countdown", "抢单倒计时提醒",
                    "MATCH_COUNTDOWN", unit.getId(), candidate.getMerchant().getUserId(), "分钟级派单倒计时提醒",
                    "lb_grab_countdown:" + candidate.getMerchant().getUserId() + ":" + unit.getId() + ":" + stageNo);
        }
        messagePushDispatchService.dispatchBatch("lb_match_pushed", "匹配推送通知", "MULTI_USER",
                "MATCH_PUSH", unit.getId(), "分钟级派单推送", targets);
    }

    private List<MerchantCandidate> buildCandidates(OrderInfoDO order, MatchStrategyService.StageRule stageRule) {
        Set<Long> merchantIds = merchantCategoryRelMapper.selectList(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                        .eq(MerchantCategoryRelDO::getCategoryId, order.getCategoryId())
                        .eq(MerchantCategoryRelDO::getStatus, "ENABLE"))
                .stream()
                .map(MerchantCategoryRelDO::getMerchantId)
                .collect(Collectors.toSet());
        if (merchantIds.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        return merchantInfoMapper.selectBatchIds(merchantIds).stream()
                .filter(item -> Objects.equals(item.getStatus(), "ENABLE"))
                .filter(item -> Objects.equals(item.getAcceptStatus(), "ENABLE"))
                .map(item -> buildCandidate(order, item, stageRule))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(MerchantCandidate::sortKey, Comparator.reverseOrder())
                        .thenComparing(MerchantCandidate::getDistanceKm))
                .collect(Collectors.toList());
    }

    private MerchantCandidate buildCandidate(OrderInfoDO order, MerchantInfoDO merchant,
                                             MatchStrategyService.StageRule stageRule) {
        MerchantDispatchSettingDO setting = merchantDispatchSettingService.getOrCreate(merchant.getId());
        if (!Boolean.TRUE.equals(setting.getDispatchEnabled())) {
            return null;
        }
        BigDecimal distanceKm = calculateDistance(order, merchant.getId());
        if (distanceKm == null) {
            return null;
        }
        if (distanceKm.compareTo(stageRule.getRadiusStartKm()) < 0 || distanceKm.compareTo(stageRule.getRadiusEndKm()) > 0) {
            return null;
        }
        if (setting.getMaxAcceptRadiusKm() != null && distanceKm.compareTo(setting.getMaxAcceptRadiusKm()) > 0) {
            return null;
        }
        String priorityLayer = resolvePriorityLayer(merchant.getUserId(), merchant.getId());
        boolean priorityPool = priorityPoolService.isInPriorityPool(merchant.getId());
        BigDecimal score = BigDecimal.valueOf(resolveLayerWeight(priorityLayer))
                .add(priorityPool ? new BigDecimal("100") : BigDecimal.ZERO)
                .add(BigDecimal.valueOf(merchant.getCreditScore() == null ? 0 : merchant.getCreditScore()))
                .subtract(distanceKm);
        return new MerchantCandidate(merchant, distanceKm, priorityLayer, priorityPool, "EXACT", score);
    }

    private String resolvePriorityLayer(Long userId, Long merchantId) {
        List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectList(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, userId)
                .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                .eq(MemberUserQualificationDO::getPriorityEnabled, true));
        LocalDate today = LocalDate.now();
        boolean clothing = qualifications.stream().anyMatch(item -> Objects.equals(item.getQualificationType(), "PLATFORM_CLOTHING")
                && (item.getValidEndDate() == null || item.getValidEndDate().isAfter(today)));
        if (clothing) {
            return "PLATFORM_CLOTHING";
        }
        boolean toolbox = qualifications.stream().anyMatch(item -> Objects.equals(item.getQualificationType(), "TOOLBOX")
                && (item.getValidEndDate() == null || item.getValidEndDate().isAfter(today)));
        if (toolbox) {
            return "TOOLBOX";
        }
        if (showcaseRewardService.hasActiveReward(merchantId)) {
            return "SHOWCASE_REWARD";
        }
        return "NORMAL";
    }

    private int resolveLayerWeight(String priorityLayer) {
        if (Objects.equals(priorityLayer, "PLATFORM_CLOTHING")) {
            return 4000;
        }
        if (Objects.equals(priorityLayer, "TOOLBOX")) {
            return 3000;
        }
        if (Objects.equals(priorityLayer, "SHOWCASE_REWARD")) {
            return 2000;
        }
        return 1000;
    }

    private BigDecimal calculateDistance(OrderInfoDO order, Long merchantId) {
        if (order == null || merchantId == null || order.getLongitude() == null || order.getLatitude() == null) {
            return null;
        }
        List<MerchantServicePointDO> points = merchantServicePointMapper.selectListByMerchantId(merchantId);
        if (CollUtil.isEmpty(points)) {
            return null;
        }
        BigDecimal minDistance = null;
        for (MerchantServicePointDO point : points) {
            if (!Objects.equals(point.getStatus(), "ENABLE")
                    || point.getLongitude() == null || point.getLatitude() == null) {
                continue;
            }
            BigDecimal distance = amapLocationService.calculateDistanceKm(
                    point.getLongitude(), point.getLatitude(), order.getLongitude(), order.getLatitude());
            if (distance == null) {
                continue;
            }
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private void markBatchStatus(Long batchId, String status) {
        matchPushBatchMapper.updateById(MatchPushBatchDO.builder()
                .id(batchId)
                .status(status)
                .build());
    }

    private void flowUnit(OrderUnitDO unit) {
        LocalDateTime now = LocalDateTime.now();
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .dispatchStatus("FLOWED")
                .flowTime(now)
                .flowReason("5分钟无人接单自动流单")
                .autoRefundStatus("PROCESSING")
                .build());
        messagePushDispatchService.dispatchSingleIdempotent("lb_order_flow_advice", "流单建议通知",
                "ORDER_FLOW", unit.getId(), orderInfoMapper.selectById(unit.getOrderId()).getUserId(),
                "流单后建议通知", "lb_order_flow_advice:" + unit.getId() + ":" + now.toLocalDate());
        autoFlowRefundService.createAutoRefund(unit.getOrderId(), unit.getId(), now);
    }

    private static class MerchantCandidate {

        private final MerchantInfoDO merchant;
        private final BigDecimal distanceKm;
        private final String priorityLayer;
        private final boolean priorityPoolFlag;
        private final String categoryMatchLevel;
        private final BigDecimal score;

        private MerchantCandidate(MerchantInfoDO merchant, BigDecimal distanceKm, String priorityLayer,
                                  boolean priorityPoolFlag, String categoryMatchLevel, BigDecimal score) {
            this.merchant = merchant;
            this.distanceKm = distanceKm;
            this.priorityLayer = priorityLayer;
            this.priorityPoolFlag = priorityPoolFlag;
            this.categoryMatchLevel = categoryMatchLevel;
            this.score = score;
        }

        private MerchantInfoDO getMerchant() {
            return merchant;
        }

        private BigDecimal getDistanceKm() {
            return distanceKm;
        }

        private String getPriorityLayer() {
            return priorityLayer;
        }

        private boolean isPriorityPoolFlag() {
            return priorityPoolFlag;
        }

        private String getCategoryMatchLevel() {
            return categoryMatchLevel;
        }

        private BigDecimal getScore() {
            return score;
        }

        private BigDecimal sortKey() {
            return score;
        }
    }
}
