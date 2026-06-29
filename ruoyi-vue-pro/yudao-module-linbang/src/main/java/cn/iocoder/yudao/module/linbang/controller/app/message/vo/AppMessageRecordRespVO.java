package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 消息记录 Response VO")
@Data
public class AppMessageRecordRespVO {

    @Schema(description = "消息记录 ID", example = "1")
    private Long id;

    @Schema(description = "消息模板 ID", example = "10")
    private Long templateId;

    @Schema(description = "投放活动 ID", example = "100")
    private Long campaignId;

    @Schema(description = "接收用户 ID", example = "1001")
    private Long receiverUserId;

    @Schema(description = "渠道类型，例如 APP_POPUP、SMS、WECHAT_MP_TEMPLATE、APP_VOICE", example = "APP_POPUP")
    private String channelType;

    @Schema(description = "消息分类，例如 ORDER、FINANCE、DISPUTE", example = "ORDER")
    private String messageCategory;

    @Schema(description = "场景编码", example = "ORDER_STATUS_CHANGED")
    private String sceneCode;

    @Schema(description = "业务类型，例如 ORDER 订单通知、WITHDRAW 提现通知、AUDIT 审核通知", example = "ORDER")
    private String bizType;

    @Schema(description = "业务 ID，关联具体订单、提现单或审核单据", example = "2001")
    private Long bizId;

    @Schema(description = "发送状态，常见值如 SUCCESS 发送成功、FAILED 发送失败、PENDING 待发送", example = "SUCCESS")
    private String sendStatus;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "失败原因，发送失败时返回")
    private String failReason;

    @Schema(description = "标题", example = "订单状态更新")
    private String title;

    @Schema(description = "正文快照")
    private String contentSnapshot;

    @Schema(description = "已读状态", example = "UNREAD")
    private String readStatus;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
