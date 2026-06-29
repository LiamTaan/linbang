package cn.iocoder.yudao.module.linbang.dal.mysql.matchstrategy;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.matchstrategy.MatchStrategyDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MatchStrategyMapper extends BaseMapperX<MatchStrategyDO> {

    default MatchStrategyDO selectEnabledOne() {
        return selectOne(new LambdaQueryWrapperX<MatchStrategyDO>()
                .eq(MatchStrategyDO::getStatus, "ENABLE")
                .orderByDesc(MatchStrategyDO::getId)
                .last("LIMIT 1"));
    }

    default MatchStrategyDO selectByStrategyCode(String strategyCode) {
        return selectOne(MatchStrategyDO::getStrategyCode, strategyCode);
    }
}
