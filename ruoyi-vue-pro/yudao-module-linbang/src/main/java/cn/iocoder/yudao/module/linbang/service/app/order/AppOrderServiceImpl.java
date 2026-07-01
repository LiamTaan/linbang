package cn.iocoder.yudao.module.linbang.service.app.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchpushbatch.MatchPushBatchDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderattachment.OrderAttachmentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderpriceitem.OrderPriceItemDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunitproof.OrderUnitProofDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord.OrderAcceptRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderattachment.OrderAttachmentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderpriceitem.OrderPriceItemMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.matchpushbatch.MatchPushBatchMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord.OrderMatchRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunitproof.OrderUnitProofMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantOperatorContext;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantOperatorContextService;
import cn.iocoder.yudao.module.linbang.service.map.AmapLocationService;
import cn.iocoder.yudao.module.linbang.service.finance.LinbangFinanceService;
import cn.iocoder.yudao.module.linbang.service.match.MatchDispatchService;
import cn.iocoder.yudao.module.linbang.service.match.MatchStrategyService;
import cn.iocoder.yudao.module.linbang.service.memberqualification.MemberQualificationExpiryService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.orderinfo.OrderDetailAggregateService;
import cn.iocoder.yudao.module.linbang.service.ordersplitrule.OrderSplitPlan;
import cn.iocoder.yudao.module.linbang.service.ordersplitrule.OrderSplitPreviewContext;
import cn.iocoder.yudao.module.linbang.service.ordersplitrule.OrderSplitRuleService;
import cn.iocoder.yudao.module.linbang.service.promoter.PromoterService;
import cn.iocoder.yudao.module.linbang.service.risk.LinbangRiskFacade;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveContentDetectService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveDetectResult;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AppOrderServiceImpl implements AppOrderService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderPriceItemMapper orderPriceItemMapper;
    @Resource
    private OrderAttachmentMapper orderAttachmentMapper;
    @Resource
    private MatchPushBatchMapper matchPushBatchMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderMatchRecordMapper orderMatchRecordMapper;
    @Resource
    private OrderAcceptRecordMapper orderAcceptRecordMapper;
    @Resource
    private OrderUnitProofMapper orderUnitProofMapper;
    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private MemberQualificationExpiryService memberQualificationExpiryService;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private PayRefundMapper payRefundMapper;
    @Resource
    private PayOrderMapper payOrderMapper;
    @Resource
    private AmapLocationService amapLocationService;
    @Resource
    private PromoterService promoterService;
    @Resource
    private OrderSplitRuleService orderSplitRuleService;
    @Resource
    private ConfigService configService;
    @Resource
    private FileService fileService;
    @Resource
    private LinbangFinanceService linbangFinanceService;
    @Resource
    private MatchDispatchService matchDispatchService;
    @Resource
    private MatchStrategyService matchStrategyService;
    @Resource
    private LinbangRiskFacade linbangRiskFacade;
    @Resource
    private SensitiveContentDetectService sensitiveContentDetectService;
    @Resource
    private AppMerchantOperatorContextService merchantOperatorContextService;
    @Resource
    private OrderDetailAggregateService orderDetailAggregateService;

    @Override
    public AppOrderPreviewRespVO previewOrder(Long authUserId, @Valid AppOrderPreviewReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        linbangRiskFacade.validateBeforeCreateOrder(authUserId, loginUser);
        MerchantServiceCategoryDO category = validateAndGetCategory(reqVO.getCategoryId());
        validateCategoryPricingMode(category, reqVO.getPricingMode());
        validateInvoiceCapability(category, reqVO.getNeedInvoice());
        validateSplitCapability(category, reqVO.getNeedSplit());

        AmapLocationService.ResolvedAddress address = resolveOrderAddress(reqVO);
        BigDecimal orderAmount = calculateOrderAmount(reqVO);
        OrderSplitPlan splitPlan = buildSplitPlan(category, reqVO, orderAmount);
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_ORDER_PUBLISH, loginUser.getId(), "ORDER_PREVIEW", null,
                reqVO.getRequireDesc(), reqVO.getAttachmentFileIds());
        if (detectResult.isOcrFailed()) {
            throw exception(CONTENT_OCR_FAILED);
        }

        String agreementType = resolveAgreementType(category);
        AppOrderPreviewRespVO respVO = new AppOrderPreviewRespVO();
        respVO.setPreviewToken(buildPreviewToken(category, reqVO, address, splitPlan, detectResult));
        respVO.setCategoryId(category.getId());
        respVO.setCategoryName(category.getCategoryName());
        respVO.setPricingMode(reqVO.getPricingMode());
        respVO.setPricingModeName(resolvePricingModeName(reqVO.getPricingMode()));
        respVO.setBudgetAmount(reqVO.getBudgetAmount());
        respVO.setOrderAmount(orderAmount);
        respVO.setQuantity(reqVO.getQuantity());
        respVO.setWorkerCount(resolveWorkerCount(reqVO.getWorkerCount()));
        respVO.setRequireDesc(detectResult.getProcessedContent());
        respVO.setServiceDurationDesc(reqVO.getServiceDurationDesc());
        respVO.setInvoiceImpactReminder(Boolean.TRUE.equals(reqVO.getNeedInvoice()) ? category.getInvoiceRateReminderText() : null);
        respVO.setInvoiceSupported(Boolean.TRUE.equals(category.getSupportInvoice()));
        respVO.setSplitSupported(Boolean.TRUE.equals(category.getSupportSplit()));
        respVO.setAgreementRequired(Boolean.TRUE);
        respVO.setAgreementType(agreementType);
        respVO.setAgreementTitle(resolveAgreementTitle(agreementType));
        respVO.setAgreementContent(resolveAgreementContent(agreementType));
        respVO.setRiskStrategy(detectResult.getStrategy());
        respVO.setSensitiveHit(detectResult.isHit());
        respVO.setSensitiveHitCount(detectResult.getHitCount());
        respVO.setSensitiveHitSummaries(resolveSensitiveHitSummaries(detectResult));
        respVO.setSplitPreview(toSplitPreviewResp(splitPlan));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(Long authUserId, @Valid AppOrderCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        linbangRiskFacade.validateBeforeCreateOrder(authUserId, loginUser);
        if (!Boolean.TRUE.equals(reqVO.getAgreementConfirmed())) {
            throw exception(ORDER_AGREEMENT_NOT_CONFIRMED);
        }
        if (!Boolean.TRUE.equals(reqVO.getAntiEscapeConfirmed())) {
            throw exception(ORDER_ANTI_ESCAPE_NOT_CONFIRMED);
        }

        MerchantServiceCategoryDO category = validateAndGetCategory(reqVO.getCategoryId());
        validateCategoryPricingMode(category, reqVO.getPricingMode());
        validateInvoiceCapability(category, reqVO.getNeedInvoice());
        validateSplitCapability(category, reqVO.getNeedSplit());
        validateAgreementRequirement(category, reqVO.getAgreementVersion());
        AmapLocationService.ResolvedAddress address = resolveOrderAddress(reqVO);

        BigDecimal orderAmount = calculateOrderAmount(reqVO);
        OrderSplitPlan splitPlan = buildSplitPlan(category, reqVO, orderAmount);
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_ORDER_PUBLISH, loginUser.getId(), "ORDER_INFO", null,
                reqVO.getRequireDesc(), reqVO.getAttachmentFileIds());
        if (detectResult.isOcrFailed()) {
            throw exception(CONTENT_OCR_FAILED);
        }
        if (detectResult.isHit() && LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(detectResult.getStrategy())) {
            throw exception(CONTENT_SENSITIVE_BLOCKED);
        }
        if (detectResult.isHit() && LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(detectResult.getStrategy())) {
            throw exception(CONTENT_SENSITIVE_REVIEW_REQUIRED);
        }
        validatePreviewToken(category, reqVO, address, splitPlan, detectResult);
        LocalDateTime now = LocalDateTime.now();
        OrderInfoDO order = OrderInfoDO.builder()
                .orderNo("LBO" + IdUtil.getSnowflakeNextIdStr())
                .userId(loginUser.getId())
                .categoryId(category.getId())
                .pricingMode(StrUtil.blankToDefault(reqVO.getPricingMode(), category.getDefaultPricingMode()))
                .budgetAmount(reqVO.getBudgetAmount())
                .orderAmount(orderAmount)
                .serviceDurationDesc(reqVO.getServiceDurationDesc())
                .quantity(reqVO.getQuantity())
                .workerCount(resolveWorkerCount(reqVO.getWorkerCount()))
                .requireDesc(detectResult.getProcessedContent())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .detailAddress(address.getDetailAddress())
                .longitude(address.getLongitude())
                .latitude(address.getLatitude())
                .needInvoice(reqVO.getNeedInvoice())
                .needSplit(reqVO.getNeedSplit())
                .splitRuleId(splitPlan.getRuleId())
                .splitStatus(Boolean.TRUE.equals(splitPlan.isSplitRequired()) ? "SPLIT" : "UNSPLIT")
                .splitMode(splitPlan.getSplitMode())
                .splitRuleSnapshot(splitPlan.getRuleSnapshot())
                .agreementConfirmed(Boolean.TRUE)
                .tradeAgreementVersion(reqVO.getAgreementVersion())
                .tradeAgreementConfirmedTime(now)
                .antiEscapeConfirmed(Boolean.TRUE)
                .status("PENDING_PAY")
                .build();
        linbangRiskFacade.fillOrderDeposit(order);
        orderInfoMapper.insert(order);

        savePriceItems(order.getId(), reqVO.getPriceItems());
        saveAttachments(order.getId(), reqVO.getAttachmentFileIds());
        saveUnits(order, splitPlan);
        saveOperateLog(order.getId(), null, "CREATE_ORDER", "USER", loginUser.getId(), null, order.getStatus(),
                "用户创建订单，并确认交易保障协议与防逃单提醒");
        return order.getId();
    }

    @Override
    public AppOrderSplitRuleMatchRespVO matchSplitRule(AppOrderSplitRuleMatchReqVO reqVO) {
        OrderSplitPlan splitPlan = orderSplitRuleService.matchRule(OrderSplitPreviewContext.builder()
                .categoryId(reqVO.getCategoryId())
                .pricingMode(reqVO.getPricingMode())
                .orderAmount(reqVO.getBudgetAmount())
                .quantity(reqVO.getQuantity())
                .workerCount(reqVO.getWorkerCount())
                .requireDesc(reqVO.getRequireDesc())
                .build());
        AppOrderSplitRuleMatchRespVO respVO = new AppOrderSplitRuleMatchRespVO();
        respVO.setMatched(splitPlan.isMatched());
        respVO.setSplitRequired(splitPlan.isSplitRequired());
        respVO.setRuleId(splitPlan.getRuleId());
        respVO.setRuleName(splitPlan.getRuleName());
        respVO.setRuleCode(splitPlan.getRuleCode());
        respVO.setMatchMode(splitPlan.getMatchMode());
        respVO.setSplitMode(splitPlan.getSplitMode());
        respVO.setUnitAmountLimit(splitPlan.getUnitAmountLimit());
        respVO.setUnitCount(splitPlan.getUnitCount());
        respVO.setUnits(splitPlan.getUnits().stream().map(unit -> {
            AppOrderSplitRuleMatchRespVO.UnitPreviewRespVO item = new AppOrderSplitRuleMatchRespVO.UnitPreviewRespVO();
            item.setUnitSeq(unit.getUnitSeq());
            item.setUnitTitle(unit.getUnitTitle());
            item.setUnitType(unit.getUnitType());
            item.setUnitContent(unit.getUnitContent());
            item.setUnitProgress(unit.getUnitProgress());
            item.setUnitAmount(unit.getUnitAmount());
            item.setWorkerCount(unit.getWorkerCount());
            item.setLocked(unit.getLocked());
            item.setLockReason(unit.getLockReason());
            return item;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<AppOrderAcceptPageItemRespVO> getAcceptOrderPage(Long authUserId, AppOrderAcceptPageReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredOrderAcceptContext(authUserId);
        MerchantInfoDO merchant = getRequiredMerchant(context);
        String keyword = StrUtil.trimToNull(reqVO.getKeyword());
        Set<Long> categoryFilterIds = resolveCategoryFilterIds(reqVO.getCategoryId());
        Set<Long> keywordCategoryIds = resolveCategoryIdsByKeyword(keyword);

        List<OrderMatchRecordDO> activeRecords = orderMatchRecordMapper.selectActiveListByMerchantId(merchant.getId(), LocalDateTime.now());
        if (CollUtil.isEmpty(activeRecords)) {
            return PageResult.empty();
        }

        Map<Long, OrderMatchRecordDO> latestRecordByUnitId = activeRecords.stream()
                .collect(Collectors.toMap(OrderMatchRecordDO::getUnitId, item -> item,
                        (left, right) -> left.getId() >= right.getId() ? left : right, LinkedHashMap::new));
        List<Long> unitIds = new ArrayList<>(latestRecordByUnitId.keySet());
        Map<Long, OrderUnitDO> unitMap = orderUnitMapper.selectBatchIds(unitIds).stream()
                .collect(Collectors.toMap(OrderUnitDO::getId, item -> item));
        List<Long> orderIds = unitMap.values().stream().map(OrderUnitDO::getOrderId).filter(Objects::nonNull)
                .distinct().collect(Collectors.toList());
        Map<Long, OrderInfoDO> orderMap = orderInfoMapper.selectBatchIds(orderIds).stream()
                .collect(Collectors.toMap(OrderInfoDO::getId, item -> item));

        List<OrderInfoDO> filteredOrders = orderMap.values().stream()
                .filter(order -> Objects.equals(order.getStatus(), "PENDING_ACCEPT"))
                .filter(order -> categoryFilterIds == null || categoryFilterIds.contains(order.getCategoryId()))
                .filter(order -> reqVO.getPricingMode() == null || Objects.equals(order.getPricingMode(), reqVO.getPricingMode()))
                .filter(order -> reqVO.getMinOrderAmount() == null || (order.getOrderAmount() != null
                        && order.getOrderAmount().compareTo(reqVO.getMinOrderAmount()) >= 0))
                .filter(order -> reqVO.getMaxOrderAmount() == null || (order.getOrderAmount() != null
                        && order.getOrderAmount().compareTo(reqVO.getMaxOrderAmount()) <= 0))
                .filter(order -> {
                    if (StrUtil.isBlank(keyword)) {
                        return true;
                    }
                    return StrUtil.containsIgnoreCase(order.getRequireDesc(), keyword)
                            || (CollUtil.isNotEmpty(keywordCategoryIds) && keywordCategoryIds.contains(order.getCategoryId()));
                })
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(filteredOrders)) {
            return PageResult.empty();
        }

        Set<Long> filteredOrderIds = filteredOrders.stream().map(OrderInfoDO::getId).collect(Collectors.toSet());
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(filteredOrders);
        Map<Long, BigDecimal> distanceMap = new HashMap<>();
        List<AppOrderAcceptPageItemRespVO> list = new ArrayList<>();
        for (OrderMatchRecordDO record : latestRecordByUnitId.values()) {
            OrderUnitDO unit = unitMap.get(record.getUnitId());
            if (unit == null || !Objects.equals(unit.getStatus(), "PENDING_ACCEPT")
                    || Boolean.TRUE.equals(unit.getIsLocked()) || unit.getMerchantId() != null) {
                continue;
            }
            OrderInfoDO order = orderMap.get(unit.getOrderId());
            if (order == null || !filteredOrderIds.contains(order.getId())) {
                continue;
            }
            BigDecimal distanceKm = record.getDistanceKm() != null ? record.getDistanceKm()
                    : calculateDistanceToMerchant(order, context);
            distanceMap.put(order.getId(), distanceKm);

            AppOrderAcceptPageItemRespVO respVO = new AppOrderAcceptPageItemRespVO();
            respVO.setOrderId(order.getId());
            respVO.setUnitId(unit.getId());
            respVO.setOrderNo(order.getOrderNo());
            respVO.setCategoryId(order.getCategoryId());
            respVO.setCategoryName(Optional.ofNullable(categoryMap.get(order.getCategoryId()))
                    .map(MerchantServiceCategoryDO::getCategoryName).orElse(null));
            respVO.setPricingMode(order.getPricingMode());
            respVO.setRequireDesc(order.getRequireDesc());
            respVO.setOrderAmount(order.getOrderAmount());
            respVO.setServiceDurationDesc(order.getServiceDurationDesc());
            respVO.setDistanceKm(distanceKm);
            respVO.setStatus(order.getStatus());
            respVO.setDispatchStatus(unit.getDispatchStatus());
            respVO.setStageNo(record.getStageNo());
            respVO.setPushBatchNo(record.getPushBatchNo());
            respVO.setCountdownSeconds(record.getExpiredTime() == null ? null
                    : Math.max(0, (int) java.time.Duration.between(LocalDateTime.now(), record.getExpiredTime()).getSeconds()));
            respVO.setPriorityLayer(record.getPriorityLayer());
            respVO.setPriorityPoolFlag(record.getPriorityPoolFlag());
            respVO.setCategoryMatchLevel(record.getCategoryMatchLevel());
            respVO.setAcceptDeadlineTime(record.getExpiredTime() != null ? record.getExpiredTime() : unit.getAcceptDeadlineTime());
            respVO.setCreateTime(order.getCreateTime());
            list.add(respVO);
        }
        if (CollUtil.isEmpty(list)) {
            return PageResult.empty();
        }
        list.sort(buildAcceptOrderComparator(reqVO, distanceMap));
        return manualPage(list, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    public PageResult<AppOrderPageItemRespVO> getOrderPage(Long authUserId, AppOrderPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        Set<Long> categoryFilterIds = resolveCategoryFilterIds(reqVO.getCategoryId());
        List<OrderInfoDO> orders = orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                .eq(OrderInfoDO::getUserId, loginUser.getId())
                .eqIfPresent(OrderInfoDO::getStatus, reqVO.getStatus())
                .inIfPresent(OrderInfoDO::getCategoryId, categoryFilterIds)
                .eqIfPresent(OrderInfoDO::getPricingMode, reqVO.getPricingMode())
                .orderByDesc(OrderInfoDO::getId));
        List<OrderInfoDO> filteredOrders = applyBusinessCategoryFilter(loginUser.getId(), orders, reqVO.getBusinessCategory());
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(filteredOrders);
        List<AppOrderPageItemRespVO> list = filteredOrders.stream().map(order -> {
            boolean waitReview = isWaitReviewOrder(loginUser.getId(), order);
            AppOrderPageItemRespVO respVO = new AppOrderPageItemRespVO();
            respVO.setId(order.getId());
            respVO.setOrderNo(order.getOrderNo());
            respVO.setCategoryId(order.getCategoryId());
            respVO.setCategoryName(Optional.ofNullable(categoryMap.get(order.getCategoryId()))
                    .map(MerchantServiceCategoryDO::getCategoryName).orElse(null));
            respVO.setPricingMode(order.getPricingMode());
            respVO.setOrderAmount(order.getOrderAmount());
            respVO.setServiceDurationDesc(order.getServiceDurationDesc());
            respVO.setDistanceKm(calculateOrderDistanceKm(order));
            respVO.setStatus(order.getStatus());
            respVO.setSplitStatus(order.getSplitStatus());
            respVO.setPayStatus(resolvePayStatus(order));
            respVO.setDepositRequired(order.getDepositRequired());
            respVO.setDepositAmount(order.getDepositAmount());
            respVO.setDepositPayStatus(order.getDepositPayStatus());
            respVO.setBusinessCategory(resolveBusinessCategory(order, waitReview));
            respVO.setWaitReview(waitReview);
            respVO.setCreateTime(order.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
        return manualPage(list, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    public AppOrderDetailRespVO getOrderDetail(Long authUserId, Long orderId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), orderId);
        OrderDetailAggregateService.OrderDetailAggregate aggregate = orderDetailAggregateService.aggregate(order, true);
        MerchantServiceCategoryDO category = aggregate.getCategory();
        List<OrderPriceItemDO> priceItems = aggregate.getPriceItems();
        List<OrderAttachmentDO> attachments = aggregate.getAttachments();
        List<OrderUnitDO> units = aggregate.getUnits();
        List<OrderUnitProofDO> proofs = aggregate.getProofs();
        List<OrderAcceptRecordDO> acceptRecords = aggregate.getAcceptRecords();
        List<ComplaintDO> complaints = aggregate.getComplaints();
        List<AppealDO> appeals = aggregate.getAppeals();
        PayOrderDO payOrder = aggregate.getPayOrder();
        List<PayRefundDO> refunds = aggregate.getRefunds();
        List<OrderOperateLogDO> operateLogs = aggregate.getOperateLogs();
        List<MatchPushBatchDO> matchBatches = aggregate.getMatchBatches();
        List<OrderMatchRecordDO> matchRecords = aggregate.getMatchRecords();
        Map<Long, FileDO> fileMap = aggregate.getFileMap();

        AppOrderDetailRespVO respVO = new AppOrderDetailRespVO();
        respVO.setId(order.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setUserId(order.getUserId());
        respVO.setMerchantId(order.getMerchantId());
        respVO.setCategoryId(order.getCategoryId());
        respVO.setCategoryName(category != null ? category.getCategoryName() : null);
        respVO.setPricingMode(order.getPricingMode());
        respVO.setBudgetAmount(order.getBudgetAmount());
        respVO.setOrderAmount(order.getOrderAmount());
        respVO.setQuantity(order.getQuantity());
        respVO.setWorkerCount(order.getWorkerCount());
        respVO.setServiceDurationDesc(order.getServiceDurationDesc());
        respVO.setRequireDesc(order.getRequireDesc());
        respVO.setProvince(order.getProvince());
        respVO.setCity(order.getCity());
        respVO.setDistrict(order.getDistrict());
        respVO.setStreet(order.getStreet());
        respVO.setDetailAddress(order.getDetailAddress());
        respVO.setLongitude(order.getLongitude());
        respVO.setLatitude(order.getLatitude());
        respVO.setNeedInvoice(order.getNeedInvoice());
        respVO.setNeedSplit(order.getNeedSplit());
        respVO.setSplitRuleId(order.getSplitRuleId());
        respVO.setSplitStatus(order.getSplitStatus());
        respVO.setSplitMode(order.getSplitMode());
        respVO.setAgreementConfirmed(order.getAgreementConfirmed());
        respVO.setTradeAgreementVersion(order.getTradeAgreementVersion());
        respVO.setTradeAgreementConfirmedTime(order.getTradeAgreementConfirmedTime());
        respVO.setAntiEscapeConfirmed(order.getAntiEscapeConfirmed());
        respVO.setStatus(order.getStatus());
        respVO.setPayOrderId(order.getPayOrderId());
        respVO.setDepositRequired(order.getDepositRequired());
        respVO.setDepositAmount(order.getDepositAmount());
        respVO.setDepositPayStatus(order.getDepositPayStatus());
        respVO.setDepositPayOrderId(order.getDepositPayOrderId());
        respVO.setDepositPaidTime(order.getDepositPaidTime());
        respVO.setPriceDetailEnabled(Boolean.parseBoolean(getConfigValue(
                PlatformConfigKeyConstants.APP_ORDER_PRICE_DETAIL_ENABLED, "true")));
        respVO.setMallEntry(buildMallEntryResp());
        respVO.setMallConsumeRelation(buildMallConsumeRelationResp(order));
        respVO.setPromoteDeduct(buildPromoteDeductResp(order));
        if (payOrder != null) {
            AppOrderDetailRespVO.OrderPayRecordRespVO payRecordResp = new AppOrderDetailRespVO.OrderPayRecordRespVO();
            payRecordResp.setId(payOrder.getId());
            payRecordResp.setMerchantOrderId(payOrder.getMerchantOrderId());
            payRecordResp.setSubject(payOrder.getSubject());
            payRecordResp.setPrice(payOrder.getPrice());
            payRecordResp.setStatus(payOrder.getStatus());
            payRecordResp.setChannelCode(payOrder.getChannelCode());
            payRecordResp.setChannelOrderNo(payOrder.getChannelOrderNo());
            payRecordResp.setRefundPrice(payOrder.getRefundPrice());
            payRecordResp.setExpireTime(payOrder.getExpireTime());
            payRecordResp.setSuccessTime(payOrder.getSuccessTime());
            payRecordResp.setCreateTime(payOrder.getCreateTime());
            respVO.setPayRecord(payRecordResp);
        }
        respVO.setPriceItems(priceItems.stream().map(item -> {
            AppOrderDetailRespVO.OrderPriceItemRespVO itemResp = new AppOrderDetailRespVO.OrderPriceItemRespVO();
            itemResp.setItemType(item.getItemType());
            itemResp.setItemName(item.getItemName());
            itemResp.setItemAmount(item.getItemAmount());
            itemResp.setSortNo(item.getSortNo());
            return itemResp;
        }).collect(Collectors.toList()));
        respVO.setAttachments(attachments.stream().map(item -> {
            AppOrderDetailRespVO.OrderAttachmentRespVO itemResp = new AppOrderDetailRespVO.OrderAttachmentRespVO();
            itemResp.setFileId(item.getFileId());
            itemResp.setFileType(item.getFileType());
            itemResp.setFileUrl(Optional.ofNullable(fileMap.get(item.getFileId())).map(FileDO::getUrl).orElse(null));
            itemResp.setSortNo(item.getSortNo());
            return itemResp;
        }).collect(Collectors.toList()));
        respVO.setUnits(units.stream().map(unit -> {
            ensureVerifyCode(unit);
            AppOrderDetailRespVO.OrderUnitSimpleRespVO unitResp = new AppOrderDetailRespVO.OrderUnitSimpleRespVO();
            unitResp.setId(unit.getId());
            unitResp.setUnitNo(unit.getUnitNo());
            unitResp.setUnitSeq(unit.getUnitSeq());
            unitResp.setUnitTitle(unit.getUnitTitle());
            unitResp.setUnitType(unit.getUnitType());
            unitResp.setUnitAmount(unit.getUnitAmount());
            unitResp.setStatus(unit.getStatus());
            unitResp.setSplitMode(unit.getSplitMode());
            unitResp.setUnitContent(unit.getUnitContent());
            unitResp.setUnitProgress(unit.getUnitProgress());
            unitResp.setWorkerCount(unit.getWorkerCount());
            unitResp.setMaxAmountLimit(unit.getMaxAmountLimit());
            unitResp.setIsLocked(unit.getIsLocked());
            unitResp.setLockReason(unit.getLockReason());
            unitResp.setDispatchStatus(unit.getDispatchStatus());
            unitResp.setCurrentBatchNo(unit.getCurrentBatchNo());
            unitResp.setFlowTime(unit.getFlowTime());
            unitResp.setFlowReason(unit.getFlowReason());
            unitResp.setAutoRefundStatus(unit.getAutoRefundStatus());
            unitResp.setAutoRefundId(unit.getAutoRefundId());
            unitResp.setAppealExpireTime(unit.getAppealExpireTime());
            unitResp.setAppealAvailable(isAppealAvailable(unit));
            unitResp.setVerifyStatus(unit.getVerifyStatus());
            unitResp.setVerifyCode(unit.getVerifyCode());
            unitResp.setVerifyTime(unit.getVerifyTime());
            unitResp.setVerifyBy(unit.getVerifyBy());
            unitResp.setVerifyRemark(unit.getVerifyRemark());
            return unitResp;
        }).collect(Collectors.toList()));
        respVO.setProofs(proofs.stream().map(proof -> {
            AppOrderDetailRespVO.OrderUnitProofRespVO proofResp = new AppOrderDetailRespVO.OrderUnitProofRespVO();
            proofResp.setId(proof.getId());
            proofResp.setUnitId(proof.getUnitId());
            proofResp.setMerchantId(proof.getMerchantId());
            proofResp.setFileId(proof.getFileId());
            proofResp.setFileUrl(StrUtil.blankToDefault(proof.getFileUrl(),
                    Optional.ofNullable(fileMap.get(proof.getFileId())).map(FileDO::getUrl).orElse(null)));
            proofResp.setFileHash(proof.getFileHash());
            proofResp.setProofType(proof.getProofType());
            proofResp.setProofDesc(proof.getProofDesc());
            proofResp.setProofTime(proof.getProofTime());
            proofResp.setDeviceTime(proof.getDeviceTime());
            proofResp.setLongitude(proof.getLongitude());
            proofResp.setLatitude(proof.getLatitude());
            proofResp.setAddressText(proof.getAddressText());
            return proofResp;
        }).collect(Collectors.toList()));
        respVO.setAcceptRecords(acceptRecords.stream().map(record -> {
            AppOrderDetailRespVO.OrderAcceptRecordRespVO recordResp = new AppOrderDetailRespVO.OrderAcceptRecordRespVO();
            recordResp.setId(record.getId());
            recordResp.setUnitId(record.getUnitId());
            recordResp.setMerchantId(record.getMerchantId());
            recordResp.setAcceptTime(record.getAcceptTime());
            recordResp.setDistanceKm(record.getDistanceKm());
            recordResp.setAcceptResult(record.getAcceptResult());
            recordResp.setRemark(record.getRemark());
            return recordResp;
        }).collect(Collectors.toList()));
        respVO.setRefunds(refunds.stream().map(refund -> {
            AppOrderDetailRespVO.OrderRefundRespVO refundResp = new AppOrderDetailRespVO.OrderRefundRespVO();
            refundResp.setId(refund.getId());
            refundResp.setPayOrderId(refund.getOrderId());
            refundResp.setMerchantRefundId(refund.getMerchantRefundId());
            refundResp.setStatus(refund.getStatus());
            refundResp.setStatusName(resolveRefundStatusName(refund.getStatus()));
            refundResp.setAuditStatus(refund.getAuditStatus());
            refundResp.setAuditRemark(refund.getAuditRemark());
            refundResp.setRejectReason(refund.getRejectReason());
            refundResp.setRefundPrice(refund.getRefundPrice());
            refundResp.setReason(refund.getReason());
            refundResp.setChannelErrorMsg(refund.getChannelErrorMsg());
            refundResp.setSuccessTime(refund.getSuccessTime());
            refundResp.setCreateTime(refund.getCreateTime());
            return refundResp;
        }).collect(Collectors.toList()));
        respVO.setComplaints(complaints.stream().map(complaint -> {
            AppOrderDetailRespVO.OrderComplaintRespVO complaintResp = new AppOrderDetailRespVO.OrderComplaintRespVO();
            complaintResp.setId(complaint.getId());
            complaintResp.setComplaintNo(complaint.getComplaintNo());
            complaintResp.setUnitId(complaint.getUnitId());
            complaintResp.setComplainantUserId(complaint.getComplainantUserId());
            complaintResp.setRespondentUserId(complaint.getRespondentUserId());
            complaintResp.setComplaintType(complaint.getComplaintType());
            complaintResp.setContent(complaint.getContent());
            complaintResp.setStatus(complaint.getStatus());
            complaintResp.setResultDesc(complaint.getResultDesc());
            complaintResp.setHandleTime(complaint.getHandleTime());
            complaintResp.setCreateTime(complaint.getCreateTime());
            return complaintResp;
        }).collect(Collectors.toList()));
        respVO.setAppeals(appeals.stream().map(appeal -> {
            AppOrderDetailRespVO.OrderAppealRespVO appealResp = new AppOrderDetailRespVO.OrderAppealRespVO();
            appealResp.setId(appeal.getId());
            appealResp.setAppealNo(appeal.getAppealNo());
            appealResp.setUnitId(appeal.getUnitId());
            appealResp.setUserId(appeal.getUserId());
            appealResp.setAppealType(appeal.getAppealType());
            appealResp.setContent(appeal.getContent());
            appealResp.setStatus(appeal.getStatus());
            appealResp.setAuditStatus(appeal.getAuditStatus());
            appealResp.setAuditRemark(appeal.getAuditRemark());
            appealResp.setRejectReason(appeal.getRejectReason());
            appealResp.setAuditTime(appeal.getAuditTime());
            appealResp.setCreateTime(appeal.getCreateTime());
            appealResp.setAppealExpireTime(appeal.getAppealExpireTime());
            return appealResp;
        }).collect(Collectors.toList()));
        respVO.setOperateLogs(operateLogs.stream().map(log -> {
            AppOrderDetailRespVO.OrderOperateLogRespVO logResp = new AppOrderDetailRespVO.OrderOperateLogRespVO();
            logResp.setId(log.getId());
            logResp.setUnitId(log.getUnitId());
            logResp.setOperateType(log.getOperateType());
            logResp.setOperateRole(log.getOperateRole());
            logResp.setOperateBy(log.getOperateBy());
            logResp.setBeforeStatus(log.getBeforeStatus());
            logResp.setAfterStatus(log.getAfterStatus());
            logResp.setRemark(log.getRemark());
            logResp.setOperateTime(log.getOperateTime());
            return logResp;
        }).collect(Collectors.toList()));
        respVO.setTimeline(buildOrderTimeline(order, units, payOrder, refunds, complaints, appeals, operateLogs));
        respVO.setFlowAdvice(units.stream().anyMatch(item -> Objects.equals(item.getDispatchStatus(), "FLOWED"))
                ? matchStrategyService.getFlowAdviceTemplate() : null);
        respVO.setMatchBatches(matchBatches.stream().map(batch -> {
            AppOrderDetailRespVO.OrderMatchBatchRespVO batchRespVO = new AppOrderDetailRespVO.OrderMatchBatchRespVO();
            batchRespVO.setStageNo(batch.getStageNo());
            batchRespVO.setPushBatchNo(batch.getPushBatchNo());
            batchRespVO.setRadiusStartKm(batch.getRadiusStartKm());
            batchRespVO.setRadiusEndKm(batch.getRadiusEndKm());
            batchRespVO.setPlannedAt(batch.getPlannedAt());
            batchRespVO.setExpiredAt(batch.getExpiredAt());
            batchRespVO.setStatus(batch.getStatus());
            return batchRespVO;
        }).collect(Collectors.toList()));
        respVO.setMatchSummaries(matchRecords.stream().map(record -> {
            AppOrderDetailRespVO.OrderMatchSummaryRespVO summaryRespVO = new AppOrderDetailRespVO.OrderMatchSummaryRespVO();
            summaryRespVO.setMerchantId(record.getMerchantId());
            summaryRespVO.setStageNo(record.getStageNo());
            summaryRespVO.setPriorityLayer(record.getPriorityLayer());
            summaryRespVO.setPriorityPoolFlag(record.getPriorityPoolFlag());
            summaryRespVO.setCategoryMatchLevel(record.getCategoryMatchLevel());
            summaryRespVO.setDistanceKm(record.getDistanceKm());
            summaryRespVO.setPushTime(record.getPushTime());
            summaryRespVO.setFinalResult(record.getFinalResult());
            return summaryRespVO;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrder(Long authUserId, @Valid AppOrderUpdateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), reqVO.getOrderId());
        if (!Objects.equals(order.getUserId(), loginUser.getId())) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        if (!Arrays.asList("PENDING_PAY", "PENDING_ACCEPT").contains(order.getStatus())) {
            throw exception(ORDER_STATUS_NOT_ALLOW_UPDATE);
        }
        MerchantServiceCategoryDO category = validateAndGetCategory(order.getCategoryId());
        validateInvoiceCapability(category, reqVO.getNeedInvoice());
        AmapLocationService.ResolvedAddress address = amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(reqVO.getProvince())
                .city(reqVO.getCity())
                .district(reqVO.getDistrict())
                .street(reqVO.getStreet())
                .detailAddress(reqVO.getDetailAddress())
                .longitude(reqVO.getLongitude())
                .latitude(reqVO.getLatitude())
                .adcode(reqVO.getAdcode())
                .build());
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .serviceDurationDesc(reqVO.getServiceDurationDesc())
                .requireDesc(reqVO.getRequireDesc())
                .province(address.getProvince())
                .city(address.getCity())
                .district(address.getDistrict())
                .street(address.getStreet())
                .detailAddress(address.getDetailAddress())
                .longitude(address.getLongitude())
                .latitude(address.getLatitude())
                .needInvoice(reqVO.getNeedInvoice())
                .build());
        orderAttachmentMapper.delete(new LambdaQueryWrapperX<OrderAttachmentDO>()
                .eq(OrderAttachmentDO::getOrderId, order.getId()));
        saveAttachments(order.getId(), reqVO.getAttachmentFileIds());
        saveOperateLog(order.getId(), null, "UPDATE_ORDER", "USER", loginUser.getId(),
                order.getStatus(), order.getStatus(), "用户更新订单地址、工期、说明、开票标记或附件");
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelOrder(Long authUserId, @Valid AppOrderCancelReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = orderInfoMapper.selectById(reqVO.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (!Objects.equals(order.getUserId(), loginUser.getId())) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        if (!Arrays.asList("PENDING_PAY", "PENDING_ACCEPT").contains(order.getStatus())) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CANCEL);
        }

        String beforeStatus = order.getStatus();
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .status("CLOSED")
                .build());
        orderUnitMapper.update(OrderUnitDO.builder().status("CLOSED").build(), new LambdaUpdateWrapper<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .in(OrderUnitDO::getStatus, Arrays.asList("PENDING_CREATE", "PENDING_ACCEPT", "ACCEPTED")));
        saveOperateLog(order.getId(), null, "CANCEL_ORDER", "USER", loginUser.getId(),
                beforeStatus, "CLOSED", StrUtil.blankToDefault(reqVO.getCancelReason(), "用户取消订单"));
        linbangRiskFacade.handleOrderCancelled(loginUser, order, reqVO.getCancelReason());
        notifyOrderStatusChanged(order, "CLOSED", "订单已取消");
        return Boolean.TRUE;
    }

    @Override
    public PageResult<AppOrderUnitRespVO> getOrderUnitPage(Long authUserId, AppOrderUnitPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        validateAndGetAccessibleOrder(authUserId, loginUser.getId(), reqVO.getOrderId());
        LambdaQueryWrapperX<OrderUnitDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.eq(OrderUnitDO::getOrderId, reqVO.getOrderId());
        queryWrapper.eqIfPresent(OrderUnitDO::getStatus, reqVO.getStatus());
        queryWrapper.orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId);
        PageResult<OrderUnitDO> pageResult = orderUnitMapper.selectPage(reqVO, queryWrapper);
        List<AppOrderUnitRespVO> list = pageResult.getList().stream()
                .map(this::toOrderUnitRespVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public AppOrderUnitRespVO getOrderUnit(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(id);
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (!canAccessUnit(authUserId, loginUser.getId(), unit)) {
            throw exception(ORDER_UNIT_ACCESS_DENIED);
        }
        ensureVerifyCode(unit);
        return toOrderUnitRespVO(unit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean startOrderUnitService(Long authUserId, @Valid AppOrderUnitStartServiceReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredOrderAcceptContext(authUserId);
        MerchantInfoDO merchant = getRequiredMerchant(context);
        if (!"ENABLE".equals(merchant.getAcceptStatus())) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (!Objects.equals(unit.getMerchantId(), merchant.getId())) {
            throw exception(ORDER_UNIT_ACCESS_DENIED);
        }
        validateUnitVisibleToOperator(context, unit);
        if (!Objects.equals(unit.getStatus(), "ACCEPTED")) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CONFIRM);
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }

        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .status("SERVING")
                .build());
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .status("SERVING")
                .build());
        saveOperateLog(order.getId(), unit.getId(), "START_UNIT_SERVICE", "MERCHANT", loginUser.getId(),
                unit.getStatus(), "SERVING", StrUtil.blankToDefault(reqVO.getStartRemark(), "服务商开始服务"));
        notifyOrderStatusChanged(order, "SERVING", "订单已开始服务");
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean confirmOrderUnit(Long authUserId, @Valid AppOrderUnitConfirmReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), unit.getOrderId());
        if (!Objects.equals(order.getUserId(), loginUser.getId())) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        if (!Objects.equals(unit.getStatus(), "PENDING_CONFIRM")) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CONFIRM);
        }

        LocalDateTime now = LocalDateTime.now();
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .status("FINISHED")
                .finishTime(now)
                .appealExpireTime(now.plusDays(7))
                .build());
        saveOperateLog(order.getId(), unit.getId(), "CONFIRM_UNIT_FINISH", "USER", loginUser.getId(),
                unit.getStatus(), "FINISHED", StrUtil.blankToDefault(reqVO.getConfirmRemark(), "用户确认单元完成"));

        OrderUnitDO nextUnit = orderUnitMapper.selectOne(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .eq(OrderUnitDO::getPrevUnitId, unit.getId()));
        if (nextUnit != null && Boolean.TRUE.equals(nextUnit.getIsLocked())
                && Objects.equals(nextUnit.getStatus(), "PENDING_CREATE")) {
            orderUnitMapper.updateById(OrderUnitDO.builder()
                    .id(nextUnit.getId())
                    .isLocked(Boolean.FALSE)
                    .lockReason(null)
                    .status("PENDING_ACCEPT")
                    .build());
            saveOperateLog(order.getId(), nextUnit.getId(), "UNLOCK_NEXT_UNIT", "SYSTEM", 0L,
                    nextUnit.getStatus(), "PENDING_ACCEPT", "前序单元已确认完成，自动解锁下一单元");
        }

        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
        String nextOrderStatus = resolveOrderStatusAfterUnitConfirm(units);
        if (!Objects.equals(order.getStatus(), nextOrderStatus)) {
            orderInfoMapper.updateById(OrderInfoDO.builder()
                    .id(order.getId())
                    .status(nextOrderStatus)
                    .build());
        }
        linbangFinanceService.handleUnitFinished(order, unit);
        if (Objects.equals("FINISHED", nextOrderStatus)) {
            promoterService.handleOrderFinished(order, unit);
        }
        notifyOrderStatusChanged(order, nextOrderStatus, "订单状态已更新");
        return Boolean.TRUE;
    }

    @Override
    public AppOrderVerifyCodeRespVO getOrderUnitVerifyCode(Long authUserId, Long unitId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(unitId);
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), unit.getOrderId());
        if (!Objects.equals(order.getUserId(), loginUser.getId()) && !canAccessUnit(authUserId, loginUser.getId(), unit)) {
            throw exception(ORDER_VERIFY_ACCESS_DENIED);
        }
        ensureVerifyCode(unit);
        AppOrderVerifyCodeRespVO respVO = new AppOrderVerifyCodeRespVO();
        respVO.setOrderId(order.getId());
        respVO.setUnitId(unit.getId());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setVerifyCode(unit.getVerifyCode());
        respVO.setVerifyStatus(unit.getVerifyStatus());
        respVO.setVerifyAllowed(canVerifyUnit(unit));
        respVO.setVerifyTime(unit.getVerifyTime());
        respVO.setVerifyBy(unit.getVerifyBy());
        respVO.setVerifyRemark(unit.getVerifyRemark());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean verifyOrderUnit(Long authUserId, @Valid AppOrderVerifyReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), unit.getOrderId());
        if (!canAccessUnit(authUserId, loginUser.getId(), unit) && !Objects.equals(order.getUserId(), loginUser.getId())) {
            throw exception(ORDER_VERIFY_ACCESS_DENIED);
        }
        ensureVerifyCode(unit);
        if (!Objects.equals(unit.getVerifyCode(), reqVO.getVerifyCode())) {
            notifyVerifyException(order, unit, loginUser.getId(), "核销码校验失败");
            throw exception(ORDER_VERIFY_CODE_INVALID);
        }
        if ("VERIFIED".equals(unit.getVerifyStatus())) {
            notifyVerifyException(order, unit, loginUser.getId(), "订单单元已完成核销");
            throw exception(ORDER_VERIFY_ALREADY_FINISHED);
        }
        if (!canVerifyUnit(unit)) {
            notifyVerifyException(order, unit, loginUser.getId(), "当前订单状态不允许核销");
            throw exception(ORDER_VERIFY_STATUS_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        String afterStatus = "FINISHED";
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .verifyStatus("VERIFIED")
                .verifyTime(now)
                .verifyBy(loginUser.getId())
                .verifyRemark(reqVO.getVerifyRemark())
                .status(afterStatus)
                .finishTime(unit.getFinishTime() != null ? unit.getFinishTime() : now)
                .appealExpireTime(unit.getAppealExpireTime() != null ? unit.getAppealExpireTime() : now.plusDays(7))
                .build());
        saveOperateLog(order.getId(), unit.getId(), "VERIFY_UNIT", "USER", loginUser.getId(),
                unit.getStatus(), afterStatus, StrUtil.blankToDefault(reqVO.getVerifyRemark(), "订单单元核销完成"));
        OrderUnitDO nextUnit = orderUnitMapper.selectOne(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .eq(OrderUnitDO::getPrevUnitId, unit.getId()));
        if (nextUnit != null && Boolean.TRUE.equals(nextUnit.getIsLocked())
                && Objects.equals(nextUnit.getStatus(), "PENDING_CREATE")) {
            orderUnitMapper.updateById(OrderUnitDO.builder()
                    .id(nextUnit.getId())
                    .isLocked(Boolean.FALSE)
                    .lockReason(null)
                    .status("PENDING_ACCEPT")
                    .build());
            saveOperateLog(order.getId(), nextUnit.getId(), "UNLOCK_NEXT_UNIT", "SYSTEM", 0L,
                    nextUnit.getStatus(), "PENDING_ACCEPT", "前序单元已核销完成，自动解锁下一单元");
        }
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
        String nextOrderStatus = resolveOrderStatusAfterUnitConfirm(units);
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .status(nextOrderStatus)
                .build());
        linbangFinanceService.handleUnitFinished(order, unit);
        if (Objects.equals("FINISHED", nextOrderStatus)) {
            promoterService.handleOrderFinished(order, unit);
        }
        notifyVerifySuccess(order, unit);
        notifyOrderStatusChanged(order, nextOrderStatus, "订单核销完成");
        return Boolean.TRUE;
    }

    @Override
    public List<AppOrderVerifyLogRespVO> getOrderUnitVerifyLogs(Long authUserId, Long unitId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderUnitDO unit = orderUnitMapper.selectById(unitId);
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (!canAccessUnit(authUserId, loginUser.getId(), unit)) {
            OrderInfoDO order = validateAndGetAccessibleOrder(authUserId, loginUser.getId(), unit.getOrderId());
            if (!Objects.equals(order.getUserId(), loginUser.getId())) {
                throw exception(ORDER_VERIFY_ACCESS_DENIED);
            }
        }
        return orderOperateLogMapper.selectList(new LambdaQueryWrapperX<OrderOperateLogDO>()
                        .eq(OrderOperateLogDO::getUnitId, unitId)
                        .in(OrderOperateLogDO::getOperateType, Arrays.asList("VERIFY_UNIT"))
                        .orderByDesc(OrderOperateLogDO::getOperateTime, OrderOperateLogDO::getId))
                .stream()
                .map(log -> {
                    AppOrderVerifyLogRespVO respVO = new AppOrderVerifyLogRespVO();
                    respVO.setId(log.getId());
                    respVO.setUnitId(log.getUnitId());
                    respVO.setVerifyBy(log.getOperateBy());
                    respVO.setVerifyMethod("CODE");
                    respVO.setBeforeStatus(log.getBeforeStatus());
                    respVO.setAfterStatus(log.getAfterStatus());
                    respVO.setRemark(log.getRemark());
                    respVO.setVerifyTime(log.getOperateTime());
                    return respVO;
                }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppOrderAcceptRespVO acceptOrder(Long authUserId, @Valid AppOrderAcceptCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredOrderAcceptContext(authUserId);
        MerchantInfoDO merchant = getRequiredMerchant(context);
        if (!Boolean.TRUE.equals(reqVO.getAntiEscapeConfirmed())) {
            throw exception(ORDER_ANTI_ESCAPE_NOT_CONFIRMED);
        }
        OrderInfoDO order = validateAndGetAccessibleOrderForAccept(loginUser.getId(), reqVO.getOrderId());
        linbangRiskFacade.validateBeforeAcceptOrder(authUserId, loginUser, merchant, order);
        order = ensureResolvedOrderLocation(order);
        OrderUnitDO unit = resolveTargetUnit(reqVO, order.getId());
        validateMerchantCanAcceptUnit(merchant.getId(), unit.getId(), context);
        if (Boolean.TRUE.equals(unit.getIsLocked())) {
            throw exception(ORDER_UNIT_LOCKED);
        }

        int updated = orderUnitMapper.update(null, new LambdaUpdateWrapper<OrderUnitDO>()
                .eq(OrderUnitDO::getId, unit.getId())
                .eq(OrderUnitDO::getStatus, "PENDING_ACCEPT")
                .eq(OrderUnitDO::getIsLocked, Boolean.FALSE)
                .isNull(OrderUnitDO::getMerchantId)
                .set(OrderUnitDO::getMerchantId, merchant.getId())
                .set(OrderUnitDO::getStatus, "ACCEPTED"));
        BigDecimal distanceKm = calculateDistanceToMerchant(order, context);
        OrderAcceptRecordDO acceptRecord = OrderAcceptRecordDO.builder()
                .orderId(order.getId())
                .unitId(unit.getId())
                .merchantId(merchant.getId())
                .acceptTime(LocalDateTime.now())
                .distanceKm(distanceKm)
                .acceptResult(updated > 0 ? "SUCCESS" : "FAILED")
                .remark("已确认防逃单提醒")
                .build();
        orderAcceptRecordMapper.insert(acceptRecord);
        if (updated <= 0) {
            throw exception(ORDER_UNIT_ACCEPTED_BY_OTHER);
        }

        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .merchantId(merchant.getId())
                .status("ACCEPTED")
                .build());
        matchDispatchService.markAccepted(unit.getId(), merchant.getId());
        saveOperateLog(order.getId(), unit.getId(), "ACCEPT_ORDER", "MERCHANT", loginUser.getId(),
                unit.getStatus(), "ACCEPTED", "服务商接单成功，并确认防逃单提醒");
        notifyOrderStatusChanged(order, "ACCEPTED", "订单已被服务商接单");

        AppOrderAcceptRespVO respVO = new AppOrderAcceptRespVO();
        respVO.setOrderId(order.getId());
        respVO.setUnitId(unit.getId());
        respVO.setAcceptResult("SUCCESS");
        respVO.setStatus("ACCEPTED");
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean uploadDeliveryProof(Long authUserId, @Valid AppDeliveryProofUploadReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredOrderAcceptContext(authUserId);
        MerchantInfoDO merchant = getRequiredMerchant(context);
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (!Objects.equals(unit.getMerchantId(), merchant.getId())) {
            throw exception(ORDER_UNIT_ACCESS_DENIED);
        }
        validateUnitVisibleToOperator(context, unit);
        if (!Arrays.asList("ACCEPTED", "SERVING").contains(unit.getStatus())) {
            throw exception(ORDER_STATUS_NOT_ALLOW_CONFIRM);
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        for (Long fileId : reqVO.getFileIds()) {
            FileDO file = fileService.getFile(fileId);
            orderUnitProofMapper.insert(OrderUnitProofDO.builder()
                    .unitId(unit.getId())
                    .orderId(order.getId())
                    .merchantId(merchant.getId())
                    .fileId(fileId)
                    .fileUrl(file.getUrl())
                    .fileHash(buildFileHash(file))
                    .proofType(reqVO.getProofType())
                    .proofDesc(reqVO.getProofDesc())
                    .proofTime(now)
                    .deviceTime(reqVO.getDeviceTime())
                    .longitude(reqVO.getLongitude())
                    .latitude(reqVO.getLatitude())
                    .addressText(reqVO.getAddressText())
                    .build());
        }
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .status("PENDING_CONFIRM")
                .build());
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .status("PENDING_CONFIRM")
                .build());
        saveOperateLog(order.getId(), unit.getId(), "UPLOAD_DELIVERY_PROOF", "MERCHANT", loginUser.getId(),
                unit.getStatus(), "PENDING_CONFIRM", "服务商上传交付凭证");
        notifyOrderStatusChanged(order, "PENDING_CONFIRM", "服务商已上传交付凭证，等待确认");
        return Boolean.TRUE;
    }

    @Override
    public AppOrderAppealProgressRespVO getAppealProgress(Long authUserId, Long appealId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        AppealDO appeal = appealMapper.selectById(appealId);
        if (appeal == null) {
            throw exception(APPEAL_NOT_EXISTS);
        }
        validateAndGetAccessibleOrder(authUserId, loginUser.getId(), appeal.getOrderId());
        AppOrderAppealProgressRespVO respVO = new AppOrderAppealProgressRespVO();
        respVO.setId(appeal.getId());
        respVO.setAppealNo(appeal.getAppealNo());
        respVO.setOrderId(appeal.getOrderId());
        respVO.setUnitId(appeal.getUnitId());
        respVO.setStatus(appeal.getStatus());
        respVO.setAuditStatus(appeal.getAuditStatus());
        respVO.setAuditRemark(appeal.getAuditRemark());
        respVO.setRejectReason(appeal.getRejectReason());
        respVO.setCreateTime(appeal.getCreateTime());
        respVO.setAuditTime(appeal.getAuditTime());
        return respVO;
    }

    @Override
    public AppOrderGuaranteeConfigRespVO getGuaranteeConfig() {
        AppOrderGuaranteeConfigRespVO respVO = new AppOrderGuaranteeConfigRespVO();
        respVO.setAgreementVersion(getConfigValue(PlatformConfigKeyConstants.AGREEMENT_TRADE_VERSION, "v1"));
        respVO.setAgreementTitle(getConfigValue(PlatformConfigKeyConstants.AGREEMENT_TRADE_TITLE, "交易保障协议"));
        respVO.setServiceAgreement(getConfigValue(PlatformConfigKeyConstants.AGREEMENT_SERVICE, ""));
        respVO.setProjectEscrowAgreementTitle(getConfigValue(PlatformConfigKeyConstants.AGREEMENT_PROJECT_ESCROW_TITLE, "工程托管协议"));
        respVO.setProjectEscrowAgreement(getConfigValue(PlatformConfigKeyConstants.AGREEMENT_PROJECT_ESCROW, ""));
        respVO.setAntiEscapeNotice(getConfigValue(PlatformConfigKeyConstants.ORDER_ANTI_ESCAPE_NOTICE,
                "请勿私下交易或跳过平台履约，平台将对逃单、飞单、绕单行为保留处罚与申诉取证权利。"));
        return respVO;
    }

    private MerchantServiceCategoryDO validateAndGetCategory(Long categoryId) {
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(categoryId);
        if (category == null || !"ENABLE".equals(category.getStatus())) {
            throw exception(ORDER_CATEGORY_DISABLED);
        }
        return category;
    }

    private void validateCategoryPricingMode(MerchantServiceCategoryDO category, String pricingMode) {
        List<String> supportedModes = parseSupportedPricingModes(category);
        if (CollUtil.isEmpty(supportedModes) || supportedModes.contains(pricingMode)) {
            return;
        }
        throw exception(ORDER_PRICING_MODE_NOT_SUPPORTED);
    }

    private void validateInvoiceCapability(MerchantServiceCategoryDO category, Boolean needInvoice) {
        if (!Boolean.TRUE.equals(needInvoice)) {
            return;
        }
        if (!Boolean.TRUE.equals(category.getSupportInvoice())) {
            throw exception(ORDER_INVOICE_NOT_SUPPORTED);
        }
    }

    private void validateSplitCapability(MerchantServiceCategoryDO category, Boolean needSplit) {
        if (!Boolean.TRUE.equals(needSplit)) {
            return;
        }
        if (!Boolean.TRUE.equals(category.getSupportSplit())) {
            throw exception(ORDER_SPLIT_NOT_SUPPORTED);
        }
    }

    private void validateAgreementRequirement(MerchantServiceCategoryDO category, String agreementVersion) {
        if (StrUtil.isBlank(agreementVersion)) {
            throw exception(ORDER_AGREEMENT_NOT_CONFIRMED);
        }
        if (Boolean.TRUE.equals(category.getLaborCategoryFlag())
                && "PROJECT_ESCROW".equalsIgnoreCase(resolveAgreementType(category))
                && !StrUtil.containsIgnoreCase(agreementVersion, "project")) {
            throw exception(ORDER_PROJECT_ESCROW_AGREEMENT_REQUIRED);
        }
    }

    private OrderSplitPlan buildSplitPlan(MerchantServiceCategoryDO category, AppOrderPreviewReqVO reqVO, BigDecimal orderAmount) {
        return orderSplitRuleService.matchRule(OrderSplitPreviewContext.builder()
                .categoryId(category.getId())
                .pricingMode(StrUtil.blankToDefault(reqVO.getPricingMode(), category.getDefaultPricingMode()))
                .orderAmount(orderAmount)
                .quantity(reqVO.getQuantity())
                .workerCount(reqVO.getWorkerCount())
                .requireDesc(reqVO.getRequireDesc())
                .build());
    }

    private OrderSplitPlan buildSplitPlan(MerchantServiceCategoryDO category, AppOrderCreateReqVO reqVO, BigDecimal orderAmount) {
        return orderSplitRuleService.matchRule(OrderSplitPreviewContext.builder()
                .categoryId(category.getId())
                .pricingMode(StrUtil.blankToDefault(reqVO.getPricingMode(), category.getDefaultPricingMode()))
                .orderAmount(orderAmount)
                .quantity(reqVO.getQuantity())
                .workerCount(reqVO.getWorkerCount())
                .requireDesc(reqVO.getRequireDesc())
                .build());
    }

    private List<String> parseSupportedPricingModes(MerchantServiceCategoryDO category) {
        if (StrUtil.isBlank(category.getSupportedPricingModes())) {
            return Collections.singletonList(category.getDefaultPricingMode());
        }
        List<String> parsed = JsonUtils.parseArray(category.getSupportedPricingModes(), String.class);
        return CollUtil.isEmpty(parsed) ? Collections.singletonList(category.getDefaultPricingMode()) : parsed;
    }

    private String resolveAgreementType(MerchantServiceCategoryDO category) {
        if (StrUtil.isNotBlank(category.getForceAgreementType())) {
            return category.getForceAgreementType();
        }
        return Boolean.TRUE.equals(category.getLaborCategoryFlag()) ? "PROJECT_ESCROW" : "TRADE_GUARANTEE";
    }

    private String resolveAgreementTitle(String agreementType) {
        if ("PROJECT_ESCROW".equalsIgnoreCase(agreementType)) {
            return getConfigValue(PlatformConfigKeyConstants.AGREEMENT_PROJECT_ESCROW_TITLE, "工程托管协议");
        }
        return getConfigValue(PlatformConfigKeyConstants.AGREEMENT_TRADE_TITLE, "交易保障协议");
    }

    private String resolveAgreementContent(String agreementType) {
        if ("PROJECT_ESCROW".equalsIgnoreCase(agreementType)) {
            return getConfigValue(PlatformConfigKeyConstants.AGREEMENT_PROJECT_ESCROW, "");
        }
        return getConfigValue(PlatformConfigKeyConstants.AGREEMENT_SERVICE, "");
    }

    private String resolvePricingModeName(String pricingMode) {
        if ("FIXED_PRICE".equalsIgnoreCase(pricingMode)) {
            return "一口价";
        }
        if ("CONTRACT".equalsIgnoreCase(pricingMode)) {
            return "承包";
        }
        if ("OUTSOURCING".equalsIgnoreCase(pricingMode)) {
            return "外发";
        }
        if ("HOURLY".equalsIgnoreCase(pricingMode)) {
            return "计时";
        }
        if ("BY_UNIT".equalsIgnoreCase(pricingMode)) {
            return "按单位";
        }
        return pricingMode;
    }

    private AppOrderSplitRuleMatchRespVO toSplitPreviewResp(OrderSplitPlan splitPlan) {
        AppOrderSplitRuleMatchRespVO respVO = new AppOrderSplitRuleMatchRespVO();
        respVO.setMatched(splitPlan.isMatched());
        respVO.setSplitRequired(splitPlan.isSplitRequired());
        respVO.setRuleId(splitPlan.getRuleId());
        respVO.setRuleName(splitPlan.getRuleName());
        respVO.setRuleCode(splitPlan.getRuleCode());
        respVO.setMatchMode(splitPlan.getMatchMode());
        respVO.setSplitMode(splitPlan.getSplitMode());
        respVO.setUnitAmountLimit(splitPlan.getUnitAmountLimit());
        respVO.setUnitCount(splitPlan.getUnitCount());
        respVO.setUnits(splitPlan.getUnits().stream().map(unit -> {
            AppOrderSplitRuleMatchRespVO.UnitPreviewRespVO item = new AppOrderSplitRuleMatchRespVO.UnitPreviewRespVO();
            item.setUnitSeq(unit.getUnitSeq());
            item.setUnitTitle(unit.getUnitTitle());
            item.setUnitType(unit.getUnitType());
            item.setUnitContent(unit.getUnitContent());
            item.setUnitProgress(unit.getUnitProgress());
            item.setUnitAmount(unit.getUnitAmount());
            item.setWorkerCount(unit.getWorkerCount());
            item.setLocked(unit.getLocked());
            item.setLockReason(unit.getLockReason());
            return item;
        }).collect(Collectors.toList()));
        return respVO;
    }

    private List<String> resolveSensitiveHitSummaries(SensitiveDetectResult detectResult) {
        if (detectResult == null || CollUtil.isEmpty(detectResult.getHitWords())) {
            return Collections.emptyList();
        }
        return detectResult.getHitWords().stream()
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    private String buildPreviewToken(MerchantServiceCategoryDO category, AppOrderPreviewReqVO reqVO,
                                     AmapLocationService.ResolvedAddress address, OrderSplitPlan splitPlan,
                                     SensitiveDetectResult detectResult) {
        Map<String, Object> tokenPayload = new LinkedHashMap<>();
        tokenPayload.put("categoryId", category.getId());
        tokenPayload.put("pricingMode", reqVO.getPricingMode());
        tokenPayload.put("budgetAmount", reqVO.getBudgetAmount());
        tokenPayload.put("quantity", reqVO.getQuantity());
        tokenPayload.put("workerCount", resolveWorkerCount(reqVO.getWorkerCount()));
        tokenPayload.put("needInvoice", reqVO.getNeedInvoice());
        tokenPayload.put("needSplit", reqVO.getNeedSplit());
        tokenPayload.put("province", address.getProvince());
        tokenPayload.put("city", address.getCity());
        tokenPayload.put("district", address.getDistrict());
        tokenPayload.put("street", address.getStreet());
        tokenPayload.put("detailAddress", address.getDetailAddress());
        tokenPayload.put("requireDesc", StrUtil.blankToDefault(detectResult.getProcessedContent(), reqVO.getRequireDesc()));
        tokenPayload.put("splitMode", splitPlan.getSplitMode());
        tokenPayload.put("unitCount", splitPlan.getUnitCount());
        tokenPayload.put("strategy", detectResult.getStrategy());
        tokenPayload.put("agreementType", resolveAgreementType(category));
        return DigestUtil.sha256Hex(JsonUtils.toJsonString(tokenPayload));
    }

    private void validatePreviewToken(MerchantServiceCategoryDO category, AppOrderCreateReqVO reqVO,
                                      AmapLocationService.ResolvedAddress address, OrderSplitPlan splitPlan,
                                      SensitiveDetectResult detectResult) {
        AppOrderPreviewReqVO previewReqVO = new AppOrderPreviewReqVO();
        previewReqVO.setCategoryId(reqVO.getCategoryId());
        previewReqVO.setPricingMode(reqVO.getPricingMode());
        previewReqVO.setBudgetAmount(reqVO.getBudgetAmount());
        previewReqVO.setQuantity(reqVO.getQuantity());
        previewReqVO.setWorkerCount(reqVO.getWorkerCount());
        previewReqVO.setServiceDurationDesc(reqVO.getServiceDurationDesc());
        previewReqVO.setRequireDesc(reqVO.getRequireDesc());
        previewReqVO.setProvince(reqVO.getProvince());
        previewReqVO.setCity(reqVO.getCity());
        previewReqVO.setDistrict(reqVO.getDistrict());
        previewReqVO.setStreet(reqVO.getStreet());
        previewReqVO.setDetailAddress(reqVO.getDetailAddress());
        previewReqVO.setLongitude(reqVO.getLongitude());
        previewReqVO.setLatitude(reqVO.getLatitude());
        previewReqVO.setAdcode(reqVO.getAdcode());
        previewReqVO.setNeedInvoice(reqVO.getNeedInvoice());
        previewReqVO.setNeedSplit(reqVO.getNeedSplit());
        previewReqVO.setAttachmentFileIds(reqVO.getAttachmentFileIds());
        previewReqVO.setPriceItems(reqVO.getPriceItems());
        String expected = buildPreviewToken(category, previewReqVO, address, splitPlan, detectResult);
        if (!Objects.equals(expected, reqVO.getPreviewToken())) {
            throw exception(ORDER_PREVIEW_TOKEN_INVALID);
        }
    }

    private OrderInfoDO validateAndGetAccessibleOrder(Long authUserId, Long lbUserId, Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(order.getUserId(), lbUserId)) {
            return order;
        }
        try {
            AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredContext(authUserId);
            if (context.getMerchant() != null && Objects.equals(order.getMerchantId(), context.getMerchant().getId())) {
                return order;
            }
        } catch (Exception ignored) {
            // ignore and continue fallback
        }
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId()));
        if (merchant != null && Objects.equals(order.getMerchantId(), merchant.getId())) {
            return order;
        }
        throw exception(ORDER_ACCESS_DENIED);
    }

    private OrderInfoDO validateAndGetAccessibleOrderForAccept(Long lbUserId, Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(order.getUserId(), lbUserId)) {
            throw exception(ORDER_STATUS_NOT_ALLOW_ACCEPT);
        }
        if (!Arrays.asList("PENDING_ACCEPT", "ACCEPTED", "SERVING").contains(order.getStatus())) {
            throw exception(ORDER_STATUS_NOT_ALLOW_ACCEPT);
        }
        return order;
    }

    private void validateMerchantCanAcceptUnit(Long merchantId, Long unitId, AppMerchantOperatorContext context) {
        OrderMatchRecordDO activeRecord = orderMatchRecordMapper.selectOne(new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .eq(OrderMatchRecordDO::getMerchantId, merchantId)
                .eq(OrderMatchRecordDO::getUnitId, unitId)
                .eq(OrderMatchRecordDO::getStatus, "PUSHED")
                .ge(OrderMatchRecordDO::getExpiredTime, LocalDateTime.now())
                .orderByDesc(OrderMatchRecordDO::getId)
                .last("LIMIT 1"));
        if (activeRecord == null) {
            throw exception(ORDER_STATUS_NOT_ALLOW_ACCEPT);
        }
        if (context != null && context.isSubAccount()) {
            OrderInfoDO order = orderInfoMapper.selectById(activeRecord.getOrderId());
            if (order == null || !isOrderVisibleToOperator(order, context)) {
                throw exception(ORDER_STATUS_NOT_ALLOW_ACCEPT);
            }
        }
    }

    private boolean canAccessUnit(Long authUserId, Long lbUserId, OrderUnitDO unit) {
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(order.getUserId(), lbUserId)) {
            return true;
        }
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId()));
        return merchant != null && Objects.equals(order.getMerchantId(), merchant.getId());
    }

    private OrderUnitDO resolveTargetUnit(AppOrderAcceptCreateReqVO reqVO, Long orderId) {
        OrderUnitDO unit;
        if (reqVO.getUnitId() != null) {
            unit = orderUnitMapper.selectById(reqVO.getUnitId());
            if (unit == null || !Objects.equals(unit.getOrderId(), orderId)) {
                throw exception(ORDER_UNIT_NOT_EXISTS);
            }
        } else {
            List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                    .eq(OrderUnitDO::getOrderId, orderId)
                    .eq(OrderUnitDO::getStatus, "PENDING_ACCEPT")
                    .eq(OrderUnitDO::getIsLocked, Boolean.FALSE)
                    .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
            unit = CollUtil.getFirst(units);
            if (unit == null) {
                throw exception(ORDER_UNIT_NOT_EXISTS);
            }
        }
        return unit;
    }

    private MerchantInfoDO getRequiredMerchant(AppMerchantOperatorContext context) {
        memberQualificationExpiryService.validateMerchantCanAccept(context.getMerchant().getUserId());
        MerchantInfoDO merchant = context.getMerchant();
        if (merchant == null || !"ENABLE".equals(merchant.getStatus())) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return merchant;
    }

    private AmapLocationService.ResolvedAddress resolveOrderAddress(AppOrderCreateReqVO reqVO) {
        return amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(reqVO.getProvince())
                .city(reqVO.getCity())
                .district(reqVO.getDistrict())
                .street(reqVO.getStreet())
                .detailAddress(reqVO.getDetailAddress())
                .longitude(reqVO.getLongitude())
                .latitude(reqVO.getLatitude())
                .adcode(reqVO.getAdcode())
                .build());
    }

    private AmapLocationService.ResolvedAddress resolveOrderAddress(AppOrderPreviewReqVO reqVO) {
        return amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(reqVO.getProvince())
                .city(reqVO.getCity())
                .district(reqVO.getDistrict())
                .street(reqVO.getStreet())
                .detailAddress(reqVO.getDetailAddress())
                .longitude(reqVO.getLongitude())
                .latitude(reqVO.getLatitude())
                .adcode(reqVO.getAdcode())
                .build());
    }

    private OrderInfoDO ensureResolvedOrderLocation(OrderInfoDO order) {
        if (order == null) {
            return null;
        }
        if (order.getLongitude() != null && order.getLatitude() != null
                && StrUtil.isNotBlank(order.getProvince()) && StrUtil.isNotBlank(order.getCity())
                && StrUtil.isNotBlank(order.getDistrict())) {
            return order;
        }
        AmapLocationService.ResolvedAddress resolved = amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(order.getProvince())
                .city(order.getCity())
                .district(order.getDistrict())
                .street(order.getStreet())
                .detailAddress(order.getDetailAddress())
                .longitude(order.getLongitude())
                .latitude(order.getLatitude())
                .build());
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .province(resolved.getProvince())
                .city(resolved.getCity())
                .district(resolved.getDistrict())
                .street(resolved.getStreet())
                .detailAddress(resolved.getDetailAddress())
                .longitude(resolved.getLongitude())
                .latitude(resolved.getLatitude())
                .build());
        order.setProvince(resolved.getProvince());
        order.setCity(resolved.getCity());
        order.setDistrict(resolved.getDistrict());
        order.setStreet(resolved.getStreet());
        order.setDetailAddress(resolved.getDetailAddress());
        order.setLongitude(resolved.getLongitude());
        order.setLatitude(resolved.getLatitude());
        return order;
    }

    private MerchantServicePointDO ensureResolvedServicePoint(MerchantServicePointDO point) {
        if (point == null) {
            return null;
        }
        if (point.getLongitude() != null && point.getLatitude() != null
                && StrUtil.isNotBlank(point.getProvince()) && StrUtil.isNotBlank(point.getCity())
                && StrUtil.isNotBlank(point.getDistrict())) {
            return point;
        }
        AmapLocationService.ResolvedAddress resolved = amapLocationService.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                .province(point.getProvince())
                .city(point.getCity())
                .district(point.getDistrict())
                .street(point.getStreet())
                .detailAddress(point.getDetailAddress())
                .longitude(point.getLongitude())
                .latitude(point.getLatitude())
                .build());
        merchantServicePointMapper.updateById(MerchantServicePointDO.builder()
                .id(point.getId())
                .province(resolved.getProvince())
                .city(resolved.getCity())
                .district(resolved.getDistrict())
                .street(resolved.getStreet())
                .detailAddress(resolved.getDetailAddress())
                .longitude(resolved.getLongitude())
                .latitude(resolved.getLatitude())
                .build());
        point.setProvince(resolved.getProvince());
        point.setCity(resolved.getCity());
        point.setDistrict(resolved.getDistrict());
        point.setStreet(resolved.getStreet());
        point.setDetailAddress(resolved.getDetailAddress());
        point.setLongitude(resolved.getLongitude());
        point.setLatitude(resolved.getLatitude());
        return point;
    }

    private BigDecimal calculateOrderDistanceKm(OrderInfoDO order) {
        if (order == null || order.getMerchantId() == null) {
            return null;
        }
        OrderInfoDO resolvedOrder = ensureResolvedOrderLocation(order);
        return calculateDistanceToMerchant(resolvedOrder, resolvedOrder.getMerchantId());
    }

    private BigDecimal calculateDistanceToMerchant(OrderInfoDO order, Long merchantId) {
        if (order == null || merchantId == null || order.getLongitude() == null || order.getLatitude() == null) {
            return null;
        }
        List<MerchantServicePointDO> servicePoints = merchantServicePointMapper.selectListByMerchantId(merchantId);
        if (CollUtil.isEmpty(servicePoints)) {
            return null;
        }
        BigDecimal minDistance = null;
        for (MerchantServicePointDO point : servicePoints) {
            if (!Objects.equals(point.getStatus(), "ENABLE")) {
                continue;
            }
            MerchantServicePointDO resolvedPoint = ensureResolvedServicePoint(point);
            BigDecimal distance = amapLocationService.calculateDistanceKm(
                    resolvedPoint.getLongitude(), resolvedPoint.getLatitude(),
                    order.getLongitude(), order.getLatitude());
            if (distance == null) {
                continue;
            }
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
            }
        }
        if (minDistance != null) {
            return minDistance;
        }
        for (MerchantServicePointDO point : servicePoints) {
            MerchantServicePointDO resolvedPoint = ensureResolvedServicePoint(point);
            BigDecimal distance = amapLocationService.calculateDistanceKm(
                    resolvedPoint.getLongitude(), resolvedPoint.getLatitude(),
                    order.getLongitude(), order.getLatitude());
            if (distance == null) {
                continue;
            }
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private BigDecimal calculateDistanceToMerchant(OrderInfoDO order, AppMerchantOperatorContext context) {
        if (context == null || context.getMerchant() == null) {
            return null;
        }
        List<MerchantServicePointDO> servicePoints = merchantOperatorContextService.filterVisibleServicePoints(context,
                merchantServicePointMapper.selectListByMerchantId(context.getMerchant().getId()));
        return calculateDistanceToPoints(order, servicePoints);
    }

    private BigDecimal calculateDistanceToPoints(OrderInfoDO order, List<MerchantServicePointDO> servicePoints) {
        if (order == null || order.getLongitude() == null || order.getLatitude() == null || CollUtil.isEmpty(servicePoints)) {
            return null;
        }
        BigDecimal minDistance = null;
        for (MerchantServicePointDO point : servicePoints) {
            if (!Objects.equals(point.getStatus(), "ENABLE")) {
                continue;
            }
            MerchantServicePointDO resolvedPoint = ensureResolvedServicePoint(point);
            BigDecimal distance = amapLocationService.calculateDistanceKm(
                    resolvedPoint.getLongitude(), resolvedPoint.getLatitude(),
                    order.getLongitude(), order.getLatitude());
            if (distance == null) {
                continue;
            }
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
            }
        }
        if (minDistance != null) {
            return minDistance;
        }
        for (MerchantServicePointDO point : servicePoints) {
            MerchantServicePointDO resolvedPoint = ensureResolvedServicePoint(point);
            BigDecimal distance = amapLocationService.calculateDistanceKm(
                    resolvedPoint.getLongitude(), resolvedPoint.getLatitude(),
                    order.getLongitude(), order.getLatitude());
            if (distance == null) {
                continue;
            }
            if (minDistance == null || distance.compareTo(minDistance) < 0) {
                minDistance = distance;
            }
        }
        return minDistance;
    }

    private boolean isOrderVisibleToOperator(OrderInfoDO order, AppMerchantOperatorContext context) {
        if (context == null || context.isMainAccount()) {
            return true;
        }
        return calculateDistanceToMerchant(order, context) != null;
    }

    private void validateUnitVisibleToOperator(AppMerchantOperatorContext context, OrderUnitDO unit) {
        if (context == null || context.isMainAccount()) {
            return;
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null || !isOrderVisibleToOperator(order, context)) {
            throw exception(ORDER_UNIT_ACCESS_DENIED);
        }
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<OrderInfoDO> orders) {
        Set<Long> categoryIds = orders.stream().map(OrderInfoDO::getCategoryId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollUtil.isEmpty(categoryIds)) {
            return Collections.emptyMap();
        }
        return merchantServiceCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(MerchantServiceCategoryDO::getId, item -> item));
    }

    private Set<Long> resolveCategoryIdsByKeyword(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return Collections.emptySet();
        }
        return merchantServiceCategoryMapper.selectList(new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                        .like(MerchantServiceCategoryDO::getCategoryName, keyword))
                .stream()
                .map(MerchantServiceCategoryDO::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Long> resolveCategoryFilterIds(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        List<MerchantServiceCategoryDO> categories = merchantServiceCategoryMapper.selectList();
        if (CollUtil.isEmpty(categories)) {
            return Collections.singleton(categoryId);
        }
        Map<Long, List<MerchantServiceCategoryDO>> childrenMap = categories.stream()
                .filter(item -> item.getParentId() != null)
                .collect(Collectors.groupingBy(MerchantServiceCategoryDO::getParentId));
        Set<Long> categoryIds = new LinkedHashSet<>();
        collectCategoryAndDescendantIds(categoryId, childrenMap, categoryIds);
        return categoryIds.isEmpty() ? Collections.singleton(categoryId) : categoryIds;
    }

    private void collectCategoryAndDescendantIds(Long categoryId,
                                                 Map<Long, List<MerchantServiceCategoryDO>> childrenMap,
                                                 Set<Long> results) {
        if (categoryId == null || !results.add(categoryId)) {
            return;
        }
        for (MerchantServiceCategoryDO child : childrenMap.getOrDefault(categoryId, Collections.emptyList())) {
            collectCategoryAndDescendantIds(child.getId(), childrenMap, results);
        }
    }

    private Comparator<AppOrderAcceptPageItemRespVO> buildAcceptOrderComparator(AppOrderAcceptPageReqVO reqVO,
                                                                                 Map<Long, BigDecimal> distanceMap) {
        Comparator<AppOrderAcceptPageItemRespVO> comparator = Comparator
                .comparing(AppOrderAcceptPageItemRespVO::getCreateTime, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(AppOrderAcceptPageItemRespVO::getOrderId, Comparator.nullsLast(Comparator.reverseOrder()));

        if (StrUtil.isNotBlank(reqVO.getPublishTimeSort()) && "OLDEST".equalsIgnoreCase(reqVO.getPublishTimeSort())) {
            comparator = Comparator
                    .comparing(AppOrderAcceptPageItemRespVO::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(AppOrderAcceptPageItemRespVO::getOrderId, Comparator.nullsLast(Comparator.naturalOrder()));
        }
        if (StrUtil.isNotBlank(reqVO.getPriceSort())) {
            Comparator<BigDecimal> priceComparator = "PRICE_ASC".equalsIgnoreCase(reqVO.getPriceSort())
                    ? Comparator.nullsLast(Comparator.naturalOrder())
                    : Comparator.nullsLast(Comparator.reverseOrder());
            Comparator<AppOrderAcceptPageItemRespVO> baseComparator = comparator;
            comparator = Comparator.comparing(AppOrderAcceptPageItemRespVO::getOrderAmount, priceComparator)
                    .thenComparing(baseComparator);
        }
        if (StrUtil.isNotBlank(reqVO.getDistanceSort())) {
            Comparator<BigDecimal> distanceComparator = "FARTHEST".equalsIgnoreCase(reqVO.getDistanceSort())
                    ? Comparator.nullsLast(Comparator.reverseOrder())
                    : Comparator.nullsLast(Comparator.naturalOrder());
            Comparator<AppOrderAcceptPageItemRespVO> baseComparator = comparator;
            comparator = Comparator.comparing((AppOrderAcceptPageItemRespVO item) -> distanceMap.get(item.getOrderId()), distanceComparator)
                    .thenComparing(baseComparator);
        }
        return comparator;
    }

    private List<OrderInfoDO> applyBusinessCategoryFilter(Long userId, List<OrderInfoDO> orders, String businessCategory) {
        if (StrUtil.isBlank(businessCategory) || CollUtil.isEmpty(orders)) {
            return orders;
        }
        return orders.stream()
                .filter(order -> Objects.equals(resolveBusinessCategory(order, isWaitReviewOrder(userId, order)), businessCategory))
                .collect(Collectors.toList());
    }

    private boolean isWaitReviewOrder(Long userId, OrderInfoDO order) {
        if (order == null || !Objects.equals(order.getStatus(), "FINISHED")) {
            return false;
        }
        return reviewCommentMapper.selectCount(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getOrderId, order.getId())
                .eq(ReviewCommentDO::getFromUserId, userId)) == 0;
    }

    private String resolveBusinessCategory(OrderInfoDO order, boolean waitReview) {
        if (order == null) {
            return null;
        }
        if (waitReview) {
            return "WAIT_REVIEW";
        }
        switch (StrUtil.blankToDefault(order.getStatus(), "")) {
            case "PENDING_PAY":
                return "WAIT_PAY";
            case "PENDING_ACCEPT":
                return "WAIT_ACCEPT";
            case "ACCEPTED":
            case "SERVING":
            case "PENDING_CONFIRM":
                return "IN_SERVICE";
            case "AFTER_SALE":
                return "AFTER_SALE";
            case "REFUNDED":
                return "REFUNDED";
            case "FINISHED":
                return "FINISHED";
            default:
                return order.getStatus();
        }
    }

    private void ensureVerifyCode(OrderUnitDO unit) {
        if (unit == null) {
            return;
        }
        if (StrUtil.isNotBlank(unit.getVerifyCode()) && StrUtil.isNotBlank(unit.getVerifyStatus())) {
            return;
        }
        String verifyCode = String.valueOf(100000 + Math.abs(Objects.hash(unit.getId(), unit.getUnitNo())) % 900000);
        String verifyStatus = Arrays.asList("FINISHED", "REFUNDED", "CLOSED").contains(unit.getStatus()) ? "VERIFIED" : "PENDING";
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unit.getId())
                .verifyCode(verifyCode)
                .verifyStatus(verifyStatus)
                .build());
        unit.setVerifyCode(verifyCode);
        unit.setVerifyStatus(verifyStatus);
    }

    private boolean canVerifyUnit(OrderUnitDO unit) {
        return unit != null && Arrays.asList("ACCEPTED", "SERVING", "PENDING_CONFIRM").contains(unit.getStatus());
    }

    private List<AppOrderDetailRespVO.OrderTimelineRespVO> buildOrderTimeline(OrderInfoDO order,
                                                                              List<OrderUnitDO> units,
                                                                              PayOrderDO payOrder,
                                                                              List<PayRefundDO> refunds,
                                                                              List<ComplaintDO> complaints,
                                                                              List<AppealDO> appeals,
                                                                              List<OrderOperateLogDO> operateLogs) {
        List<AppOrderDetailRespVO.OrderTimelineRespVO> timeline = new ArrayList<>();
        if (order != null) {
            timeline.add(buildTimeline("ORDER", order.getId(), null, "订单创建", order.getRequireDesc(), order.getStatus(), order.getCreateTime()));
        }
        if (payOrder != null) {
            timeline.add(buildTimeline("PAY", payOrder.getId(), null, "支付单创建", payOrder.getSubject(), String.valueOf(payOrder.getStatus()), payOrder.getCreateTime()));
            if (payOrder.getSuccessTime() != null) {
                timeline.add(buildTimeline("PAY", payOrder.getId(), null, "支付成功", payOrder.getMerchantOrderId(), "SUCCESS", payOrder.getSuccessTime()));
            }
        }
        for (OrderUnitDO unit : units) {
            timeline.add(buildTimeline("UNIT", unit.getId(), unit.getId(), "单元创建", unit.getUnitTitle(), unit.getStatus(), unit.getCreateTime()));
            if (unit.getVerifyTime() != null) {
                timeline.add(buildTimeline("VERIFY", unit.getId(), unit.getId(), "单元核销", unit.getVerifyRemark(), unit.getVerifyStatus(), unit.getVerifyTime()));
            }
            if (unit.getFinishTime() != null) {
                timeline.add(buildTimeline("UNIT", unit.getId(), unit.getId(), "单元完成", unit.getUnitTitle(), unit.getStatus(), unit.getFinishTime()));
            }
        }
        for (PayRefundDO refund : refunds) {
            timeline.add(buildTimeline("REFUND", refund.getId(), null, "退款申请", refund.getReason(), refund.getAuditStatus(), refund.getCreateTime()));
            if (refund.getSuccessTime() != null) {
                timeline.add(buildTimeline("REFUND", refund.getId(), null, "退款成功", refund.getReason(), resolveRefundStatusName(refund.getStatus()), refund.getSuccessTime()));
            }
        }
        for (ComplaintDO complaint : complaints) {
            timeline.add(buildTimeline("COMPLAINT", complaint.getId(), complaint.getUnitId(), "投诉提交", complaint.getContent(), complaint.getStatus(), complaint.getCreateTime()));
        }
        for (AppealDO appeal : appeals) {
            timeline.add(buildTimeline("APPEAL", appeal.getId(), appeal.getUnitId(), "申诉提交", appeal.getContent(), appeal.getStatus(), appeal.getCreateTime()));
        }
        for (OrderOperateLogDO log : operateLogs) {
            timeline.add(buildTimeline("LOG", log.getId(), log.getUnitId(), log.getOperateType(), log.getRemark(), log.getAfterStatus(), log.getOperateTime()));
        }
        timeline.sort(Comparator.comparing(AppOrderDetailRespVO.OrderTimelineRespVO::getEventTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return timeline;
    }

    private AppOrderDetailRespVO.OrderTimelineRespVO buildTimeline(String type, Long bizId, Long unitId,
                                                                   String title, String content, String status,
                                                                   LocalDateTime eventTime) {
        AppOrderDetailRespVO.OrderTimelineRespVO timelineRespVO = new AppOrderDetailRespVO.OrderTimelineRespVO();
        timelineRespVO.setTimelineType(type);
        timelineRespVO.setBizId(bizId);
        timelineRespVO.setUnitId(unitId);
        timelineRespVO.setTitle(title);
        timelineRespVO.setContent(content);
        timelineRespVO.setStatus(status);
        timelineRespVO.setEventTime(eventTime);
        return timelineRespVO;
    }

    private AppOrderDetailRespVO.MallEntryRespVO buildMallEntryResp() {
        AppOrderDetailRespVO.MallEntryRespVO respVO = new AppOrderDetailRespVO.MallEntryRespVO();
        respVO.setEnabled(Boolean.parseBoolean(getConfigValue(
                PlatformConfigKeyConstants.APP_MALL_ENTRY_ENABLED, "false")));
        respVO.setTitle(getConfigValue(PlatformConfigKeyConstants.APP_MALL_ENTRY_TITLE, ""));
        respVO.setUrl(getConfigValue(PlatformConfigKeyConstants.APP_MALL_ENTRY_URL, ""));
        return respVO;
    }

    private AppOrderDetailRespVO.MallConsumeRelationRespVO buildMallConsumeRelationResp(OrderInfoDO order) {
        AppOrderDetailRespVO.MallConsumeRelationRespVO respVO = new AppOrderDetailRespVO.MallConsumeRelationRespVO();
        respVO.setConsumeRecordId(order.getMallConsumeRecordId());
        respVO.setConsumeRecordNo(order.getMallConsumeRecordNo());
        respVO.setConsumeAmount(order.getMallConsumeAmount());
        respVO.setConsumeStatus(order.getMallConsumeStatus());
        return respVO;
    }

    private AppOrderDetailRespVO.PromoteDeductRespVO buildPromoteDeductResp(OrderInfoDO order) {
        AppOrderDetailRespVO.PromoteDeductRespVO respVO = new AppOrderDetailRespVO.PromoteDeductRespVO();
        respVO.setDeductAmount(order.getPromoteDeductAmount());
        respVO.setSourceType(order.getPromoteDeductSourceType());
        respVO.setSourceId(order.getPromoteDeductSourceId());
        respVO.setSourceNo(order.getPromoteDeductSourceNo());
        respVO.setPayableAmountAfterDeduct(order.getPayableAmountAfterDeduct());
        return respVO;
    }

    private <T> PageResult<T> manualPage(List<T> list, Integer pageNo, Integer pageSize) {
        if (CollUtil.isEmpty(list)) {
            return PageResult.empty();
        }
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = (safePageNo - 1) * safePageSize;
        if (fromIndex >= list.size()) {
            return PageResult.empty((long) list.size());
        }
        int toIndex = Math.min(fromIndex + safePageSize, list.size());
        return new PageResult<>(list.subList(fromIndex, toIndex), (long) list.size());
    }

    private Integer resolveWorkerCount(Integer workerCount) {
        return workerCount == null || workerCount < 1 ? 1 : workerCount;
    }

    private boolean isAppealAvailable(OrderUnitDO unit) {
        return unit != null
                && unit.getAppealExpireTime() != null
                && LocalDateTime.now().isBefore(unit.getAppealExpireTime());
    }

    private Map<Long, FileDO> buildFileMap(Set<Long> fileIds) {
        Map<Long, FileDO> fileMap = new HashMap<>();
        for (Long fileId : fileIds) {
            if (fileId == null || fileMap.containsKey(fileId)) {
                continue;
            }
            try {
                fileMap.put(fileId, fileService.getFile(fileId));
            } catch (Exception ignored) {
            }
        }
        return fileMap;
    }

    private String buildFileHash(FileDO file) {
        try {
            byte[] content = fileService.getFileContent(file.getConfigId(), file.getPath());
            return DigestUtil.sha256Hex(content);
        } catch (Exception ex) {
            return DigestUtil.sha256Hex(StrUtil.blankToDefault(file.getUrl(), String.valueOf(file.getId())));
        }
    }

    private String getConfigValue(String key, String defaultValue) {
        String value = Optional.ofNullable(configService.getConfigByKey(key))
                .map(config -> config.getValue())
                .orElse(null);
        return StrUtil.isBlank(value) ? defaultValue : value;
    }

    private BigDecimal calculateOrderAmount(AppOrderCreateReqVO reqVO) {
        if (CollUtil.isNotEmpty(reqVO.getPriceItems())) {
            return reqVO.getPriceItems().stream()
                    .map(AppOrderCreateReqVO.OrderPriceItemReqVO::getItemAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return Optional.ofNullable(reqVO.getBudgetAmount()).orElse(BigDecimal.ZERO);
    }

    private BigDecimal calculateOrderAmount(AppOrderPreviewReqVO reqVO) {
        if (CollUtil.isNotEmpty(reqVO.getPriceItems())) {
            return reqVO.getPriceItems().stream()
                    .map(AppOrderCreateReqVO.OrderPriceItemReqVO::getItemAmount)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        return Optional.ofNullable(reqVO.getBudgetAmount()).orElse(BigDecimal.ZERO);
    }

    private void savePriceItems(Long orderId, List<AppOrderCreateReqVO.OrderPriceItemReqVO> priceItems) {
        if (CollUtil.isEmpty(priceItems)) {
            return;
        }
        for (int i = 0; i < priceItems.size(); i++) {
            AppOrderCreateReqVO.OrderPriceItemReqVO item = priceItems.get(i);
            orderPriceItemMapper.insert(OrderPriceItemDO.builder()
                    .orderId(orderId)
                    .itemType(item.getItemType())
                    .itemName(item.getItemName())
                    .itemAmount(item.getItemAmount())
                    .sortNo(item.getSortNo() != null ? item.getSortNo() : i + 1)
                    .build());
        }
    }

    private void saveAttachments(Long orderId, List<Long> attachmentFileIds) {
        if (CollUtil.isEmpty(attachmentFileIds)) {
            return;
        }
        for (int i = 0; i < attachmentFileIds.size(); i++) {
            orderAttachmentMapper.insert(OrderAttachmentDO.builder()
                    .orderId(orderId)
                    .fileId(attachmentFileIds.get(i))
                    .fileType("ORDER_ATTACH")
                    .sortNo(i + 1)
                    .build());
        }
    }

    private void saveUnits(OrderInfoDO order, OrderSplitPlan splitPlan) {
        List<OrderSplitPlan.OrderSplitUnitPlan> unitPlans = splitPlan.getUnits();
        Long prevUnitId = null;
        for (OrderSplitPlan.OrderSplitUnitPlan unitPlan : unitPlans) {
            OrderUnitDO unit = OrderUnitDO.builder()
                    .orderId(order.getId())
                    .unitNo("LBU" + IdUtil.getSnowflakeNextIdStr())
                    .unitSeq(unitPlan.getUnitSeq())
                    .unitTitle(unitPlan.getUnitTitle())
                    .unitType(unitPlan.getUnitType())
                    .unitAmount(unitPlan.getUnitAmount())
                    .splitMode(splitPlan.getSplitMode())
                    .unitContent(unitPlan.getUnitContent())
                    .unitProgress(unitPlan.getUnitProgress())
                    .workerCount(unitPlan.getWorkerCount())
                    .maxAmountLimit(unitPlan.getMaxAmountLimit())
                    .prevUnitId(prevUnitId)
                    .isLocked(unitPlan.getLocked())
                    .lockReason(unitPlan.getLockReason())
                    .status(unitPlan.getInitStatus())
                    .dispatchStatus("WAITING")
                    .currentBatchNo(0)
                    .autoRefundStatus("NONE")
                    .build();
            orderUnitMapper.insert(unit);
            prevUnitId = unit.getId();
        }
    }

    private void saveOperateLog(Long orderId, Long unitId, String operateType, String operateRole,
                                Long operateBy, String beforeStatus, String afterStatus, String remark) {
        orderOperateLogMapper.insert(OrderOperateLogDO.builder()
                .orderId(orderId)
                .unitId(unitId)
                .operateType(operateType)
                .operateRole(operateRole)
                .operateBy(operateBy)
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .remark(remark)
                .operateTime(LocalDateTime.now())
                .build());
    }

    private AppOrderUnitRespVO toOrderUnitRespVO(OrderUnitDO unit) {
        AppOrderUnitRespVO respVO = new AppOrderUnitRespVO();
        respVO.setId(unit.getId());
        respVO.setOrderId(unit.getOrderId());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setUnitSeq(unit.getUnitSeq());
        respVO.setUnitTitle(unit.getUnitTitle());
        respVO.setUnitType(unit.getUnitType());
        respVO.setUnitAmount(unit.getUnitAmount());
        respVO.setSplitMode(unit.getSplitMode());
        respVO.setUnitContent(unit.getUnitContent());
        respVO.setUnitProgress(unit.getUnitProgress());
        respVO.setWorkerCount(unit.getWorkerCount());
        respVO.setMaxAmountLimit(unit.getMaxAmountLimit());
        respVO.setPrevUnitId(unit.getPrevUnitId());
        respVO.setIsLocked(unit.getIsLocked());
        respVO.setLockReason(unit.getLockReason());
        respVO.setMerchantId(unit.getMerchantId());
        respVO.setStatus(unit.getStatus());
        respVO.setDispatchStatus(unit.getDispatchStatus());
        respVO.setCurrentBatchNo(unit.getCurrentBatchNo());
        respVO.setFlowTime(unit.getFlowTime());
        respVO.setFlowReason(unit.getFlowReason());
        respVO.setAutoRefundStatus(unit.getAutoRefundStatus());
        respVO.setAutoRefundId(unit.getAutoRefundId());
        respVO.setAcceptDeadlineTime(unit.getAcceptDeadlineTime());
        respVO.setFinishTime(unit.getFinishTime());
        respVO.setAppealExpireTime(unit.getAppealExpireTime());
        respVO.setAppealAvailable(isAppealAvailable(unit));
        respVO.setVerifyStatus(unit.getVerifyStatus());
        respVO.setVerifyCode(unit.getVerifyCode());
        respVO.setVerifyTime(unit.getVerifyTime());
        respVO.setVerifyBy(unit.getVerifyBy());
        respVO.setVerifyRemark(unit.getVerifyRemark());
        respVO.setCreateTime(unit.getCreateTime());
        return respVO;
    }

    private String resolveOrderStatusAfterUnitConfirm(List<OrderUnitDO> units) {
        if (!units.isEmpty() && units.stream().allMatch(item -> Objects.equals(item.getStatus(), "FINISHED"))) {
            return "FINISHED";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "APPEALING"))) {
            return "AFTER_SALE";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "PENDING_CONFIRM"))) {
            return "PENDING_CONFIRM";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "SERVING"))) {
            return "SERVING";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "ACCEPTED"))) {
            return "ACCEPTED";
        }
        if (units.stream().anyMatch(item -> Objects.equals(item.getStatus(), "PENDING_ACCEPT")
                || Objects.equals(item.getStatus(), "PENDING_CREATE"))) {
            return "PENDING_ACCEPT";
        }
        return "FINISHED";
    }

    private String resolvePayStatus(OrderInfoDO order) {
        if ("PENDING_PAY".equals(order.getStatus())) {
            return "WAITING";
        }
        if ("CLOSED".equals(order.getStatus())) {
            return "CLOSED";
        }
        return "SUCCESS";
    }

    private String resolveRefundStatusName(Integer status) {
        if (PayRefundStatusEnum.isSuccess(status)) {
            return PayRefundStatusEnum.SUCCESS.getName();
        }
        if (PayRefundStatusEnum.isFailure(status)) {
            return PayRefundStatusEnum.FAILURE.getName();
        }
        return PayRefundStatusEnum.WAITING.getName();
    }

    private void notifyOrderStatusChanged(OrderInfoDO order, String afterStatus, String remark) {
        if (order == null || order.getId() == null || order.getUserId() == null) {
            return;
        }
        messagePushDispatchService.dispatchSingle("ORDER_STATUS_CHANGED", "订单状态通知", "ORDER",
                order.getId(), order.getUserId(),
                StrUtil.blankToDefault(remark, "订单状态已更新为" + formatOrderStatus(afterStatus)));
    }

    private void notifyVerifySuccess(OrderInfoDO order, OrderUnitDO unit) {
        if (order == null || unit == null) {
            return;
        }
        messagePushDispatchService.dispatchSingle("VERIFY_SUCCESS", "核销成功通知", "ORDER",
                unit.getId(), order.getUserId(), "订单单元核销成功");
        if (unit.getMerchantId() != null) {
            messagePushDispatchService.dispatchSingle("VERIFY_SUCCESS", "核销成功通知", "ORDER",
                    unit.getId(), unit.getMerchantId(), "订单单元核销成功");
        }
    }

    private void notifyVerifyException(OrderInfoDO order, OrderUnitDO unit, Long receiverUserId, String remark) {
        Long bizId = unit != null ? unit.getId() : (order != null ? order.getId() : null);
        if (receiverUserId == null || bizId == null) {
            return;
        }
        messagePushDispatchService.dispatchSingle("VERIFY_EXCEPTION", "核销异常通知", "ORDER",
                bizId, receiverUserId, StrUtil.blankToDefault(remark, "订单单元核销异常"));
    }

    private String formatOrderStatus(String status) {
        if (Objects.equals(status, "PENDING_PAY")) {
            return "待支付";
        }
        if (Objects.equals(status, "PENDING_ACCEPT")) {
            return "待接单";
        }
        if (Objects.equals(status, "ACCEPTED")) {
            return "已接单";
        }
        if (Objects.equals(status, "SERVING")) {
            return "服务中";
        }
        if (Objects.equals(status, "PENDING_CONFIRM")) {
            return "待确认";
        }
        if (Objects.equals(status, "AFTER_SALE")) {
            return "售后中";
        }
        if (Objects.equals(status, "FINISHED")) {
            return "已完成";
        }
        if (Objects.equals(status, "REFUNDED")) {
            return "已退款";
        }
        if (Objects.equals(status, "CLOSED")) {
            return "已关闭";
        }
        return StrUtil.blankToDefault(status, "未知状态");
    }
}
