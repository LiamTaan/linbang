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
    public static class UserRespVO {
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
        private Long userId;
        private String merchantName;
        private String contactName;
        private String contactMobile;
        private String status;
        private String acceptStatus;
        private Integer creditScore;
        private String creditLevel;
        private String serviceScopeDesc;
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
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
        private String auditRemark;
        private String rejectReason;
        private Boolean priorityEnabled;
    }

    @Data
    public static class RelatedApplyRespVO {
        private Long id;
        private String exemptionType;
        private Long qualificationId;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
        private String auditStatus;
        private LocalDateTime effectiveStartTime;
        private LocalDateTime effectiveEndTime;
        private LocalDateTime createTime;
    }
}
