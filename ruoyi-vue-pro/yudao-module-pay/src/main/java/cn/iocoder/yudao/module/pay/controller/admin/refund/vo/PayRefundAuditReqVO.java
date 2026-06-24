package cn.iocoder.yudao.module.pay.controller.admin.refund.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 退款订单审核 Request VO")
@Data
public class PayRefundAuditReqVO {

    @Schema(description = "退款单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "退款单编号不能为空")
    private Long id;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注", example = "资料齐全，允许原路退款")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "退款资料不足")
    private String rejectReason;

}
