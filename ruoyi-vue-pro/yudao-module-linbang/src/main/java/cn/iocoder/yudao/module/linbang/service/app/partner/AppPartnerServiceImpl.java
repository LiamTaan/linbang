package cn.iocoder.yudao.module.linbang.service.app.partner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.util.MyBatisUtils;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerCoordinationCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerDisputePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerDisputeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerEntryAuditRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerInstructionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPriceReportRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerPromoteStatRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerRegionRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerWorkbenchRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.PartnerCoordinationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerDisputePageRecordDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerPromoteAggregateDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerPromoteTradeAggregateDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerWorkbenchAggregateDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination.PartnerCoordinationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerregionrel.PartnerRegionRelMapper;
import cn.iocoder.yudao.module.linbang.enums.PartnerDisputeTypeEnum;
import cn.iocoder.yudao.module.linbang.service.app.message.AppMessageService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.merchantentry.MerchantEntrySnapshotUtils;
import cn.iocoder.yudao.module.linbang.service.partnerinfo.PartnerInfoService;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CURRENT_ROLE_NOT_ALLOWED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_ENTRY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_PRICE_REPORT_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_ENTRY_AUDIT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_DISPUTE_TYPE_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_ESCALATE_REMARK_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_INSTRUCTION_CATEGORY_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_PRICE_REPORT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_ROLE_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_REGION_ACCESS_DENIED;

@Service
@Validated
public class AppPartnerServiceImpl implements AppPartnerService {

    @Resource
    private PartnerInfoService partnerInfoService;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private MerchantPriceReportMapper merchantPriceReportMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private PartnerRegionRelMapper partnerRegionRelMapper;
    @Resource
    private PartnerCoordinationMapper partnerCoordinationMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private AppMessageService appMessageService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private MemberUserService memberUserService;
    @Resource
    private FileService fileService;

    @Override
    public AppPartnerWorkbenchRespVO getWorkbench(Long userId) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        List<String> regionAdcodes = getPartnerRegionCodes(partnerInfo);
        List<PartnerRegionRelDO> regions = partnerRegionRelMapper.selectListByPartnerId(partnerInfo.getId());
        PartnerWorkbenchAggregateDTO aggregate = regionAdcodes.isEmpty()
                ? new PartnerWorkbenchAggregateDTO()
                : partnerCoordinationMapper.selectWorkbenchAggregate(regionAdcodes);
        List<MerchantPriceReportDO> priceReports = selectRecentPriceReports(partnerInfo.getId(), regionAdcodes);

