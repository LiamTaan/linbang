package cn.iocoder.yudao.module.linbang.dal.mysql.userriskrelation;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.userriskrelation.vo.UserRiskRelationPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userriskrelation.UserRiskRelationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface UserRiskRelationMapper extends BaseMapperX<UserRiskRelationDO> {

    default PageResult<UserRiskRelationDO> selectPage(UserRiskRelationPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserRiskRelationDO>()
                .eqIfPresent(UserRiskRelationDO::getUserId, reqVO.getUserId())
                .inIfPresent(UserRiskRelationDO::getUserId, userIds)
                .eqIfPresent(UserRiskRelationDO::getRelatedUserId, reqVO.getRelatedUserId())
                .eqIfPresent(UserRiskRelationDO::getRelationType, reqVO.getRelationType())
                .eqIfPresent(UserRiskRelationDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(UserRiskRelationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserRiskRelationDO::getId));
    }

    default UserRiskRelationDO selectActive(Long userId, Long relatedUserId) {
        return selectOne(new LambdaQueryWrapperX<UserRiskRelationDO>()
                .eq(UserRiskRelationDO::getStatus, "ENABLE")
                .and(wrapper -> wrapper
                        .eq(UserRiskRelationDO::getUserId, userId)
                        .eq(UserRiskRelationDO::getRelatedUserId, relatedUserId)
                        .or()
                        .eq(UserRiskRelationDO::getUserId, relatedUserId)
                        .eq(UserRiskRelationDO::getRelatedUserId, userId))
                .last("LIMIT 1"));
    }
}
