package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 钱包详情 Response VO")
@Data
public class AppWalletAccountRespVO {

    @Schema(description = "钱包账户 ID", example = "1")
    private Long id;

    @Schema(description = "钱包总金额")
    private BigDecimal totalAmount;

    @Schema(description = "可用余额")
    private BigDecimal availableAmount;

    @Schema(description = "冻结金额")
    private BigDecimal frozenAmount;

    @Schema(description = "担保中金额")
    private BigDecimal escrowAmount;

    @Schema(description = "佣金金额")
    private BigDecimal commissionAmount;

    @Schema(description = "钱包状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;

}
