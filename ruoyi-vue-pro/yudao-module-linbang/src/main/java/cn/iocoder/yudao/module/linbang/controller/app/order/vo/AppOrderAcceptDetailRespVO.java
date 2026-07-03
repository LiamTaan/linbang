package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 抢单详情 Response VO")
@Data
public class AppOrderAcceptDetailRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long orderId;

    @Schema(description = "订单单元 ID", example = "1")
    private Long unitId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "发布用户 ID")
    private Long userId;

    @Schema(description = "当前接单服务商 ID")
    private Long merchantId;

    @Schema(description = "服务类目 ID")
    private Long categoryId;

    @Schema(description = "服务类目名称")
    private String categoryName;

    @Schema(description = "需求描述")
    private String requireDesc;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "计价方式")
    private String pricingMode;

    @Schema(description = "拆分状态")
    private String splitStatus;

    @Schema(description = "拆分后单元数")
    private Integer unitCount;

    @Schema(description = "服务时长说明")
    private String serviceDurationDesc;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "街道")
    private String street;

    @Schema(description = "详细地址")
    private String detailAddress;

    @Schema(description = "与当前服务商的距离，单位公里")
    private BigDecimal distanceKm;

    @Schema(description = "订单状态")
    private String status;

    @Schema(description = "派单状态")
    private String dispatchStatus;

    @Schema(description = "阶段号")
    private Integer stageNo;

    @Schema(description = "推送批次号")
    private Integer pushBatchNo;

    @Schema(description = "剩余可抢秒数")
    private Integer countdownSeconds;

    @Schema(description = "接单截止时间")
    private LocalDateTime acceptDeadlineTime;

    @Schema(description = "优先层标识")
    private String priorityLayer;

    @Schema(description = "防逃单提示文案")
    private String antiEscapeNotice;

    @Schema(description = "当前是否可抢单")
    private Boolean canAccept;

    @Schema(description = "价格明细")
    private List<OrderPriceItemRespVO> priceItems;

    @Schema(description = "订单附件")
    private List<OrderAttachmentRespVO> attachments;

    @Schema(description = "用户 App - 抢单详情价格项 Response VO")
    @Data
    public static class OrderPriceItemRespVO {

        @Schema(description = "价格项类型")
        private String itemType;

        @Schema(description = "价格项名称")
        private String itemName;

        @Schema(description = "价格项金额")
        private BigDecimal itemAmount;

        @Schema(description = "排序号")
        private Integer sortNo;
    }

    @Schema(description = "用户 App - 抢单详情附件 Response VO")
    @Data
    public static class OrderAttachmentRespVO {

        @Schema(description = "文件 ID")
        private Long fileId;

        @Schema(description = "文件类型")
        private String fileType;

        @Schema(description = "文件地址")
        private String fileUrl;

        @Schema(description = "排序号")
        private Integer sortNo;
    }
}
