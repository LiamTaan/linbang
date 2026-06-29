package cn.iocoder.yudao.module.linbang.dal.mysql.ordersplitrule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRulePageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordersplitrule.OrderSplitRuleDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderSplitRuleMapper extends BaseMapperX<OrderSplitRuleDO> {

    default PageResult<OrderSplitRuleDO> selectPage(OrderSplitRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderSplitRuleDO>()
                .likeIfPresent(OrderSplitRuleDO::getRuleName, reqVO.getRuleName())
                .eqIfPresent(OrderSplitRuleDO::getRuleCode, reqVO.getRuleCode())
                .eqIfPresent(OrderSplitRuleDO::getMatchMode, reqVO.getMatchMode())
                .eqIfPresent(OrderSplitRuleDO::getCategoryId, reqVO.getCategoryId())
                .likeIfPresent(OrderSplitRuleDO::getApplicablePricingModes, reqVO.getPricingMode())
                .eqIfPresent(OrderSplitRuleDO::getSplitMode, reqVO.getSplitMode())
                .eqIfPresent(OrderSplitRuleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(OrderSplitRuleDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(OrderSplitRuleDO::getSortNo, OrderSplitRuleDO::getId));
    }

}
