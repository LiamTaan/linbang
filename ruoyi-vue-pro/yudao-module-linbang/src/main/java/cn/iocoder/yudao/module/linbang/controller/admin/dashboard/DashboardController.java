package cn.iocoder.yudao.module.linbang.controller.admin.dashboard;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardOverviewRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardTrendPointRespVO;
import cn.iocoder.yudao.module.linbang.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 工作台")
@RestController
@RequestMapping("/dashboard")
@Validated
public class DashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/overview")
    @Operation(summary = "获得平台工作台概览")
    @PreAuthorize("@ss.hasPermission('linbang:dashboard:query')")
    public CommonResult<DashboardOverviewRespVO> getOverview() {
        return success(dashboardService.getOverview());
    }

    @GetMapping("/order-stat")
    @Operation(summary = "获得订单趋势")
    @PreAuthorize("@ss.hasPermission('linbang:dashboard:query')")
    public CommonResult<List<DashboardTrendPointRespVO>> getOrderStat() {
        return success(dashboardService.getOrderStat());
    }

    @GetMapping("/finance-stat")
    @Operation(summary = "获得财务趋势")
    @PreAuthorize("@ss.hasPermission('linbang:dashboard:query')")
    public CommonResult<List<DashboardTrendPointRespVO>> getFinanceStat() {
        return success(dashboardService.getFinanceStat());
    }

    @GetMapping("/user-stat")
    @Operation(summary = "获得用户趋势")
    @PreAuthorize("@ss.hasPermission('linbang:dashboard:query')")
    public CommonResult<List<DashboardTrendPointRespVO>> getUserStat() {
        return success(dashboardService.getUserStat());
    }
}
