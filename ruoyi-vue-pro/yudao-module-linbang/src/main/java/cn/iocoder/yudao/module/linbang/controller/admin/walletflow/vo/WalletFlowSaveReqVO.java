package cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 钱包流水新增/修改 Request VO")
@Data
public class WalletFlowSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "6790")
    private Long id;

    @Schema(description = "流水号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "流水号不能为空")
    private String flowNo;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "8346")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "钱包账户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16689")
    @NotNull(message = "钱包账户ID不能为空")
    private Long walletAccountId;

    @Schema(description = OpenApiSchemaConstants.WALLET_FLOW_BIZ_TYPE, requiredMode = Schema.RequiredMode.REQUIRED, example = "WITHDRAW_APPLY")
    @NotEmpty(message = "业务类型不能为空")
    private String bizType;

    @Schema(description = OpenApiSchemaConstants.WALLET_FLOW_TYPE, requiredMode = Schema.RequiredMode.REQUIRED, example = "OUT")
    @NotEmpty(message = "流水类型不能为空")
    private String flowType;

    @Schema(description = "变动金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "变动金额不能为空")
    private BigDecimal changeAmount;

    @Schema(description = "变动前金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "变动前金额不能为空")
    private BigDecimal beforeAmount;

    @Schema(description = "变动后金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "变动后金额不能为空")
    private BigDecimal afterAmount;

    @Schema(description = "关联订单ID", example = "25614")
    private Long relatedOrderId;

    @Schema(description = "关联单元ID", example = "28489")
    private Long relatedUnitId;

    @Schema(description = "关联支付订单ID", example = "16116")
    private Long relatedPayOrderId;

    @Schema(description = "关联退款ID", example = "12674")
    private Long relatedRefundId;

    @Schema(description = "备注", example = "随便")
    private String remark;

}
