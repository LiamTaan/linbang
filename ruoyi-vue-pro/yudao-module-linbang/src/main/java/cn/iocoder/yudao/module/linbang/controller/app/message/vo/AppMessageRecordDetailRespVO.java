package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 消息记录详情 Response VO")
@Data
public class AppMessageRecordDetailRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "模板 ID", example = "10")
    private Long templateId;

    @Schema(description = "投放活动 ID", example = "100")
    private Long campaignId;

    @Schema(description = "接收用户 ID", example = "1001")
    private Long receiverUserId;

    @Schema(description = "渠道类型，例如 APP_POPUP、SMS、WECHAT_MP_TEMPLATE、APP_VOICE", example = "APP_POPUP")
    private String channelType;

    @Schema(description = "消息分类", example = "ORDER")
    private String messageCategory;

    @Schema(description = "场景编码", example = "ORDER_STATUS_CHANGED")
    private String sceneCode;

    @Schema(description = "业务类型，例如 ORDER 订单通知、WITHDRAW 提现通知、AUDIT 审核通知", example = "ORDER")
    private String bizType;

    @Schema(description = "业务 ID，关联具体订单、提现单或审核单据", example = "2001")
    private Long bizId;

    @Schema(description = "发送状态，按消息发送结果字典展示，常见值如 SUCCESS 发送成功、FAILED 发送失败", example = "SUCCESS")
    private String sendStatus;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "失败原因，发送失败时返回")
    private String failReason;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "正文快照")
    private String contentSnapshot;

    @Schema(description = "路由类型", example = "APP_PAGE")
    private String routeType;

    @Schema(description = "路由值", example = "/pages/order/detail?id=1001")
    private String routeValue;

    @Schema(description = "已读状态", example = "UNREAD")
    private String readStatus;

    @Schema(description = "已读时间")
    private LocalDateTime readTime;

    @Schema(description = "曝光时间")
    private LocalDateTime exposedTime;

    @Schema(description = "点击时间")
    private LocalDateTime clickTime;

    @Schema(description = "语音播放时间")
    private LocalDateTime voicePlayedTime;

    @Schema(description = "语音朗读文本")
    private String voiceText;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "模板摘要")
    private TemplateRespVO template;

    @Data
    public static class TemplateRespVO {

        @Schema(description = "模板 ID", example = "10")
        private Long id;

        @Schema(description = "模板编码", example = "ORDER_ACCEPT_NOTICE")
        private String templateCode;

        @Schema(description = "模板名称", example = "接单通知")
        private String templateName;

        @Schema(description = "模板类型，例如 BIZ 业务消息模板、MARKETING 营销消息模板", example = "BIZ")
        private String templateType;

        @Schema(description = "场景编码", example = "ORDER_STATUS_CHANGED")
        private String sceneCode;

        @Schema(description = "消息分类", example = "ORDER")
        private String messageCategory;

        @Schema(description = "渠道类型，例如 APP_POPUP、SMS、WECHAT_MP_TEMPLATE、APP_VOICE", example = "APP_POPUP")
        private String channelType;

        @Schema(description = "标题模板")
        private String titleTemplate;

        @Schema(description = "模板内容")
        private String contentTemplate;

        @Schema(description = "模板状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
        private String status;
    }
}
