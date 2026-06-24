package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Schema(description = "用户 App - 发送短信登录/注册验证码 Request VO")
@Data
public class AppMemberSendSmsCodeReqVO {

    @Schema(description = "手机号。短信登录和短信注册共用验证码发送能力。", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotEmpty(message = "手机号不能为空")
    @Mobile
    private String mobile;
}
