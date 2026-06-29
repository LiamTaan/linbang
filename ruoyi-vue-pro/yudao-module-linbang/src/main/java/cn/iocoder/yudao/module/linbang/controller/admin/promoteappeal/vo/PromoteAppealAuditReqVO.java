package cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 推广申诉审核 Request VO")
@Data
public class PromoteAppealAuditReqVO {

    @Schema(description = "申诉 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "申诉 ID 不能为空")
    private Long id;

    @Schema(description = "审核结果：APPROVED 通过、REJECTED 驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "审核结果不能为空")
    private String auditResult;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;
}
