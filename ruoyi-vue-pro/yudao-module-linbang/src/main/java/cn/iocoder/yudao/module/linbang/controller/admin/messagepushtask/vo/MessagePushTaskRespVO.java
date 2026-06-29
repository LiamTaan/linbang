package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessagePushTaskRespVO {

    private Long id;

    private String taskName;

    private Long campaignId;

    private String sceneCode;

    private String messageCategory;

    private String targetScope;

    private String channelType;

    private Long templateId;

    private String templateName;

    private String bizType;

    private String status;

    private String executeStatus;

    private LocalDateTime plannedSendTime;

    private LocalDateTime executeTime;

    private Integer successCount;

    private Integer failCount;

    private Integer plannedAudienceCount;

    private Integer reachedCount;

    private Integer clickedCount;

    private Integer readCount;

    private Integer voicePlayedCount;

    private LocalDateTime createTime;
}
