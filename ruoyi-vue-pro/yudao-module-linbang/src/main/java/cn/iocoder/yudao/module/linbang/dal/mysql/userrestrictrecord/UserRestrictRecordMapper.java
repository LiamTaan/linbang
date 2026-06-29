package cn.iocoder.yudao.module.linbang.dal.mysql.userrestrictrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord.UserRestrictRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;

@Mapper
public interface UserRestrictRecordMapper extends BaseMapperX<UserRestrictRecordDO> {

    default PageResult<UserRestrictRecordDO> selectPage(UserRestrictRecordPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserRestrictRecordDO>()
                .eqIfPresent(UserRestrictRecordDO::getUserId, reqVO.getUserId())
                .inIfPresent(UserRestrictRecordDO::getUserId, userIds)
                .eqIfPresent(UserRestrictRecordDO::getRestrictType, reqVO.getRestrictType())
                .eqIfPresent(UserRestrictRecordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(UserRestrictRecordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserRestrictRecordDO::getId));
    }

    default UserRestrictRecordDO selectActive(Long userId, String restrictType, LocalDateTime now) {
        return selectOne(new LambdaQueryWrapperX<UserRestrictRecordDO>()
                .eq(UserRestrictRecordDO::getUserId, userId)
                .eq(UserRestrictRecordDO::getRestrictType, restrictType)
                .eq(UserRestrictRecordDO::getStatus, "ACTIVE")
                .le(UserRestrictRecordDO::getStartTime, now)
                .ge(UserRestrictRecordDO::getEndTime, now)
                .last("LIMIT 1"));
    }

    default java.util.List<UserRestrictRecordDO> selectBatchByMinId(Long minId, int limit) {
        return selectList(new LambdaQueryWrapperX<UserRestrictRecordDO>()
                .gtIfPresent(UserRestrictRecordDO::getId, minId)
                .orderByAsc(UserRestrictRecordDO::getId)
                .last("LIMIT " + limit));
    }
}
