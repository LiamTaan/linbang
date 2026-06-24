package cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 消息记录详情 Response VO")
@Data
public class MessageRecordDetailRespVO {

    private Long id;
    private Long templateId;
    private Long pushTaskId;
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
    private LocalDateTime updateTime;
    private TemplateSummaryRespVO template;

    @Data
    public static class TemplateSummaryRespVO {
        private Long id;
        private String templateCode;
        private String templateName;
        private String templateType;
        private String channelType;
        private String status;
    }
}
