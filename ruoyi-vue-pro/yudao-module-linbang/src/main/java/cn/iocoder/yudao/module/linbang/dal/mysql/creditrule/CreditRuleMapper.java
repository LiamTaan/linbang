package cn.iocoder.yudao.module.linbang.dal.mysql.creditrule;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.*;

/**
 * 信用分规则 Mapper
 *
 * @author dawn
 */
@Mapper
public interface CreditRuleMapper extends BaseMapperX<CreditRuleDO> {

    default PageResult<CreditRuleDO> selectPage(CreditRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CreditRuleDO>()
                .eqIfPresent(CreditRuleDO::getRuleCode, reqVO.getRuleCode())
                .likeIfPresent(CreditRuleDO::getRuleName, reqVO.getRuleName())
                .eqIfPresent(CreditRuleDO::getScoreChange, reqVO.getScoreChange())
                .eqIfPresent(CreditRuleDO::getTriggerType, reqVO.getTriggerType())
                .eqIfPresent(CreditRuleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CreditRuleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CreditRuleDO::getId));
    }

}