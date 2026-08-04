package cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(description = "管理后台 - 消息优化 Response VO")
public class MessageOptimizationRespVO {

    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "优化对象类型：TEMPLATE 消息模板、CAMPAIGN 消息投放活动")
    private String refType;

    @Schema(description = "消息模板 ID；为空表示未使用模板")
    private Long templateId;

    @Schema(description = "消息投放活动 ID；为空表示非活动消息")
    private Long campaignId;

    @Schema(description = "消息业务场景编码，例如 ORDER_STATUS_CHANGED 订单状态变更、FINANCE_REFUND_SUCCESS 退款成功")
    private String sceneCode;

    @Schema(description = "消息分类：SYSTEM 系统、FINANCE 金额、ORDER 订单、COMPLIANCE 合规、DISPUTE 纠纷、MARKETING 营销、MEETING_NOTICE 会议通知、SUPERIOR_INSTRUCTION 上级指令")
    private String messageCategory;

    @Schema(description = "消息渠道：APP_POPUP 站内消息、WECHAT_MP_TEMPLATE 微信模板消息、SMS 短信、APP_VOICE App 语音朗读")
    private String channelType;

    @Schema(description = "统计开始日期，格式为 yyyy-MM-dd")
    private LocalDate statStartDate;

    @Schema(description = "统计结束日期，格式为 yyyy-MM-dd")
    private LocalDate statEndDate;

    @Schema(description = "触达率，单位：百分比，保留两位小数")
    private BigDecimal reachRate;

    @Schema(description = "点击率，单位：百分比，保留两位小数")
    private BigDecimal clickRate;

    @Schema(description = "消息优化建议说明")
    private String optimizationNote;

    @Schema(description = "建议的下一步处理动作")
    private String nextAction;

    @Schema(description = "配置归属方或维护责任方")
    private String owner;

    @Schema(description = "建议处理截止时间")
    private LocalDateTime deadline;

    @Schema(description = "记录最后更新时间")
    private LocalDateTime updateTime;
}
