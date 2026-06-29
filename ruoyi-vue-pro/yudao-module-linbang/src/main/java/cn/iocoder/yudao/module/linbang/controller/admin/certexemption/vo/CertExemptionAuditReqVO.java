package cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 证件豁免审核 Request VO")
@Data
public class CertExemptionAuditReqVO {

    @Schema(description = "豁免申请 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "豁免申请编号不能为空")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;
}
