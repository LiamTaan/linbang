package cn.iocoder.yudao.module.linbang.dal.mysql.riskrule;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo.*;

/**
 * 风控规则表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface RiskRuleMapper extends BaseMapperX<RiskRuleDO> {

    default PageResult<RiskRuleDO> selectPage(RiskRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<RiskRuleDO>()
                .eqIfPresent(RiskRuleDO::getRuleCode, reqVO.getRuleCode())
                .likeIfPresent(RiskRuleDO::getRuleName, reqVO.getRuleName())
                .eqIfPresent(RiskRuleDO::getRuleGroup, reqVO.getRuleGroup())
                .eqIfPresent(RiskRuleDO::getRuleValue, reqVO.getRuleValue())
                .eqIfPresent(RiskRuleDO::getValueType, reqVO.getValueType())
                .eqIfPresent(RiskRuleDO::getStatus, reqVO.getStatus())
                .eqIfPresent(RiskRuleDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(RiskRuleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(RiskRuleDO::getId));
    }

}