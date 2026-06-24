package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 取消订单 Request VO")
@Data
public class AppOrderCancelReqVO {

    @Schema(description = "订单 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @Schema(description = "取消原因")
    private String cancelReason;
}
