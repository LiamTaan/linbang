package cn.iocoder.yudao.module.linbang.dal.mysql.userrestrictrecord;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo.UserRestrictRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord.UserRestrictRecordDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

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
                .and(wrapper -> wrapper.isNull(UserRestrictRecordDO::getEndTime)
                        .or().ge(UserRestrictRecordDO::getEndTime, now))
                .last("LIMIT 1"));
    }

    default UserRestrictRecordDO selectActiveBlocking(Long userId, LocalDateTime now) {
        return selectOne(new LambdaQueryWrapperX<UserRestrictRecordDO>()
                .eq(UserRestrictRecordDO::getUserId, userId)
                .in(UserRestrictRecordDO::getSourceBizType, "BAN", "BLACKLIST")
                .eq(UserRestrictRecordDO::getStatus, "ACTIVE")
                .le(UserRestrictRecordDO::getStartTime, now)
                .and(wrapper -> wrapper.isNull(UserRestrictRecordDO::getEndTime)
                        .or().ge(UserRestrictRecordDO::getEndTime, now))
                .last("LIMIT 1"));
    }

    default List<UserRestrictRecordDO> selectBatchByMinId(Long minId, int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 1000));
        return selectPage(new Page<UserRestrictRecordDO>(1, safeLimit, false),
                new LambdaQueryWrapperX<UserRestrictRecordDO>()
                .gtIfPresent(UserRestrictRecordDO::getId, minId)
                .orderByAsc(UserRestrictRecordDO::getId)).getRecords();
    }
}
