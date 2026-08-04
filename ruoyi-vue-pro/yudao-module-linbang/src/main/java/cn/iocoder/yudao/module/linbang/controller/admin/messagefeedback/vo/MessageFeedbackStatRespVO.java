package cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "管理后台 - 消息反馈统计 Response VO")
public class MessageFeedbackStatRespVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "统计日期，格式为 yyyy-MM-dd")
    private LocalDate statDate;

    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @Schema(description = "消息模板 ID；为空表示未使用模板")
    private Long templateId;

    @Schema(description = "消息投放活动 ID；为空表示非活动消息")
    private Long campaignId;

    @Schema(description = "消息推送任务 ID；为空表示非任务消息")
    private Long pushTaskId;

    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @Schema(description = "计划触达的去重用户数量，单位：人")
    private Integer plannedAudienceCount;

    @Schema(description = "实际送达的去重用户数量，单位：人")
    private Integer reachedCount;

    @Schema(description = "已点击消息的去重用户数量，单位：人")
    private Integer clickedCount;

    @Schema(description = "已读消息的去重用户数量，单位：人")
    private Integer readCount;

    @Schema(description = "已完成语音播放的去重用户数量，单位：人")
    private Integer voicePlayedCount;

    @Schema(description = "触达率，单位：百分比，保留两位小数")
    private BigDecimal reachRate;

    @Schema(description = "点击率，单位：百分比，保留两位小数")
    private BigDecimal clickRate;

    @Schema(description = "阅读率，单位：百分比，保留两位小数")
    private BigDecimal readRate;
}
