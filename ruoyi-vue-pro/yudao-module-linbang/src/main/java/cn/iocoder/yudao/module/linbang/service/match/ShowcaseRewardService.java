package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;

import java.util.List;

public interface ShowcaseRewardService {

    boolean hasActiveReward(Long merchantId);

    void disableExpiredRewards();

    Long createReward(ShowcaseRewardDO rewardDO);

    PageResult<ShowcaseRewardRespVO> getRewardPage(PageParam pageParam, Long merchantId, String auditStatus);

    List<ShowcaseRewardRespVO> getRewardListByMerchantId(Long merchantId);

    ShowcaseRewardRespVO getReward(Long id);

    void auditReward(Long id, String auditStatus, String auditRemark, String rejectReason, Long auditBy);
}
