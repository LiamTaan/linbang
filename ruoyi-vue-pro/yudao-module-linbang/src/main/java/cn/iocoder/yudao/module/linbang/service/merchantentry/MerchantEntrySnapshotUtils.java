package cn.iocoder.yudao.module.linbang.service.merchantentry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class MerchantEntrySnapshotUtils {

    private MerchantEntrySnapshotUtils() {
    }

    public static MerchantEntrySnapshot buildSnapshot(String merchantName, String contactName, String contactMobile,
                                                      String serviceScopeDesc, MemberUserRealNameDO realName,
                                                      List<MerchantServiceCategoryDO> categories,
                                                      List<MemberUserQualificationDO> qualifications) {
        MerchantEntrySnapshot snapshot = new MerchantEntrySnapshot();
        snapshot.setMerchantName(merchantName);
        snapshot.setContactName(contactName);
        snapshot.setContactMobile(contactMobile);
        snapshot.setServiceScopeDesc(serviceScopeDesc);
        snapshot.setApplicantRealName(realName == null ? null : realName.getRealName());
        snapshot.setCategories(CollUtil.isEmpty(categories) ? Collections.emptyList() : categories.stream().map(item -> {
            CategorySnapshot category = new CategorySnapshot();
            category.setCategoryId(item.getId());
            category.setCategoryName(item.getCategoryName());
            category.setParentId(item.getParentId());
            category.setCategoryLevel(item.getCategoryLevel());
            category.setDefaultPricingMode(item.getDefaultPricingMode());
            category.setSupportSplit(item.getSupportSplit());
            category.setSupportInvoice(item.getSupportInvoice());
            return category;
        }).collect(Collectors.toList()));
        snapshot.setQualifications(CollUtil.isEmpty(qualifications) ? Collections.emptyList() : qualifications.stream().map(item -> {
            QualificationSnapshot qualification = new QualificationSnapshot();
            qualification.setId(item.getId());
            qualification.setQualificationType(item.getQualificationType());
            qualification.setQualificationName(item.getQualificationName());
            qualification.setQualificationNo(item.getQualificationNo());
            qualification.setFileId(item.getFileId());
            qualification.setValidStartDate(item.getValidStartDate());
            qualification.setValidEndDate(item.getValidEndDate());
            qualification.setAuditStatus(item.getAuditStatus());
            qualification.setAuditRemark(item.getAuditRemark());
            qualification.setRejectReason(item.getRejectReason());
            return qualification;
        }).collect(Collectors.toList()));
        return snapshot;
    }

    public static MerchantEntrySnapshot parseSnapshot(MerchantEntryDO entry) {
        if (entry == null) {
            return null;
        }
        boolean hasBaseSnapshot = StrUtil.isNotBlank(entry.getMerchantNameSnapshot())
                || StrUtil.isNotBlank(entry.getContactNameSnapshot())
                || StrUtil.isNotBlank(entry.getContactMobileSnapshot())
                || StrUtil.isNotBlank(entry.getServiceScopeDescSnapshot())
                || StrUtil.isNotBlank(entry.getApplicantRealNameSnapshot())
                || StrUtil.isNotBlank(entry.getCategorySnapshotJson())
                || StrUtil.isNotBlank(entry.getQualificationSnapshotJson());
        if (!hasBaseSnapshot) {
            return null;
        }
        MerchantEntrySnapshot snapshot = new MerchantEntrySnapshot();
        snapshot.setMerchantName(entry.getMerchantNameSnapshot());
        snapshot.setContactName(entry.getContactNameSnapshot());
        snapshot.setContactMobile(entry.getContactMobileSnapshot());
        snapshot.setServiceScopeDesc(entry.getServiceScopeDescSnapshot());
        snapshot.setApplicantRealName(entry.getApplicantRealNameSnapshot());
        snapshot.setCategories(parseCategorySnapshot(entry.getCategorySnapshotJson()));
        snapshot.setQualifications(parseQualificationSnapshot(entry.getQualificationSnapshotJson()));
        return snapshot;
    }

    public static List<CategorySnapshot> parseCategorySnapshot(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyList();
        }
        List<CategorySnapshot> result = JsonUtils.parseObjectQuietly(json, new TypeReference<List<CategorySnapshot>>() {
        });
        return result == null ? Collections.emptyList() : result;
    }

    public static List<QualificationSnapshot> parseQualificationSnapshot(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyList();
        }
        List<QualificationSnapshot> result = JsonUtils.parseObjectQuietly(json, new TypeReference<List<QualificationSnapshot>>() {
        });
        return result == null ? Collections.emptyList() : result;
    }

    public static String toCategorySnapshotJson(MerchantEntrySnapshot snapshot) {
        return snapshot == null || snapshot.getCategories() == null ? null : JsonUtils.toJsonString(snapshot.getCategories());
    }

    public static String toQualificationSnapshotJson(MerchantEntrySnapshot snapshot) {
        return snapshot == null || snapshot.getQualifications() == null ? null : JsonUtils.toJsonString(snapshot.getQualifications());
    }

    public static List<Long> extractCategoryIds(MerchantEntrySnapshot snapshot) {
        if (snapshot == null || CollUtil.isEmpty(snapshot.getCategories())) {
            return Collections.emptyList();
        }
        return snapshot.getCategories().stream()
                .map(CategorySnapshot::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Long> extractQualificationIds(MerchantEntrySnapshot snapshot) {
        if (snapshot == null || CollUtil.isEmpty(snapshot.getQualifications())) {
            return Collections.emptyList();
        }
        return snapshot.getQualifications().stream()
                .map(QualificationSnapshot::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Data
    public static class MerchantEntrySnapshot {
        private String merchantName;
        private String contactName;
        private String contactMobile;
        private String serviceScopeDesc;
        private String applicantRealName;
        private List<CategorySnapshot> categories;
        private List<QualificationSnapshot> qualifications;
    }

    @Data
    public static class CategorySnapshot {
        private Long categoryId;
        private String categoryName;
        private Long parentId;
        private Integer categoryLevel;
        private String defaultPricingMode;
        private Boolean supportSplit;
        private Boolean supportInvoice;
    }

    @Data
    public static class QualificationSnapshot {
        private Long id;
        private String qualificationType;
        private String qualificationName;
        private String qualificationNo;
        private Long fileId;
        private LocalDate validStartDate;
        private LocalDate validEndDate;
        private String auditStatus;
        private String auditRemark;
        private String rejectReason;
    }
}
