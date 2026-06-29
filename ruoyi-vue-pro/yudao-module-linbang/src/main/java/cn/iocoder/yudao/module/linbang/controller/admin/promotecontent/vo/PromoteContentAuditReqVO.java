package cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 推广内容人工审核 Request VO")
@Data
public class PromoteContentAuditReqVO {

    @Schema(description = "内容 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "内容 ID 不能为空")
    private Long id;

    @Schema(description = "人工审核结果：APPROVED 通过、REJECTED 驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "审核结果不能为空")
    private String auditResult;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "处罚动作：DEMOTE/RESTRICT_PROMOTE/DISABLE_PROMOTER/SCORE")
    private String penaltyAction;

    @Schema(description = "扣分值，动作为 SCORE 时使用")
    private Integer scoreChange;

    @Schema(description = "处罚原因")
    private String penaltyReason;
}
