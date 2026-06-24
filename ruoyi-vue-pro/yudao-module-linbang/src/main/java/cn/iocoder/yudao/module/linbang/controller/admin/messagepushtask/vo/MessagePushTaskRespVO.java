package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessagePushTaskRespVO {

    private Long id;

    private String taskName;

    private String targetScope;

    private String channelType;

    private Long templateId;

    private String templateName;

    private String bizType;

    private String status;

    private LocalDateTime plannedSendTime;

    private LocalDateTime executeTime;

    private Integer successCount;

    private Integer failCount;

    private LocalDateTime createTime;
}
