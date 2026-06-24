package cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 钱包流水详情 Response VO")
@Data
public class WalletFlowDetailRespVO {

    private Long id;
    private String flowNo;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private Long walletAccountId;
    private String bizType;
    private String flowType;
    private BigDecimal changeAmount;
    private BigDecimal beforeAmount;
    private BigDecimal afterAmount;
    private Long relatedOrderId;
    private Long relatedUnitId;
    private Long relatedPayOrderId;
    private Long relatedRefundId;
    private String remark;
    private LocalDateTime createTime;
    private WalletAccountSimpleRespVO walletAccount;
    private OrderSimpleRespVO order;
    private OrderUnitSimpleRespVO unit;
    private PayOrderSimpleRespVO payOrder;
    private PayRefundSimpleRespVO refund;

    @Data
    public static class WalletAccountSimpleRespVO {
        private Long id;
        private Long userId;
        private String roleCode;
        private BigDecimal totalAmount;
        private BigDecimal availableAmount;
        private BigDecimal frozenAmount;
        private BigDecimal escrowAmount;
        private BigDecimal commissionAmount;
        private String status;
    }

    @Data
    public static class OrderSimpleRespVO {
        private Long id;
        private String orderNo;
        private Long userId;
        private Long merchantId;
        private String title;
        private String pricingMode;
        private BigDecimal orderAmount;
        private String splitStatus;
        private String status;
        private Long payOrderId;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderUnitSimpleRespVO {
        private Long id;
        private Long orderId;
        private String unitNo;
        private Integer unitSeq;
        private String unitTitle;
        private BigDecimal unitAmount;
        private Boolean isLocked;
        private String lockReason;
        private Long merchantId;
        private String merchantName;
        private String merchantContactName;
        private String merchantContactMobile;
        private String status;
        private LocalDateTime createTime;
    }

    @Data
    public static class PayOrderSimpleRespVO {
        private Long id;
        private String merchantOrderId;
        private String subject;
        private Integer price;
        private Integer status;
        private String channelCode;
        private String channelOrderNo;
        private String no;
        private Integer refundPrice;
        private LocalDateTime successTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class PayRefundSimpleRespVO {
        private Long id;
        private String merchantRefundId;
        private String merchantOrderId;
        private Integer status;
        private String auditStatus;
        private String auditRemark;
        private String rejectReason;
        private Integer refundPrice;
        private String reason;
        private LocalDateTime successTime;
        private LocalDateTime createTime;
    }

}
