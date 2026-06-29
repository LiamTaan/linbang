package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 更新银行卡 Request VO")
@Data
public class AppBankCardUpdateReqVO {

    @Schema(description = "银行卡 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "银行卡编号不能为空")
    private Long id;

    @Schema(description = "银行名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "中国工商银行")
    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @Schema(description = "银行编码", example = "ICBC")
    private String bankCode;

    @Schema(description = "开户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "开户名不能为空")
    private String accountName;

    @Schema(description = "预留手机号", example = "13800138000")
    private String reservedMobile;

    @Schema(description = "是否默认卡", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否默认卡不能为空")
    private Boolean isDefault;
}
