package cn.iocoder.yudao.module.linbang.dal.mysql.registerreminder;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.registerreminder.RegisterReminderRecordDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RegisterReminderRecordMapper extends BaseMapperX<RegisterReminderRecordDO> {

    default RegisterReminderRecordDO selectByReminderKey(String reminderKey) {
        return selectOne(new LambdaQueryWrapperX<RegisterReminderRecordDO>()
                .eq(RegisterReminderRecordDO::getReminderKey, reminderKey)
                .last("LIMIT 1"));
    }
}
