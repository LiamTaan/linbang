package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.framework.common.validation.Mobile;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "用户 App - 第三方登录绑定手机号 Request VO")
@Data
public class AppMemberSocialBindMobileReqVO {

    @Schema(description = "社交平台类型，仅支持 32=微信开放平台授权、40=支付宝授权", requiredMode = Schema.RequiredMode.REQUIRED, example = "32")
    @InEnum(SocialTypeEnum.class)
    @NotNull(message = "社交平台类型不能为空")
    private Integer type;

    @Schema(description = "第三方授权码，由微信开放平台或支付宝授权成功后返回", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "授权码不能为空")
    private String code;

    @Schema(description = "第三方授权 state 参数，用于防重放和匹配授权请求", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "state 不能为空")
    private String state;

    @Schema(description = "待绑定的手机号。若手机号未注册则自动创建账号；若已注册则绑定到既有账号。", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;

    @Schema(description = "手机号短信验证码，用于校验绑定手机号的归属。发送接口与短信登录/注册共用。", requiredMode = Schema.RequiredMode.REQUIRED, example = "1234")
    @NotEmpty(message = "短信验证码不能为空")
    @Length(min = 4, max = 6, message = "短信验证码长度为 4-6 位")
    @Pattern(regexp = "^[0-9]+$", message = "短信验证码必须为数字")
    private String codeSms;
}
