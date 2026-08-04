package cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 证件豁免申请详情 Response VO")
@Data
public class CertExemptionDetailRespVO extends CertExemptionRespVO {

    @Schema(description = "附件文件 ID 列表 JSON")
    private String attachmentFileIdsJson;

    @Schema(description = "审核人", example = "1")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "用户摘要")
    private UserRespVO user;

    @Schema(description = "服务商摘要")
    private MerchantRespVO merchant;

    @Schema(description = "资质摘要")
    private QualificationRespVO qualification;

    @Schema(description = "同用户其他豁免申请")
    private List<RelatedApplyRespVO> relatedApplies;

    @Data
    @Schema(description = "管理后台 - 用户摘要 Response VO")
    public static class UserRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "平台用户业务编号")
        private String userNo;
        @Schema(description = "用户手机号")
        private String mobile;
        @Schema(description = "用户昵称")
        private String nickname;
        @Schema(description = "当前生效角色编码；App 角色专属动作以该角色为准")
        private String currentRoleCode;
        @Schema(description = "用户状态：ENABLE 启用、DISABLE 停用")
        private String status;
        @Schema(description = "最近一次登录时间")
        private LocalDateTime lastLoginTime;
        @Schema(description = "最近一次登录 IP 地址")
        private String lastLoginIp;
    }

    @Data
    @Schema(description = "管理后台 - 服务商摘要 Response VO")
    public static class MerchantRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "平台用户 ID，关联用户档案")
        private Long userId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人姓名")
        private String contactName;
        @Schema(description = "联系人手机号")
        private String contactMobile;
        @Schema(description = "服务商状态：ENABLE 启用、DISABLE 停用")
        private String status;
        @Schema(description = "服务商接单状态：ENABLE 可接单、DISABLE 暂停接单")
        private String acceptStatus;
        @Schema(description = "当前信用分")
        private Integer creditScore;
        @Schema(description = "信用等级编码，按平台信用等级规则展示")
        private String creditLevel;
        @Schema(description = "服务商服务范围说明")
        private String serviceScopeDesc;
    }

    @Data
    @Schema(description = "管理后台 - 资质摘要 Response VO")
    public static class QualificationRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "资质类型，按 lb_qualification_type 字典展示，例如 BUSINESS_LICENSE 营业执照、ELECTRICIAN 电工证、WELDER 焊工证")
        private String qualificationType;
        @Schema(description = "资质名称")
        private String qualificationName;
        @Schema(description = "资质证书编号")
        private String qualificationNo;
        @Schema(description = "文件 ID，关联文件中心文件")
        private Long fileId;
        @Schema(description = "资质有效期开始日期，格式为 yyyy-MM-dd")
        private LocalDate validStartDate;
        @Schema(description = "资质有效期结束日期，格式为 yyyy-MM-dd")
        private LocalDate validEndDate;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "审核驳回原因")
        private String rejectReason;
        @Schema(description = "证件豁免是否启用优先处理权益：true 启用、false 不启用")
        private Boolean priorityEnabled;
    }

    @Data
    @Schema(description = "管理后台 - 关联豁免申请摘要 Response VO")
    public static class RelatedApplyRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "证件豁免类型，按平台证件豁免类型字典展示")
        private String exemptionType;
        @Schema(description = "用户资质 ID，关联用户资质记录")
        private Long qualificationId;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
        private String auditStatus;
        @Schema(description = "业务生效开始时间")
        private LocalDateTime effectiveStartTime;
        @Schema(description = "业务生效结束时间；为空表示长期有效")
        private LocalDateTime effectiveEndTime;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }
}
