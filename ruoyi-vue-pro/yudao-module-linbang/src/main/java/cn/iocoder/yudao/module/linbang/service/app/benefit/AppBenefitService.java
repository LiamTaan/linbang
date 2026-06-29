package cn.iocoder.yudao.module.linbang.service.app.benefit;

import cn.iocoder.yudao.module.linbang.controller.app.benefit.vo.AppBenefitOverviewRespVO;

public interface AppBenefitService {

    AppBenefitOverviewRespVO getBenefitOverview(Long authUserId);
}
