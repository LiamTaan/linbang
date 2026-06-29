package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.prioritypool.vo.AppPriorityPoolCurrentRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardRespVO;

import javax.validation.Valid;

public interface AppMerchantPriorityFacadeService {

    AppPriorityPoolCurrentRespVO getCurrentPriorityPool(Long authUserId);

    Long createShowcaseReward(Long authUserId, @Valid AppShowcaseRewardCreateReqVO reqVO);

    PageResult<AppShowcaseRewardRespVO> getShowcaseRewardPage(Long authUserId);

    AppShowcaseRewardRespVO getShowcaseReward(Long authUserId, Long id);
}
