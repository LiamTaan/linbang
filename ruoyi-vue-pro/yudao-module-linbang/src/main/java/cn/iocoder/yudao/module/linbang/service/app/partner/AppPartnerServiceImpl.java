package cn.iocoder.yudao.module.linbang.service.app.partner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
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
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberaddress.MemberUserAddressDO;
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
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberaddress.MemberUserAddressMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination.PartnerCoordinationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerregionrel.PartnerRegionRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation.PromoterRelationMapper;
import cn.iocoder.yudao.module.linbang.service.app.message.AppMessageService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.merchantentry.MerchantEntrySnapshotUtils;
import cn.iocoder.yudao.module.linbang.service.partnerinfo.PartnerInfoService;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CURRENT_ROLE_NOT_ALLOWED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_ENTRY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_PRICE_REPORT_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_ENTRY_AUDIT_STATUS_INVALID;
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
    private MemberUserAddressMapper memberUserAddressMapper;
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
    private PromoterRelationMapper promoterRelationMapper;
    @Resource
    private PartnerCoordinationMapper partnerCoordinationMapper;
    @Resource
    private CommissionOrderMapper commissionOrderMapper;
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
        List<String> regionAdcodes = partnerInfoService.getPartnerRegionAdcodes(partnerInfo.getId());
        List<PartnerRegionRelDO> regions = partnerRegionRelMapper.selectListByPartnerId(partnerInfo.getId());
        List<Long> merchantIds = resolveMerchantIdsByRegions(regionAdcodes);
        List<OrderInfoDO> orders = merchantIds.isEmpty() ? Collections.emptyList()
                : orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                .in(OrderInfoDO::getMerchantId, merchantIds));
        List<Long> orderIds = convertList(orders, OrderInfoDO::getId);
        List<MerchantPriceReportDO> priceReports = regionAdcodes.isEmpty() ? Collections.emptyList()
                : merchantPriceReportMapper.selectList(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .orderByDesc(MerchantPriceReportDO::getId));

        Long pendingEntryAuditCount = countPendingEntries(regionAdcodes);
        Long pendingComplaintCount = countPendingComplaints(orderIds);
        Long pendingPriceReportCount = countPendingPriceReports(regionAdcodes);
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
        respVO.setOrderCount((long) orders.size());
        respVO.setTradeAmount(sumTradeAmount(orders));
        respVO.setSummary(buildSummary(regions, orders, priceReports, pendingEntryAuditCount, pendingComplaintCount,
                pendingPriceReportCount));
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
        merchantEntryMapper.updateById(updateObj);
        messagePushDispatchService.dispatchSingle("lb_merchant_entry_audited", "入驻初审结果通知", "MERCHANT_ENTRY",
                entry.getId(), entry.getUserId(), StrUtil.blankToDefault(updateObj.getRemark(), "合作商已完成入驻初审"));
    }

    @Override
    public PageResult<AppPartnerDisputeRespVO> getDisputePage(Long userId, AppPartnerDisputePageReqVO reqVO) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        List<Long> orderIds = resolvePartnerOrderIds(partnerInfo);
        List<AppPartnerDisputeRespVO> list = new ArrayList<>();
        if (CollUtil.isEmpty(orderIds)) {
            return PageResult.empty();
        }
        if (!"APPEAL".equalsIgnoreCase(reqVO.getDisputeType())) {
            complaintMapper.selectList(new LambdaQueryWrapperX<ComplaintDO>()
                            .in(ComplaintDO::getOrderId, orderIds)
                            .eqIfPresent(ComplaintDO::getStatus, reqVO.getStatus())
                            .orderByDesc(ComplaintDO::getCreateTime)
                            .orderByDesc(ComplaintDO::getId))
                    .forEach(item -> list.add(buildComplaintDisputeResp(item)));
        }
        if (!"COMPLAINT".equalsIgnoreCase(reqVO.getDisputeType())) {
            appealMapper.selectList(new LambdaQueryWrapperX<AppealDO>()
                            .in(AppealDO::getOrderId, orderIds)
                            .eqIfPresent(AppealDO::getStatus, reqVO.getStatus())
                            .orderByDesc(AppealDO::getCreateTime)
                            .orderByDesc(AppealDO::getId))
                    .forEach(item -> list.add(buildAppealDisputeResp(item)));
        }
        List<AppPartnerDisputeRespVO> filteredList = list.stream()
                .filter(item -> StrUtil.isBlank(reqVO.getKeyword())
                        || StrUtil.containsIgnoreCase(StrUtil.blankToDefault(item.getOrderNo(), ""), reqVO.getKeyword())
                        || StrUtil.containsIgnoreCase(StrUtil.blankToDefault(item.getDisputeNo(), ""), reqVO.getKeyword()))
                .filter(item -> StrUtil.isBlank(reqVO.getRegionCode())
                        || Objects.equals(reqVO.getRegionCode(), item.getRegionCode()))
                .collect(Collectors.toList());
        filteredList.sort(Comparator.comparing(AppPartnerDisputeRespVO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return buildManualPage(filteredList, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    public AppPartnerDisputeRespVO getDispute(Long userId, String disputeType, Long disputeId) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        if ("COMPLAINT".equalsIgnoreCase(disputeType)) {
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
        Long orderId;
        Long unitId;
        if ("COMPLAINT".equalsIgnoreCase(reqVO.getDisputeType())) {
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
                .disputeType(reqVO.getDisputeType())
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
        merchantPriceReportMapper.updateById(MerchantPriceReportDO.builder()
                .id(id)
                .status("WITHDRAWN")
                .auditRemark("合作商主动撤回")
                .build());
    }

    @Override
    public AppPartnerPromoteStatRespVO getPromoteStat(Long userId) {
        PartnerInfoDO partnerInfo = getRequiredPartner(userId);
        List<String> regionCodes = getPartnerRegionCodes(partnerInfo);
        List<Long> scopedUserIds = resolveScopedUserIdsByRegions(regionCodes);
        List<MemberUserDO> scopedUsers = scopedUserIds.isEmpty() ? Collections.emptyList()
                : memberUserMapper.selectBatchIds(scopedUserIds);
        LocalDate today = LocalDate.now();
        List<PromoterRelationDO> promoterRelations = scopedUserIds.isEmpty() ? Collections.emptyList()
                : promoterRelationMapper.selectList(new LambdaQueryWrapperX<PromoterRelationDO>()
                .in(PromoterRelationDO::getUserId, scopedUserIds)
                .orderByDesc(PromoterRelationDO::getId));
        Set<Long> promoterIds = convertSet(promoterRelations, PromoterRelationDO::getPromoterId, Objects::nonNull);
        List<CommissionOrderDO> commissionOrders = scopedUserIds.isEmpty() ? Collections.emptyList()
                : commissionOrderMapper.selectList(new LambdaQueryWrapperX<CommissionOrderDO>()
                .in(CommissionOrderDO::getUserId, scopedUserIds)
                .inIfPresent(CommissionOrderDO::getPromoterId, new ArrayList<>(promoterIds))
                .orderByDesc(CommissionOrderDO::getId));
        Set<Long> convertedOrderIds = convertSet(commissionOrders, CommissionOrderDO::getSourceOrderId, Objects::nonNull);
        List<OrderInfoDO> convertedOrders = convertedOrderIds.isEmpty() ? Collections.emptyList()
                : orderInfoMapper.selectBatchIds(convertedOrderIds);
        AppPartnerPromoteStatRespVO respVO = new AppPartnerPromoteStatRespVO();
        respVO.setPartnerId(partnerInfo.getId());
        respVO.setTodayNewUserCount((int) scopedUsers.stream()
                .filter(item -> item.getCreateTime() != null && Objects.equals(item.getCreateTime().toLocalDate(), today))
                .count());
        respVO.setNewUserCount(scopedUsers.size());
        respVO.setBoundPromoterCount(promoterIds.size());
        respVO.setConvertOrderCount(convertedOrderIds.size());
        respVO.setTradeAmount(convertedOrders.stream()
                .map(OrderInfoDO::getOrderAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return respVO;
    }

    @Override
    public PageResult<AppMessageRecordRespVO> getInstructionPage(Long userId, AppPartnerInstructionPageReqVO reqVO) {
        getRequiredPartner(userId);
        AppMessageRecordPageReqVO pageReqVO = new AppMessageRecordPageReqVO();
        pageReqVO.setPageNo(reqVO.getPageNo());
        pageReqVO.setPageSize(reqVO.getPageSize());
        pageReqVO.setMessageCategory(reqVO.getMessageCategory());
        PageResult<MessageRecordDO> pageResult = appMessageService.getMessageRecordPage(userId, pageReqVO);
        List<AppMessageRecordRespVO> list = BeanUtils.toBean(pageResult.getList(), AppMessageRecordRespVO.class);
        list = list.stream().filter(item -> isInstructionCategory(item.getMessageCategory())).collect(Collectors.toList());
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
        return partnerInfoService.getPartnerRegionAdcodes(partnerInfo.getId());
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

    private void validateOrderInPartnerRegion(PartnerInfoDO partnerInfo, Long orderId) {
        if (orderId == null) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
        List<Long> merchantIds = resolveMerchantIdsByRegions(getPartnerRegionCodes(partnerInfo));
        if (!merchantIds.contains(order.getMerchantId())) {
            throw exception(PARTNER_REGION_ACCESS_DENIED);
        }
    }

    private List<Long> resolveMerchantIdsByRegions(List<String> regionAdcodes) {
        return merchantEntryMapper.selectList(new LambdaQueryWrapperX<MerchantEntryDO>()
                        .inIfPresent(MerchantEntryDO::getRegionCode, regionAdcodes)
                        .eq(MerchantEntryDO::getStatus, "APPROVED"))
                .stream()
                .map(MerchantEntryDO::getMerchantId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Long> resolvePartnerOrderIds(PartnerInfoDO partnerInfo) {
        List<Long> merchantIds = resolveMerchantIdsByRegions(getPartnerRegionCodes(partnerInfo));
        if (merchantIds.isEmpty()) {
            return Collections.emptyList();
        }
        return convertList(orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                .in(OrderInfoDO::getMerchantId, merchantIds)), OrderInfoDO::getId);
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

    private Long countPendingComplaints(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return 0L;
        }
        long complaintCount = complaintMapper.selectCount(new LambdaQueryWrapperX<ComplaintDO>()
                .in(ComplaintDO::getOrderId, orderIds)
                .in(ComplaintDO::getStatus, "PENDING", "PROCESSING"));
        long appealCount = appealMapper.selectCount(new LambdaQueryWrapperX<AppealDO>()
                .in(AppealDO::getOrderId, orderIds)
                .in(AppealDO::getStatus, "PENDING", "PROCESSING"));
        return complaintCount + appealCount;
    }

    private Long countPendingPriceReports(List<String> regionAdcodes) {
        if (regionAdcodes == null || regionAdcodes.isEmpty()) {
            return 0L;
        }
        return merchantPriceReportMapper.selectCount(new LambdaQueryWrapperX<MerchantPriceReportDO>()
                .in(MerchantPriceReportDO::getRegionCode, regionAdcodes)
                .eq(MerchantPriceReportDO::getStatus, "PENDING"));
    }

    private BigDecimal sumTradeAmount(List<OrderInfoDO> orders) {
        return orders.stream()
                .map(OrderInfoDO::getOrderAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private AppPartnerWorkbenchRespVO.SummaryRespVO buildSummary(List<PartnerRegionRelDO> regions, List<OrderInfoDO> orders,
                                                                 List<MerchantPriceReportDO> priceReports,
                                                                 Long pendingEntryAuditCount, Long pendingComplaintCount,
                                                                 Long pendingPriceReportCount) {
        AppPartnerWorkbenchRespVO.SummaryRespVO summary = new AppPartnerWorkbenchRespVO.SummaryRespVO();
        summary.setRegionCount(regions == null ? 0 : regions.size());
        summary.setEnabledRegionCount(regions == null ? 0 : (int) regions.stream()
                .filter(item -> "ENABLE".equalsIgnoreCase(item.getStatus()))
                .count());
        summary.setPendingEntryAuditCount(pendingEntryAuditCount == null ? 0L : pendingEntryAuditCount);
        summary.setPendingComplaintCount(pendingComplaintCount == null ? 0L : pendingComplaintCount);
        summary.setPendingPriceReportCount(pendingPriceReportCount == null ? 0L : pendingPriceReportCount);
        summary.setOrderCount(orders == null ? 0L : (long) orders.size());
        summary.setTradeAmount(sumTradeAmount(orders));
        summary.setApprovedPriceReportCount(priceReports == null ? 0 : (int) priceReports.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(item.getStatus()))
                .count());
        summary.setRejectedPriceReportCount(priceReports == null ? 0 : (int) priceReports.stream()
                .filter(item -> "REJECTED".equalsIgnoreCase(item.getStatus()))
                .count());
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

    private List<Long> resolveScopedUserIdsByRegions(List<String> regionCodes) {
        if (CollUtil.isEmpty(regionCodes)) {
            return Collections.emptyList();
        }
        List<MemberUserAddressDO> candidateAddresses = memberUserAddressMapper.selectList(new LambdaQueryWrapperX<MemberUserAddressDO>()
                .in(MemberUserAddressDO::getAdcode, regionCodes));
        Set<Long> candidateUserIds = convertSet(candidateAddresses, MemberUserAddressDO::getUserId, Objects::nonNull);
        if (candidateUserIds.isEmpty()) {
            return Collections.emptyList();
        }
        return memberUserAddressMapper.selectListByUserIds(candidateUserIds).stream()
                .collect(Collectors.toMap(MemberUserAddressDO::getUserId, item -> item, (left, right) -> left,
                        java.util.LinkedHashMap::new))
                .values()
                .stream()
                .filter(item -> regionCodes.contains(item.getAdcode()))
                .map(MemberUserAddressDO::getUserId)
                .collect(Collectors.toList());
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
        List<MerchantEntryDO> entries = merchantEntryMapper.selectList(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getMerchantId, merchantId)
                .eq(MerchantEntryDO::getStatus, "APPROVED")
                .orderByDesc(MerchantEntryDO::getId));
        return entries.isEmpty() ? null : entries.get(0).getRegionCode();
    }

    private List<AppPartnerDisputeRespVO.CoordinationItem> buildCoordinationRecords(String disputeType, Long disputeId) {
        return partnerCoordinationMapper.selectListByDispute(disputeType, disputeId).stream().map(item -> {
            AppPartnerDisputeRespVO.CoordinationItem respVO = BeanUtils.toBean(item, AppPartnerDisputeRespVO.CoordinationItem.class);
            respVO.setCoordinationRemark(item.getCoordinationRemark());
            return respVO;
        }).collect(Collectors.toList());
    }

    private PageResult<AppPartnerDisputeRespVO> buildManualPage(List<AppPartnerDisputeRespVO> list, Integer pageNo, Integer pageSize) {
        if (list.isEmpty()) {
            return PageResult.empty();
        }
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = Math.min((safePageNo - 1) * safePageSize, list.size());
        int toIndex = Math.min(fromIndex + safePageSize, list.size());
        return new PageResult<>(list.subList(fromIndex, toIndex), (long) list.size());
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
}
