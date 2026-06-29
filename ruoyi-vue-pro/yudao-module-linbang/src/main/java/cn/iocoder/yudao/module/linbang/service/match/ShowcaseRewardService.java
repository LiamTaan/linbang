package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;

public interface ShowcaseRewardService {

    boolean hasActiveReward(Long merchantId);

    void disableExpiredRewards();

    Long createReward(ShowcaseRewardDO rewardDO);

    PageResult<ShowcaseRewardDO> getRewardPage(cn.iocoder.yudao.framework.common.pojo.PageParam pageParam,
                                               Long merchantId, String auditStatus);

    java.util.List<ShowcaseRewardDO> getRewardListByMerchantId(Long merchantId);

    ShowcaseRewardDO getReward(Long id);

    void auditReward(Long id, String auditStatus, String auditRemark, String rejectReason, Long auditBy);
}
