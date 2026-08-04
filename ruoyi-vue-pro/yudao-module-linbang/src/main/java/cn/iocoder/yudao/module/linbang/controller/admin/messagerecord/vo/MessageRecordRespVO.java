package cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 消息记录 Response VO")
public class MessageRecordRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("模板ID")
    @Schema(description = "消息模板 ID；为空表示未使用模板")
    private Long templateId;

    @ExcelProperty("活动ID")
    @Schema(description = "消息投放活动 ID；为空表示非活动消息")
    private Long campaignId;

    @ExcelProperty("推送任务ID")
    @Schema(description = "消息推送任务 ID；为空表示非任务消息")
    private Long pushTaskId;

    @ExcelProperty("接收用户ID")
    @Schema(description = "消息接收用户 ID，关联平台用户")
    private Long receiverUserId;

    @ExcelProperty("接收用户编号")
    @Schema(description = "消息接收用户编号")
    private String receiverUserNo;

    @ExcelProperty("接收用户昵称")
    @Schema(description = "消息接收用户昵称")
    private String receiverUserNickname;

    @ExcelProperty("接收用户手机号")
    @Schema(description = "消息接收用户手机号")
    private String receiverUserMobile;

    @ExcelProperty("渠道类型")
    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @ExcelProperty("场景编码")
    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @ExcelProperty("消息分类")
    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @ExcelProperty("业务类型")
    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @ExcelProperty("业务ID")
    @Schema(description = "关联业务对象 ID；由 bizType 指明对象类型")
    private Long bizId;

    @ExcelProperty("发送状态")
    @Schema(description = "消息发送状态：SUCCESS 成功、FAILED 失败、PENDING 待发送")
    private String sendStatus;

    @ExcelProperty("发送时间")
    @Schema(description = "消息实际发送时间")
    private LocalDateTime sendTime;

    @ExcelProperty("失败原因")
    @Schema(description = "任务失败原因")
    private String failReason;

    @ExcelProperty("已读状态")
    @Schema(description = "消息阅读状态：UNREAD 未读、READ 已读")
    private String readStatus;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
