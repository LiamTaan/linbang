package cn.iocoder.yudao.module.linbang.service.merchantentry;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo.MerchantEntryDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class MerchantEntryDetailAssembler {

    private MerchantEntryDetailAssembler() {
    }

    static MerchantEntryDetailRespVO build(MerchantEntryDO entry, MemberUserDO applicant, MerchantInfoDO merchant,
                                           MemberUserRealNameDO realName, List<MerchantCategoryRelDO> categoryRels,
                                           Map<Long, MerchantServiceCategoryDO> categoryMap,
                                           List<MemberUserQualificationDO> qualifications,
                                           List<MerchantEntryDO> historyEntries) {
        MerchantEntryDetailRespVO respVO = BeanUtils.toBean(entry, MerchantEntryDetailRespVO.class);
        if (applicant != null) {
            respVO.setApplicant(BeanUtils.toBean(applicant, MerchantEntryDetailRespVO.ApplicantRespVO.class));
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, MerchantEntryDetailRespVO.MerchantRespVO.class));
        }
        if (realName != null) {
            respVO.setRealName(BeanUtils.toBean(realName, MerchantEntryDetailRespVO.RealNameRespVO.class));
        }
        respVO.setSummary(buildSummary(categoryRels, qualifications, historyEntries));
        respVO.setCategories(buildCategories(categoryRels, categoryMap));
        respVO.setQualifications(buildQualifications(qualifications));
        respVO.setHistoryEntries(buildHistoryEntries(historyEntries, entry.getId()));
        return respVO;
    }

    private static MerchantEntryDetailRespVO.SummaryRespVO buildSummary(List<MerchantCategoryRelDO> categoryRels,
                                                                        List<MemberUserQualificationDO> qualifications,
                                                                        List<MerchantEntryDO> historyEntries) {
        List<MemberUserQualificationDO> qualificationSource = qualifications == null ? Collections.emptyList() : qualifications;
        List<MerchantEntryDO> historySource = historyEntries == null ? Collections.emptyList() : historyEntries;
        MerchantEntryDetailRespVO.SummaryRespVO summary = new MerchantEntryDetailRespVO.SummaryRespVO();
        summary.setHistoryEntryCount(historySource.size());
        summary.setApprovedEntryCount((int) historySource.stream().filter(item -> "APPROVED".equalsIgnoreCase(item.getStatus())).count());
        summary.setRejectedEntryCount((int) historySource.stream().filter(item -> "REJECTED".equalsIgnoreCase(item.getStatus())).count());
        summary.setCategoryCount(categoryRels == null ? 0 : categoryRels.size());
        summary.setQualificationCount(qualificationSource.size());
        summary.setApprovedQualificationCount((int) qualificationSource.stream()
                .filter(item -> "APPROVED".equalsIgnoreCase(item.getAuditStatus()))
                .count());
        summary.setBusinessLicenseUploaded(qualificationSource.stream()
                .anyMatch(item -> "BUSINESS_LICENSE".equalsIgnoreCase(item.getQualificationType())));
        summary.setInsuranceUploaded(qualificationSource.stream()
                .anyMatch(item -> "INSURANCE_POLICY".equalsIgnoreCase(item.getQualificationType())));
        return summary;
    }

    private static List<MerchantEntryDetailRespVO.CategoryRespVO> buildCategories(List<MerchantCategoryRelDO> categoryRels,
                                                                                   Map<Long, MerchantServiceCategoryDO> categoryMap) {
        if (categoryRels == null || categoryRels.isEmpty()) {
            return Collections.emptyList();
        }
        return categoryRels.stream().map(rel -> {
            MerchantEntryDetailRespVO.CategoryRespVO item = new MerchantEntryDetailRespVO.CategoryRespVO();
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

    private static List<MerchantEntryDetailRespVO.QualificationRespVO> buildQualifications(List<MemberUserQualificationDO> qualifications) {
        if (qualifications == null || qualifications.isEmpty()) {
            return Collections.emptyList();
        }
        return qualifications.stream()
                .map(item -> BeanUtils.toBean(item, MerchantEntryDetailRespVO.QualificationRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<MerchantEntryDetailRespVO.HistoryEntryRespVO> buildHistoryEntries(List<MerchantEntryDO> historyEntries,
                                                                                           Long currentId) {
        if (historyEntries == null || historyEntries.isEmpty()) {
            return Collections.emptyList();
        }
        return historyEntries.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, MerchantEntryDetailRespVO.HistoryEntryRespVO.class))
                .collect(Collectors.toList());
    }
}
