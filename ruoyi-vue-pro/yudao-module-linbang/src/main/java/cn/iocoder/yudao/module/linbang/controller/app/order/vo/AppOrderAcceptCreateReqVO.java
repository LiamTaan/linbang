package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 接单 Request VO")
@Data
public class AppOrderAcceptCreateReqVO {

    @Schema(description = "订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单不能为空")
    private Long orderId;

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "是否确认防逃单提醒。接单前必须传 true", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "请确认防逃单提醒")
    private Boolean antiEscapeConfirmed;

}
