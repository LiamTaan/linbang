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
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.PartnerCoordinationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnercoordination.PartnerCoordinationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerregionrel.PartnerRegionRelMapper;
import cn.iocoder.yudao.module.linbang.service.app.message.AppMessageService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.partnerinfo.PartnerInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_ENTRY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_PRICE_REPORT_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_ENTRY_AUDIT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_PRICE_REPORT_STATUS_INVALID;
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
    private MemberUserMapper memberUserMapper;
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
    private CommissionOrderMapper commissionOrderMapper;
    @Resource
    private AppMessageService appMessageService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

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
        } else if ("REJECTED".equalsIgnoreCase(reqVO.getAuditStatus())) {
            updateObj.setFirstAuditStatus("REJECTED");
            updateObj.setStatus("REJECTED");
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
        list.sort(Comparator.comparing(AppPartnerDisputeRespVO::getCreateTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        return buildManualPage(list, reqVO.getPageNo(), reqVO.getPageSize());
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
        List<Long> orderIds = resolvePartnerOrderIds(partnerInfo);
        List<OrderInfoDO> orders = orderIds.isEmpty() ? Collections.emptyList() : orderInfoMapper.selectBatchIds(orderIds);
        Set<Long> orderUserIds = convertSet(orders, OrderInfoDO::getUserId, Objects::nonNull);
        List<CommissionOrderDO> commissionOrders = orderIds.isEmpty() ? Collections.emptyList()
                : commissionOrderMapper.selectList(new LambdaQueryWrapperX<CommissionOrderDO>()
                .in(CommissionOrderDO::getSourceOrderId, orderIds));
        Set<Long> promoterIds = convertSet(commissionOrders, CommissionOrderDO::getPromoterId, Objects::nonNull);
        Set<Long> convertedOrderIds = convertSet(commissionOrders, CommissionOrderDO::getSourceOrderId, Objects::nonNull);
        AppPartnerPromoteStatRespVO respVO = new AppPartnerPromoteStatRespVO();
        respVO.setPartnerId(partnerInfo.getId());
        respVO.setNewUserCount(orderUserIds.size());
        respVO.setBoundPromoterCount(promoterIds.size());
        respVO.setConvertOrderCount(convertedOrderIds.size());
        respVO.setTradeAmount(orders.stream()
                .filter(item -> convertedOrderIds.contains(item.getId()))
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
        PartnerInfoDO partnerInfo = partnerInfoService.getPartnerInfoByUserId(userId);
        if (partnerInfo == null) {
            throw exception(PARTNER_INFO_NOT_EXISTS);
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
        return complaintMapper.selectCount(new LambdaQueryWrapperX<ComplaintDO>()
                .in(ComplaintDO::getOrderId, orderIds)
                .in(ComplaintDO::getStatus, "PENDING", "PROCESSING"));
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

    private List<AppPartnerEntryAuditRespVO> buildEntryAuditRespList(List<MerchantEntryDO> entries) {
        Map<Long, MemberUserDO> userMap = buildMemberUserMap(convertList(entries, MerchantEntryDO::getUserId));
        Map<Long, MerchantInfoDO> merchantMap = buildMerchantInfoMap(convertList(entries, MerchantEntryDO::getMerchantId));
        return entries.stream().map(item -> buildEntryAuditResp(item, userMap, merchantMap)).collect(Collectors.toList());
    }

    private AppPartnerEntryAuditRespVO buildEntryAuditResp(MerchantEntryDO entry, Map<Long, MemberUserDO> userMap,
                                                           Map<Long, MerchantInfoDO> merchantMap) {
        AppPartnerEntryAuditRespVO respVO = BeanUtils.toBean(entry, AppPartnerEntryAuditRespVO.class);
        MemberUserDO user = userMap.get(entry.getUserId());
        if (user != null) {
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(user.getMobile());
        }
        MerchantInfoDO merchant = merchantMap.get(entry.getMerchantId());
        if (merchant != null) {
            respVO.setMerchantName(merchant.getMerchantName());
        }
        return respVO;
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
        }
        OrderUnitDO unit = unitId == null ? null : orderUnitMapper.selectById(unitId);
        if (unit != null) {
            respVO.setUnitNo(unit.getUnitNo());
        }
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
