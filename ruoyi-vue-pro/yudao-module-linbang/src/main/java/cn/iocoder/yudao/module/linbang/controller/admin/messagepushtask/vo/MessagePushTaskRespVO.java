package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 消息推送任务 Response VO")
public class MessagePushTaskRespVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "消息推送任务名称")
    private String taskName;

    @Schema(description = "消息投放活动 ID；为空表示非活动消息")
    private Long campaignId;

    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @Schema(description = "推送范围：SINGLE_USER 单用户；其他范围按消息任务范围字典扩展")
    private String targetScope;

    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @Schema(description = "消息模板 ID；为空表示未使用模板")
    private Long templateId;

    @Schema(description = "消息模板名称")
    private String templateName;

    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @Schema(description = "推送任务状态：PENDING 待执行、PROCESSING 处理中、SUCCESS 全部成功、PARTIAL_FAILED 部分失败、FAILED 全部失败、CANCELLED 已取消")
    private String status;

    @Schema(description = "执行状态：PENDING 待执行、PROCESSING 处理中、SUCCESS 全部成功、PARTIAL_FAILED 部分失败、FAILED 全部失败、CANCELLED 已取消")
    private String executeStatus;

    @Schema(description = "计划发送时间")
    private LocalDateTime plannedSendTime;

    @Schema(description = "任务实际执行时间")
    private LocalDateTime executeTime;

    @Schema(description = "发送成功数量，单位：条")
    private Integer successCount;

    @Schema(description = "发送失败数量，单位：条")
    private Integer failCount;

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

    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
