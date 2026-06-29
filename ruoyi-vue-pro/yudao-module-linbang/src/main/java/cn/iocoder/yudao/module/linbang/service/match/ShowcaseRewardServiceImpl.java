package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.showcasereward.ShowcaseRewardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ShowcaseRewardServiceImpl implements ShowcaseRewardService {

    @Resource
    private ShowcaseRewardMapper showcaseRewardMapper;

    @Override
    public boolean hasActiveReward(Long merchantId) {
        return merchantId != null && showcaseRewardMapper.selectActiveByMerchantId(merchantId, LocalDateTime.now()) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableExpiredRewards() {
        List<ShowcaseRewardDO> expired = showcaseRewardMapper.selectList(new LambdaQueryWrapperX<ShowcaseRewardDO>()
                .eq(ShowcaseRewardDO::getPriorityEnabled, true)
                .lt(ShowcaseRewardDO::getEffectiveEndTime, LocalDateTime.now()));
        for (ShowcaseRewardDO item : expired) {
            showcaseRewardMapper.updateById(ShowcaseRewardDO.builder()
                    .id(item.getId())
                    .priorityEnabled(false)
                    .build());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReward(ShowcaseRewardDO rewardDO) {
        rewardDO.setAuditStatus("PENDING");
        rewardDO.setPriorityEnabled(false);
        showcaseRewardMapper.insert(rewardDO);
        return rewardDO.getId();
    }

    @Override
    public PageResult<ShowcaseRewardDO> getRewardPage(PageParam pageParam, Long merchantId, String auditStatus) {
        return showcaseRewardMapper.selectPage(pageParam, merchantId, auditStatus);
    }

    @Override
    public List<ShowcaseRewardDO> getRewardListByMerchantId(Long merchantId) {
        return showcaseRewardMapper.selectListByMerchantId(merchantId);
    }

    @Override
    public ShowcaseRewardDO getReward(Long id) {
        return showcaseRewardMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReward(Long id, String auditStatus, String auditRemark, String rejectReason, Long auditBy) {
        ShowcaseRewardDO reward = showcaseRewardMapper.selectById(id);
        if (reward == null) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        ShowcaseRewardDO updateObj = ShowcaseRewardDO.builder()
                .id(id)
                .auditStatus(auditStatus)
                .auditRemark(auditRemark)
                .rejectReason(rejectReason)
                .auditBy(auditBy)
                .auditTime(now)
                .priorityEnabled(Objects.equals(auditStatus, "APPROVED"))
                .effectiveStartTime(Objects.equals(auditStatus, "APPROVED") ? now : reward.getEffectiveStartTime())
                .effectiveEndTime(Objects.equals(auditStatus, "APPROVED") ? now.plusDays(7) : reward.getEffectiveEndTime())
                .build();
        showcaseRewardMapper.updateById(updateObj);
    }
}
