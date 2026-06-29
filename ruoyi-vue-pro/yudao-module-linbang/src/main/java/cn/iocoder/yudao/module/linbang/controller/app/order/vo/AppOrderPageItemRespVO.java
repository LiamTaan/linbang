package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 订单分页 Response VO")
@Data
public class AppOrderPageItemRespVO {

    @Schema(description = "订单 ID", example = "1")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "服务类目 ID")
    private Long categoryId;

    @Schema(description = "服务类目名称")
    private String categoryName;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
    private String pricingMode;

    @Schema(description = "订单金额，单位元")
    private BigDecimal orderAmount;

    @Schema(description = "服务时长说明，例如 1小时、半天、3天")
    private String serviceDurationDesc;

    @Schema(description = "距离，单位公里")
    private BigDecimal distanceKm;

    @Schema(description = "主订单状态：PENDING_PAY 待支付、PENDING_ACCEPT 待接单、ACCEPTED 已接单、SERVING 服务中、PENDING_CONFIRM 待确认、AFTER_SALE 售后中、FINISHED 已完成、REFUNDED 已退款、CLOSED 已关闭")
    private String status;

    @Schema(description = "拆单状态：UNSPLIT 不拆单、SPLIT 已拆单")
    private String splitStatus;

    @Schema(description = "支付状态：WAITING 待支付、SUCCESS 支付成功、FAILED 支付失败、CLOSED 已关闭")
    private String payStatus;

    @Schema(description = "是否要求先缴纳大额订单保证金")
    private Boolean depositRequired;

    @Schema(description = "保证金金额，单位元")
    private BigDecimal depositAmount;

    @Schema(description = "保证金支付状态：NOT_REQUIRED 无需保证金、UNPAID 待支付、PAID 已支付")
    private String depositPayStatus;

    @Schema(description = OpenApiSchemaConstants.ORDER_BUSINESS_CATEGORY, example = "WAIT_REVIEW")
    private String businessCategory;

    @Schema(description = "是否待评价")
    private Boolean waitReview;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
