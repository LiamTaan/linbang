package cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 用户资质详情 Response VO")
@Data
public class MemberQualificationDetailRespVO {

    @Schema(description = "资质 ID", example = "1")
    private Long id;
    @Schema(description = "用户 ID", example = "1001")
    private Long userId;
    @Schema(description = OpenApiSchemaConstants.QUALIFICATION_TYPE, example = "ELECTRICIAN")
    private String qualificationType;
    @Schema(description = "资质名称", example = "电工证")
    private String qualificationName;
    @Schema(description = "资质编号", example = "CERT-2026-001")
    private String qualificationNo;
    @Schema(description = "文件 ID", example = "11")
    private Long fileId;
    @Schema(description = "有效开始日期")
    private LocalDate validStartDate;
    @Schema(description = "有效结束日期")
    private LocalDate validEndDate;
    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;
    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "审核人", example = "1")
    private Long auditBy;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    @Schema(description = "驳回原因")
    private String rejectReason;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "用户摘要")
    private UserRespVO user;
    @Schema(description = "实名认证摘要")
    private RealNameRespVO realName;
    @Schema(description = "服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "最近入驻申请摘要")
    private LatestEntryRespVO latestEntry;
    @Schema(description = "统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "同用户关联资质")
    private List<RelatedQualificationRespVO> relatedQualifications;
    @Schema(description = "关联证件豁免申请")
    private List<CertExemptionRespVO> certExemptions;
    @Schema(description = "信用记录")
    private List<CreditRecordRespVO> creditRecords;

    @Schema(description = "用户摘要")
    @Data
    public static class UserRespVO {
        @Schema(description = "用户 ID", example = "1001")
        private Long id;
        @Schema(description = "用户编号")
        private String userNo;
        @Schema(description = "手机号")
        private String mobile;
        @Schema(description = "昵称")
        private String nickname;
        @Schema(description = "当前角色编码", example = "MERCHANT")
        private String currentRoleCode;
        @Schema(description = OpenApiSchemaConstants.USER_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "最近登录时间")
        private LocalDateTime lastLoginTime;
        @Schema(description = "最近登录 IP")
        private String lastLoginIp;
    }

    @Schema(description = "实名认证摘要")
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
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "审核人", example = "1")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "驳回原因")
        private String rejectReason;
    }

    @Schema(description = "服务商摘要")
    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "2001")
        private Long id;
        @Schema(description = "关联用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人")
        private String contactName;
        @Schema(description = "联系电话")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分")
        private Integer creditScore;
        @Schema(description = "信用等级")
        private String creditLevel;
        @Schema(description = "服务范围说明")
        private String serviceScopeDesc;
    }

    @Schema(description = "最近入驻申请摘要")
    @Data
    public static class LatestEntryRespVO {
        @Schema(description = "入驻申请 ID", example = "1")
        private Long id;
        @Schema(description = "入驻单号")
        private String entryNo;
        @Schema(description = "区域编码")
        private String regionCode;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String firstAuditStatus;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String finalAuditStatus;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "APPROVED")
        private String status;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "统计摘要")
    @Data
    public static class SummaryRespVO {
        @Schema(description = "同用户资质总数")
        private Integer sameUserQualificationCount;
        @Schema(description = "已通过资质数")
        private Integer approvedQualificationCount;
        @Schema(description = "已驳回资质数")
        private Integer rejectedQualificationCount;
        @Schema(description = "即将过期资质数")
        private Integer expiringSoonCount;
        @Schema(description = "信用记录数")
        private Integer creditRecordCount;
        @Schema(description = "最新信用分")
        private Integer latestCreditScore;
        @Schema(description = "最新信用等级")
        private String latestCreditLevel;
        @Schema(description = "实名认证是否已通过")
        private Boolean realNameApproved;
        @Schema(description = "是否已绑定服务商")
        private Boolean merchantBound;
        @Schema(description = "已通过豁免数量")
        private Integer approvedExemptionCount;
    }

    @Schema(description = "证件豁免申请摘要")
    @Data
    public static class CertExemptionRespVO {
        @Schema(description = "豁免申请 ID", example = "1")
        private Long id;
        @Schema(description = "豁免类型")
        private String exemptionType;
        @Schema(description = "申请原因")
        private String reason;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
        private String auditStatus;
        @Schema(description = "生效开始时间")
        private LocalDateTime effectiveStartTime;
        @Schema(description = "生效结束时间")
        private LocalDateTime effectiveEndTime;
        @Schema(description = "驳回原因")
        private String rejectReason;
    }

    @Schema(description = "关联资质摘要")
    @Data
    public static class RelatedQualificationRespVO {
        @Schema(description = "资质 ID", example = "2")
        private Long id;
        @Schema(description = OpenApiSchemaConstants.QUALIFICATION_TYPE, example = "ELECTRICIAN")
        private String qualificationType;
        @Schema(description = "资质名称", example = "电工证")
        private String qualificationName;
        @Schema(description = "资质编号")
        private String qualificationNo;
        @Schema(description = "文件 ID", example = "12")
        private Long fileId;
        @Schema(description = "有效开始日期")
        private LocalDate validStartDate;
        @Schema(description = "有效结束日期")
        private LocalDate validEndDate;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "审核人", example = "1")
        private Long auditBy;
        @Schema(description = "审核时间")
        private LocalDateTime auditTime;
        @Schema(description = "驳回原因")
        private String rejectReason;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Schema(description = "信用记录摘要")
    @Data
    public static class CreditRecordRespVO {
        @Schema(description = "记录 ID", example = "1")
        private Long id;
        @Schema(description = "规则编码")
        private String ruleCode;
        @Schema(description = "规则名称")
        private String ruleName;
        @Schema(description = "分值变动")
        private Integer scoreChange;
        @Schema(description = "变动前分值")
        private Integer beforeScore;
        @Schema(description = "变动后分值")
        private Integer afterScore;
        @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
        private String triggerType;
        @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "ORDER")
        private String bizType;
        @Schema(description = "业务 ID，关联订单、单元、投诉、申诉或提现等业务主键", example = "1001")
        private Long bizId;
        @Schema(description = "备注，记录加减分原因说明")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
