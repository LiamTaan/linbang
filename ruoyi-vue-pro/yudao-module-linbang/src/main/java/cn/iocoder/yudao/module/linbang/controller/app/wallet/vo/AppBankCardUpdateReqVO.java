package cn.iocoder.yudao.module.linbang.controller.app.wallet.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 更新银行卡 Request VO；当前仅用于系统内提现收款账户管理，不代表第三方鉴权绑卡")
@Data
public class AppBankCardUpdateReqVO {

    @Schema(description = "银行卡 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "银行卡编号不能为空")
    private Long id;

    @Schema(description = "银行名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "中国工商银行")
    @NotBlank(message = "银行名称不能为空")
    private String bankName;

    @Schema(description = "银行编码，用于第三方提现出款", requiredMode = Schema.RequiredMode.REQUIRED, example = "ICBC")
    @NotBlank(message = "银行编码不能为空")
    private String bankCode;

    @Schema(description = "开户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotBlank(message = "开户名不能为空")
    private String accountName;

    @Schema(description = "开户省份，用于第三方提现出款，例如 广东省", requiredMode = Schema.RequiredMode.REQUIRED, example = "广东省")
    @NotBlank(message = "开户省份不能为空")
    private String bankProvince;

    @Schema(description = "开户城市，用于第三方提现出款，例如 深圳市", requiredMode = Schema.RequiredMode.REQUIRED, example = "深圳市")
    @NotBlank(message = "开户城市不能为空")
    private String bankCity;

    @Schema(description = "预留手机号，用于第三方提现出款", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "预留手机号不能为空")
    private String reservedMobile;

    @Schema(description = "是否默认卡", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否默认卡不能为空")
    private Boolean isDefault;
}
