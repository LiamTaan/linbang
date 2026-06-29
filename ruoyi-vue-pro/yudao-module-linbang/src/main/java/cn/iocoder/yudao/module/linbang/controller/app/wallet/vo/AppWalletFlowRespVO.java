package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 钱包流水 Response VO")
@Data
public class AppWalletFlowRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "流水号")
    private String flowNo;

    @Schema(description = "业务类型，例如 ORDER_PAY 支付、WITHDRAW_APPLY 提现申请、WITHDRAW_SUCCESS 提现成功、REFUND 退款")
    private String bizType;

    @Schema(description = "流水类型：IN 入账、OUT 出账、FREEZE 冻结、UNFREEZE 解冻", example = "OUT")
    private String flowType;

    @Schema(description = "变动金额")
    private BigDecimal changeAmount;

    @Schema(description = "变动前金额")
    private BigDecimal beforeAmount;

    @Schema(description = "变动后金额")
    private BigDecimal afterAmount;

    @Schema(description = "关联订单 ID")
    private Long relatedOrderId;

    @Schema(description = "关联单元 ID")
    private Long relatedUnitId;

    @Schema(description = "关联支付订单 ID")
    private Long relatedPayOrderId;

    @Schema(description = "关联退款 ID")
    private Long relatedRefundId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "税务扣减金额，单位元")
    private BigDecimal taxAmount;

    @Schema(description = "业务标签", example = "托管锁定")
    private String bizLabel;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
