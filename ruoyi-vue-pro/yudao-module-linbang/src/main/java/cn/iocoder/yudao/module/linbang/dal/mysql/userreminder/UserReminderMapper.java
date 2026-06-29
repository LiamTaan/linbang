package cn.iocoder.yudao.module.linbang.dal.mysql.userreminder;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userreminder.UserReminderDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserReminderMapper extends BaseMapperX<UserReminderDO> {

    default List<UserReminderDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<UserReminderDO>()
                .eq(UserReminderDO::getUserId, userId)
                .orderByAsc(UserReminderDO::getNextRemindTime, UserReminderDO::getId));
    }

    default UserReminderDO selectByIdAndUserId(Long id, Long userId) {
        return selectOne(new LambdaQueryWrapperX<UserReminderDO>()
                .eq(UserReminderDO::getId, id)
                .eq(UserReminderDO::getUserId, userId)
                .last("LIMIT 1"));
    }

    default List<UserReminderDO> selectDueList(LocalDateTime now) {
        return selectList(new LambdaQueryWrapperX<UserReminderDO>()
                .eq(UserReminderDO::getStatus, "ACTIVE")
                .le(UserReminderDO::getNextRemindTime, now)
                .orderByAsc(UserReminderDO::getNextRemindTime, UserReminderDO::getId));
    }
}
