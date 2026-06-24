package cn.iocoder.yudao.module.pay.mq.message.refund;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 退款审核结果消息
 */
@Data
public class PayRefundAuditResultMessage {

    @NotNull(message = "退款单编号不能为空")
    private Long refundId;

    @NotNull(message = "支付订单编号不能为空")
    private Long orderId;

    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    private String auditRemark;

    private String rejectReason;

    @NotNull(message = "审核人不能为空")
    private Long auditBy;

    @NotNull(message = "审核时间不能为空")
    private LocalDateTime auditTime;
}
