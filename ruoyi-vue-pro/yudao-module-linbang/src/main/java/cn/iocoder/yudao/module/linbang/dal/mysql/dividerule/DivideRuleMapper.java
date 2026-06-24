package cn.iocoder.yudao.module.linbang.dal.mysql.dividerule;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo.*;

/**
 * 分账规则 Mapper
 *
 * @author dawn
 */
@Mapper
public interface DivideRuleMapper extends BaseMapperX<DivideRuleDO> {

    default PageResult<DivideRuleDO> selectPage(DivideRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DivideRuleDO>()
                .likeIfPresent(DivideRuleDO::getRuleName, reqVO.getRuleName())
                .eqIfPresent(DivideRuleDO::getCityLevel, reqVO.getCityLevel())
                .eqIfPresent(DivideRuleDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(DivideRuleDO::getMerchantRate, reqVO.getMerchantRate())
                .eqIfPresent(DivideRuleDO::getPlatformRate, reqVO.getPlatformRate())
                .eqIfPresent(DivideRuleDO::getPartnerRate, reqVO.getPartnerRate())
                .eqIfPresent(DivideRuleDO::getPromoterRate, reqVO.getPromoterRate())
                .eqIfPresent(DivideRuleDO::getTaxWithholdRate, reqVO.getTaxWithholdRate())
                .eqIfPresent(DivideRuleDO::getMinWithdrawAmount, reqVO.getMinWithdrawAmount())
                .eqIfPresent(DivideRuleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(DivideRuleDO::getEffectiveTime, reqVO.getEffectiveTime())
                .betweenIfPresent(DivideRuleDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DivideRuleDO::getId));
    }

}