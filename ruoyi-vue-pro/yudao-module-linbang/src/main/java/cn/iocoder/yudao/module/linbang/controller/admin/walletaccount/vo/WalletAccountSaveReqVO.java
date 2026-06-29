package cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 钱包账户新增/修改 Request VO")
@Data
public class WalletAccountSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6344")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "6967")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "角色编码不能为空")
    private String roleCode;

    @Schema(description = "总资产", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总资产不能为空")
    private BigDecimal totalAmount;

    @Schema(description = "可提现金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "可提现金额不能为空")
    private BigDecimal availableAmount;

    @Schema(description = "冻结金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "冻结金额不能为空")
    private BigDecimal frozenAmount;

    @Schema(description = "托管金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "托管金额不能为空")
    private BigDecimal escrowAmount;

    @Schema(description = "佣金金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "佣金金额不能为空")
    private BigDecimal commissionAmount;

    @Schema(description = OpenApiSchemaConstants.WALLET_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotEmpty(message = "状态不能为空")
    private String status;

}
