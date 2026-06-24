package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 接单 Response VO")
@Data
public class AppOrderAcceptRespVO {

    @Schema(description = "主订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "单元订单 ID，未拆单场景下可能为空", example = "10")
    private Long unitId;

    @Schema(description = "接单结果，按抢单/接单业务结果字典展示", example = "SUCCESS")
    private String acceptResult;

    @Schema(description = "接单后的订单状态：ACCEPTED 已接单", example = "ACCEPTED")
    private String status;

}
