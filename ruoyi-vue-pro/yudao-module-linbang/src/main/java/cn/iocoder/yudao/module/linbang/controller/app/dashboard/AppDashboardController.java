package cn.iocoder.yudao.module.linbang.controller.app.dashboard;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardOverviewRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardTrendPointRespVO;
import cn.iocoder.yudao.module.linbang.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 平台看板")
@RestController
@RequestMapping("/dashboard")
public class AppDashboardController {

    @Resource
    private DashboardService dashboardService;

    @GetMapping("/overview")
    @Operation(summary = "获取平台看板概览")
    public CommonResult<DashboardOverviewRespVO> getOverview() {
        return success(dashboardService.getOverview());
    }

    @GetMapping("/order-stat")
    @Operation(summary = "获取平台订单趋势")
    public CommonResult<List<DashboardTrendPointRespVO>> getOrderStat() {
        return success(dashboardService.getOrderStat());
    }

    @GetMapping("/finance-stat")
    @Operation(summary = "获取平台财务趋势")
    public CommonResult<List<DashboardTrendPointRespVO>> getFinanceStat() {
        return success(dashboardService.getFinanceStat());
    }

    @GetMapping("/user-stat")
    @Operation(summary = "获取平台用户趋势")
    public CommonResult<List<DashboardTrendPointRespVO>> getUserStat() {
        return success(dashboardService.getUserStat());
    }
}
