package cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DashboardTrendPointRespVO {

    private String statDate;

    private Long orderCount;

    private BigDecimal tradeAmount;

    private Long newUserCount;

    private BigDecimal withdrawAmount;
}
