package cn.iocoder.yudao.module.linbang.dal.mysql.appmessagesetting;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appmessagesetting.AppMessageSettingDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppMessageSettingMapper extends BaseMapperX<AppMessageSettingDO> {

    default AppMessageSettingDO selectByUserId(Long userId) {
        return selectOne(AppMessageSettingDO::getUserId, userId);
    }

    default AppMessageSettingDO selectByUserIdForUpdate(Long userId) {
        return selectOne(new LambdaQueryWrapperX<AppMessageSettingDO>()
                .eq(AppMessageSettingDO::getUserId, userId)
                .last("FOR UPDATE"));
    }
}
