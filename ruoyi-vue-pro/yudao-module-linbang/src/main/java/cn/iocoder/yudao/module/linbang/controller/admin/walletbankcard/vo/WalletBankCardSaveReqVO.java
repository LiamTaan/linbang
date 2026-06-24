package cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 用户银行卡新增/修改 Request VO")
@Data
public class WalletBankCardSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21704")
    private Long id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3809")
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @Schema(description = "银行名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "银行名称不能为空")
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "加密卡号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "加密卡号不能为空")
    private String cardNoEncrypt;

    @Schema(description = "脱敏卡号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "脱敏卡号不能为空")
    private String cardNoMask;

    @Schema(description = "开户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "王五")
    @NotEmpty(message = "开户名不能为空")
    private String accountName;

    @Schema(description = "预留手机号")
    private String reservedMobile;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "是否默认", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认不能为空")
    private Boolean isDefault;

}