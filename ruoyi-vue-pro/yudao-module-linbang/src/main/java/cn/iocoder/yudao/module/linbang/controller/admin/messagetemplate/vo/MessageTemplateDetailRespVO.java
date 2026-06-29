package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 消息模板详情 Response VO")
@Data
public class MessageTemplateDetailRespVO {

    @Schema(description = "模板 ID", example = "10")
    private Long id;
    @Schema(description = "模板编码，供业务代码按编码触发消息模板", example = "ORDER_ACCEPT_NOTICE")
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
    @Schema(description = "标题模板")
    private String titleTemplate;
    @Schema(description = "模板正文内容，可包含业务变量占位符")
    private String contentTemplate;
    @Schema(description = "路由类型", example = "APP_PAGE")
    private String routeType;
    @Schema(description = "路由值", example = "/pages/order/detail?id=1001")
    private String routeValue;
    @Schema(description = "公众号模板ID")
    private String mpTemplateId;
    @Schema(description = "短信模板编码")
    private String smsTemplateCode;
    @Schema(description = "语音朗读模板")
    private String voiceTextTemplate;
    @Schema(description = "排序", example = "10")
    private Integer sort;
    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "累计发送次数", example = "120")
    private Integer sendCount;
    @Schema(description = "成功发送次数", example = "118")
    private Integer successCount;
    @Schema(description = "失败发送次数", example = "2")
    private Integer failedCount;
    @Schema(description = "待发送次数", example = "0")
    private Integer pendingCount;
    @Schema(description = "按渠道汇总的发送统计")
    private List<ChannelStatRespVO> channelStats;
    @Schema(description = "最近消息发送记录")
    private List<MessageRecordSimpleRespVO> recentRecords;

    @Data
    public static class ChannelStatRespVO {
        @Schema(description = OpenApiSchemaConstants.MESSAGE_CHANNEL_TYPE, example = "APP_POPUP")
        private String channelType;
        @Schema(description = "该渠道下的消息记录数", example = "120")
        private Integer recordCount;
    }

    @Data
    public static class MessageRecordSimpleRespVO {
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
        @Schema(description = OpenApiSchemaConstants.MESSAGE_CHANNEL_TYPE, example = "APP_POPUP")
        private String channelType;
        @Schema(description = "业务类型，例如 ORDER 订单通知、WITHDRAW 提现通知、AUDIT 审核通知", example = "ORDER")
        private String bizType;
        @Schema(description = "业务 ID", example = "2001")
        private Long bizId;
        @Schema(description = OpenApiSchemaConstants.MESSAGE_SEND_STATUS, example = "SUCCESS")
        private String sendStatus;
        @Schema(description = "发送时间")
        private LocalDateTime sendTime;
        @Schema(description = "失败原因；发送失败时返回")
        private String failReason;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
