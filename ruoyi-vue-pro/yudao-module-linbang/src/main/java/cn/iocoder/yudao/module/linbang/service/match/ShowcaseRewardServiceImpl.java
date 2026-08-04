package cn.iocoder.yudao.module.linbang.service.match;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.ShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.showcasereward.ShowcaseRewardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.showcasereward.ShowcaseRewardMapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.SHOWCASE_REWARD_AUDIT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.SHOWCASE_REWARD_NOT_EXISTS;

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
        showcaseRewardMapper.update(null, new LambdaUpdateWrapper<ShowcaseRewardDO>()
                .eq(ShowcaseRewardDO::getPriorityEnabled, true)
                .lt(ShowcaseRewardDO::getEffectiveEndTime, LocalDateTime.now())
                .set(ShowcaseRewardDO::getPriorityEnabled, false));
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
    public PageResult<ShowcaseRewardRespVO> getRewardPage(PageParam pageParam, Long merchantId, String auditStatus) {
        return BeanUtils.toBean(showcaseRewardMapper.selectPage(pageParam, merchantId, auditStatus), ShowcaseRewardRespVO.class);
    }

    @Override
    public List<ShowcaseRewardRespVO> getRewardListByMerchantId(Long merchantId) {
        return BeanUtils.toBean(showcaseRewardMapper.selectListByMerchantId(merchantId), ShowcaseRewardRespVO.class);
    }

    @Override
    public ShowcaseRewardRespVO getReward(Long id) {
        ShowcaseRewardDO reward = showcaseRewardMapper.selectById(id);
        return reward == null ? null : BeanUtils.toBean(reward, ShowcaseRewardRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReward(Long id, String auditStatus, String auditRemark, String rejectReason, Long auditBy) {
        ShowcaseRewardDO reward = showcaseRewardMapper.selectByIdForUpdate(id);
        if (reward == null) {
            throw exception(SHOWCASE_REWARD_NOT_EXISTS);
        }
        String normalizedAuditStatus = StrUtil.trimToEmpty(auditStatus).toUpperCase(Locale.ROOT);
        if (!"PENDING".equals(reward.getAuditStatus())
                || (!"APPROVED".equals(normalizedAuditStatus) && !"REJECTED".equals(normalizedAuditStatus))) {
            throw exception(SHOWCASE_REWARD_AUDIT_STATUS_INVALID);
        }
        if ("REJECTED".equals(normalizedAuditStatus) && StrUtil.isBlank(rejectReason)) {
            throw exception(SHOWCASE_REWARD_AUDIT_STATUS_INVALID);
        }
        LocalDateTime now = LocalDateTime.now();
        ShowcaseRewardDO updateObj = ShowcaseRewardDO.builder()
                .id(id)
                .auditStatus(normalizedAuditStatus)
                .auditRemark(auditRemark)
                .rejectReason("REJECTED".equals(normalizedAuditStatus) ? rejectReason : null)
                .auditBy(auditBy)
                .auditTime(now)
                .priorityEnabled(Objects.equals(normalizedAuditStatus, "APPROVED"))
                .effectiveStartTime(Objects.equals(normalizedAuditStatus, "APPROVED") ? now : reward.getEffectiveStartTime())
                .effectiveEndTime(Objects.equals(normalizedAuditStatus, "APPROVED") ? now.plusDays(7) : reward.getEffectiveEndTime())
                .build();
        showcaseRewardMapper.updateById(updateObj);
    }
}
