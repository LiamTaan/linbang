package cn.iocoder.yudao.module.linbang.service.app.review;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditBenefitsRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppMerchantReviewSummaryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppPendingReviewUnitRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewCreditRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appealfilerel.AppealFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaintfilerel.ComplaintFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditlevelbenefit.CreditLevelBenefitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.appealfilerel.AppealFileRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaintfilerel.ComplaintFileRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrule.CreditRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditLevelBenefitService;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditLevelResolver;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import cn.iocoder.yudao.module.linbang.service.match.PriorityPoolService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.MerchantReviewMetricsResp;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.ReviewCommentMetricsService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveContentDetectService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveDetectResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.APPEAL_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.COMPLAINT_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CONTENT_SENSITIVE_BLOCKED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CONTENT_SENSITIVE_REVIEW_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_APPEAL_EXPIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_UNIT_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_ACCESS_DENIED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_COMMENT_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_DUPLICATED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_EDIT_EXPIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_EDIT_NOT_ALLOWED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_UNIT_STATUS_INVALID;

@Service
@Validated
public class AppReviewServiceImpl implements AppReviewService {

    private static final String REVIEW_STATUS_ENABLE = "ENABLE";
    private static final String UNIT_STATUS_FINISHED = "FINISHED";
    private static final String AUTO_REVIEW_DEFAULT_CONTENT = "系统自动评价：服务已完成，评价超时自动生成";
    private static final String REVIEW_BIZ_TYPE = "REVIEW";
    private static final int REVIEW_EDIT_LIMIT = 1;

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private ComplaintFileRelMapper complaintFileRelMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private AppealFileRelMapper appealFileRelMapper;
    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private CreditRuleMapper creditRuleMapper;
    @Resource
    private CreditRecordService creditRecordService;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private SensitiveContentDetectService sensitiveContentDetectService;
    @Resource
    private CreditLevelBenefitService creditLevelBenefitService;
    @Resource
    private ReviewCommentMetricsService reviewCommentMetricsService;
    @Resource
    private PriorityPoolService priorityPoolService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComplaint(Long authUserId, @Valid AppComplaintCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateOrderAccess(authUserId, loginUser.getId(), reqVO.getOrderId());
        validateUnit(reqVO.getUnitId(), order.getId());
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_COMPLAINT, loginUser.getId(), "COMPLAINT", order.getId(), reqVO.getContent());

        ComplaintDO complaint = ComplaintDO.builder()
                .complaintNo("LBC" + IdUtil.getSnowflakeNextIdStr())
                .orderId(order.getId())
                .unitId(reqVO.getUnitId())
                .complainantUserId(loginUser.getId())
                .respondentUserId(reqVO.getRespondentUserId())
                .complaintType(reqVO.getComplaintType())
                .content(detectResult.getProcessedContent())
                .status("PENDING")
                .build();
        complaintMapper.insert(complaint);
        saveComplaintFiles(complaint.getId(), reqVO.getFileIds());
        orderInfoMapper.updateById(OrderInfoDO.builder().id(order.getId()).status("AFTER_SALE").build());
        return complaint.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAppeal(Long authUserId, @Valid AppAppealCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateOrderAccess(authUserId, loginUser.getId(), reqVO.getOrderId());
        OrderUnitDO unit = validateUnit(reqVO.getUnitId(), order.getId());
        if (unit != null && (unit.getAppealExpireTime() == null || !LocalDateTime.now().isBefore(unit.getAppealExpireTime()))) {
            throw exception(ORDER_APPEAL_EXPIRED);
        }
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_APPEAL, loginUser.getId(), "APPEAL", order.getId(), reqVO.getContent());

