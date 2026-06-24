package cn.iocoder.yudao.module.pay.controller.admin.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 支付订单详细信息 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PayOrderDetailsRespVO extends PayOrderBaseVO {

    @Schema(description = "支付订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋道源码")
    private String appName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    @Schema(description = "退款记录")
    private List<PayRefundSimple> refunds;

    @Schema(description = "支付扩展历史")
    private List<PayOrderExtensionHistory> extensionHistory;

    /**
     * 支付订单扩展
     */
    private PayOrderExtension extension;

    @Data
    @Schema(description = "支付订单扩展")
    public static class PayOrderExtension {

        @Schema(description = "支付订单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private String no;

        @Schema(description = "支付异步通知的内容")
        private String channelNotifyData;

    }

    @Data
    @Schema(description = "退款记录摘要")
    public static class PayRefundSimple {

        @Schema(description = "退款单 ID", example = "2048")
        private Long id;

        @Schema(description = "退款单号", example = "R202606230001")
        private String no;

        @Schema(description = "商户退款单号", example = "LB-REFUND-001")
        private String merchantRefundId;

        @Schema(description = "退款状态", example = "10")
        private Integer status;

        @Schema(description = "审核状态", example = "APPROVED")
        private String auditStatus;

        @Schema(description = "退款金额，单位：分", example = "1000")
        private Integer refundPrice;

        @Schema(description = "退款原因", example = "用户取消")
        private String reason;

        @Schema(description = "渠道退款单号", example = "4200000000001")
        private String channelRefundNo;

        @Schema(description = "成功时间")
        private LocalDateTime successTime;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "支付扩展历史")
    public static class PayOrderExtensionHistory {

        @Schema(description = "扩展 ID", example = "4096")
        private Long id;

        @Schema(description = "支付单号", example = "P202606230001")
        private String no;

        @Schema(description = "渠道编码", example = "wx_lite")
        private String channelCode;

        @Schema(description = "支付状态", example = "0")
        private Integer status;

        @Schema(description = "用户 IP", example = "127.0.0.1")
        private String userIp;

        @Schema(description = "渠道错误码", example = "SYSTEM_ERROR")
        private String channelErrorCode;

        @Schema(description = "渠道错误信息", example = "系统繁忙")
        private String channelErrorMsg;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;

        @Schema(description = "更新时间")
        private LocalDateTime updateTime;

        @Schema(description = "支付通道异步回调内容")
        private String channelNotifyData;
    }

}
