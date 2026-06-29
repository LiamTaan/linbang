package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = "单元类型：DIRECT 直单、AUTO_SPLIT 自动拆分")
    private String unitType;

    @Schema(description = "单元金额")
    private BigDecimal unitAmount;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "DIRECT")
    private String splitMode;

    @Schema(description = "单元内容摘要")
    private String unitContent;

    @Schema(description = "单元进度描述，例如 1/3")
    private String unitProgress;

    @Schema(description = "单元服务人数")
    private Integer workerCount;

    @Schema(description = "单元金额上限快照，单位元")
    private BigDecimal maxAmountLimit;

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

    @Schema(description = "派单状态", example = "PUSHING")
    private String dispatchStatus;

    @Schema(description = "当前批次号", example = "1")
    private Integer currentBatchNo;

    @Schema(description = "流单时间")
    private LocalDateTime flowTime;

    @Schema(description = "流单原因")
    private String flowReason;

    @Schema(description = "自动退款状态", example = "SUCCESS")
    private String autoRefundStatus;

    @Schema(description = "自动退款单 ID")
    private Long autoRefundId;

    @Schema(description = "完成时间")
    private LocalDateTime finishTime;

    @Schema(description = "申诉截止时间")
    private LocalDateTime appealExpireTime;

    @Schema(description = "当前是否仍在申诉期")
    private Boolean appealAvailable;

    @Schema(description = OpenApiSchemaConstants.ORDER_VERIFY_STATUS, example = "PENDING")
    private String verifyStatus;

    @Schema(description = "核销码，仅下单用户查看时返回")
    private String verifyCode;

    @Schema(description = "核销时间")
    private LocalDateTime verifyTime;

    @Schema(description = "核销人用户 ID")
    private Long verifyBy;

    @Schema(description = "核销备注")
    private String verifyRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
