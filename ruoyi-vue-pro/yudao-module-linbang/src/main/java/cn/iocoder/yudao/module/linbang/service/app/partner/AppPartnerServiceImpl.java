package cn.iocoder.yudao.module.linbang.service.app.partner;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.partner.vo.AppPartnerWorkbenchRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.partnerregionrel.PartnerRegionRelMapper;
import cn.iocoder.yudao.module.linbang.service.partnerinfo.PartnerInfoService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.PARTNER_INFO_NOT_EXISTS;

@Service
@Validated
public class AppPartnerServiceImpl implements AppPartnerService {

    @Resource
    private PartnerInfoService partnerInfoService;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private MerchantPriceReportMapper merchantPriceReportMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private PartnerRegionRelMapper partnerRegionRelMapper;

    @Override
    public AppPartnerWorkbenchRespVO getWorkbench(Long userId) {
        PartnerInfoDO partnerInfo = partnerInfoService.getPartnerInfoByUserId(userId);
        if (partnerInfo == null) {
            throw exception(PARTNER_INFO_NOT_EXISTS);
        }
        List<String> regionAdcodes = partnerInfoService.getPartnerRegionAdcodes(partnerInfo.getId());
        List<PartnerRegionRelDO> regions = partnerRegionRelMapper.selectListByPartnerId(partnerInfo.getId());
        List<Long> merchantIds = merchantEntryMapper.selectList(new LambdaQueryWrapperX<MerchantEntryDO>()
                        .inIfPresent(MerchantEntryDO::getRegionCode, regionAdcodes)
                        .eq(MerchantEntryDO::getStatus, "APPROVED"))
                .stream()
                .map(MerchantEntryDO::getMerchantId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        List<OrderInfoDO> orders = merchantIds.isEmpty() ? Collections.emptyList()
                : orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                .in(OrderInfoDO::getMerchantId, merchantIds));
        List<Long> orderIds = orders.stream()
                .map(OrderInfoDO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
}
