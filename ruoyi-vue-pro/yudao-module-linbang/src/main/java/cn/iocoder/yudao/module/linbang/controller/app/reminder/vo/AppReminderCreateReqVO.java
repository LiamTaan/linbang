package cn.iocoder.yudao.module.linbang.controller.app.reminder.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "用户 App - 新增提醒 Request VO")
@Data
public class AppReminderCreateReqVO {

    @Schema(description = OpenApiSchemaConstants.REMINDER_TYPE, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "CUSTOM_EVENT")
    @NotBlank(message = "提醒类型不能为空")
    private String reminderType;

    @Schema(description = "提醒标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "孩子生日提醒")
    @NotBlank(message = "提醒标题不能为空")
    private String title;

    @Schema(description = "提醒内容", example = "提前准备生日蛋糕")
    private String content;

    @Schema(description = "事件时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "事件时间不能为空")
    private LocalDateTime eventTime;

    @Schema(description = OpenApiSchemaConstants.REMINDER_REPEAT_TYPE, example = "NONE")
    private String repeatType;

    @Schema(description = "提醒路由类型", example = "APP_PAGE")
    private String routeType;

    @Schema(description = "提醒路由值", example = "/pages/user/reminder/index")
    private String routeValue;
}
