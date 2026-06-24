package cn.iocoder.yudao.module.linbang.dal.mysql.riskevent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventBizMatchCondition;
import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RiskEventMapper extends BaseMapperX<RiskEventDO> {

    default PageResult<RiskEventDO> selectPage(RiskEventPageReqVO reqVO, List<RiskEventBizMatchCondition> bizMatchConditions) {
        LambdaQueryWrapperX<RiskEventDO> queryWrapper = new LambdaQueryWrapperX<RiskEventDO>()
                .eqIfPresent(RiskEventDO::getBizType, reqVO.getBizType())
                .eqIfPresent(RiskEventDO::getRiskType, reqVO.getRiskType())
                .eqIfPresent(RiskEventDO::getRiskLevel, reqVO.getRiskLevel())
                .eqIfPresent(RiskEventDO::getHitRuleCode, reqVO.getHitRuleCode())
                .eqIfPresent(RiskEventDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(RiskEventDO::getCreateTime, reqVO.getCreateTime());
        if (bizMatchConditions != null && !bizMatchConditions.isEmpty()) {
            queryWrapper.and(wrapper -> {
                boolean first = true;
                for (RiskEventBizMatchCondition condition : bizMatchConditions) {
                    if (condition == null || condition.getBizIds() == null || condition.getBizIds().isEmpty()) {
                        continue;
                    }
                    if (first) {
                        wrapper.eq(RiskEventDO::getBizType, condition.getBizType())
                                .in(RiskEventDO::getBizId, condition.getBizIds());
                        first = false;
                    } else {
                        wrapper.or(orWrapper -> orWrapper.eq(RiskEventDO::getBizType, condition.getBizType())
                                .in(RiskEventDO::getBizId, condition.getBizIds()));
                    }
                }
            });
        }
        return selectPage(reqVO, queryWrapper.orderByDesc(RiskEventDO::getId));
    }
}