        AppealDO appeal = AppealDO.builder()
                .appealNo("LBA" + IdUtil.getSnowflakeNextIdStr())
                .orderId(order.getId())
                .unitId(reqVO.getUnitId())
                .userId(loginUser.getId())
                .appealType(reqVO.getAppealType())
                .content(detectResult.getProcessedContent())
                .status("PENDING")
                .auditStatus("PENDING")
                .appealExpireTime(unit != null ? unit.getAppealExpireTime() : null)
                .build();
        appealMapper.insert(appeal);
        saveAppealFiles(appeal.getId(), reqVO.getFileIds());
        orderInfoMapper.updateById(OrderInfoDO.builder().id(order.getId()).status("AFTER_SALE").build());
        if (unit != null) {
            orderUnitMapper.updateById(OrderUnitDO.builder().id(unit.getId()).status("APPEALING").build());
        }
        saveOrderOperateLog(order.getId(), unit != null ? unit.getId() : null, "CREATE_APPEAL",
                "USER", loginUser.getId(), unit != null ? unit.getStatus() : order.getStatus(),
                "APPEALING", "用户发起申诉");
        return appeal.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Long authUserId, @Valid AppReviewCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        ReviewContext context = validateReviewContext(authUserId, loginUser.getId(), reqVO.getOrderId(), reqVO.getUnitId(),
                reqVO.getToUserId());
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_COMMENT, loginUser.getId(), REVIEW_BIZ_TYPE, reqVO.getOrderId(), reqVO.getContent());
        if (detectResult.isHit() && LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(detectResult.getStrategy())) {
            throw exception(CONTENT_SENSITIVE_BLOCKED);
        }
        ReviewCommentDO review = buildReview(context, loginUser.getId(), reqVO.getToUserId(),
                reqVO.getStarLevel(), detectResult.getProcessedContent(), false, true);
        reviewCommentMapper.insert(review);
        applyReviewCreditRule(review, context.targetMerchant);
        return review.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReview(Long authUserId, @Valid AppReviewUpdateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        ReviewCommentDO review = reviewCommentMapper.selectById(reqVO.getId());
        if (review == null || !Objects.equals(review.getStatus(), REVIEW_STATUS_ENABLE)) {
            throw exception(REVIEW_COMMENT_NOT_EXISTS);
        }
        if (!Objects.equals(review.getFromUserId(), loginUser.getId())) {
            throw exception(REVIEW_EDIT_NOT_ALLOWED);
        }
        if (review.getEditDeadlineTime() == null || LocalDateTime.now().isAfter(review.getEditDeadlineTime())) {
            throw exception(REVIEW_EDIT_EXPIRED);
        }
        if (review.getEditCount() != null && review.getEditCount() >= REVIEW_EDIT_LIMIT) {
            throw exception(REVIEW_EDIT_NOT_ALLOWED);
        }
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_COMMENT, loginUser.getId(), REVIEW_BIZ_TYPE, review.getOrderId(), reqVO.getContent());
        if (detectResult.isHit() && LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(detectResult.getStrategy())) {
            throw exception(CONTENT_SENSITIVE_BLOCKED);
        }
        if (detectResult.isHit() && LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(detectResult.getStrategy())) {
            throw exception(CONTENT_SENSITIVE_REVIEW_REQUIRED);
        }

        boolean starChanged = !Objects.equals(review.getStarLevel(), reqVO.getStarLevel());
        reviewCommentMapper.updateById(ReviewCommentDO.builder()
                .id(review.getId())
                .starLevel(reqVO.getStarLevel())
                .content(detectResult.getProcessedContent())
                .isContentSupplemented(Boolean.TRUE)
                .lastEditTime(LocalDateTime.now())
                .editCount((review.getEditCount() == null ? 0 : review.getEditCount()) + 1)
                .build());
        if (starChanged) {
            creditRecordService.rollbackBizCreditRecords(review.getToUserId(), REVIEW_BIZ_TYPE, review.getId());
            MerchantInfoDO merchant = getMerchantByUserId(review.getToUserId());
            ReviewCommentDO latestReview = reviewCommentMapper.selectById(review.getId());
            applyReviewCreditRule(latestReview, merchant);
        }
    }

    @Override
    public PageResult<AppComplaintRespVO> getComplaintPage(Long authUserId, AppComplaintPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = getMerchantByAuthUserId(authUserId);
        LambdaQueryWrapperX<ComplaintDO> queryWrapper = new LambdaQueryWrapperX<>();
        queryWrapper.and(wrapper -> wrapper
                .eq(ComplaintDO::getComplainantUserId, loginUser.getId())
                .or(merchant != null, w -> w.eq(ComplaintDO::getRespondentUserId, loginUser.getId())));
        queryWrapper.eqIfPresent(ComplaintDO::getStatus, reqVO.getStatus());
        queryWrapper.betweenIfPresent(ComplaintDO::getCreateTime, reqVO.getCreateTime());
        queryWrapper.orderByDesc(ComplaintDO::getId);
        PageResult<ComplaintDO> pageResult = complaintMapper.selectPage(reqVO, queryWrapper);
        return new PageResult<>(pageResult.getList().stream().map(this::toComplaintRespVO).collect(Collectors.toList()),
                pageResult.getTotal());
    }

    @Override
    public AppComplaintRespVO getComplaint(Long authUserId, Long id) {
        ComplaintDO complaint = complaintMapper.selectById(id);
        validateComplaintAccess(authUserId, complaint);
        return toComplaintRespVO(complaint);
    }

    @Override
    public PageResult<AppAppealRespVO> getAppealPage(Long authUserId, AppAppealPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<AppealDO> pageResult = appealMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<AppealDO>()
                        .eq(AppealDO::getUserId, loginUser.getId())
                        .eqIfPresent(AppealDO::getStatus, reqVO.getStatus())
                        .eqIfPresent(AppealDO::getAuditStatus, reqVO.getAuditStatus())
                        .betweenIfPresent(AppealDO::getCreateTime, reqVO.getCreateTime())
                        .orderByDesc(AppealDO::getId));
        return new PageResult<>(pageResult.getList().stream().map(this::toAppealRespVO).collect(Collectors.toList()),
                pageResult.getTotal());
    }

    @Override
    public AppAppealRespVO getAppeal(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        AppealDO appeal = appealMapper.selectOne(new LambdaQueryWrapperX<AppealDO>()
                .eq(AppealDO::getId, id)
                .eq(AppealDO::getUserId, loginUser.getId()));
        if (appeal == null) {
            throw exception(APPEAL_NOT_EXISTS);
        }
        return toAppealRespVO(appeal);
    }

    @Override
    public PageResult<AppReviewRespVO> getReviewPage(Long authUserId, AppReviewPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        LambdaQueryWrapperX<ReviewCommentDO> queryWrapper = new LambdaQueryWrapperX<>();
        if ("SENT".equalsIgnoreCase(reqVO.getDirection())) {
            queryWrapper.eq(ReviewCommentDO::getFromUserId, loginUser.getId());
        } else if ("RECEIVED".equalsIgnoreCase(reqVO.getDirection())) {
            queryWrapper.eq(ReviewCommentDO::getToUserId, loginUser.getId());
        } else {
            queryWrapper.and(wrapper -> wrapper.eq(ReviewCommentDO::getFromUserId, loginUser.getId())
                    .or()
                    .eq(ReviewCommentDO::getToUserId, loginUser.getId()));
        }
        queryWrapper.eqIfPresent(ReviewCommentDO::getStatus, reqVO.getStatus());
        queryWrapper.betweenIfPresent(ReviewCommentDO::getCreateTime, reqVO.getCreateTime());
        queryWrapper.orderByDesc(ReviewCommentDO::getCreateTime, ReviewCommentDO::getId);
        PageResult<ReviewCommentDO> pageResult = reviewCommentMapper.selectPage(reqVO, queryWrapper);
        List<AppReviewRespVO> list = pageResult.getList().stream()
                .map(this::toReviewRespVO)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public AppReviewRespVO getReview(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        ReviewCommentDO reviewComment = reviewCommentMapper.selectOne(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getId, id)
                .and(wrapper -> wrapper
                        .eq(ReviewCommentDO::getFromUserId, loginUser.getId())
                        .or()
                        .eq(ReviewCommentDO::getToUserId, loginUser.getId())));
        if (reviewComment == null) {
            throw exception(REVIEW_COMMENT_NOT_EXISTS);
        }
        return toReviewRespVO(reviewComment);
    }

    @Override
    public List<AppPendingReviewUnitRespVO> getPendingReviewUnits(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchant = getMerchantByAuthUserId(authUserId);
        List<Long> orderIds = resolveOrderIdsForUser(loginUser.getId());
        if (CollUtil.isEmpty(orderIds) && merchant == null) {
            return Collections.emptyList();
        }
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getStatus, UNIT_STATUS_FINISHED)
                .and(wrapper -> wrapper
                        .in(CollUtil.isNotEmpty(orderIds), OrderUnitDO::getOrderId, orderIds)
                        .or(merchant != null, w -> w.eq(OrderUnitDO::getMerchantId, merchant.getId())))
                .orderByDesc(OrderUnitDO::getFinishTime, OrderUnitDO::getId));
        if (units.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> unitOrderIds = units.stream().map(OrderUnitDO::getOrderId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, OrderInfoDO> orderMap = unitOrderIds.isEmpty() ? Collections.emptyMap()
                : orderInfoMapper.selectBatchIds(unitOrderIds).stream().collect(Collectors.toMap(OrderInfoDO::getId, item -> item));
        Set<Long> unitIds = units.stream().map(OrderUnitDO::getId).collect(Collectors.toSet());
        List<ReviewCommentDO> reviews = reviewCommentMapper.selectList(new LambdaQueryWrapperX<ReviewCommentDO>()
                .in(ReviewCommentDO::getUnitId, unitIds)
                .eq(ReviewCommentDO::getStatus, REVIEW_STATUS_ENABLE));
        Map<String, ReviewCommentDO> reviewMap = new HashMap<>();
        for (ReviewCommentDO review : reviews) {
            reviewMap.put(buildUnitUserKey(review.getUnitId(), review.getFromUserId()), review);
        }

        List<AppPendingReviewUnitRespVO> result = new ArrayList<>();
        for (OrderUnitDO unit : units) {
            OrderInfoDO order = orderMap.get(unit.getOrderId());
            if (order == null) {
                continue;
            }
            if (Objects.equals(order.getUserId(), loginUser.getId())) {
                AppPendingReviewUnitRespVO item = buildPendingUnit(unit, order, loginUser.getId(),
                        unit.getMerchantId() == null ? null : getMerchantUserId(unit.getMerchantId()), "TO_MERCHANT");
                if (item != null && !reviewMap.containsKey(buildUnitUserKey(unit.getId(), loginUser.getId()))) {
                    result.add(item);
                }
            }
            if (merchant != null && Objects.equals(unit.getMerchantId(), merchant.getId())) {
                AppPendingReviewUnitRespVO item = buildPendingUnit(unit, order, loginUser.getId(), order.getUserId(), "TO_USER");
                if (item != null && !reviewMap.containsKey(buildUnitUserKey(unit.getId(), loginUser.getId()))) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public AppMerchantReviewSummaryRespVO getMerchantReviewSummary(Long merchantId) {
        MerchantReviewMetricsResp metrics = reviewCommentMetricsService.calculateMerchantMetrics(merchantId);
        AppMerchantReviewSummaryRespVO respVO = new AppMerchantReviewSummaryRespVO();
        respVO.setMerchantId(metrics.getMerchantId());
        respVO.setCompositeScore(metrics.getCompositeScore());
        respVO.setReviewCount(metrics.getReviewCount());
        respVO.setPositiveReviewCount(metrics.getPositiveReviewCount());
        respVO.setNeutralReviewCount(metrics.getNeutralReviewCount());
        respVO.setNegativeReviewCount(metrics.getNegativeReviewCount());
        respVO.setPositiveRate(metrics.getPositiveRate());
        respVO.setInPositivePriorityPool(metrics.getInPositivePriorityPool());
        return respVO;
    }

    @Override
    public AppReviewCreditRespVO getCredit(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        AppCreditRecordPageReqVO creditRecordPageReqVO = new AppCreditRecordPageReqVO();
        creditRecordPageReqVO.setPageNo(1);
        creditRecordPageReqVO.setPageSize(5);
        PageResult<AppCreditRecordRespVO> recentPage = creditRecordService.getAppCreditRecordPage(loginUser.getId(), creditRecordPageReqVO);

        int currentScore = merchantInfo != null && merchantInfo.getCreditScore() != null ? merchantInfo.getCreditScore() : 100;
        String currentLevel = merchantInfo != null && StrUtil.isNotBlank(merchantInfo.getCreditLevel())
                ? merchantInfo.getCreditLevel() : CreditLevelResolver.resolve(currentScore);
        List<CreditLevelBenefitDO> allBenefits = creditLevelBenefitService.getEnabledBenefits();

        AppReviewCreditRespVO respVO = new AppReviewCreditRespVO();
        respVO.setCreditScore(currentScore);
        respVO.setCreditLevel(currentLevel);
        respVO.setNextLevelCode(CreditLevelResolver.nextLevel(currentLevel));
        respVO.setNextLevelNeedScore(CreditLevelResolver.nextLevelNeedScore(currentScore));
        respVO.setRecordCount(recentPage.getTotal());
        respVO.setRecentRecords(recentPage.getList().stream().map(record -> {
            AppReviewCreditRespVO.RecentRecordItem item = new AppReviewCreditRespVO.RecentRecordItem();
            item.setId(record.getId());
            item.setRuleCode(record.getRuleCode());
            item.setRuleName(record.getRuleName());
            item.setScoreChange(record.getScoreChange());
            item.setAfterScore(record.getAfterScore());
            item.setCreateTime(record.getCreateTime());
            return item;
        }).collect(Collectors.toList()));
        respVO.setRules(creditRuleMapper.selectList(new LambdaQueryWrapperX<CreditRuleDO>()
                        .eq(CreditRuleDO::getStatus, "ENABLE")
                        .orderByDesc(CreditRuleDO::getId))
                .stream()
                .map(rule -> {
                    AppReviewCreditRespVO.CreditRuleItem item = new AppReviewCreditRespVO.CreditRuleItem();
                    item.setRuleCode(rule.getRuleCode());
                    item.setRuleName(rule.getRuleName());
                    item.setScoreChange(rule.getScoreChange());
                    item.setTriggerType(rule.getTriggerType());
                    item.setStatus(rule.getStatus());
                    return item;
                })
                .collect(Collectors.toList()));
        respVO.setBenefits(allBenefits.stream().map(this::toCreditBenefitItem).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public AppCreditBenefitsRespVO getCreditBenefits(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        int currentScore = merchantInfo != null && merchantInfo.getCreditScore() != null ? merchantInfo.getCreditScore() : 100;
        String currentLevel = merchantInfo != null && StrUtil.isNotBlank(merchantInfo.getCreditLevel())
                ? merchantInfo.getCreditLevel() : CreditLevelResolver.resolve(currentScore);

        AppCreditBenefitsRespVO respVO = new AppCreditBenefitsRespVO();
        respVO.setCurrentLevelCode(currentLevel);
        respVO.setCurrentLevelName(resolveCreditLevelName(currentLevel));
        respVO.setBenefits(creditLevelBenefitService.getEnabledBenefits().stream().map(item -> {
            AppCreditBenefitsRespVO.BenefitItem benefitItem = new AppCreditBenefitsRespVO.BenefitItem();
            benefitItem.setLevelCode(item.getLevelCode());
            benefitItem.setLevelName(item.getLevelName());
            benefitItem.setBenefitTitle(item.getBenefitTitle());
            benefitItem.setBenefitDesc(item.getBenefitDesc());
            benefitItem.setSortNo(item.getSortNo());
            return benefitItem;
        }).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<AppCreditRecordRespVO> getCreditRecordPage(Long authUserId, AppCreditRecordPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        return creditRecordService.getAppCreditRecordPage(loginUser.getId(), reqVO);
    }

    @Override
    public AppCreditRecordDetailRespVO getCreditRecord(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        return creditRecordService.getAppCreditRecordDetail(loginUser.getId(), id);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long createAutoReview(OrderUnitDO unit, Long fromUserId, Long toUserId) {
        if (unit == null || fromUserId == null || toUserId == null) {
            return null;
        }
        ReviewCommentDO existed = reviewCommentMapper.selectOne(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getUnitId, unit.getId())
                .eq(ReviewCommentDO::getFromUserId, fromUserId)
                .eq(ReviewCommentDO::getStatus, REVIEW_STATUS_ENABLE)
                .last("LIMIT 1"));
        if (existed != null) {
            return existed.getId();
        }
        ReviewCommentDO review = ReviewCommentDO.builder()
                .orderId(unit.getOrderId())
                .unitId(unit.getId())
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .starLevel(4)
                .content(AUTO_REVIEW_DEFAULT_CONTENT)
                .isAutoReview(Boolean.TRUE)
                .isContentSupplemented(Boolean.FALSE)
                .editDeadlineTime(LocalDateTime.now().plusDays(7))
                .editCount(0)
                .status(REVIEW_STATUS_ENABLE)
                .build();
        reviewCommentMapper.insert(review);
        applyReviewCreditRule(review, getMerchantByUserId(toUserId));
        return review.getId();
    }

    private ReviewContext validateReviewContext(Long authUserId, Long loginUserId, Long orderId, Long unitId, Long toUserId) {
        OrderInfoDO order = validateOrderAccess(authUserId, loginUserId, orderId);
        OrderUnitDO unit = validateUnit(unitId, order.getId());
        if (!Objects.equals(unit.getStatus(), UNIT_STATUS_FINISHED)) {
            throw exception(REVIEW_UNIT_STATUS_INVALID);
        }
        MerchantInfoDO merchant = unit.getMerchantId() == null ? null : merchantInfoMapper.selectById(unit.getMerchantId());
        Long merchantUserId = merchant != null ? merchant.getUserId() : null;
        boolean isOrderUser = Objects.equals(order.getUserId(), loginUserId);
        boolean isMerchantUser = merchantUserId != null && Objects.equals(merchantUserId, loginUserId);
        if (!isOrderUser && !isMerchantUser) {
            throw exception(REVIEW_ACCESS_DENIED);
        }
        Long expectedTargetUserId = isOrderUser ? merchantUserId : order.getUserId();
        if (expectedTargetUserId == null || !Objects.equals(expectedTargetUserId, toUserId) || Objects.equals(loginUserId, toUserId)) {
            throw exception(REVIEW_ACCESS_DENIED);
        }
        ReviewCommentDO duplicated = reviewCommentMapper.selectOne(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getUnitId, unit.getId())
                .eq(ReviewCommentDO::getFromUserId, loginUserId)
                .eq(ReviewCommentDO::getStatus, REVIEW_STATUS_ENABLE)
                .last("LIMIT 1"));
        if (duplicated != null) {
            throw exception(REVIEW_DUPLICATED);
        }
        ReviewContext context = new ReviewContext();
        context.order = order;
        context.unit = unit;
        context.targetMerchant = getMerchantByUserId(toUserId);
        return context;
    }

    private OrderInfoDO validateOrderAccess(Long authUserId, Long lbUserId, Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (Objects.equals(order.getUserId(), lbUserId)) {
            return order;
        }
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, authUserId));
        if (merchant != null && Objects.equals(order.getMerchantId(), merchant.getId())) {
            return order;
        }
        List<OrderUnitDO> orderUnits = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .eq(merchant != null, OrderUnitDO::getMerchantId, merchant != null ? merchant.getId() : null)
                .last("LIMIT 1"));
        if (merchant != null && CollUtil.isNotEmpty(orderUnits)) {
            return order;
        }
        throw exception(REVIEW_ACCESS_DENIED);
    }

    private OrderUnitDO validateUnit(Long unitId, Long orderId) {
        if (unitId == null) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        OrderUnitDO unit = orderUnitMapper.selectById(unitId);
        if (unit == null || !Objects.equals(unit.getOrderId(), orderId)) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        return unit;
    }

    private ReviewCommentDO buildReview(ReviewContext context, Long fromUserId, Long toUserId,
                                        Integer starLevel, String content, boolean autoReview,
                                        boolean contentSupplemented) {
        return ReviewCommentDO.builder()
                .orderId(context.order.getId())
                .unitId(context.unit.getId())
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .starLevel(starLevel)
                .content(content)
                .isAutoReview(autoReview)
                .isContentSupplemented(contentSupplemented)
                .editDeadlineTime(LocalDateTime.now().plusDays(7))
                .editCount(0)
                .status(REVIEW_STATUS_ENABLE)
                .build();
    }

    private void applyReviewCreditRule(ReviewCommentDO review, MerchantInfoDO targetMerchant) {
        if (review == null || review.getStarLevel() == null || targetMerchant == null) {
            return;
        }
        creditRecordService.applyCreditRule(review.getToUserId(), targetMerchant.getId(),
                resolveRuleCodeByStar(review.getStarLevel()), REVIEW_BIZ_TYPE, review.getId(),
                StrUtil.blankToDefault(review.getContent(), AUTO_REVIEW_DEFAULT_CONTENT));
        priorityPoolService.recomputeMerchantPriorityPool(targetMerchant.getId());
    }

    private String resolveRuleCodeByStar(Integer starLevel) {
        if (starLevel == null) {
            return "ORDER_FINISHED_NEUTRAL";
        }
        if (starLevel >= 4) {
            return "ORDER_FINISHED_POSITIVE";
        }
        if (starLevel == 3) {
            return "ORDER_FINISHED_NEUTRAL";
        }
        return "ORDER_FINISHED_NEGATIVE";
    }

    private AppPendingReviewUnitRespVO buildPendingUnit(OrderUnitDO unit, OrderInfoDO order, Long currentUserId,
                                                        Long toUserId, String direction) {
        if (toUserId == null || Objects.equals(currentUserId, toUserId)) {
            return null;
        }
        MemberUserDO targetUser = memberUserService.getMemberUser(toUserId);
        if (targetUser == null) {
            return null;
        }
        AppPendingReviewUnitRespVO respVO = new AppPendingReviewUnitRespVO();
        respVO.setOrderId(order.getId());
        respVO.setUnitId(unit.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setUnitTitle(unit.getUnitTitle());
        respVO.setUnitAmount(unit.getUnitAmount());
        respVO.setToUserId(toUserId);
        respVO.setToUserNickname(targetUser.getNickname());
        respVO.setToUserMobile(targetUser.getMobile());
        respVO.setReviewDirection(direction);
        respVO.setFinishTime(unit.getFinishTime());
        respVO.setAutoReviewDeadlineTime(unit.getFinishTime() == null ? null : unit.getFinishTime().plusHours(24));
        return respVO;
    }

    private String buildUnitUserKey(Long unitId, Long userId) {
        return unitId + ":" + userId;
    }

    private Long getMerchantUserId(Long merchantId) {
        MerchantInfoDO merchant = merchantId == null ? null : merchantInfoMapper.selectById(merchantId);
        return merchant != null ? merchant.getUserId() : null;
    }

    private List<Long> resolveOrderIdsForUser(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                        .eq(OrderInfoDO::getUserId, userId)
                        .select(OrderInfoDO::getId))
                .stream()
                .map(OrderInfoDO::getId)
                .collect(Collectors.toList());
    }

    private MerchantInfoDO getMerchantByAuthUserId(Long authUserId) {
        return merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, authUserId)
                .last("LIMIT 1"));
    }

    private MerchantInfoDO getMerchantByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        return merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, userId)
                .last("LIMIT 1"));
    }

    private void saveComplaintFiles(Long complaintId, List<Long> fileIds) {
        if (CollUtil.isEmpty(fileIds)) {
            return;
        }
        for (Long fileId : fileIds) {
            complaintFileRelMapper.insert(ComplaintFileRelDO.builder()
                    .complaintId(complaintId)
                    .fileId(fileId)
                    .build());
        }
    }

    private void saveAppealFiles(Long appealId, List<Long> fileIds) {
        if (CollUtil.isEmpty(fileIds)) {
            return;
        }
        for (Long fileId : fileIds) {
            appealFileRelMapper.insert(AppealFileRelDO.builder()
                    .appealId(appealId)
                    .fileId(fileId)
                    .build());
        }
    }

    private void saveOrderOperateLog(Long orderId, Long unitId, String operateType, String operateRole,
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

    private void validateComplaintAccess(Long authUserId, ComplaintDO complaint) {
        if (complaint == null) {
            throw exception(COMPLAINT_NOT_EXISTS);
        }
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        if (Objects.equals(complaint.getComplainantUserId(), loginUser.getId())
                || Objects.equals(complaint.getRespondentUserId(), loginUser.getId())) {
            return;
        }
        throw exception(REVIEW_ACCESS_DENIED);
    }

    private AppComplaintRespVO toComplaintRespVO(ComplaintDO complaint) {
        AppComplaintRespVO respVO = BeanUtils.toBean(complaint, AppComplaintRespVO.class);
        respVO.setFileIds(complaintFileRelMapper.selectList(new LambdaQueryWrapperX<ComplaintFileRelDO>()
                        .eq(ComplaintFileRelDO::getComplaintId, complaint.getId()))
                .stream()
                .map(ComplaintFileRelDO::getFileId)
                .collect(Collectors.toList()));
        return respVO;
    }

    private AppAppealRespVO toAppealRespVO(AppealDO appeal) {
        AppAppealRespVO respVO = BeanUtils.toBean(appeal, AppAppealRespVO.class);
        respVO.setFileIds(appealFileRelMapper.selectList(new LambdaQueryWrapperX<AppealFileRelDO>()
                        .eq(AppealFileRelDO::getAppealId, appeal.getId()))
                .stream()
                .map(AppealFileRelDO::getFileId)
                .collect(Collectors.toList()));
        return respVO;
    }

    private AppReviewRespVO toReviewRespVO(ReviewCommentDO reviewComment) {
        AppReviewRespVO respVO = BeanUtils.toBean(reviewComment, AppReviewRespVO.class);
        boolean editable = Objects.equals(reviewComment.getStatus(), REVIEW_STATUS_ENABLE)
                && (reviewComment.getEditCount() == null || reviewComment.getEditCount() < REVIEW_EDIT_LIMIT)
                && reviewComment.getEditDeadlineTime() != null
                && !LocalDateTime.now().isAfter(reviewComment.getEditDeadlineTime());
        respVO.setReviewEditable(editable);
        respVO.setReviewEditDeadline(reviewComment.getEditDeadlineTime());
        return respVO;
    }

    private AppReviewCreditRespVO.CreditBenefitItem toCreditBenefitItem(CreditLevelBenefitDO benefit) {
        AppReviewCreditRespVO.CreditBenefitItem item = new AppReviewCreditRespVO.CreditBenefitItem();
        item.setLevelCode(benefit.getLevelCode());
        item.setLevelName(benefit.getLevelName());
        item.setBenefitTitle(benefit.getBenefitTitle());
        item.setBenefitDesc(benefit.getBenefitDesc());
        item.setSortNo(benefit.getSortNo());
        return item;
    }

    private String resolveCreditLevelName(String levelCode) {
        if ("EXCELLENT".equalsIgnoreCase(levelCode)) {
            return "优秀";
        }
        if ("NORMAL".equalsIgnoreCase(levelCode)) {
            return "正常";
        }
        if ("WARNING".equalsIgnoreCase(levelCode)) {
            return "预警";
        }
        if ("DISABLED".equalsIgnoreCase(levelCode)) {
            return "禁用";
        }
        return levelCode;
    }

    private static class ReviewContext {
        private OrderInfoDO order;
        private OrderUnitDO unit;
        private MerchantInfoDO targetMerchant;
    }
}
