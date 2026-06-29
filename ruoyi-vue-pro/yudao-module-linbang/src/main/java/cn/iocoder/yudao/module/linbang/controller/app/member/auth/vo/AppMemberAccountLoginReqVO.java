package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 账号密码登录 Request VO")
@Data
public class AppMemberAccountLoginReqVO {

    @Schema(description = "账号用户名或手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "linbang_user")
    @NotBlank(message = "账号不能为空")
    private String account;

    @Schema(description = "登录密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "P@ssw0rd")
    @NotBlank(message = "密码不能为空")
    private String password;
}
