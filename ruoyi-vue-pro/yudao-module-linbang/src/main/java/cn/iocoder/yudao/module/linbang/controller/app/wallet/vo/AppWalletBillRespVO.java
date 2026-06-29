package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 钱包账单 Response VO")
@Data
public class AppWalletBillRespVO {

    @Schema(description = "账单主键 ID", example = "1")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.WALLET_BILL_TYPE, example = "ORDER")
    private String billType;

    @Schema(description = "账单标题", example = "订单托管锁定")
    private String billTitle;

    @Schema(description = "账单摘要", example = "订单支付完成，资金进入托管")
    private String billSummary;

    @Schema(description = OpenApiSchemaConstants.WALLET_FLOW_BIZ_TYPE, example = "ORDER_PAY")
    private String bizType;

    @Schema(description = "业务状态，例如 SUCCESS、PENDING、FAILED", example = "SUCCESS")
    private String bizStatus;

    @Schema(description = "金额变动方向：IN 入账、OUT 出账", example = "IN")
    private String amountDirection;

    @Schema(description = "账单金额，单位元", example = "88.50")
    private BigDecimal amount;

    @Schema(description = "变动前余额口径值，单位元", example = "100.00")
    private BigDecimal beforeAmount;

    @Schema(description = "变动后余额口径值，单位元", example = "188.50")
    private BigDecimal afterAmount;

    @Schema(description = "关联订单 ID", example = "10001")
    private Long relatedOrderId;

    @Schema(description = "关联订单单元 ID", example = "20001")
    private Long relatedUnitId;

    @Schema(description = "关联退款 ID", example = "30001")
    private Long relatedRefundId;

    @Schema(description = "关联提现 ID", example = "40001")
    private Long relatedWithdrawId;

    @Schema(description = "关联推广佣金订单 ID", example = "50001")
    private Long relatedCommissionOrderId;

    @Schema(description = "业务单号，例如流水号/提现单号", example = "LBF202606280001")
    private String bizNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
