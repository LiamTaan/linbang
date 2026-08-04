package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 消息推送任务 分页查询 Request VO")
public class MessagePushTaskPageReqVO extends PageParam {

    @Schema(description = "消息推送任务名称")
    private String taskName;

    @Schema(description = "消息投放活动 ID；为空表示非活动消息")
    private Long campaignId;

    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @Schema(description = "推送任务状态筛选：PENDING 待执行、PROCESSING 处理中、SUCCESS 全部成功、PARTIAL_FAILED 部分失败、FAILED 全部失败、CANCELLED 已取消")
    private String status;

    @Schema(description = "执行状态：PENDING 待执行、PROCESSING 处理中、SUCCESS 全部成功、PARTIAL_FAILED 部分失败、FAILED 全部失败、CANCELLED 已取消")
    private String executeStatus;

    @Schema(description = "关联业务类型，例如 ORDER 主订单、ORDER_UNIT 订单单元、REFUND 退款、QUALIFICATION 资质、MARKETING 营销；具体值按所属模块业务枚举展示")
    private String bizType;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
