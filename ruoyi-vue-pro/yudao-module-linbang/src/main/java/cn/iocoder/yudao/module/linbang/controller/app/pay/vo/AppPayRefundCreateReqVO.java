package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Schema(description = "用户 App - 邻里退款申请 Request VO")
@Data
public class AppPayRefundCreateReqVO {

    @Schema(description = "业务订单 ID；退款只能针对该订单对应的原支付单发起，退款渠道由原支付渠道决定，前端不传退款渠道",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @Schema(description = "单元 ID；为空表示整单退款，传值表示仅申请该订单单元退款", example = "11")
    private Long unitId;

    @Schema(description = "申请退款金额，单位元；整单退款不能超过订单剩余可退金额，单元退款不能超过该单元金额",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "88.00")
    @NotNull(message = "申请退款金额不能为空")
    @DecimalMin(value = "0.01", message = "申请退款金额必须大于 0")
    private BigDecimal applyAmount;

    @Schema(description = "退款原因；审核通过后支付模块按原支付单发起原路退款", requiredMode = Schema.RequiredMode.REQUIRED, example = "服务未按约履行")
    @NotBlank(message = "退款原因不能为空")
    @Size(max = 128, message = "退款原因长度不能超过 128 位")
    private String reason;

}
