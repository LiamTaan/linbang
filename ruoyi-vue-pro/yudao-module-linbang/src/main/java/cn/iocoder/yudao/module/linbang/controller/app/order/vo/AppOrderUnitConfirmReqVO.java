package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 订单单元确认完成 Request VO")
@Data
public class AppOrderUnitConfirmReqVO {

    @Schema(description = "订单单元 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单单元 ID 不能为空")
    private Long unitId;

    @Schema(description = "确认备注")
    private String confirmRemark;
}
