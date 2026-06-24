package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 消息模板详情 Response VO")
@Data
public class MessageTemplateDetailRespVO {

    private Long id;
    private String templateCode;
    private String templateName;
    private String templateType;
    private String channelType;
    private String content;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer sendCount;
    private Integer successCount;
    private Integer failedCount;
    private Integer pendingCount;
    private List<ChannelStatRespVO> channelStats;
    private List<MessageRecordSimpleRespVO> recentRecords;

    @Data
    public static class ChannelStatRespVO {
        private String channelType;
        private Integer recordCount;
    }

    @Data
    public static class MessageRecordSimpleRespVO {
        private Long id;
        private Long receiverUserId;
        private String receiverUserNo;
        private String receiverUserNickname;
        private String receiverUserMobile;
        private String channelType;
        private String bizType;
        private Long bizId;
        private String sendStatus;
        private LocalDateTime sendTime;
        private String failReason;
        private LocalDateTime createTime;
    }
}
