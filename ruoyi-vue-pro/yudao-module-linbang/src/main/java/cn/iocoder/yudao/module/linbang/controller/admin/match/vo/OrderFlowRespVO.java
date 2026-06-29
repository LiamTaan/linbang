package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 流单退款看板 Response VO")
@Data
public class OrderFlowRespVO {

    @Schema(description = "单元 ID")
    private Long unitId;

    @Schema(description = "订单 ID")
    private Long orderId;

    @Schema(description = "单元金额")
    private BigDecimal unitAmount;

    @Schema(description = "派单状态")
    private String dispatchStatus;

    @Schema(description = "当前批次号")
    private Integer currentBatchNo;

    @Schema(description = "流单时间")
    private LocalDateTime flowTime;

    @Schema(description = "流单原因")
    private String flowReason;

    @Schema(description = "自动退款状态")
    private String autoRefundStatus;

    @Schema(description = "自动退款单 ID")
    private Long autoRefundId;
}
