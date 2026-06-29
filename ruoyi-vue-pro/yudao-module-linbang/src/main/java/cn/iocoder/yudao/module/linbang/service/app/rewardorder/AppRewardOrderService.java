package cn.iocoder.yudao.module.linbang.service.app.rewardorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderRespVO;

import javax.validation.Valid;

public interface AppRewardOrderService {

    Long createRewardOrder(Long authUserId, @Valid AppRewardOrderCreateReqVO reqVO);

    PageResult<AppRewardOrderRespVO> getRewardOrderPage(Long authUserId, AppRewardOrderPageReqVO reqVO);

    AppRewardOrderRespVO getRewardOrder(Long authUserId, Long id);

    Long participateRewardOrder(Long authUserId, @Valid AppRewardOrderParticipateReqVO reqVO);

    PageResult<AppRewardOrderParticipationRespVO> getParticipatedRewardOrderPage(Long authUserId,
                                                                                 AppRewardOrderParticipationPageReqVO reqVO);
}
