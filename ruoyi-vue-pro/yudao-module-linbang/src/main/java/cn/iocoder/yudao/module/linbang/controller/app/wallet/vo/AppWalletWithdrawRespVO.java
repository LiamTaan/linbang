package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 提现记录 Response VO")
@Data
public class AppWalletWithdrawRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "提现单号")
    private String withdrawNo;

    @Schema(description = "钱包账户 ID")
    private Long walletAccountId;

    @Schema(description = "银行卡 ID")
    private Long bankCardId;

    @Schema(description = "申请金额")
    private BigDecimal applyAmount;

    @Schema(description = "手续费")
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额")
    private BigDecimal realAmount;

    @Schema(description = "提现状态：PENDING 待审核、APPROVED 审核通过、REJECTED 审核驳回、SUCCESS 打款成功、FAILED 打款失败")
    private String status;

    @Schema(description = "审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String auditStatus;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "打款时间")
    private LocalDateTime payTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
