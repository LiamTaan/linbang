package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 创建银行卡 Request VO")
@Data
public class AppBankCardCreateReqVO {

    @Schema(description = "银行名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "银行卡号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "银行卡号不能为空")
    private String cardNo;

    @Schema(description = "开户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "开户名不能为空")
    private String accountName;

    @Schema(description = "预留手机号")
    private String reservedMobile;

    @Schema(description = "是否默认卡", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否默认卡不能为空")
    private Boolean isDefault;

}
