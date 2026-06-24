package cn.iocoder.yudao.module.linbang.service.riskrule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo.RiskRuleDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo.RiskRulePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo.RiskRuleSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.blacklist.BlacklistMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskevent.RiskEventMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskrule.RiskRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.RISK_RULE_NOT_EXISTS;

/**
 * 风控规则表 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class RiskRuleServiceImpl implements RiskRuleService {

    @Resource
    private RiskRuleMapper riskRuleMapper;
    @Resource
    private RiskEventMapper riskEventMapper;
    @Resource
    private BlacklistMapper blacklistMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public Long createRiskRule(RiskRuleSaveReqVO createReqVO) {
        // 插入
        RiskRuleDO riskRule = BeanUtils.toBean(createReqVO, RiskRuleDO.class);
        riskRuleMapper.insert(riskRule);

        // 返回
        return riskRule.getId();
    }

    @Override
    public void updateRiskRule(RiskRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateRiskRuleExists(updateReqVO.getId());
        // 更新
        RiskRuleDO updateObj = BeanUtils.toBean(updateReqVO, RiskRuleDO.class);
        riskRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteRiskRule(Long id) {
        // 校验存在
        validateRiskRuleExists(id);
        // 删除
        riskRuleMapper.deleteById(id);
    }

    @Override
        public void deleteRiskRuleListByIds(List<Long> ids) {
        // 删除
        riskRuleMapper.deleteByIds(ids);
        }


    private void validateRiskRuleExists(Long id) {
        if (riskRuleMapper.selectById(id) == null) {
            throw exception(RISK_RULE_NOT_EXISTS);
        }
    }

    @Override
    public RiskRuleDO getRiskRule(Long id) {
        return riskRuleMapper.selectById(id);
    }

    @Override
    public RiskRuleDetailRespVO getRiskRuleDetail(Long id) {
        RiskRuleDO riskRule = riskRuleMapper.selectById(id);
        if (riskRule == null) {
            throw exception(RISK_RULE_NOT_EXISTS);
        }

        List<RiskEventDO> riskEvents = riskEventMapper.selectList(new LambdaQueryWrapperX<RiskEventDO>()
                .eq(RiskEventDO::getHitRuleCode, riskRule.getRuleCode())
                .orderByDesc(RiskEventDO::getId));
        List<RiskEventDO> recentEvents = riskEvents.size() > 10 ? riskEvents.subList(0, 10) : riskEvents;
        long pendingEventCount = riskEvents.stream()
                .filter(event -> "PENDING".equalsIgnoreCase(event.getStatus()))
                .count();

        Set<Long> userIds = new LinkedHashSet<>();
        for (RiskEventDO riskEvent : riskEvents) {
            if ("USER".equalsIgnoreCase(riskEvent.getBizType()) && riskEvent.getBizId() != null) {
                userIds.add(riskEvent.getBizId());
            }
        }

        List<BlacklistDO> blacklists = userIds.isEmpty() ? Collections.emptyList() : blacklistMapper.selectList(
                new LambdaQueryWrapperX<BlacklistDO>()
                        .in(BlacklistDO::getUserId, userIds)
                        .orderByDesc(BlacklistDO::getId));
        List<BlacklistDO> relatedBlacklists = blacklists.size() > 10 ? blacklists.subList(0, 10) : blacklists;
        Map<Long, MemberUserDO> userMap = blacklists.isEmpty() ? Collections.emptyMap() : convertMap(
                memberUserMapper.selectBatchIds(userIds), MemberUserDO::getId);

        RiskRuleDetailRespVO respVO = BeanUtils.toBean(riskRule, RiskRuleDetailRespVO.class);
        respVO.setHitCount(riskEvents.size());
        respVO.setPendingEventCount((int) pendingEventCount);
        respVO.setUserBlacklistCount(blacklists.size());
        respVO.setRecentEvents(RiskRuleDetailAssembler.buildRiskEvents(recentEvents, userMap));
        respVO.setRelatedBlacklists(RiskRuleDetailAssembler.buildBlacklists(relatedBlacklists, userMap));
        return respVO;
    }

    @Override
    public PageResult<RiskRuleDO> getRiskRulePage(RiskRulePageReqVO pageReqVO) {
        return riskRuleMapper.selectPage(pageReqVO);
    }

}
