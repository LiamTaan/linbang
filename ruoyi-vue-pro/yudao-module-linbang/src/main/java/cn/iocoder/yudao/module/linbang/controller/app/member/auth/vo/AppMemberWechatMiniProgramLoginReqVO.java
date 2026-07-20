package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "用户 App - 微信小程序手机号授权登录 Request VO")
@Data
public class AppMemberWechatMiniProgramLoginReqVO {

    @Schema(description = "微信小程序 button open-type=getPhoneNumber 回调提供的一次性手机号授权码。"
            + "服务端使用该值向微信换取手机号；不可重复使用，不可由客户端替换为手机号。",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "081abcDEFghijkLMNopqrsTUV")
    @NotEmpty(message = "微信手机号授权码不能为空")
    private String phoneCode;

    @Schema(description = "微信小程序 wx.login 返回的一次性登录凭证。服务端使用该值换取并绑定当前用户 openid，"
            + "供微信小程序支付使用；不可重复使用。",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "0a3BcdEfGhIjKlMnOpQrStUvWxYz")
    @NotEmpty(message = "微信小程序登录凭证不能为空")
    private String loginCode;

}
