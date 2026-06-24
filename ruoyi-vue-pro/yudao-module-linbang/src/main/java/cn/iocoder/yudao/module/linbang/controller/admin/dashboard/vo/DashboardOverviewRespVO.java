package cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardOverviewRespVO {

    private Long todayOrderCount;

    private BigDecimal todayTradeAmount;

    private Long todayNewUserCount;

    private BigDecimal completionRate;

    private Long pendingAuditCount;

    private Long pendingRoleApplyCount;

    private Long expiringQualificationCount;

    private Long pendingPriceReportCount;

    private Long pendingPushTaskCount;

    private Long failedPushTaskCount;

    private Long abnormalOrderCount;

    private Long riskAlertCount;

    private Long refundPendingCount;
}
