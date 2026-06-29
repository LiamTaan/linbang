package cn.iocoder.yudao.module.linbang.controller.app.reminder.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 提醒历史 Response VO")
@Data
public class AppReminderHistoryRespVO {

    @Schema(description = "消息记录 ID", example = "1001")
    private Long messageRecordId;

    @Schema(description = "来源提醒 ID；资质提醒为空", example = "1")
    private Long reminderId;

    @Schema(description = OpenApiSchemaConstants.REMINDER_TYPE, example = "BIRTHDAY")
    private String reminderType;

    @Schema(description = "提醒标题")
    private String title;

    @Schema(description = "提醒内容快照")
    private String contentSnapshot;

    @Schema(description = "已读状态：UNREAD 未读、READ 已读")
    private String readStatus;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "关联业务 ID，例如提醒 ID 或资质 ID")
    private Long bizId;
}
