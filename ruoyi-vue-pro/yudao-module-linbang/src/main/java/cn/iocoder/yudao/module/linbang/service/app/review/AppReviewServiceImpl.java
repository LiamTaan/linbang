package cn.iocoder.yudao.module.linbang.service.app.review;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewCreditRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appealfilerel.AppealFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaintfilerel.ComplaintFileRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.appealfilerel.AppealFileRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaintfilerel.ComplaintFileRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrule.CreditRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AppReviewServiceImpl implements AppReviewService {

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComplaint(Long authUserId, @Valid AppComplaintCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateOrderAccess(authUserId, loginUser.getId(), reqVO.getOrderId());
        validateUnit(reqVO.getUnitId(), order.getId());

        ComplaintDO complaint = ComplaintDO.builder()
                .complaintNo("LBC" + IdUtil.getSnowflakeNextIdStr())
                .orderId(order.getId())
                .unitId(reqVO.getUnitId())
                .complainantUserId(loginUser.getId())
                .respondentUserId(reqVO.getRespondentUserId())
                .complaintType(reqVO.getComplaintType())
                .content(reqVO.getContent())
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

        AppealDO appeal = AppealDO.builder()
                .appealNo("LBA" + IdUtil.getSnowflakeNextIdStr())
                .orderId(order.getId())
                .unitId(reqVO.getUnitId())
                .userId(loginUser.getId())
                .appealType(reqVO.getAppealType())
                .content(reqVO.getContent())
                .status("PENDING")
                .auditStatus("PENDING")
                .build();
        appealMapper.insert(appeal);
        saveAppealFiles(appeal.getId(), reqVO.getFileIds());
        orderInfoMapper.updateById(OrderInfoDO.builder().id(order.getId()).status("AFTER_SALE").build());
        if (unit != null) {
            orderUnitMapper.updateById(OrderUnitDO.builder().id(unit.getId()).status("APPEALING").build());
        }
        return appeal.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Long authUserId, @Valid AppReviewCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateOrderAccess(authUserId, loginUser.getId(), reqVO.getOrderId());
        validateUnit(reqVO.getUnitId(), order.getId());
        if (Objects.equals(loginUser.getId(), reqVO.getToUserId())) {
            throw exception(REVIEW_ACCESS_DENIED);
        }

        ReviewCommentDO review = ReviewCommentDO.builder()
                .orderId(order.getId())
                .unitId(reqVO.getUnitId())
                .fromUserId(loginUser.getId())
                .toUserId(reqVO.getToUserId())
                .starLevel(reqVO.getStarLevel())
                .content(reqVO.getContent())
                .isAutoReview(Boolean.FALSE)
                .status("ENABLE")
                .build();
        reviewCommentMapper.insert(review);
        if (reqVO.getStarLevel() != null && reqVO.getStarLevel() >= 4) {
            MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                    .eq(MerchantInfoDO::getUserId, reqVO.getToUserId())
                    .last("LIMIT 1"));
            if (merchant != null) {
                creditRecordService.applyCreditRule(reqVO.getToUserId(), merchant.getId(),
                        "ORDER_FINISHED_POSITIVE", "REVIEW", review.getId(),
                        reqVO.getContent() != null ? reqVO.getContent() : "订单完结且评价良好");
            }
        }
        return review.getId();
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
        return new PageResult<>(pageResult.getList().stream().map(this::toComplaintRespVO).collect(java.util.stream.Collectors.toList()),
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
        return new PageResult<>(pageResult.getList().stream().map(this::toAppealRespVO).collect(java.util.stream.Collectors.toList()),
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
        queryWrapper.and(wrapper -> wrapper
                .eq(ReviewCommentDO::getFromUserId, loginUser.getId())
                .or()
                .eq(ReviewCommentDO::getToUserId, loginUser.getId()));
        queryWrapper.eqIfPresent(ReviewCommentDO::getStatus, reqVO.getStatus());
        queryWrapper.betweenIfPresent(ReviewCommentDO::getCreateTime, reqVO.getCreateTime());
        queryWrapper.orderByDesc(ReviewCommentDO::getId);
        PageResult<ReviewCommentDO> pageResult = reviewCommentMapper.selectPage(reqVO, queryWrapper);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), AppReviewRespVO.class), pageResult.getTotal());
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
        return BeanUtils.toBean(reviewComment, AppReviewRespVO.class);
    }

    @Override
    public AppReviewCreditRespVO getCredit(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, authUserId));
        AppCreditRecordPageReqVO creditRecordPageReqVO = new AppCreditRecordPageReqVO();
        creditRecordPageReqVO.setPageNo(1);
        creditRecordPageReqVO.setPageSize(5);
        PageResult<AppCreditRecordRespVO> recentPage = creditRecordService.getAppCreditRecordPage(loginUser.getId(), creditRecordPageReqVO);
        AppReviewCreditRespVO respVO = new AppReviewCreditRespVO();
        respVO.setCreditScore(merchantInfo != null ? merchantInfo.getCreditScore() : 100);
        respVO.setCreditLevel(merchantInfo != null ? merchantInfo.getCreditLevel() : "NORMAL");
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
        }).collect(java.util.stream.Collectors.toList()));
        java.util.List<AppReviewCreditRespVO.CreditRuleItem> rules = creditRuleMapper.selectList(new LambdaQueryWrapperX<CreditRuleDO>()
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
                .collect(java.util.stream.Collectors.toList());
        respVO.setRules(rules);
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
        throw exception(REVIEW_ACCESS_DENIED);
    }

    private OrderUnitDO validateUnit(Long unitId, Long orderId) {
        if (unitId == null) {
            return null;
        }
        OrderUnitDO unit = orderUnitMapper.selectById(unitId);
        if (unit == null || !Objects.equals(unit.getOrderId(), orderId)) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        return unit;
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

    private MerchantInfoDO getMerchantByAuthUserId(Long authUserId) {
        return merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, authUserId));
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
                .collect(java.util.stream.Collectors.toList()));
        return respVO;
    }

    private AppAppealRespVO toAppealRespVO(AppealDO appeal) {
        AppAppealRespVO respVO = BeanUtils.toBean(appeal, AppAppealRespVO.class);
        respVO.setFileIds(appealFileRelMapper.selectList(new LambdaQueryWrapperX<AppealFileRelDO>()
                        .eq(AppealFileRelDO::getAppealId, appeal.getId()))
                .stream()
                .map(AppealFileRelDO::getFileId)
                .collect(java.util.stream.Collectors.toList()));
        return respVO;
    }
}
