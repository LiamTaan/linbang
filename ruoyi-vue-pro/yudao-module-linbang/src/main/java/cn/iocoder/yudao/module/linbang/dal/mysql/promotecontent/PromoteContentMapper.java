package cn.iocoder.yudao.module.linbang.dal.mysql.promotecontent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promotecontent.PromoteContentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface PromoteContentMapper extends BaseMapperX<PromoteContentDO> {

    default PageResult<PromoteContentDO> selectPageByPromoterIds(PromoteContentPageReqVO reqVO, Collection<Long> promoterIds) {
        return BaseMapperX.super.selectPage(reqVO, new LambdaQueryWrapperX<PromoteContentDO>()
                .eqIfPresent(PromoteContentDO::getUserId, reqVO.getUserId())
                .eqIfPresent(PromoteContentDO::getPromoterId, reqVO.getPromoterId())
                .inIfPresent(PromoteContentDO::getPromoterId, promoterIds)
                .likeIfPresent(PromoteContentDO::getTitle, reqVO.getTitle())
                .eqIfPresent(PromoteContentDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PromoteContentDO::getSystemAuditResult, reqVO.getSystemAuditResult())
                .eqIfPresent(PromoteContentDO::getManualAuditResult, reqVO.getManualAuditResult())
                .betweenIfPresent(PromoteContentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PromoteContentDO::getId));
    }
}
