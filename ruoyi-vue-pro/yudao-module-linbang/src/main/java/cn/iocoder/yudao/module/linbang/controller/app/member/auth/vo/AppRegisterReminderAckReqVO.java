package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 注册提醒确认 Request VO")
@Data
public class AppRegisterReminderAckReqVO {

    @Schema(description = "提醒键", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOCIAL_32_openid_xxx")
    @NotBlank(message = "提醒键不能为空")
    private String reminderKey;
}
