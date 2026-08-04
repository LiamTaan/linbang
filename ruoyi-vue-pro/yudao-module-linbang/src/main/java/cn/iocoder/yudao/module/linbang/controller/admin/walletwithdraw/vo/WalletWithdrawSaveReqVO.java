package cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.number.MoneyUtils.MAX_YUAN_AMOUNT_STR;

@Schema(description = "管理后台 - 提现申请新增/修改 Request VO")
@Data
public class WalletWithdrawSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4287")
    private Long id;

    @Schema(description = "提现单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "提现单号不能为空")
    private String withdrawNo;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24266")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "钱包账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "19469")
    @NotNull(message = "钱包账户ID不能为空")
    private Long walletAccountId;

    @Schema(description = "银行卡ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8562")
    @NotNull(message = "银行卡ID不能为空")
    private Long bankCardId;

    @Schema(description = "申请金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.01", message = "申请金额必须大于 0")
    @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "申请金额超过支付渠道支持上限")
    @Digits(integer = 8, fraction = 2, message = "申请金额最多 8 位整数和 2 位小数")
    private BigDecimal applyAmount;

    @Schema(description = "手续费", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "手续费不能为空")
    @DecimalMin(value = "0.00", message = "手续费不能小于 0")
    @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "手续费超过支付渠道支持上限")
    @Digits(integer = 8, fraction = 2, message = "手续费最多 8 位整数和 2 位小数")
    private BigDecimal feeAmount;

    @Schema(description = "实际到账金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "实际到账金额不能为空")
    @DecimalMin(value = "0.01", message = "实际到账金额必须大于 0")
    @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "实际到账金额超过支付渠道支持上限")
    @Digits(integer = 8, fraction = 2, message = "实际到账金额最多 8 位整数和 2 位小数")
    private BigDecimal realAmount;

    @Schema(description = OpenApiSchemaConstants.WITHDRAW_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING")
    @NotEmpty(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注", example = "你猜")
    private String auditRemark;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @Schema(description = "驳回原因", example = "不对")
    private String rejectReason;

    @Schema(description = "打款时间")
    private LocalDateTime payTime;

}
