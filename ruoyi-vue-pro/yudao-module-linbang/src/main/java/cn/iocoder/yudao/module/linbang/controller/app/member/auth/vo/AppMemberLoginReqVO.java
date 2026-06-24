package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Schema(description = "用户 App - 手机号验证码登录/注册 Request VO")
@Data
public class AppMemberLoginReqVO {

    @Schema(description = "手机号。登录页和注册页共用本入参；若手机号未注册，则验证码校验通过后自动完成注册。", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "短信验证码。由发送短信登录/注册验证码接口下发，登录和注册共用。", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @NotEmpty(message = "短信验证码不能为空")
    @Length(min = 4, max = 6, message = "短信验证码长度为 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "短信验证码必须为数字")
    private String code;
}
