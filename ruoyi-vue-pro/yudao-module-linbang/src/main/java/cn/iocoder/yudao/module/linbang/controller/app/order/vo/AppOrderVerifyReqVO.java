package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 订单单元核销 Request VO")
@Data
public class AppOrderVerifyReqVO {

    @Schema(description = "订单单元 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单单元 ID 不能为空")
    private Long unitId;

    @Schema(description = "核销码", requiredMode = Schema.RequiredMode.REQUIRED, example = "824613")
    @NotBlank(message = "核销码不能为空")
    private String verifyCode;

    @Schema(description = OpenApiSchemaConstants.ORDER_VERIFY_METHOD, requiredMode = Schema.RequiredMode.REQUIRED, example = "CODE")
    @NotBlank(message = "核销方式不能为空")
    private String verifyMethod;

    @Schema(description = "核销备注", example = "上门服务完成后核销")
    private String verifyRemark;
}
