package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 账号注册 Request VO")
@Data
public class AppMemberAccountRegisterReqVO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "linbang_user")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "短信验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;

    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "P@ssw0rd")
    @NotBlank(message = "登录密码不能为空")
    private String password;

    @Schema(description = "账户类型：PERSONAL 个人、ENTERPRISE 企业", requiredMode = Schema.RequiredMode.REQUIRED, example = "PERSONAL")
    @NotBlank(message = "账户类型不能为空")
    private String accountType;

    @Schema(description = "注册协议版本", requiredMode = Schema.RequiredMode.REQUIRED, example = "v2026.06")
    @NotBlank(message = "注册协议版本不能为空")
    private String agreementVersion;

    @Schema(description = "是否确认注册协议和受益人连带责任说明", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "请先确认注册协议")
    private Boolean agreementConfirmed;

    @Schema(description = "营业执照文件 ID；企业注册时必传", example = "1001")
    private Long businessLicenseFileId;
}
