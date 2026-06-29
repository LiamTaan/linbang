package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchstrategy.MatchStrategyDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.matchstrategy.MatchStrategyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Validated
public class MatchStrategyServiceImpl implements MatchStrategyService {

    @Resource
    private MatchStrategyMapper matchStrategyMapper;

    @Override
    public MatchStrategyDO getEnabledStrategy() {
        return matchStrategyMapper.selectEnabledOne();
    }

    @Override
    public List<StageRule> getStageRules() {
        MatchStrategyDO strategy = getEnabledStrategy();
        if (strategy == null) {
            return Collections.emptyList();
        }
        List<StageRule> rules = JsonUtils.parseArray(strategy.getStageConfigJson(), StageRule.class);
        return rules.stream()
                .sorted(Comparator.comparing(StageRule::getStageNo))
                .collect(Collectors.toList());
    }

    @Override
    public String getFlowAdviceTemplate() {
        MatchStrategyDO strategy = getEnabledStrategy();
        return strategy != null ? strategy.getFlowAdviceTemplate() : "";
    }

    @Override
    public boolean isAutoRefundEnabled() {
        MatchStrategyDO strategy = getEnabledStrategy();
        return strategy != null && Boolean.TRUE.equals(strategy.getAutoRefundEnabled());
    }

    @Override
    public int getAutoRefundRetryTimes() {
        MatchStrategyDO strategy = getEnabledStrategy();
        return strategy != null && strategy.getAutoRefundRetryTimes() != null ? strategy.getAutoRefundRetryTimes() : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEnabledStrategy(MatchStrategyDO strategy) {
        MatchStrategyDO current = getEnabledStrategy();
        if (current == null) {
            strategy.setStatus("ENABLE");
            matchStrategyMapper.insert(strategy);
            return;
        }
        strategy.setId(current.getId());
        strategy.setStatus("ENABLE");
        matchStrategyMapper.updateById(strategy);
    }
}
