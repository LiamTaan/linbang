package cn.iocoder.yudao.module.linbang.service.partnerinfo;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo.PartnerInfoDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnercoordination.dto.PartnerWorkbenchAggregateDTO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerregionrel.PartnerRegionRelDO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class PartnerInfoDetailAssembler {

    private PartnerInfoDetailAssembler() {
    }

    static PartnerInfoDetailRespVO build(PartnerInfoDO partnerInfo, MemberUserDO user, List<PartnerRegionRelDO> regions,
                                         List<String> regionAdcodes, PartnerWorkbenchAggregateDTO aggregate,
                                         List<MerchantPriceReportDO> priceReports, Long pendingEntryAuditCount,
                                         Long pendingComplaintCount, Long pendingPriceReportCount,
                                         Long approvedPriceReportCount, Long rejectedPriceReportCount,
                                         Map<Long, MerchantInfoDO> merchantMap,
                                         Map<Long, MerchantServiceCategoryDO> categoryMap) {
        PartnerInfoDetailRespVO respVO = BeanUtils.toBean(partnerInfo, PartnerInfoDetailRespVO.class);
        if (user != null) {
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(user.getMobile());
        }
        respVO.setRegionAdcodes(regionAdcodes == null ? Collections.emptyList() : regionAdcodes);
        respVO.setRegions(buildRegions(regions));
        respVO.setSummary(buildSummary(regions, aggregate, pendingEntryAuditCount, pendingComplaintCount,
                pendingPriceReportCount, approvedPriceReportCount, rejectedPriceReportCount));
        respVO.setRecentPriceReports(buildRecentPriceReports(priceReports, merchantMap, categoryMap));
        return respVO;
    }

    private static List<PartnerInfoDetailRespVO.RegionRespVO> buildRegions(List<PartnerRegionRelDO> regions) {
        if (regions == null || regions.isEmpty()) {
            return Collections.emptyList();
        }
        return regions.stream().map(region -> BeanUtils.toBean(region, PartnerInfoDetailRespVO.RegionRespVO.class))
                .collect(Collectors.toList());
    }

    private static PartnerInfoDetailRespVO.SummaryRespVO buildSummary(List<PartnerRegionRelDO> regions,
                                                                      PartnerWorkbenchAggregateDTO aggregate,
                                                                      Long pendingEntryAuditCount, Long pendingComplaintCount,
                                                                      Long pendingPriceReportCount,
                                                                      Long approvedPriceReportCount,
                                                                      Long rejectedPriceReportCount) {
        PartnerInfoDetailRespVO.SummaryRespVO summary = new PartnerInfoDetailRespVO.SummaryRespVO();
        summary.setRegionCount(regions == null ? 0 : regions.size());
        summary.setEnabledRegionCount(regions == null ? 0 : (int) regions.stream()
                .filter(item -> "ENABLE".equalsIgnoreCase(item.getStatus()))
                .count());
        summary.setPendingEntryAuditCount(pendingEntryAuditCount == null ? 0L : pendingEntryAuditCount);
        summary.setPendingComplaintCount(pendingComplaintCount == null ? 0L : pendingComplaintCount);
        summary.setPendingPriceReportCount(pendingPriceReportCount == null ? 0L : pendingPriceReportCount);
        summary.setOrderCount(aggregate == null || aggregate.getOrderCount() == null ? 0L : aggregate.getOrderCount());
        summary.setTradeAmount(aggregate == null || aggregate.getTradeAmount() == null
                ? java.math.BigDecimal.ZERO : aggregate.getTradeAmount());
        summary.setApprovedPriceReportCount(Math.toIntExact(approvedPriceReportCount == null ? 0L : approvedPriceReportCount));
        summary.setRejectedPriceReportCount(Math.toIntExact(rejectedPriceReportCount == null ? 0L : rejectedPriceReportCount));
        return summary;
    }

    private static List<PartnerInfoDetailRespVO.RecentPriceReportRespVO> buildRecentPriceReports(
            List<MerchantPriceReportDO> priceReports,
            Map<Long, MerchantInfoDO> merchantMap,
            Map<Long, MerchantServiceCategoryDO> categoryMap) {
        if (priceReports == null || priceReports.isEmpty()) {
            return Collections.emptyList();
        }
        return priceReports.stream()
                .limit(10)
                .map(report -> {
                    PartnerInfoDetailRespVO.RecentPriceReportRespVO respVO =
                            BeanUtils.toBean(report, PartnerInfoDetailRespVO.RecentPriceReportRespVO.class);
                    MerchantInfoDO merchant = merchantMap == null ? null : merchantMap.get(report.getMerchantId());
                    if (merchant != null) {
                        respVO.setMerchantName(merchant.getMerchantName());
                        respVO.setMerchantContactName(merchant.getContactName());
                        respVO.setMerchantContactMobile(merchant.getContactMobile());
                    }
                    MerchantServiceCategoryDO category = categoryMap == null ? null : categoryMap.get(report.getCategoryId());
                    if (category != null) {
                        respVO.setCategoryName(category.getCategoryName());
                    }
                    return respVO;
                })
                .collect(Collectors.toList());
    }
}
