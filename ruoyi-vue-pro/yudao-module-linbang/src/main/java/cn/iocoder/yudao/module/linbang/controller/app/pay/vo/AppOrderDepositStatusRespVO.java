package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 订单保证金支付状态 Response VO")
@Data
public class AppOrderDepositStatusRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "保证金支付状态：NOT_REQUIRED 无需保证金、UNPAID 待支付、PAID 已支付")
    private String depositPayStatus;

    @Schema(description = "保证金支付单 ID")
    private Long depositPayOrderId;

    @Schema(description = "支付平台状态名称")
    private String payStatusName;

    @Schema(description = "保证金支付完成时间")
    private LocalDateTime depositPaidTime;
}
