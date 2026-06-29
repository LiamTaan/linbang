package cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 异常订单终审 Request VO")
@Data
public class OrderAbnormalFinalAuditReqVO {

    @Schema(description = "异常单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "异常单编号不能为空")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.ORDER_ABNORMAL_FINAL_AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "终审结果不能为空")
    private String finalAuditStatus;

    @Schema(description = "终审意见", example = "已核实为真实异常，维持异常处理")
    @NotBlank(message = "终审意见不能为空")
    private String finalAuditRemark;
}
