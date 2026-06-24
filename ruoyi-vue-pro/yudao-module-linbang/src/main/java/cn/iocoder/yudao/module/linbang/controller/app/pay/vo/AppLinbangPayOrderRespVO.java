package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 邻里支付订单 Response VO")
@Data
public class AppLinbangPayOrderRespVO {

    @Schema(description = "支付订单 ID")
    private Long id;

    @Schema(description = "业务订单 ID")
    private Long orderId;

    @Schema(description = "支付渠道编码")
    private String channelCode;

    @Schema(description = "商户订单号")
    private String merchantOrderId;

    @Schema(description = "支付标题")
    private String subject;

    @Schema(description = "支付金额，单位分")
    private Integer price;

    @Schema(description = "支付状态数值，来源于支付模块内部枚举；前端展示优先使用 statusName")
    private Integer status;

    @Schema(description = "支付状态名称")
    private String statusName;

    @Schema(description = "支付订单号")
    private String no;

    @Schema(description = "渠道订单号")
    private String channelOrderNo;

    @Schema(description = "订单过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "支付成功时间")
    private LocalDateTime successTime;

    @Schema(description = "退款金额，单位分")
    private Integer refundPrice;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
