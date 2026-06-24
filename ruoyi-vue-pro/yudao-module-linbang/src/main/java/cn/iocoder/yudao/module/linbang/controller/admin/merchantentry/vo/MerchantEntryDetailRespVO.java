package cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 服务商入驻详情 Response VO")
@Data
public class MerchantEntryDetailRespVO {

    private Long id;
    private Long merchantId;
    private Long userId;
    private String entryNo;
    private String regionCode;
    private String firstAuditStatus;
    private Long firstAuditBy;
    private LocalDateTime firstAuditTime;
    private String finalAuditStatus;
    private Long finalAuditBy;
    private LocalDateTime finalAuditTime;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private ApplicantRespVO applicant;
    private MerchantRespVO merchant;
    private RealNameRespVO realName;
    private SummaryRespVO summary;
    private List<CategoryRespVO> categories;
    private List<QualificationRespVO> qualifications;
    private List<HistoryEntryRespVO> historyEntries;

    @Data
    public static class ApplicantRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
        private LocalDateTime lastLoginTime;
        private String lastLoginIp;
    }

    @Data
    public static class MerchantRespVO {
        private Long id;
        private String merchantName;
        private String contactName;
        private String contactMobile;
        private String serviceScopeDesc;
        private String status;
        private String acceptStatus;
        private Integer creditScore;
        private String creditLevel;
    }

    @Data
    public static class RealNameRespVO {
        private Long id;
        private String realName;
        private String idCardNo;
        private String auditStatus;
        private String auditRemark;
        private Long auditBy;
        private LocalDateTime auditTime;
        private String rejectReason;
    }

    @Data
    public static class SummaryRespVO {
        private Integer historyEntryCount;
        private Integer approvedEntryCount;
        private Integer rejectedEntryCount;
        private Integer categoryCount;
        private Integer qualificationCount;
        private Integer approvedQualificationCount;
    }

    @Data
    public static class CategoryRespVO {
        private Long categoryId;
        private String categoryName;
        private Long parentId;
        private Integer categoryLevel;
        private String defaultPricingMode;
        private Boolean supportSplit;
        private Boolean supportInvoice;
    }

    @Data
    public static class QualificationRespVO {
        private Long id;
        private String qualificationType;
        private String qualificationName;
        private String qualificationNo;
        private Long fileId;
        private LocalDate validStartDate;
        private LocalDate validEndDate;
        private String auditStatus;
        private String auditRemark;
        private Long auditBy;
        private LocalDateTime auditTime;
        private String rejectReason;
        private LocalDateTime createTime;
    }

    @Data
    public static class HistoryEntryRespVO {
        private Long id;
        private String entryNo;
        private String regionCode;
        private String firstAuditStatus;
        private String finalAuditStatus;
        private String status;
        private String remark;
        private LocalDateTime createTime;
    }
}
