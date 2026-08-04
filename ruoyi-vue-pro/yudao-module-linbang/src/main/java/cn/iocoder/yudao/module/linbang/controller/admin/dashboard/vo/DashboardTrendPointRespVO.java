package cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@Schema(description = "管理后台 - 运营看板 Response VO")
public class DashboardTrendPointRespVO {

    @Schema(description = "统计日期，格式为 yyyy-MM-dd")
    private String statDate;

    @Schema(description = "订单数量，单位：单")
    private Long orderCount;

    @Schema(description = "当日订单交易金额，单位：元，保留两位小数")
    private BigDecimal tradeAmount;

    @Schema(description = "新增用户数量，单位：人")
    private Long newUserCount;

    @Schema(description = "当日提现申请金额，单位：元，保留两位小数")
    private BigDecimal withdrawAmount;
}
