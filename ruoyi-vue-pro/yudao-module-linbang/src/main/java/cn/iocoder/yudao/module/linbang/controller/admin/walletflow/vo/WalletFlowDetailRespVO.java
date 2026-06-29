package cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 钱包流水详情 Response VO")
@Data
public class WalletFlowDetailRespVO {

    @Schema(description = "流水 ID", example = "1")
    private Long id;
    @Schema(description = "流水号", example = "WF202606260001")
    private String flowNo;
    @Schema(description = "用户 ID", example = "1001")
    private Long userId;
    @Schema(description = "用户编号", example = "LBU123456")
    private String userNo;
    @Schema(description = "用户昵称", example = "邻里用户")
    private String userNickname;
    @Schema(description = "用户手机号", example = "13800138000")
    private String userMobile;
    @Schema(description = "钱包账户 ID", example = "2001")
    private Long walletAccountId;
    @Schema(description = OpenApiSchemaConstants.WALLET_FLOW_BIZ_TYPE, example = "WITHDRAW_APPLY")
    private String bizType;
    @Schema(description = OpenApiSchemaConstants.WALLET_FLOW_TYPE, example = "OUT")
    private String flowType;
    @Schema(description = "变动金额")
    private BigDecimal changeAmount;
    @Schema(description = "变动前金额")
    private BigDecimal beforeAmount;
    @Schema(description = "变动后金额")
    private BigDecimal afterAmount;
    @Schema(description = "关联订单 ID", example = "3001")
    private Long relatedOrderId;
    @Schema(description = "关联单元 ID", example = "4001")
    private Long relatedUnitId;
    @Schema(description = "关联支付订单 ID", example = "5001")
    private Long relatedPayOrderId;
    @Schema(description = "关联退款 ID", example = "6001")
    private Long relatedRefundId;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "钱包账户摘要")
    private WalletAccountSimpleRespVO walletAccount;
    @Schema(description = "订单摘要")
    private OrderSimpleRespVO order;
    @Schema(description = "订单单元摘要")
    private OrderUnitSimpleRespVO unit;
    @Schema(description = "支付订单摘要")
    private PayOrderSimpleRespVO payOrder;
    @Schema(description = "退款单摘要")
    private PayRefundSimpleRespVO refund;

    @Data
    public static class WalletAccountSimpleRespVO {
        @Schema(description = "钱包账户 ID", example = "2001")
        private Long id;
        @Schema(description = "用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "钱包角色编码", example = "MERCHANT")
        private String roleCode;
        @Schema(description = "累计总金额")
        private BigDecimal totalAmount;
        @Schema(description = "可用余额")
        private BigDecimal availableAmount;
        @Schema(description = "冻结金额")
        private BigDecimal frozenAmount;
        @Schema(description = "托管金额")
        private BigDecimal escrowAmount;
        @Schema(description = "佣金金额")
        private BigDecimal commissionAmount;
        @Schema(description = "钱包状态", example = "ENABLE")
        private String status;
    }

    @Data
    public static class OrderSimpleRespVO {
        @Schema(description = "订单 ID", example = "3001")
        private Long id;
        @Schema(description = "订单号", example = "LB202606260001")
        private String orderNo;
        @Schema(description = "下单用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "服务商 ID", example = "2001")
        private Long merchantId;
        @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
        private String pricingMode;
        @Schema(description = "订单金额")
        private BigDecimal orderAmount;
        @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_STATUS, example = "UNSPLIT")
        private String splitStatus;
        @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, example = "SERVING")
        private String status;
        @Schema(description = "支付订单 ID", example = "5001")
        private Long payOrderId;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderUnitSimpleRespVO {
        @Schema(description = "单元 ID", example = "4001")
        private Long id;
        @Schema(description = "订单 ID", example = "3001")
        private Long orderId;
        @Schema(description = "单元号")
        private String unitNo;
        @Schema(description = "单元序号", example = "1")
        private Integer unitSeq;
        @Schema(description = "单元标题")
        private String unitTitle;
        @Schema(description = "单元金额")
        private BigDecimal unitAmount;
        @Schema(description = "是否锁单")
        private Boolean isLocked;
        @Schema(description = "锁单原因")
        private String lockReason;
        @Schema(description = "服务商 ID", example = "2001")
        private Long merchantId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人")
        private String merchantContactName;
        @Schema(description = "联系电话")
        private String merchantContactMobile;
        @Schema(description = OpenApiSchemaConstants.ORDER_UNIT_STATUS, example = "PENDING_ACCEPT")
        private String status;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class PayOrderSimpleRespVO {
        @Schema(description = "支付订单 ID", example = "5001")
        private Long id;
        @Schema(description = "商户订单号")
        private String merchantOrderId;
        @Schema(description = "支付标题")
        private String subject;
        @Schema(description = "支付金额，单位分", example = "19900")
        private Integer price;
        @Schema(description = "支付状态，按支付模块状态字典展示", example = "20")
        private Integer status;
        @Schema(description = "支付渠道编码", example = "wx_pub")
        private String channelCode;
        @Schema(description = "渠道单号")
        private String channelOrderNo;
        @Schema(description = "支付单号")
        private String no;
        @Schema(description = "已退款金额，单位分", example = "0")
        private Integer refundPrice;
        @Schema(description = "支付成功时间")
        private LocalDateTime successTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class PayRefundSimpleRespVO {
        @Schema(description = "退款单 ID", example = "6001")
        private Long id;
        @Schema(description = "商户退款单号")
        private String merchantRefundId;
        @Schema(description = "商户订单号")
        private String merchantOrderId;
        @Schema(description = "退款状态，按支付模块状态字典展示", example = "20")
        private Integer status;
        @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
        private String auditStatus;
        @Schema(description = "审核备注")
        private String auditRemark;
        @Schema(description = "驳回原因")
        private String rejectReason;
        @Schema(description = "退款金额，单位分", example = "19900")
        private Integer refundPrice;
        @Schema(description = "退款原因")
        private String reason;
        @Schema(description = "退款成功时间")
        private LocalDateTime successTime;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

}
