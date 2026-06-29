package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 订单保证金信息 Response VO")
@Data
public class AppOrderDepositInfoRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "是否要求先缴纳保证金")
    private Boolean depositRequired;

    @Schema(description = "保证金金额，单位元")
    private BigDecimal depositAmount;

    @Schema(description = "保证金支付状态：NOT_REQUIRED 无需保证金、UNPAID 待支付、PAID 已支付")
    private String depositPayStatus;

    @Schema(description = "保证金支付单 ID")
    private Long depositPayOrderId;

    @Schema(description = "保证金支付完成时间")
    private LocalDateTime depositPaidTime;

    @Schema(description = "订单是否仍可继续支付保证金")
    private Boolean canCreateDepositPayOrder;
}
