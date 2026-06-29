package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 订单单元核销码 Response VO")
@Data
public class AppOrderVerifyCodeRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "订单单元 ID", example = "11")
    private Long unitId;

    @Schema(description = "订单单元号", example = "LBU20260001")
    private String unitNo;

    @Schema(description = "核销码", example = "824613")
    private String verifyCode;

    @Schema(description = OpenApiSchemaConstants.ORDER_VERIFY_STATUS, example = "PENDING")
    private String verifyStatus;

    @Schema(description = "是否允许核销", example = "true")
    private Boolean verifyAllowed;

    @Schema(description = "核销时间")
    private LocalDateTime verifyTime;

    @Schema(description = "核销人用户 ID", example = "1001")
    private Long verifyBy;

    @Schema(description = "核销备注")
    private String verifyRemark;
}
