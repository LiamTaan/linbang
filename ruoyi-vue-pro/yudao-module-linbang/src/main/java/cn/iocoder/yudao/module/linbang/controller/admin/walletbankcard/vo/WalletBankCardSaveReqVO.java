package cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = "银行编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ICBC")
    @NotEmpty(message = "银行编码不能为空")
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

    @Schema(description = "出款收款账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "6222020202020202020")
    @NotEmpty(message = "出款收款账号不能为空")
    private String transferAccount;

    @Schema(description = "开户省份，用于第三方提现出款", requiredMode = Schema.RequiredMode.REQUIRED, example = "广东省")
    @NotEmpty(message = "开户省份不能为空")
    private String bankProvince;

    @Schema(description = "开户城市，用于第三方提现出款", requiredMode = Schema.RequiredMode.REQUIRED, example = "深圳市")
    @NotEmpty(message = "开户城市不能为空")
    private String bankCity;

    @Schema(description = "预留手机号，用于第三方提现出款", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotEmpty(message = "预留手机号不能为空")
    private String reservedMobile;

    @Schema(description = OpenApiSchemaConstants.BANK_CARD_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "是否默认", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认不能为空")
    private Boolean isDefault;

}
