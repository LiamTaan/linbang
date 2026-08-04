package cn.iocoder.yudao.module.linbang.controller.admin.messagerecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 消息记录 分页查询 Request VO")
public class MessageRecordPageReqVO extends PageParam {

    @Schema(description = "消息模板 ID；为空表示未使用模板")
    private Long templateId;

    @Schema(description = "消息投放活动 ID；为空表示非活动消息")
    private Long campaignId;

    @Schema(description = "接收用户筛选关键词，可匹配用户编号、昵称或手机号")
    private String receiverUserKeyword;

    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @Schema(description = "消息阅读状态：UNREAD 未读、READ 已读")
    private String readStatus;

    @Schema(description = "消息发送状态：SUCCESS 成功、FAILED 失败、PENDING 待发送")
    private String sendStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
