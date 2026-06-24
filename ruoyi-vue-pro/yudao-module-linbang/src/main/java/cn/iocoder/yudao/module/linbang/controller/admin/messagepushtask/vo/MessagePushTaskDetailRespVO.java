package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MessagePushTaskDetailRespVO {

    private Long id;

    private String taskName;

    private String targetScope;

    private String channelType;

    private Long templateId;

    private String templateName;

    private String bizType;

    private Long bizId;

    private String status;

    private LocalDateTime plannedSendTime;

    private LocalDateTime executeTime;

    private Integer successCount;

    private Integer failCount;

    private String creatorRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private List<RecordSummary> recentRecords;

    @Data
    public static class RecordSummary {
        private Long id;
        private Long receiverUserId;
        private String receiverUserNo;
        private String receiverUserNickname;
        private String receiverUserMobile;
        private String sendStatus;
        private String failReason;
        private LocalDateTime sendTime;
    }
}
