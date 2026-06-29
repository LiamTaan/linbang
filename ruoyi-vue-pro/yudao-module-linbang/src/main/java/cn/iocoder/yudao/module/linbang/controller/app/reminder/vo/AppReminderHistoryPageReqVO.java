package cn.iocoder.yudao.module.linbang.controller.app.reminder.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 提醒历史分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppReminderHistoryPageReqVO extends PageParam {

    @Schema(description = OpenApiSchemaConstants.REMINDER_TYPE, example = "QUALIFICATION_EXPIRY")
    private String reminderType;

    @Schema(description = "已读状态：UNREAD 未读、READ 已读", example = "UNREAD")
    private String readStatus;
}
