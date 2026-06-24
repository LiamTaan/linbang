package cn.iocoder.yudao.module.linbang.service.dashboard;

import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardOverviewRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.dashboard.vo.DashboardTrendPointRespVO;

import java.util.List;

public interface DashboardService {

    DashboardOverviewRespVO getOverview();

    List<DashboardTrendPointRespVO> getOrderStat();

    List<DashboardTrendPointRespVO> getFinanceStat();

    List<DashboardTrendPointRespVO> getUserStat();
}
