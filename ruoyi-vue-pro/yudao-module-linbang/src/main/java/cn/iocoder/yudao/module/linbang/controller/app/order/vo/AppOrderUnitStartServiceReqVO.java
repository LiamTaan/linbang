package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 订单单元开始服务 Request VO")
@Data
public class AppOrderUnitStartServiceReqVO {

    @Schema(description = "订单单元 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "订单单元 ID 不能为空")
    private Long unitId;

    @Schema(description = "开始服务备注")
    private String startRemark;
}
