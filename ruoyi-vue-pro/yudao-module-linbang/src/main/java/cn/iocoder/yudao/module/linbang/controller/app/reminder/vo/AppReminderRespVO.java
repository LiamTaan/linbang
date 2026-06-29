package cn.iocoder.yudao.module.linbang.controller.app.reminder.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 提醒 Response VO")
@Data
public class AppReminderRespVO {

    @Schema(description = "提醒 ID；系统生成提醒为空", example = "1")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.REMINDER_TYPE, example = "CUSTOM_EVENT")
    private String reminderType;

    @Schema(description = "提醒标题", example = "身份证到期提醒")
    private String title;

    @Schema(description = "提醒内容")
    private String content;

    @Schema(description = "事件时间")
    private LocalDateTime eventTime;

    @Schema(description = "下一次提醒时间")
    private LocalDateTime nextRemindTime;

    @Schema(description = "最近一次触发时间")
    private LocalDateTime lastTriggerTime;

    @Schema(description = OpenApiSchemaConstants.REMINDER_REPEAT_TYPE, example = "YEARLY")
    private String repeatType;

    @Schema(description = OpenApiSchemaConstants.REMINDER_STATUS, example = "ACTIVE")
    private String status;

    @Schema(description = "是否系统生成")
    private Boolean systemGenerated;

    @Schema(description = "当前是否允许编辑")
    private Boolean editable;

    @Schema(description = "关联业务 ID，例如资质 ID")
    private Long bizId;

    @Schema(description = "提醒路由类型", example = "APP_PAGE")
    private String routeType;

    @Schema(description = "提醒路由值", example = "/pages/qualification/index")
    private String routeValue;
}
