package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 消息记录 Response VO")
@Data
public class AppMessageRecordRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "模板ID")
    private Long templateId;

    @Schema(description = "接收用户ID")
    private Long receiverUserId;

    @Schema(description = "渠道类型")
    private String channelType;

    @Schema(description = "业务类型")
    private String bizType;

    @Schema(description = "业务ID")
    private Long bizId;

    @Schema(description = "发送状态")
    private String sendStatus;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
