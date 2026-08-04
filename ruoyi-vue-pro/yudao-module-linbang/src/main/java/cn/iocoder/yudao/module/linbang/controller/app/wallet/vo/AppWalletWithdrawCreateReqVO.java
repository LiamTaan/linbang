package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.number.MoneyUtils.MAX_YUAN_AMOUNT_STR;

@Schema(description = "用户 App - 创建提现 Request VO")
@Data
public class AppWalletWithdrawCreateReqVO {

    @Schema(description = "银行卡 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "银行卡不能为空")
    private Long bankCardId;

    @Schema(description = "提现金额，单位元，最低 10 元、最高 21474836.47 元，最多 2 位小数",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "100.00")
    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "10.00", message = "提现金额不能低于 10 元")
    @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "提现金额超过支付渠道支持上限")
    @Digits(integer = 8, fraction = 2, message = "提现金额最多 8 位整数和 2 位小数")
    private BigDecimal applyAmount;

}
