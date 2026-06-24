package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 原生 App 第三方授权登录 Request VO")
@Data
public class AppMemberSocialLoginReqVO {

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
}
