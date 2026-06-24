package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 订单单元 Response VO")
@Data
public class AppOrderUnitRespVO {

    @Schema(description = "订单单元 ID", example = "1")
    private Long id;

    @Schema(description = "主订单 ID")
    private Long orderId;

    @Schema(description = "单元订单号")
    private String unitNo;

    @Schema(description = "单元序号，从 1 开始")
    private Integer unitSeq;

    @Schema(description = "单元标题")
    private String unitTitle;

    @Schema(description = "单元金额")
    private BigDecimal unitAmount;

    @Schema(description = "拆分模式：DIRECT 直接单、BY_AMOUNT 按金额拆分")
    private String splitMode;

    @Schema(description = "前置单元 ID，没有前置单元时为空")
    private Long prevUnitId;

    @Schema(description = "是否锁定，锁定后通常需等待前置单元完成或人工解锁")
    private Boolean isLocked;

    @Schema(description = "锁定原因")
    private String lockReason;

    @Schema(description = "当前接单服务商 ID")
    private Long merchantId;

    @Schema(description = "单元状态：PENDING_CREATE 待生成、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待验收、FINISHED 已完成、APPEALING 申诉中、REFUNDED 已退款、CLOSED 已关闭")
    private String status;

    @Schema(description = "接单截止时间")
    private LocalDateTime acceptDeadlineTime;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
