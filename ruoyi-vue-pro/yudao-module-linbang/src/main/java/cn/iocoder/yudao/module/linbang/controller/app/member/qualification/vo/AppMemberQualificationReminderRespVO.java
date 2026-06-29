package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 证件提醒 Response VO")
@Data
public class AppMemberQualificationReminderRespVO {

    @Schema(description = "消息记录 ID", example = "1")
    private Long id;

    @Schema(description = "资质提醒标题")
    private String title;

    @Schema(description = "提醒内容")
    private String contentSnapshot;

    @Schema(description = "已读状态", example = "UNREAD")
    private String readStatus;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "关联资质 ID", example = "1001")
    private Long bizId;
}
