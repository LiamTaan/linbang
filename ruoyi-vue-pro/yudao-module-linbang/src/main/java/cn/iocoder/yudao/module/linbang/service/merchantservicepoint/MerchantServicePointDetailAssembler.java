package cn.iocoder.yudao.module.linbang.service.merchantservicepoint;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

final class MerchantServicePointDetailAssembler {

    private MerchantServicePointDetailAssembler() {
    }

    static MerchantServicePointDetailRespVO build(MerchantServicePointDO point, MerchantInfoDO merchant, MerchantEntryDO latestEntry,
                                                  List<MerchantServicePointDO> merchantPoints, List<MerchantCategoryRelDO> categoryRels,
                                                  Map<Long, MerchantServiceCategoryDO> categoryMap) {
        MerchantServicePointDetailRespVO respVO = BeanUtils.toBean(point, MerchantServicePointDetailRespVO.class);
        respVO.setMerchant(buildMerchant(merchant, latestEntry));
        respVO.setSummary(buildSummary(point, merchantPoints, categoryRels));
        respVO.setCategories(buildCategories(categoryRels, categoryMap));
        respVO.setRelatedPoints(buildRelatedPoints(merchantPoints, point.getId()));
        return respVO;
    }

    private static MerchantServicePointDetailRespVO.MerchantRespVO buildMerchant(MerchantInfoDO merchant, MerchantEntryDO latestEntry) {
        if (merchant == null) {
            return null;
        }
        MerchantServicePointDetailRespVO.MerchantRespVO respVO =
                BeanUtils.toBean(merchant, MerchantServicePointDetailRespVO.MerchantRespVO.class);
        if (latestEntry != null) {
            respVO.setLatestEntryId(latestEntry.getId());
            respVO.setLatestEntryNo(latestEntry.getEntryNo());
            respVO.setLatestEntryStatus(latestEntry.getStatus());
            respVO.setLatestRegionCode(latestEntry.getRegionCode());
        }
        return respVO;
    }

    private static MerchantServicePointDetailRespVO.SummaryRespVO buildSummary(MerchantServicePointDO point,
                                                                                List<MerchantServicePointDO> merchantPoints,
                                                                                List<MerchantCategoryRelDO> categoryRels) {
        List<MerchantServicePointDO> source = merchantPoints == null ? Collections.emptyList() : merchantPoints;
        MerchantServicePointDetailRespVO.SummaryRespVO summary = new MerchantServicePointDetailRespVO.SummaryRespVO();
        summary.setServicePointCount(source.size());
        summary.setEnabledServicePointCount((int) source.stream()
                .filter(item -> "ENABLE".equalsIgnoreCase(item.getStatus()))
                .count());
        summary.setDisabledServicePointCount((int) source.stream()
                .filter(item -> !"ENABLE".equalsIgnoreCase(item.getStatus()))
                .count());
        summary.setSameDistrictPointCount((int) source.stream()
                .filter(item -> Objects.equals(item.getDistrict(), point.getDistrict()))
                .count());
        summary.setSameCityPointCount((int) source.stream()
                .filter(item -> Objects.equals(item.getCity(), point.getCity()))
                .count());
        summary.setCategoryCount(categoryRels == null ? 0 : categoryRels.size());
        return summary;
    }

    private static List<MerchantServicePointDetailRespVO.CategoryRespVO> buildCategories(List<MerchantCategoryRelDO> categoryRels,
                                                                                          Map<Long, MerchantServiceCategoryDO> categoryMap) {
        if (categoryRels == null || categoryRels.isEmpty()) {
            return Collections.emptyList();
        }
        return categoryRels.stream().map(rel -> {
            MerchantServicePointDetailRespVO.CategoryRespVO item = new MerchantServicePointDetailRespVO.CategoryRespVO();
            item.setCategoryId(rel.getCategoryId());
            MerchantServiceCategoryDO category = categoryMap == null ? null : categoryMap.get(rel.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getCategoryName());
                item.setParentId(category.getParentId());
                item.setCategoryLevel(category.getCategoryLevel());
                item.setDefaultPricingMode(category.getDefaultPricingMode());
                item.setSupportSplit(category.getSupportSplit());
                item.setSupportInvoice(category.getSupportInvoice());
            }
            return item;
        }).collect(Collectors.toList());
    }

    private static List<MerchantServicePointDetailRespVO.RelatedPointRespVO> buildRelatedPoints(List<MerchantServicePointDO> merchantPoints,
                                                                                                 Long currentId) {
        if (merchantPoints == null || merchantPoints.isEmpty()) {
            return Collections.emptyList();
        }
        return merchantPoints.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MerchantServicePointDetailRespVO.RelatedPointRespVO.class))
                .collect(Collectors.toList());
    }
}
