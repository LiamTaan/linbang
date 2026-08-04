package cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 消息反馈统计 分页查询 Request VO")
public class MessageFeedbackStatPageReqVO extends PageParam {

    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @Schema(description = "消息模板 ID；为空表示未使用模板")
    private Long templateId;

    @Schema(description = "消息投放活动 ID；为空表示非活动消息")
    private Long campaignId;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    @Schema(description = "统计日期，格式为 yyyy-MM-dd")
    private LocalDate[] statDate;
}
