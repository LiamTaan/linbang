package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 待接单需求分页项 Response VO")
@Data
public class AppOrderAcceptPageItemRespVO {

    @Schema(description = "主订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "当前可接单元 ID", example = "10")
    private Long unitId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "服务类目 ID")
    private Long categoryId;

    @Schema(description = "服务类目名称")
    private String categoryName;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "HOURLY")
    private String pricingMode;

    @Schema(description = "需求描述，列表卡片标题来源")
    private String requireDesc;

    @Schema(description = "订单金额，单位元")
    private BigDecimal orderAmount;

    @Schema(description = "服务时长/工期说明，例如 1小时、半天、今天内、3天内上门")
    private String serviceDurationDesc;

    @Schema(description = "距离最近服务点的距离，单位公里")
    private BigDecimal distanceKm;

    @Schema(description = "主订单状态，待接单大厅默认仅返回 PENDING_ACCEPT")
    private String status;

    @Schema(description = "派单状态", example = "PUSHING")
    private String dispatchStatus;

    @Schema(description = "当前阶段号", example = "1")
    private Integer stageNo;

    @Schema(description = "当前推送批次号", example = "1")
    private Integer pushBatchNo;

    @Schema(description = "剩余倒计时秒数", example = "52")
    private Integer countdownSeconds;

    @Schema(description = "优先层", example = "PLATFORM_CLOTHING")
    private String priorityLayer;

    @Schema(description = "是否优先池命中")
    private Boolean priorityPoolFlag;

    @Schema(description = "品类命中等级", example = "EXACT")
    private String categoryMatchLevel;

    @Schema(description = "接单截止时间")
    private LocalDateTime acceptDeadlineTime;

    @Schema(description = "发布时间")
    private LocalDateTime createTime;
}
