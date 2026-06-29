package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 消息推送任务详情 Response VO")
@Data
public class MessagePushTaskDetailRespVO {

    @Schema(description = "推送任务 ID", example = "100")
    private Long id;

    @Schema(description = "任务名称，优先取消息模板名称，兜底为业务传入任务名", example = "接单通知")
    private String taskName;

    @Schema(description = "投放活动 ID", example = "100")
    private Long campaignId;

    @Schema(description = "场景编码", example = "ORDER_STATUS_CHANGED")
    private String sceneCode;

    @Schema(description = OpenApiSchemaConstants.MESSAGE_CATEGORY, example = "ORDER")
    private String messageCategory;

    @Schema(description = OpenApiSchemaConstants.MESSAGE_TARGET_SCOPE, example = "SINGLE_USER")
    private String targetScope;

    @Schema(description = OpenApiSchemaConstants.MESSAGE_CHANNEL_TYPE, example = "APP_POPUP")
    private String channelType;

    @Schema(description = "消息模板 ID；为空时表示未命中启用模板，按兜底任务名发送", example = "10")
    private Long templateId;

    @Schema(description = "模板名称", example = "接单通知")
    private String templateName;

    @Schema(description = "业务类型，例如 ORDER 订单通知、WITHDRAW 提现通知、AUDIT 审核通知", example = "ORDER")
    private String bizType;

    @Schema(description = "业务 ID，关联本次推送对应的业务单据主键", example = "2001")
    private Long bizId;

    @Schema(description = OpenApiSchemaConstants.MESSAGE_PUSH_TASK_STATUS, example = "SUCCESS")
    private String status;

    @Schema(description = OpenApiSchemaConstants.MESSAGE_PUSH_TASK_STATUS, example = "SUCCESS")
    private String executeStatus;

    @Schema(description = "计划发送时间")
    private LocalDateTime plannedSendTime;

    @Schema(description = "任务执行完成时间")
    private LocalDateTime executeTime;

    @Schema(description = "成功发送数量", example = "12")
    private Integer successCount;

    @Schema(description = "失败发送数量", example = "1")
    private Integer failCount;

    @Schema(description = "计划触达人数", example = "200")
    private Integer plannedAudienceCount;

    @Schema(description = "触达人数", example = "180")
    private Integer reachedCount;

    @Schema(description = "点击人数", example = "35")
    private Integer clickedCount;

    @Schema(description = "已读人数", example = "80")
    private Integer readCount;

    @Schema(description = "语音播放人数", example = "20")
    private Integer voicePlayedCount;

    @Schema(description = "创建备注，记录业务触发来源或运营说明")
    private String creatorRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "最近消息记录摘要")
    private List<RecordSummary> recentRecords;

    @Data
    public static class RecordSummary {
        @Schema(description = "消息记录 ID", example = "1")
        private Long id;
        @Schema(description = "接收用户 ID", example = "5001")
        private Long receiverUserId;
        @Schema(description = "接收用户编号", example = "LBU123456")
        private String receiverUserNo;
        @Schema(description = "接收用户昵称", example = "邻里用户")
        private String receiverUserNickname;
        @Schema(description = "接收用户手机号", example = "13800138000")
        private String receiverUserMobile;
        @Schema(description = OpenApiSchemaConstants.MESSAGE_SEND_STATUS, example = "SUCCESS")
        private String sendStatus;
        @Schema(description = "失败原因；发送失败时返回")
        private String failReason;
        @Schema(description = "发送时间")
        private LocalDateTime sendTime;
    }
}
