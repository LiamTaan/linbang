package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - 注册提醒确认 Request VO")
@Data
public class AppRegisterReminderAckReqVO {

    @Schema(description = "提醒键，由获取未注册提醒接口返回", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "SOCIAL_32_aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
    @NotBlank(message = "提醒键不能为空")
    @Size(max = 96, message = "提醒键不能超过 96 个字符")
    @Pattern(regexp = "(?:SOCIAL_[0-9]+|DEVICE)_[0-9a-f]{64}", message = "提醒键格式不正确")
    private String reminderKey;
}
