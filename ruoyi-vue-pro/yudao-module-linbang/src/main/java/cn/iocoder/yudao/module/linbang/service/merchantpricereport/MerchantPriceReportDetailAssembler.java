package cn.iocoder.yudao.module.linbang.service.merchantpricereport;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.partnerinfo.PartnerInfoDO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class MerchantPriceReportDetailAssembler {

    private MerchantPriceReportDetailAssembler() {
    }

    static MerchantPriceReportDetailRespVO build(MerchantPriceReportDO report, MerchantInfoDO merchantInfo, PartnerInfoDO partnerInfo,
                                                 MerchantServiceCategoryDO category,
                                                 MerchantEntryDO merchantEntry, MemberUserDO merchantEntryUser,
                                                 List<MerchantPriceReportDO> relatedReports,
                                                 Map<Long, MerchantInfoDO> relatedMerchantMap,
                                                 Map<Long, PartnerInfoDO> relatedPartnerMap,
                                                 Map<Long, MerchantServiceCategoryDO> relatedCategoryMap) {
        MerchantPriceReportDetailRespVO respVO = BeanUtils.toBean(report, MerchantPriceReportDetailRespVO.class);
        if (merchantInfo != null) {
            respVO.setMerchant(BeanUtils.toBean(merchantInfo, MerchantPriceReportDetailRespVO.MerchantRespVO.class));
        }
        if (partnerInfo != null) {
            respVO.setPartner(BeanUtils.toBean(partnerInfo, MerchantPriceReportDetailRespVO.PartnerRespVO.class));
        }
        if (category != null) {
            respVO.setCategoryName(category.getCategoryName());
        }
        if (merchantEntry != null) {
            MerchantPriceReportDetailRespVO.MerchantEntryRespVO merchantEntryRespVO =
                    BeanUtils.toBean(merchantEntry, MerchantPriceReportDetailRespVO.MerchantEntryRespVO.class);
            if (merchantEntryUser != null) {
                merchantEntryRespVO.setUserNo(merchantEntryUser.getUserNo());
                merchantEntryRespVO.setUserNickname(merchantEntryUser.getNickname());
                merchantEntryRespVO.setUserMobile(merchantEntryUser.getMobile());
            }
            respVO.setMerchantEntry(merchantEntryRespVO);
        }
        respVO.setSummary(buildSummary(relatedReports));
        respVO.setRelatedReports(buildRelatedReports(relatedReports, relatedMerchantMap, relatedPartnerMap, relatedCategoryMap));
        return respVO;
    }

    private static MerchantPriceReportDetailRespVO.SummaryRespVO buildSummary(List<MerchantPriceReportDO> relatedReports) {
        MerchantPriceReportDetailRespVO.SummaryRespVO summary = new MerchantPriceReportDetailRespVO.SummaryRespVO();
        if (relatedReports == null || relatedReports.isEmpty()) {
            summary.setTotalRelatedCount(0);
            summary.setPendingCount(0);
            summary.setApprovedCount(0);
            summary.setRejectedCount(0);
            summary.setAvgSuggestedPrice(BigDecimal.ZERO);
            summary.setMinSuggestedPrice(BigDecimal.ZERO);
            summary.setMaxSuggestedPrice(BigDecimal.ZERO);
            return summary;
        }

        List<BigDecimal> prices = relatedReports.stream()
                .map(MerchantPriceReportDO::getSuggestedPrice)
                .filter(item -> item != null)
                .collect(Collectors.toList());
        BigDecimal total = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        summary.setTotalRelatedCount(relatedReports.size());
        summary.setPendingCount((int) relatedReports.stream().filter(item -> "PENDING".equalsIgnoreCase(item.getStatus())).count());
        summary.setApprovedCount((int) relatedReports.stream().filter(item -> "APPROVED".equalsIgnoreCase(item.getStatus())).count());
        summary.setRejectedCount((int) relatedReports.stream().filter(item -> "REJECTED".equalsIgnoreCase(item.getStatus())).count());
        summary.setAvgSuggestedPrice(prices.isEmpty() ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP));
        summary.setMinSuggestedPrice(prices.isEmpty() ? BigDecimal.ZERO : prices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        summary.setMaxSuggestedPrice(prices.isEmpty() ? BigDecimal.ZERO : prices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        return summary;
    }

    private static List<MerchantPriceReportDetailRespVO.RelatedReportRespVO> buildRelatedReports(
            List<MerchantPriceReportDO> relatedReports,
            Map<Long, MerchantInfoDO> relatedMerchantMap,
            Map<Long, PartnerInfoDO> relatedPartnerMap,
            Map<Long, MerchantServiceCategoryDO> relatedCategoryMap) {
        if (relatedReports == null || relatedReports.isEmpty()) {
            return Collections.emptyList();
        }
        return relatedReports.stream()
                .limit(10)
                .map(item -> {
                    MerchantPriceReportDetailRespVO.RelatedReportRespVO respVO =
                            BeanUtils.toBean(item, MerchantPriceReportDetailRespVO.RelatedReportRespVO.class);
                    MerchantInfoDO merchant = relatedMerchantMap == null ? null : relatedMerchantMap.get(item.getMerchantId());
                    if (merchant != null) {
                        respVO.setMerchantName(merchant.getMerchantName());
                    }
                    PartnerInfoDO partner = relatedPartnerMap == null ? null : relatedPartnerMap.get(item.getPartnerId());
                    if (partner != null) {
                        respVO.setPartnerName(partner.getPartnerName());
                    }
                    MerchantServiceCategoryDO category = relatedCategoryMap == null ? null : relatedCategoryMap.get(item.getCategoryId());
                    if (category != null) {
                        respVO.setCategoryName(category.getCategoryName());
                    }
                    return respVO;
                })
                .collect(Collectors.toList());
    }
}
