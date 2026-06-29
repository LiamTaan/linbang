package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 注册提醒 Response VO")
@Data
public class AppRegisterReminderRespVO {

    @Schema(description = "是否需要提醒", example = "true")
    private Boolean remindRequired;

    @Schema(description = "提醒键", example = "SOCIAL_32_openid_xxx")
    private String reminderKey;

    @Schema(description = "提醒场景", example = "SOCIAL_UNREGISTERED")
    private String reminderScene;

    @Schema(description = "提醒标题", example = "完成注册后才可继续")
    private String title;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "弹窗冷却分钟数", example = "60")
    private Integer cooldownMinutes;

    @Schema(description = "已触发次数", example = "3")
    private Integer triggerCount;

    @Schema(description = "最后触发时间")
    private LocalDateTime lastTriggerTime;
}
