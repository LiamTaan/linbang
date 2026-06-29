package cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 实名认证审核 Request VO")
@Data
public class MemberUserRealNameAuditReqVO {

    @Schema(description = "实名认证编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "实名认证编号不能为空")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注", example = "信息核验通过")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "证件信息不一致")
    private String rejectReason;

}
