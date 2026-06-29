package cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 身份申请审核 Request VO")
@Data
public class MemberRoleApplyAuditReqVO {

    @Schema(description = "申请编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "申请编号不能为空")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.ROLE_APPLY_AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注", example = "资料完整")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "角色说明不足")
    private String rejectReason;
}
