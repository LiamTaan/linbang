package cn.iocoder.yudao.module.linbang.controller.admin.payrefund.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 退款业务上下文 Response VO")
@Data
public class PayRefundBizContextRespVO {

    private Long payRefundId;
    private String merchantRefundId;
    private Long orderId;
    private Long unitId;
    private OrderSimpleRespVO order;
    private OrderUnitSimpleRespVO unit;
    private List<WalletFlowSimpleRespVO> walletFlows;
    private List<ComplaintSimpleRespVO> complaints;
    private List<AppealSimpleRespVO> appeals;
    private List<OrderOperateLogSimpleRespVO> operateLogs;

    @Data
    public static class OrderSimpleRespVO {
        private Long id;
        private String orderNo;
        private Long userId;
        private Long merchantId;
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
        private String status;
        private LocalDateTime createTime;
    }

    @Data
    public static class WalletFlowSimpleRespVO {
        private Long id;
        private String flowNo;
        private Long userId;
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
    }

    @Data
    public static class ComplaintSimpleRespVO {
        private Long id;
        private String complaintNo;
        private Long orderId;
        private Long unitId;
        private Long complainantUserId;
        private Long respondentUserId;
        private String complaintType;
        private String content;
        private String status;
        private String resultDesc;
        private LocalDateTime handleTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class AppealSimpleRespVO {
        private Long id;
        private String appealNo;
        private Long orderId;
        private Long unitId;
        private Long userId;
        private String appealType;
        private String content;
        private String status;
        private String auditStatus;
        private String auditRemark;
        private String rejectReason;
        private LocalDateTime auditTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderOperateLogSimpleRespVO {
        private Long id;
        private Long orderId;
        private Long unitId;
        private String operateType;
        private String operateRole;
        private Long operateBy;
        private String beforeStatus;
        private String afterStatus;
        private String remark;
        private LocalDateTime operateTime;
    }

}
