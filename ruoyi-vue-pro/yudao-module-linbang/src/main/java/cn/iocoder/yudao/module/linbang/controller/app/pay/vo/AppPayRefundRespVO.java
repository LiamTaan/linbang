package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 退款记录 Response VO")
@Data
public class AppPayRefundRespVO {

    @Schema(description = "支付退款记录 ID", example = "1")
    private Long id;

    @Schema(description = "业务订单 ID", example = "1001")
    private Long orderId;

    @Schema(description = "单元 ID；为空表示整单退款", example = "2001")
    private Long unitId;

    @Schema(description = "支付订单 ID", example = "3001")
    private Long payOrderId;

    @Schema(description = "商户退款单号；用于支付模块和聚合支付通道定位本次退款")
    private String merchantRefundId;

    @Schema(description = "支付退款状态数值：0 未退款/退款中、10 退款成功、20 退款失败；最终结果以支付模块退款查询或回调为准")
    private Integer status;

    @Schema(description = "支付退款状态名称")
    private String statusName;

    @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过并发起原路退款、REJECTED 已驳回")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "退款金额，单位分")
    private Integer refundPrice;

    @Schema(description = "退款原因")
    private String reason;

    @Schema(description = "原支付渠道返回的退款失败原因；首期为微信支付，未来启用聚合支付后按实际原支付渠道返回，仅退款失败或异常时展示")
    private String channelErrorMsg;

    @Schema(description = "退款成功时间；成功后资金原路退回，不允许用户选择退款渠道")
    private LocalDateTime successTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
