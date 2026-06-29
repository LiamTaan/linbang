package cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 服务商入驻详情 Response VO")
@Data
public class MerchantEntryDetailRespVO {

    @Schema(description = "入驻申请 ID", example = "1")
    private Long id;
    @Schema(description = "服务商 ID", example = "2001")
    private Long merchantId;
    @Schema(description = "申请用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "入驻单号", example = "LBE202606260001")
    private String entryNo;
    @Schema(description = "入驻区域编码", example = "310115")
    private String regionCode;
    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String firstAuditStatus;
    @Schema(description = "初审人 ID", example = "1")
    private Long firstAuditBy;
    @Schema(description = "初审时间")
    private LocalDateTime firstAuditTime;
    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String finalAuditStatus;
    @Schema(description = "终审人 ID", example = "1")
    private Long finalAuditBy;
    @Schema(description = "终审时间")
    private LocalDateTime finalAuditTime;
    @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "APPROVED")
    private String status;
    @Schema(description = "入驻进度状态", example = "APPROVED_WAIT_BANK_CARD")
    private String progressStatus;
    @Schema(description = "当前阶段名称")
    private String currentStageName;
    @Schema(description = "当前阶段时间")
    private LocalDateTime currentStageTime;
    @Schema(description = "驳回原因")
    private String rejectReason;
    @Schema(description = "当前阻塞原因")
    private String onboardingBlockedReason;
    @Schema(description = "是否已开通接单")
    private Boolean acceptEnabled;
    @Schema(description = "是否必须先绑卡才可接单")
    private Boolean bankCardRequired;
    @Schema(description = "审核备注", example = "资料齐全，允许入驻")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "申请人摘要")
    private ApplicantRespVO applicant;
    @Schema(description = "服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "实名认证摘要")
    private RealNameRespVO realName;
    @Schema(description = "统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "申请服务类目")
    private List<CategoryRespVO> categories;
    @Schema(description = "申请资质")
    private List<QualificationRespVO> qualifications;
    @Schema(description = "历史入驻记录")
    private List<HistoryEntryRespVO> historyEntries;

    @Data
    public static class ApplicantRespVO {
        @Schema(description = "用户 ID", example = "1001")
        private Long id;
        @Schema(description = "用户编号", example = "LBU123456")
        private String userNo;
        @Schema(description = "手机号", example = "13800138000")
        private String mobile;
        @Schema(description = "昵称", example = "邻里用户")
        private String nickname;
        @Schema(description = "当前角色编码", example = "USER")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
        @Schema(description = "最近登录 IP", example = "127.0.0.1")
        private String lastLoginIp;
    }

    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "2001")
        private Long id;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "联系人", example = "李师傅")
        private String contactName;
        @Schema(description = "联系手机", example = "13800138001")
        private String contactMobile;
        @Schema(description = "服务范围说明", example = "覆盖浦东新区 15 公里内上门服务")
        private String serviceScopeDesc;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分", example = "100")
        private Integer creditScore;
        @Schema(description = "信用等级", example = "NORMAL")
        private String creditLevel;
    }

    @Data
    public static class RealNameRespVO {
        @Schema(description = "实名认证 ID", example = "1")
        private Long id;
        @Schema(description = "真实姓名", example = "张三")
        private String realName;
        @Schema(description = "身份证号")
        private String idCardNo;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
        @Schema(description = "审核备注", example = "实名认证通过")
        private String auditRemark;
        @Schema(description = "审核人 ID", example = "1")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "驳回原因", example = "身份证照片不清晰")
        private String rejectReason;
    }

    @Data
    public static class SummaryRespVO {
        @Schema(description = "历史入驻次数")
        private Integer historyEntryCount;
        @Schema(description = "通过次数")
        private Integer approvedEntryCount;
        @Schema(description = "驳回次数")
        private Integer rejectedEntryCount;
        @Schema(description = "申请类目数")
        private Integer categoryCount;
        @Schema(description = "申请资质数")
        private Integer qualificationCount;
        @Schema(description = "已通过资质数")
        private Integer approvedQualificationCount;
        @Schema(description = "是否存在营业执照")
        private Boolean businessLicenseUploaded;
        @Schema(description = "是否存在保险凭证")
        private Boolean insuranceUploaded;
    }

    @Data
    public static class CategoryRespVO {
        @Schema(description = "类目 ID", example = "340201")
        private Long categoryId;
        @Schema(description = "类目名称", example = "家电清洗")
        private String categoryName;
        @Schema(description = "父级类目 ID", example = "340200")
        private Long parentId;
        @Schema(description = "类目层级", example = "2")
        private Integer categoryLevel;
        @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
        private String defaultPricingMode;
        @Schema(description = "是否支持拆单")
        private Boolean supportSplit;
        @Schema(description = "是否支持开票")
        private Boolean supportInvoice;
    }

    @Data
    public static class QualificationRespVO {
        @Schema(description = "资质 ID", example = "1")
        private Long id;
        @Schema(description = "资质类型", example = "BUSINESS_LICENSE")
        private String qualificationType;
        @Schema(description = "资质名称", example = "营业执照")
        private String qualificationName;
        @Schema(description = "资质编号")
        private String qualificationNo;
        @Schema(description = "资质文件 ID", example = "9001")
        private Long fileId;
        @Schema(description = "有效开始日期")
        private LocalDate validStartDate;
        @Schema(description = "有效截止日期")
        private LocalDate validEndDate;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
        @Schema(description = "审核备注", example = "资质有效")
        private String auditRemark;
        @Schema(description = "审核人 ID", example = "1")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "驳回原因", example = "资质已过期")
        private String rejectReason;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class HistoryEntryRespVO {
        @Schema(description = "历史入驻记录 ID", example = "1")
        private Long id;
        @Schema(description = "入驻单号", example = "LBE202606260001")
        private String entryNo;
        @Schema(description = "区域编码", example = "310115")
        private String regionCode;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String firstAuditStatus;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String finalAuditStatus;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "APPROVED")
        private String status;
        @Schema(description = "备注", example = "资料齐全，允许入驻")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
