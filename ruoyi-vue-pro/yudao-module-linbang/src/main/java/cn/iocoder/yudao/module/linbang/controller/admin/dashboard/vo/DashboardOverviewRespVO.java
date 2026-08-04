package cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "管理后台 - 运营看板 Response VO")
public class DashboardOverviewRespVO {

    @Schema(description = "今日新建订单数量，单位：单")
    private Long todayOrderCount;

    @Schema(description = "今日订单交易金额，单位：元，保留两位小数")
    private BigDecimal todayTradeAmount;

    @Schema(description = "今日新增用户数量，单位：人")
    private Long todayNewUserCount;

    @Schema(description = "订单完成率，单位：百分比，范围 0 至 100，保留两位小数")
    private BigDecimal completionRate;

    @Schema(description = "当前待审核业务总数，单位：条")
    private Long pendingAuditCount;

    @Schema(description = "待审核身份申请数量，单位：条")
    private Long pendingRoleApplyCount;

    @Schema(description = "即将到期的资质数量，单位：个")
    private Long expiringQualificationCount;

    @Schema(description = "待审核价格申报数量，单位：条")
    private Long pendingPriceReportCount;

    @Schema(description = "待执行或执行中的消息任务数量，单位：个")
    private Long pendingPushTaskCount;

    @Schema(description = "执行失败或部分失败的消息任务数量，单位：个")
    private Long failedPushTaskCount;

    @Schema(description = "当前待处理异常订单数量，单位：单")
    private Long abnormalOrderCount;

    @Schema(description = "当前待处理风险事件数量，单位：条")
    private Long riskAlertCount;

    @Schema(description = "待处理退款数量，单位：笔")
    private Long refundPendingCount;
}
