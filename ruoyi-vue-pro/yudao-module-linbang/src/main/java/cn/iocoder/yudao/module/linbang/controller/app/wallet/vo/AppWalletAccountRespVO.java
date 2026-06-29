package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

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

    @Schema(description = "最低提现金额，单位元", example = "10.00")
    private BigDecimal minWithdrawAmount;

    @Schema(description = "是否已完成实名认证", example = "true")
    private Boolean realNameVerified;

    @Schema(description = "待结算推广收益", example = "12.50")
    private BigDecimal pendingPromoteIncome;

    @Schema(description = "累计推广收益", example = "88.80")
    private BigDecimal totalPromoteIncome;

    @Schema(description = "默认收款账户摘要")
    private DefaultBankCardRespVO defaultBankCard;

    @Schema(description = "权益摘要")
    private List<BenefitItemRespVO> benefits;

    @Data
    public static class DefaultBankCardRespVO {
        @Schema(description = "银行卡 ID", example = "1")
        private Long id;

        @Schema(description = "银行名称")
        private String bankName;

        @Schema(description = "银行卡掩码")
        private String cardNoMask;

        @Schema(description = "持卡人")
        private String accountName;
    }

    @Data
    public static class BenefitItemRespVO {
        @Schema(description = "权益类型，例如 CREDIT_LEVEL、PROMOTER、PRIORITY_DISPATCH")
        private String benefitType;

        @Schema(description = "权益标题")
        private String benefitTitle;

        @Schema(description = "权益说明")
        private String benefitDesc;
    }

}
