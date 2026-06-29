package cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 消息记录详情 Response VO")
@Data
public class MessageRecordDetailRespVO {

    @Schema(description = "消息记录 ID", example = "1")
    private Long id;
    @Schema(description = "消息模板 ID；为空时表示按兜底任务名直接发送", example = "10")
    private Long templateId;
    @Schema(description = "投放活动 ID", example = "100")
    private Long campaignId;
    @Schema(description = "消息推送任务 ID，关联批量或单次推送任务", example = "100")
    private Long pushTaskId;
    @Schema(description = "接收用户 ID", example = "5001")
    private Long receiverUserId;
    @Schema(description = "接收用户编号", example = "LBU123456")
    private String receiverUserNo;
    @Schema(description = "接收用户昵称", example = "邻里用户")
    private String receiverUserNickname;
    @Schema(description = "接收用户手机号", example = "13800138000")
    private String receiverUserMobile;
    @Schema(description = "场景编码", example = "ORDER_STATUS_CHANGED")
    private String sceneCode;
    @Schema(description = OpenApiSchemaConstants.MESSAGE_CATEGORY, example = "ORDER")
    private String messageCategory;
    @Schema(description = OpenApiSchemaConstants.MESSAGE_CHANNEL_TYPE, example = "APP_POPUP")
    private String channelType;
    @Schema(description = "业务类型，例如 ORDER 订单通知、WITHDRAW 提现通知、AUDIT 审核通知", example = "ORDER")
    private String bizType;
    @Schema(description = "业务 ID，关联订单、提现单、审核单等业务主键", example = "2001")
    private Long bizId;
    @Schema(description = OpenApiSchemaConstants.MESSAGE_SEND_STATUS, example = "SUCCESS")
    private String sendStatus;
    @Schema(description = "实际发送时间；成功发送后返回")
    private LocalDateTime sendTime;
    @Schema(description = "失败原因；发送失败时返回")
    private String failReason;
    @Schema(description = "消息标题")
    private String title;
    @Schema(description = "消息内容快照")
    private String contentSnapshot;
    @Schema(description = "路由类型")
    private String routeType;
    @Schema(description = "路由值")
    private String routeValue;
    @Schema(description = OpenApiSchemaConstants.MESSAGE_READ_STATUS, example = "UNREAD")
    private String readStatus;
    @Schema(description = "已读时间")
    private LocalDateTime readTime;
    @Schema(description = "曝光时间")
    private LocalDateTime exposedTime;
    @Schema(description = "点击时间")
    private LocalDateTime clickTime;
    @Schema(description = "语音播放时间")
    private LocalDateTime voicePlayedTime;
    @Schema(description = "语音文本")
    private String voiceText;
    @Schema(description = "渠道回执ID")
    private String providerMessageId;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "模板摘要")
    private TemplateSummaryRespVO template;

    @Data
    public static class TemplateSummaryRespVO {
        @Schema(description = "模板 ID", example = "10")
        private Long id;
        @Schema(description = "模板编码", example = "ORDER_ACCEPT_NOTICE")
        private String templateCode;
        @Schema(description = "模板名称", example = "接单通知")
        private String templateName;
        @Schema(description = "场景编码", example = "ORDER_STATUS_CHANGED")
        private String sceneCode;
        @Schema(description = OpenApiSchemaConstants.MESSAGE_CATEGORY, example = "ORDER")
        private String messageCategory;
        @Schema(description = OpenApiSchemaConstants.MESSAGE_TEMPLATE_TYPE, example = "BIZ")
        private String templateType;
        @Schema(description = OpenApiSchemaConstants.MESSAGE_CHANNEL_TYPE, example = "APP_POPUP")
        private String channelType;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
    }
}
