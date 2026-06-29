package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 订单核销日志 Response VO")
@Data
public class AppOrderVerifyLogRespVO {

    @Schema(description = "日志 ID", example = "1")
    private Long id;

    @Schema(description = "订单单元 ID", example = "11")
    private Long unitId;

    @Schema(description = "核销人用户 ID", example = "1001")
    private Long verifyBy;

    @Schema(description = OpenApiSchemaConstants.ORDER_VERIFY_METHOD, example = "CODE")
    private String verifyMethod;

    @Schema(description = "核销前状态", example = "PENDING_CONFIRM")
    private String beforeStatus;

    @Schema(description = "核销后状态", example = "FINISHED")
    private String afterStatus;

    @Schema(description = "核销备注")
    private String remark;

    @Schema(description = "核销时间")
    private LocalDateTime verifyTime;
}