        Long pendingEntryAuditCount = countPendingEntries(regionAdcodes);
        Long pendingComplaintCount = defaultLong(aggregate == null ? null : aggregate.getPendingDisputeCount());
        Long pendingPriceReportCount = countPriceReports(partnerInfo.getId(), regionAdcodes, "PENDING");
        Long approvedPriceReportCount = countPriceReports(partnerInfo.getId(), regionAdcodes, "APPROVED");
        Long rejectedPriceReportCount = countPriceReports(partnerInfo.getId(), regionAdcodes, "REJECTED");
        AppPartnerWorkbenchRespVO respVO = new AppPartnerWorkbenchRespVO();
        respVO.setPartnerId(partnerInfo.getId());
        respVO.setPartnerName(partnerInfo.getPartnerName());
        respVO.setContactName(partnerInfo.getContactName());
        respVO.setContactMobile(partnerInfo.getContactMobile());
        respVO.setStatus(partnerInfo.getStatus());
        respVO.setRegionAdcodes(regionAdcodes);
        respVO.setPendingEntryAuditCount(pendingEntryAuditCount);
        respVO.setPendingComplaintCount(pendingComplaintCount);
        respVO.setPendingPriceReportCount(pendingPriceReportCount);
        respVO.setOrderCount(defaultLong(aggregate == null ? null : aggregate.getOrderCount()));
        respVO.setTradeAmount(defaultAmount(aggregate == null ? null : aggregate.getTradeAmount()));
        respVO.setSummary(buildSummary(regions, aggregate, pendingEntryAuditCount, pendingComplaintCount,
                pendingPriceReportCount, approvedPriceReportCount, rejectedPriceReportCount));
        respVO.setRecentPriceReports(buildRecentPriceReports(priceReports));
        return respVO;
    }

    @Override
    public AppPartnerRegionRespVO getRegionDetail(Long userId) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        List<PartnerRegionRelDO> regions = partnerRegionRelMapper.selectListByPartnerId(partnerInfo.getId());
        AppPartnerRegionRespVO respVO = new AppPartnerRegionRespVO();
        respVO.setPartnerId(partnerInfo.getId());
        respVO.setPartnerName(partnerInfo.getPartnerName());
        respVO.setRegions(regions.stream().map(this::buildRegionItem).collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public PageResult<AppPartnerEntryAuditRespVO> getEntryAuditPage(Long userId, AppPartnerEntryAuditPageReqVO reqVO) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        List<String> regionCodes = getPartnerRegionCodes(partnerInfo);
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        PageResult<MerchantEntryDO> pageResult = merchantEntryMapper.selectPage(reqVO, new LambdaQueryWrapperX<MerchantEntryDO>()
                .inIfPresent(MerchantEntryDO::getRegionCode, regionCodes)
                .inIfPresent(MerchantEntryDO::getUserId, matchedUserIds)
                .eqIfPresent(MerchantEntryDO::getEntryNo, reqVO.getEntryNo())
                .eqIfPresent(MerchantEntryDO::getRegionCode, reqVO.getRegionCode())
                .eqIfPresent(MerchantEntryDO::getStatus, reqVO.getStatus())
                .orderByDesc(MerchantEntryDO::getId));
        return new PageResult<>(buildEntryAuditRespList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public AppPartnerEntryAuditRespVO getEntryAudit(Long userId, Long id) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        MerchantEntryDO entry = merchantEntryMapper.selectById(id);
        validateEntryInPartnerRegion(partnerInfo, entry);
        return buildEntryAuditResp(entry, buildMemberUserMap(Collections.singletonList(entry.getUserId())),
                buildMerchantInfoMap(Collections.singletonList(entry.getMerchantId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditEntry(Long userId, AppPartnerEntryAuditReqVO reqVO) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        MerchantEntryDO entry = merchantEntryMapper.selectById(reqVO.getId());
        validateEntryInPartnerRegion(partnerInfo, entry);
        if (!"PENDING".equalsIgnoreCase(entry.getStatus())) {
            throw exception(PARTNER_ENTRY_AUDIT_STATUS_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        MerchantEntryDO updateObj = MerchantEntryDO.builder()
                .id(entry.getId())
                .firstAuditBy(userId)
                .firstAuditTime(now)
                .remark("REJECTED".equalsIgnoreCase(reqVO.getAuditStatus()) ? reqVO.getRejectReason() : reqVO.getAuditRemark())
                .rejectReason("REJECTED".equalsIgnoreCase(reqVO.getAuditStatus()) ? reqVO.getRejectReason() : null)
                .build();
        if ("APPROVED".equalsIgnoreCase(reqVO.getAuditStatus())) {
            updateObj.setFirstAuditStatus("APPROVED");
            updateObj.setStatus("FIRST_APPROVED");
            updateObj.setProgressStatus("PENDING_FINAL_AUDIT");
            updateObj.setCurrentStageName("待平台终审");
            updateObj.setCurrentStageTime(now);
            updateObj.setOnboardingBlockedReason(null);
        } else if ("REJECTED".equalsIgnoreCase(reqVO.getAuditStatus())) {
            updateObj.setFirstAuditStatus("REJECTED");
            updateObj.setStatus("REJECTED");
            updateObj.setProgressStatus("REJECTED");
            updateObj.setCurrentStageName("入驻初审已驳回");
            updateObj.setCurrentStageTime(now);
            updateObj.setOnboardingBlockedReason(reqVO.getRejectReason());
        } else {
            throw exception(PARTNER_ENTRY_AUDIT_STATUS_INVALID);
        }
        int updated = merchantEntryMapper.update(null, new LambdaUpdateWrapper<MerchantEntryDO>()
                .eq(MerchantEntryDO::getId, entry.getId())
                .eq(MerchantEntryDO::getStatus, "PENDING")
                .set(MerchantEntryDO::getFirstAuditBy, updateObj.getFirstAuditBy())
                .set(MerchantEntryDO::getFirstAuditTime, updateObj.getFirstAuditTime())
                .set(MerchantEntryDO::getRemark, updateObj.getRemark())
                .set(MerchantEntryDO::getRejectReason, updateObj.getRejectReason())
                .set(MerchantEntryDO::getFirstAuditStatus, updateObj.getFirstAuditStatus())
                .set(MerchantEntryDO::getStatus, updateObj.getStatus())
                .set(MerchantEntryDO::getProgressStatus, updateObj.getProgressStatus())
                .set(MerchantEntryDO::getCurrentStageName, updateObj.getCurrentStageName())
                .set(MerchantEntryDO::getCurrentStageTime, updateObj.getCurrentStageTime())
                .set(MerchantEntryDO::getOnboardingBlockedReason, updateObj.getOnboardingBlockedReason()));
        if (updated == 0) {
            throw exception(PARTNER_ENTRY_AUDIT_STATUS_INVALID);
        }
        messagePushDispatchService.dispatchSingle("lb_merchant_entry_audited", "入驻初审结果通知", "MERCHANT_ENTRY",
                entry.getId(), entry.getUserId(), StrUtil.blankToDefault(updateObj.getRemark(), "合作商已完成入驻初审"));
    }

    @Override
    public PageResult<AppPartnerDisputeRespVO> getDisputePage(Long userId, AppPartnerDisputePageReqVO reqVO) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        reqVO.setDisputeType(normalizeDisputeType(reqVO.getDisputeType(), false));
        List<String> regionCodes = getPartnerRegionCodes(partnerInfo);
        if (StrUtil.isNotBlank(reqVO.getRegionCode())) {
            validateRegionAllowed(partnerInfo, reqVO.getRegionCode());
        }
        if (CollUtil.isEmpty(regionCodes)) {
            return PageResult.empty();
        }
        IPage<PartnerDisputePageRecordDTO> page = MyBatisUtils.buildPage(reqVO);
        partnerCoordinationMapper.selectDisputePage(page, regionCodes, reqVO);
        return new PageResult<>(buildDisputeRespList(page.getRecords()), page.getTotal());
    }

    @Override
    public AppPartnerDisputeRespVO getDispute(Long userId, String disputeType, Long disputeId) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        String normalizedDisputeType = normalizeDisputeType(disputeType, true);
        if (PartnerDisputeTypeEnum.COMPLAINT.getCode().equals(normalizedDisputeType)) {
            ComplaintDO complaint = complaintMapper.selectById(disputeId);
            validateOrderInPartnerRegion(partnerInfo, complaint == null ? null : complaint.getOrderId());
            return buildComplaintDisputeResp(complaint);
        }
        AppealDO appeal = appealMapper.selectById(disputeId);
        validateOrderInPartnerRegion(partnerInfo, appeal == null ? null : appeal.getOrderId());
        return buildAppealDisputeResp(appeal);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoordination(Long userId, AppPartnerCoordinationCreateReqVO reqVO) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        String disputeType = normalizeDisputeType(reqVO.getDisputeType(), true);
        if (Boolean.TRUE.equals(reqVO.getEscalateToPlatform()) && StrUtil.isBlank(reqVO.getEscalateRemark())) {
            throw exception(PARTNER_ESCALATE_REMARK_REQUIRED);
        }
        Long orderId;
        Long unitId;
        if (PartnerDisputeTypeEnum.COMPLAINT.getCode().equals(disputeType)) {
            ComplaintDO complaint = complaintMapper.selectById(reqVO.getDisputeId());
            validateOrderInPartnerRegion(partnerInfo, complaint == null ? null : complaint.getOrderId());
            orderId = complaint.getOrderId();
            unitId = complaint.getUnitId();
        } else {
            AppealDO appeal = appealMapper.selectById(reqVO.getDisputeId());
            validateOrderInPartnerRegion(partnerInfo, appeal == null ? null : appeal.getOrderId());
            orderId = appeal.getOrderId();
            unitId = appeal.getUnitId();
        }
        LocalDateTime now = LocalDateTime.now();
        PartnerCoordinationDO coordination = PartnerCoordinationDO.builder()
                .partnerId(partnerInfo.getId())
                .disputeType(disputeType)
                .disputeId(reqVO.getDisputeId())
                .orderId(orderId)
                .unitId(unitId)
                .status(Boolean.TRUE.equals(reqVO.getEscalateToPlatform()) ? "ESCALATED" : "PROCESSING")
                .coordinationRemark(reqVO.getCoordinationRemark())
                .escalateRemark(reqVO.getEscalateRemark())
                .initiatedBy(userId)
                .initiatedTime(now)
                .finishedBy(Boolean.TRUE.equals(reqVO.getEscalateToPlatform()) ? userId : null)
                .finishedTime(Boolean.TRUE.equals(reqVO.getEscalateToPlatform()) ? now : null)
                .build();
        partnerCoordinationMapper.insert(coordination);
        orderOperateLogMapper.insert(cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO.builder()
                .orderId(orderId)
                .unitId(unitId)
                .operateType(Boolean.TRUE.equals(reqVO.getEscalateToPlatform()) ? "PARTNER_ESCALATE" : "PARTNER_COORDINATION")
                .operateRole("PARTNER")
                .operateBy(userId)
                .remark(StrUtil.blankToDefault(reqVO.getEscalateRemark(), reqVO.getCoordinationRemark()))
                .operateTime(now)
                .build());
        return coordination.getId();
    }

    @Override
    public PageResult<AppPartnerPriceReportRespVO> getPriceReportPage(Long userId, AppPartnerPriceReportPageReqVO reqVO) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        List<String> regionCodes = getPartnerRegionCodes(partnerInfo);
        PageResult<MerchantPriceReportDO> pageResult = merchantPriceReportMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<MerchantPriceReportDO>()
                        .eq(MerchantPriceReportDO::getPartnerId, partnerInfo.getId())
                        .inIfPresent(MerchantPriceReportDO::getRegionCode, regionCodes)
                        .eqIfPresent(MerchantPriceReportDO::getRegionCode, reqVO.getRegionCode())
                        .eqIfPresent(MerchantPriceReportDO::getStatus, reqVO.getStatus())
                        .orderByDesc(MerchantPriceReportDO::getId));
        return new PageResult<>(buildPriceReportRespList(pageResult.getList()), pageResult.getTotal());
    }

    @Override
    public AppPartnerPriceReportRespVO getPriceReport(Long userId, Long id) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        MerchantPriceReportDO report = merchantPriceReportMapper.selectById(id);
        validatePriceReportInPartnerScope(partnerInfo, report);
        return buildPriceReportResp(report, buildMerchantInfoMap(Collections.singletonList(report.getMerchantId())),
                buildCategoryMap(Collections.singletonList(report.getCategoryId())));
    }

    @Override
    public Long createPriceReport(Long userId, AppPartnerPriceReportCreateReqVO reqVO) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        validateRegionAllowed(partnerInfo, reqVO.getRegionCode());
        validatePriceReportTarget(reqVO);
        MerchantPriceReportDO report = MerchantPriceReportDO.builder()
                .merchantId(reqVO.getMerchantId())
                .partnerId(partnerInfo.getId())
                .categoryId(reqVO.getCategoryId())
                .regionCode(reqVO.getRegionCode())
                .suggestedPrice(reqVO.getSuggestedPrice())
                .remark(reqVO.getRemark())
                .status("PENDING")
                .auditStatus("PENDING")
                .build();
        merchantPriceReportMapper.insert(report);
        return report.getId();
    }

    @Override
    public void withdrawPriceReport(Long userId, Long id) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        MerchantPriceReportDO report = merchantPriceReportMapper.selectById(id);
        validatePriceReportInPartnerScope(partnerInfo, report);
        if (!"PENDING".equalsIgnoreCase(report.getStatus()) || !"PENDING".equalsIgnoreCase(report.getAuditStatus())) {
            throw exception(PARTNER_PRICE_REPORT_STATUS_INVALID);
        }
        int updated = merchantPriceReportMapper.update(null, new LambdaUpdateWrapper<MerchantPriceReportDO>()
                .eq(MerchantPriceReportDO::getId, id)
                .eq(MerchantPriceReportDO::getPartnerId, partnerInfo.getId())
                .eq(MerchantPriceReportDO::getStatus, "PENDING")
                .eq(MerchantPriceReportDO::getAuditStatus, "PENDING")
                .set(MerchantPriceReportDO::getStatus, "WITHDRAWN")
                .set(MerchantPriceReportDO::getAuditRemark, "合作商主动撤回"));
        if (updated == 0) {
            throw exception(PARTNER_PRICE_REPORT_STATUS_INVALID);
        }
    }

    @Override
    public AppPartnerPromoteStatRespVO getPromoteStat(Long userId) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        List<String> regionCodes = getPartnerRegionCodes(partnerInfo);
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        PartnerPromoteAggregateDTO aggregate = regionCodes.isEmpty() ? new PartnerPromoteAggregateDTO()
                : partnerCoordinationMapper.selectPromoteAggregate(regionCodes, todayStart, todayStart.plusDays(1));
        PartnerPromoteTradeAggregateDTO tradeAggregate = regionCodes.isEmpty() ? new PartnerPromoteTradeAggregateDTO()
                : partnerCoordinationMapper.selectPromoteTradeAggregate(regionCodes);
        AppPartnerPromoteStatRespVO respVO = new AppPartnerPromoteStatRespVO();
        respVO.setPartnerId(partnerInfo.getId());
        respVO.setTodayNewUserCount(toIntCount(aggregate == null ? null : aggregate.getTodayNewUserCount()));
        respVO.setNewUserCount(toIntCount(aggregate == null ? null : aggregate.getNewUserCount()));
        respVO.setBoundPromoterCount(toIntCount(aggregate == null ? null : aggregate.getBoundPromoterCount()));
        respVO.setRelationCount(toIntCount(aggregate == null ? null : aggregate.getRelationCount()));
        respVO.setConvertedRelationCount(toIntCount(aggregate == null ? null : aggregate.getConvertedRelationCount()));
        respVO.setCommissionOrderCount(toIntCount(aggregate == null ? null : aggregate.getCommissionOrderCount()));
        respVO.setCommissionAmount(defaultAmount(aggregate == null ? null : aggregate.getCommissionAmount()));
        respVO.setConvertOrderCount(toIntCount(tradeAggregate == null ? null : tradeAggregate.getConvertOrderCount()));
        respVO.setTradeAmount(defaultAmount(tradeAggregate == null ? null : tradeAggregate.getTradeAmount()));
        return respVO;
    }

    @Override
    public PageResult<AppMessageRecordRespVO> getInstructionPage(Long userId, AppPartnerInstructionPageReqVO reqVO) {
        getRequiredPartner(userId);
        String category = normalizeInstructionCategory(reqVO.getMessageCategory());
        List<String> categories = category == null
                ? Arrays.asList(MessageCenterConstants.CATEGORY_MEETING_NOTICE,
                        MessageCenterConstants.CATEGORY_SUPERIOR_INSTRUCTION)
                : Collections.singletonList(category);
        PageResult<MessageRecordDO> pageResult = messageRecordMapper.selectAppPage(
                userId, reqVO, "SUCCESS", categories);
        List<AppMessageRecordRespVO> list = BeanUtils.toBean(pageResult.getList(), AppMessageRecordRespVO.class);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public AppMessageRecordDetailRespVO getInstruction(Long userId, Long id) {
        getRequiredPartner(userId);
        AppMessageRecordDetailRespVO respVO = appMessageService.getMessageRecord(userId, id);
        if (!isInstructionCategory(respVO.getMessageCategory())) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        return respVO;
    }

    private PartnerInfoDO getRequiredPartner(Long userId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(userId);
        PartnerInfoDO partnerInfo = partnerInfoService.getPartnerInfoByUserId(userId);
        if (partnerInfo == null) {
            throw exception(PARTNER_ROLE_REQUIRED);
        }
        if (!"PARTNER".equalsIgnoreCase(loginUser.getCurrentRoleCode())) {
            throw exception(CURRENT_ROLE_NOT_ALLOWED, "区域合作商");
        }
        return partnerInfo;
    }

    private List<String> getPartnerRegionCodes(PartnerInfoDO partnerInfo) {
        return partnerRegionRelMapper.selectListByPartnerId(partnerInfo.getId()).stream()
                .filter(item -> "ENABLE".equalsIgnoreCase(item.getStatus()))
                .map(PartnerRegionRelDO::getAdcode)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateRegionAllowed(PartnerInfoDO partnerInfo, String regionCode) {
        if (!getPartnerRegionCodes(partnerInfo).contains(regionCode)) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
    }

    private void validateEntryInPartnerRegion(PartnerInfoDO partnerInfo, MerchantEntryDO entry) {
        if (entry == null) {
            throw exception(MERCHANT_ENTRY_NOT_EXISTS);
        }
        validateRegionAllowed(partnerInfo, entry.getRegionCode());
    }

    private void validatePriceReportInPartnerScope(PartnerInfoDO partnerInfo, MerchantPriceReportDO report) {
        if (report == null) {
            throw exception(MERCHANT_PRICE_REPORT_NOT_EXISTS);
        }
        if (!Objects.equals(report.getPartnerId(), partnerInfo.getId())) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        validateRegionAllowed(partnerInfo, report.getRegionCode());
    }

    private void validatePriceReportTarget(AppPartnerPriceReportCreateReqVO reqVO) {
        MerchantInfoDO merchant = merchantInfoMapper.selectById(reqVO.getMerchantId());
        MerchantEntryDO approvedEntry = merchant == null ? null : merchantEntryMapper.selectOne(
                new LambdaQueryWrapperX<MerchantEntryDO>()
                        .eq(MerchantEntryDO::getMerchantId, merchant.getId())
                        .eq(MerchantEntryDO::getRegionCode, reqVO.getRegionCode())
                        .eq(MerchantEntryDO::getStatus, "APPROVED")
                        .last("LIMIT 1"));
        if (approvedEntry == null) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(reqVO.getCategoryId());
        MerchantCategoryRelDO categoryRel = merchantCategoryRelMapper.selectOne(
                new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                        .eq(MerchantCategoryRelDO::getMerchantId, merchant.getId())
                        .eq(MerchantCategoryRelDO::getCategoryId, reqVO.getCategoryId())
                        .last("LIMIT 1"));
        if (category == null || !"ENABLE".equalsIgnoreCase(category.getStatus()) || categoryRel == null) {
            throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_NOT_EXISTS);
        }
    }

    private void validateOrderInPartnerRegion(PartnerInfoDO partnerInfo, Long orderId) {
        if (orderId == null) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        List<String> regionCodes = getPartnerRegionCodes(partnerInfo);
        if (regionCodes.isEmpty()
                || defaultLong(partnerCoordinationMapper.selectOrderInRegionCount(orderId, regionCodes)) == 0L) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
    }

    private AppPartnerRegionRespVO.RegionItem buildRegionItem(PartnerRegionRelDO region) {
        AppPartnerRegionRespVO.RegionItem item = new AppPartnerRegionRespVO.RegionItem();
        item.setId(region.getId());
        item.setProvince(region.getProvince());
        item.setCity(region.getCity());
        item.setDistrict(region.getDistrict());
        item.setRegionCode(region.getAdcode());
        item.setStatus(region.getStatus());
        item.setStreetCode(region.getAdcode());
        item.setStreetName(region.getDistrict());
        item.setParentRegionCode(region.getCity());
        item.setSort(Math.toIntExact(region.getId() == null ? 0L : region.getId()));
        return item;
    }

    private Long countPendingEntries(List<String> regionAdcodes) {
        if (regionAdcodes == null || regionAdcodes.isEmpty()) {
            return 0L;
        }
        return merchantEntryMapper.selectCount(new LambdaQueryWrapperX<MerchantEntryDO>()
                .in(MerchantEntryDO::getRegionCode, regionAdcodes)
                .eq(MerchantEntryDO::getStatus, "PENDING"));
    }

    private Long countPriceReports(Long partnerId, List<String> regionAdcodes, String status) {
        if (regionAdcodes == null || regionAdcodes.isEmpty()) {
            return 0L;
        }
        return merchantPriceReportMapper.selectCount(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eq(MerchantPriceReportDO::getPartnerId, partnerId)
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .eq(MerchantPriceReportDO::getStatus, status));
    }

    private List<MerchantPriceReportDO> selectRecentPriceReports(Long partnerId, List<String> regionAdcodes) {
        if (CollUtil.isEmpty(regionAdcodes)) {
            return Collections.emptyList();
        }
        return merchantPriceReportMapper.selectList(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .eq(MerchantPriceReportDO::getPartnerId, partnerId)
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .orderByDesc(MerchantPriceReportDO::getId)
                .last("LIMIT 10"));
    }

    private AppPartnerWorkbenchRespVO.SummaryRespVO buildSummary(List<PartnerRegionRelDO> regions,
                                                                 PartnerWorkbenchAggregateDTO aggregate,
                                                                 Long pendingEntryAuditCount, Long pendingComplaintCount,
                                                                 Long pendingPriceReportCount,
                                                                 Long approvedPriceReportCount,
                                                                 Long rejectedPriceReportCount) {
        AppPartnerWorkbenchRespVO.SummaryRespVO summary = new AppPartnerWorkbenchRespVO.SummaryRespVO();
        summary.setRegionCount(regions == null ? 0 : regions.size());
        summary.setEnabledRegionCount(regions == null ? 0 : (int) regions.stream()
                .filter(item -> "ENABLE".equalsIgnoreCase(item.getStatus()))
                .count());
        summary.setPendingEntryAuditCount(pendingEntryAuditCount == null ? 0L : pendingEntryAuditCount);
        summary.setPendingComplaintCount(pendingComplaintCount == null ? 0L : pendingComplaintCount);
        summary.setPendingPriceReportCount(pendingPriceReportCount == null ? 0L : pendingPriceReportCount);
        summary.setOrderCount(defaultLong(aggregate == null ? null : aggregate.getOrderCount()));
        summary.setTradeAmount(defaultAmount(aggregate == null ? null : aggregate.getTradeAmount()));
        summary.setApprovedPriceReportCount(toIntCount(approvedPriceReportCount));
        summary.setRejectedPriceReportCount(toIntCount(rejectedPriceReportCount));
        return summary;
    }

    private List<AppPartnerWorkbenchRespVO.RecentPriceReportRespVO> buildRecentPriceReports(List<MerchantPriceReportDO> priceReports) {
        if (priceReports == null || priceReports.isEmpty()) {
            return Collections.emptyList();
        }
        return priceReports.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, AppPartnerWorkbenchRespVO.RecentPriceReportRespVO.class))
                .collect(Collectors.toList());
    }

    private List<Long> resolveMatchedUserIds(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(keyword), MemberUserDO::getId);
    }

    private List<AppPartnerEntryAuditRespVO> buildEntryAuditRespList(List<MerchantEntryDO> entries) {
        Map<Long, MemberUserDO> userMap = buildMemberUserMap(convertList(entries, MerchantEntryDO::getUserId));
        Map<Long, MerchantInfoDO> merchantMap = buildMerchantInfoMap(convertList(entries, MerchantEntryDO::getMerchantId));
        return entries.stream().map(item -> buildEntryAuditResp(item, userMap, merchantMap)).collect(Collectors.toList());
    }

    private AppPartnerEntryAuditRespVO buildEntryAuditResp(MerchantEntryDO entry, Map<Long, MemberUserDO> userMap,
                                                           Map<Long, MerchantInfoDO> merchantMap) {
        AppPartnerEntryAuditRespVO respVO = BeanUtils.toBean(entry, AppPartnerEntryAuditRespVO.class);
        MerchantEntrySnapshotUtils.MerchantEntrySnapshot snapshot = MerchantEntrySnapshotUtils.parseSnapshot(entry);
        MemberUserDO user = userMap.get(entry.getUserId());
        if (user != null) {
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(user.getMobile());
        }
        MerchantInfoDO merchant = merchantMap.get(entry.getMerchantId());
        if (snapshot != null) {
            respVO.setMerchantName(snapshot.getMerchantName());
            respVO.setMerchantContactName(snapshot.getContactName());
            respVO.setMerchantContactMobile(snapshot.getContactMobile());
            respVO.setServiceScopeDesc(snapshot.getServiceScopeDesc());
            respVO.setApplicantRealName(snapshot.getApplicantRealName());
            respVO.setCategories(buildEntryCategoriesFromSnapshot(snapshot.getCategories()));
            respVO.setQualifications(buildEntryQualificationsFromSnapshot(snapshot.getQualifications()));
        } else if (merchant != null) {
            respVO.setMerchantName(merchant.getMerchantName());
            respVO.setMerchantContactName(merchant.getContactName());
            respVO.setMerchantContactMobile(merchant.getContactMobile());
            respVO.setServiceScopeDesc(merchant.getServiceScopeDesc());
            MemberUserRealNameDO realName = entry.getUserId() == null ? null : memberUserRealNameMapper.selectByUserId(entry.getUserId());
            if (realName != null) {
                respVO.setApplicantRealName(realName.getRealName());
                respVO.setApplicantRealNameAuditStatus(realName.getAuditStatus());
            }
            List<MerchantCategoryRelDO> categoryRels = entry.getMerchantId() == null
                    ? Collections.emptyList()
                    : merchantCategoryRelMapper.selectListByMerchantId(entry.getMerchantId());
            respVO.setCategories(buildEntryCategories(categoryRels));
            List<MemberUserQualificationDO> qualifications = entry.getUserId() == null
                    ? Collections.emptyList()
                    : memberUserQualificationMapper.selectListByUserId(entry.getUserId());
            respVO.setQualifications(buildEntryQualifications(qualifications));
        }
        if (respVO.getApplicantRealNameAuditStatus() == null) {
            MemberUserRealNameDO realName = entry.getUserId() == null ? null : memberUserRealNameMapper.selectByUserId(entry.getUserId());
            if (realName != null) {
                respVO.setApplicantRealNameAuditStatus(realName.getAuditStatus());
                if (StrUtil.isBlank(respVO.getApplicantRealName())) {
                    respVO.setApplicantRealName(realName.getRealName());
                }
            }
        }
        respVO.setRejectReason(entry.getRejectReason());
        return respVO;
    }

    private List<AppPartnerEntryAuditRespVO.CategoryItem> buildEntryCategories(List<MerchantCategoryRelDO> categoryRels) {
        if (CollUtil.isEmpty(categoryRels)) {
            return Collections.emptyList();
        }
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(convertList(categoryRels, MerchantCategoryRelDO::getCategoryId));
        return categoryRels.stream().map(rel -> {
            AppPartnerEntryAuditRespVO.CategoryItem item = new AppPartnerEntryAuditRespVO.CategoryItem();
            item.setCategoryId(rel.getCategoryId());
            MerchantServiceCategoryDO category = categoryMap.get(rel.getCategoryId());
            item.setCategoryName(category == null ? null : category.getCategoryName());
            return item;
        }).collect(Collectors.toList());
    }

    private List<AppPartnerEntryAuditRespVO.CategoryItem> buildEntryCategoriesFromSnapshot(List<MerchantEntrySnapshotUtils.CategorySnapshot> categories) {
        if (CollUtil.isEmpty(categories)) {
            return Collections.emptyList();
        }
        return categories.stream().map(category -> {
            AppPartnerEntryAuditRespVO.CategoryItem item = new AppPartnerEntryAuditRespVO.CategoryItem();
            item.setCategoryId(category.getCategoryId());
            item.setCategoryName(category.getCategoryName());
            return item;
        }).collect(Collectors.toList());
    }

    private List<AppPartnerEntryAuditRespVO.QualificationItem> buildEntryQualifications(List<MemberUserQualificationDO> qualifications) {
        if (CollUtil.isEmpty(qualifications)) {
            return Collections.emptyList();
        }
        return qualifications.stream().map(qualification -> {
            AppPartnerEntryAuditRespVO.QualificationItem item = new AppPartnerEntryAuditRespVO.QualificationItem();
            item.setId(qualification.getId());
            item.setQualificationType(qualification.getQualificationType());
            item.setQualificationName(qualification.getQualificationName());
            item.setQualificationNo(qualification.getQualificationNo());
            item.setFileId(qualification.getFileId());
            item.setFileUrl(resolveFileUrl(qualification.getFileId()));
            item.setAuditStatus(qualification.getAuditStatus());
            item.setValidEndDate(qualification.getValidEndDate());
            return item;
        }).collect(Collectors.toList());
    }

    private List<AppPartnerEntryAuditRespVO.QualificationItem> buildEntryQualificationsFromSnapshot(List<MerchantEntrySnapshotUtils.QualificationSnapshot> qualifications) {
        if (CollUtil.isEmpty(qualifications)) {
            return Collections.emptyList();
        }
        return qualifications.stream().map(qualification -> {
            AppPartnerEntryAuditRespVO.QualificationItem item = new AppPartnerEntryAuditRespVO.QualificationItem();
            item.setId(qualification.getId());
            item.setQualificationType(qualification.getQualificationType());
            item.setQualificationName(qualification.getQualificationName());
            item.setQualificationNo(qualification.getQualificationNo());
            item.setFileId(qualification.getFileId());
            item.setFileUrl(resolveFileUrl(qualification.getFileId()));
            item.setAuditStatus(qualification.getAuditStatus());
            item.setValidEndDate(qualification.getValidEndDate());
            return item;
        }).collect(Collectors.toList());
    }

    private String resolveFileUrl(Long fileId) {
        if (fileId == null) {
            return null;
        }
        FileDO file = fileService.getFile(fileId);
        if (file == null || StrUtil.isBlank(file.getUrl())) {
            return null;
        }
        return file.getUrl().replace("/get/", "/preview/");
    }

    private Map<Long, MemberUserDO> buildMemberUserMap(List<Long> userIds) {
        Set<Long> ids = userIds == null ? Collections.emptySet() : new HashSet<>(userIds);
        ids.remove(null);
        return ids.isEmpty() ? Collections.emptyMap() : convertMap(memberUserMapper.selectListByIds(ids), MemberUserDO::getId);
    }

    private Map<Long, MerchantInfoDO> buildMerchantInfoMap(List<Long> merchantIds) {
        Set<Long> ids = merchantIds == null ? Collections.emptySet() : new HashSet<>(merchantIds);
        ids.remove(null);
        return ids.isEmpty() ? Collections.emptyMap() : convertMap(merchantInfoMapper.selectBatchIds(ids), MerchantInfoDO::getId);
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<Long> categoryIds) {
        Set<Long> ids = categoryIds == null ? Collections.emptySet() : new HashSet<>(categoryIds);
        ids.remove(null);
        return ids.isEmpty() ? Collections.emptyMap() : convertMap(merchantServiceCategoryMapper.selectBatchIds(ids), MerchantServiceCategoryDO::getId);
    }

    private AppPartnerDisputeRespVO buildComplaintDisputeResp(ComplaintDO complaint) {
        if (complaint == null) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        AppPartnerDisputeRespVO respVO = new AppPartnerDisputeRespVO();
        respVO.setDisputeType("COMPLAINT");
        respVO.setDisputeId(complaint.getId());
        respVO.setDisputeNo(complaint.getComplaintNo());
        respVO.setOrderId(complaint.getOrderId());
        respVO.setUnitId(complaint.getUnitId());
        respVO.setStatus(complaint.getStatus());
        respVO.setContent(complaint.getContent());
        respVO.setResultDesc(complaint.getResultDesc());
        respVO.setCreateTime(complaint.getCreateTime());
        fillOrderSummary(respVO, complaint.getOrderId(), complaint.getUnitId());
        respVO.setCoordinationRecords(buildCoordinationRecords("COMPLAINT", complaint.getId()));
        return respVO;
    }

    private List<AppPartnerDisputeRespVO> buildDisputeRespList(List<PartnerDisputePageRecordDTO> records) {
        if (CollUtil.isEmpty(records)) {
            return Collections.emptyList();
        }
        Set<Long> complaintIds = records.stream()
                .filter(item -> PartnerDisputeTypeEnum.COMPLAINT.getCode().equals(item.getDisputeType()))
                .map(PartnerDisputePageRecordDTO::getDisputeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Set<Long> appealIds = records.stream()
                .filter(item -> PartnerDisputeTypeEnum.APPEAL.getCode().equals(item.getDisputeType()))
                .map(PartnerDisputePageRecordDTO::getDisputeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, List<AppPartnerDisputeRespVO.CoordinationItem>> complaintCoordinationMap =
                buildCoordinationRecordMap(PartnerDisputeTypeEnum.COMPLAINT.getCode(), complaintIds);
        Map<Long, List<AppPartnerDisputeRespVO.CoordinationItem>> appealCoordinationMap =
                buildCoordinationRecordMap(PartnerDisputeTypeEnum.APPEAL.getCode(), appealIds);
        return records.stream().map(record -> {
            AppPartnerDisputeRespVO respVO = BeanUtils.toBean(record, AppPartnerDisputeRespVO.class);
            Map<Long, List<AppPartnerDisputeRespVO.CoordinationItem>> coordinationMap =
                    PartnerDisputeTypeEnum.COMPLAINT.getCode().equals(record.getDisputeType())
                            ? complaintCoordinationMap : appealCoordinationMap;
            respVO.setCoordinationRecords(coordinationMap.getOrDefault(record.getDisputeId(), Collections.emptyList()));
            return respVO;
        }).collect(Collectors.toList());
    }

    private AppPartnerDisputeRespVO buildAppealDisputeResp(AppealDO appeal) {
        if (appeal == null) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        AppPartnerDisputeRespVO respVO = new AppPartnerDisputeRespVO();
        respVO.setDisputeType("APPEAL");
        respVO.setDisputeId(appeal.getId());
        respVO.setDisputeNo(appeal.getAppealNo());
        respVO.setOrderId(appeal.getOrderId());
        respVO.setUnitId(appeal.getUnitId());
        respVO.setStatus(appeal.getStatus());
        respVO.setContent(appeal.getContent());
        respVO.setResultDesc(StrUtil.blankToDefault(appeal.getAuditRemark(), appeal.getRejectReason()));
        respVO.setCreateTime(appeal.getCreateTime());
        fillOrderSummary(respVO, appeal.getOrderId(), appeal.getUnitId());
        respVO.setCoordinationRecords(buildCoordinationRecords("APPEAL", appeal.getId()));
        return respVO;
    }

    private void fillOrderSummary(AppPartnerDisputeRespVO respVO, Long orderId, Long unitId) {
        OrderInfoDO order = orderId == null ? null : orderInfoMapper.selectById(orderId);
        if (order != null) {
            respVO.setOrderNo(order.getOrderNo());
            respVO.setRegionCode(resolveMerchantRegionCode(order.getMerchantId()));
        }
        OrderUnitDO unit = unitId == null ? null : orderUnitMapper.selectById(unitId);
        if (unit != null) {
            respVO.setUnitNo(unit.getUnitNo());
        }
    }

    private String resolveMerchantRegionCode(Long merchantId) {
        if (merchantId == null) {
            return null;
        }
        MerchantEntryDO entry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getMerchantId, merchantId)
                .eq(MerchantEntryDO::getStatus, "APPROVED")
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        return entry == null ? null : entry.getRegionCode();
    }

    private List<AppPartnerDisputeRespVO.CoordinationItem> buildCoordinationRecords(String disputeType, Long disputeId) {
        return partnerCoordinationMapper.selectListByDispute(disputeType, disputeId).stream()
                .map(this::toCoordinationItem)
                .collect(Collectors.toList());
    }

    private Map<Long, List<AppPartnerDisputeRespVO.CoordinationItem>> buildCoordinationRecordMap(
            String disputeType, Set<Long> disputeIds) {
        Map<Long, List<AppPartnerDisputeRespVO.CoordinationItem>> result = new HashMap<>();
        for (PartnerCoordinationDO coordination : partnerCoordinationMapper.selectListByDisputes(disputeType, disputeIds)) {
            result.computeIfAbsent(coordination.getDisputeId(), key -> new java.util.ArrayList<>())
                    .add(toCoordinationItem(coordination));
        }
        return result;
    }

    private AppPartnerDisputeRespVO.CoordinationItem toCoordinationItem(PartnerCoordinationDO coordination) {
        AppPartnerDisputeRespVO.CoordinationItem respVO =
                BeanUtils.toBean(coordination, AppPartnerDisputeRespVO.CoordinationItem.class);
        respVO.setCoordinationRemark(coordination.getCoordinationRemark());
        return respVO;
    }

    private List<AppPartnerPriceReportRespVO> buildPriceReportRespList(List<MerchantPriceReportDO> reports) {
        Map<Long, MerchantInfoDO> merchantMap = buildMerchantInfoMap(convertList(reports, MerchantPriceReportDO::getMerchantId));
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(convertList(reports, MerchantPriceReportDO::getCategoryId));
        return reports.stream().map(item -> buildPriceReportResp(item, merchantMap, categoryMap)).collect(Collectors.toList());
    }

    private AppPartnerPriceReportRespVO buildPriceReportResp(MerchantPriceReportDO report, Map<Long, MerchantInfoDO> merchantMap,
                                                             Map<Long, MerchantServiceCategoryDO> categoryMap) {
        AppPartnerPriceReportRespVO respVO = BeanUtils.toBean(report, AppPartnerPriceReportRespVO.class);
        MerchantInfoDO merchant = merchantMap.get(report.getMerchantId());
        if (merchant != null) {
            respVO.setMerchantName(merchant.getMerchantName());
        }
        MerchantServiceCategoryDO category = categoryMap.get(report.getCategoryId());
        if (category != null) {
            respVO.setCategoryName(category.getCategoryName());
        }
        return respVO;
    }

    private boolean isInstructionCategory(String category) {
        return Objects.equals(category, MessageCenterConstants.CATEGORY_MEETING_NOTICE)
                || Objects.equals(category, MessageCenterConstants.CATEGORY_SUPERIOR_INSTRUCTION);
    }

    private String normalizeDisputeType(String disputeType, boolean required) {
        if (StrUtil.isBlank(disputeType)) {
            if (required) {
                throw exception(PARTNER_DISPUTE_TYPE_INVALID);
            }
            return null;
        }
        PartnerDisputeTypeEnum type = PartnerDisputeTypeEnum.getByCode(disputeType.trim());
        if (type == null) {
            throw exception(PARTNER_DISPUTE_TYPE_INVALID);
        }
        return type.getCode();
    }

    private String normalizeInstructionCategory(String category) {
        if (StrUtil.isBlank(category)) {
            return null;
        }
        String normalized = category.trim().toUpperCase(Locale.ROOT);
        if (!isInstructionCategory(normalized)) {
            throw exception(PARTNER_INSTRUCTION_CATEGORY_INVALID);
        }
        return normalized;
    }

    private long defaultLong(Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int toIntCount(Long value) {
        return Math.toIntExact(defaultLong(value));
    }
}
