package cn.iocoder.yudao.module.linbang.dal.mysql.promoteappeal;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo.PromoteAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoteappeal.PromoteAppealDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface PromoteAppealMapper extends BaseMapperX<PromoteAppealDO> {

    default PageResult<PromoteAppealDO> selectPageByPromoterIds(PromoteAppealPageReqVO reqVO, Collection<Long> promoterIds) {
        return BaseMapperX.super.selectPage(reqVO, new LambdaQueryWrapperX<PromoteAppealDO>()
                .eqIfPresent(PromoteAppealDO::getPromoterId, reqVO.getPromoterId())
                .inIfPresent(PromoteAppealDO::getPromoterId, promoterIds)
                .eqIfPresent(PromoteAppealDO::getUserId, reqVO.getUserId())
                .eqIfPresent(PromoteAppealDO::getContentId, reqVO.getContentId())
                .eqIfPresent(PromoteAppealDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PromoteAppealDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PromoteAppealDO::getId));
    }
}
