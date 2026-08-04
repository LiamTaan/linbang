package cn.iocoder.yudao.module.linbang.dal.mysql.registerreminder;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.registerreminder.RegisterReminderRecordDO;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.time.LocalDateTime;

@Mapper
public interface RegisterReminderRecordMapper extends BaseMapperX<RegisterReminderRecordDO> {

    default RegisterReminderRecordDO selectByReminderKey(String reminderKey) {
        return selectOne(new LambdaQueryWrapperX<RegisterReminderRecordDO>()
                .eq(RegisterReminderRecordDO::getReminderKey, reminderKey)
                .last("LIMIT 1"));
    }

    default RegisterReminderRecordDO selectByReminderKeyForUpdate(String reminderKey) {
        return selectOne(new LambdaQueryWrapperX<RegisterReminderRecordDO>()
                .eq(RegisterReminderRecordDO::getReminderKey, reminderKey)
                .last("LIMIT 1 FOR UPDATE"));
    }

    default int incrementTrigger(String reminderKey, LocalDateTime triggerTime) {
        return update(null, new LambdaUpdateWrapper<RegisterReminderRecordDO>()
                .eq(RegisterReminderRecordDO::getReminderKey, reminderKey)
                .set(RegisterReminderRecordDO::getLastTriggerTime, triggerTime)
                .set(RegisterReminderRecordDO::getStatus, "PENDING")
                .setSql("trigger_count = COALESCE(trigger_count, 0) + 1"));
    }

    default int acknowledge(String reminderKey, LocalDateTime ackTime) {
        return update(null, new LambdaUpdateWrapper<RegisterReminderRecordDO>()
                .eq(RegisterReminderRecordDO::getReminderKey, reminderKey)
                .set(RegisterReminderRecordDO::getStatus, "ACKED")
                .set(RegisterReminderRecordDO::getLastAckTime, ackTime));
    }
}
