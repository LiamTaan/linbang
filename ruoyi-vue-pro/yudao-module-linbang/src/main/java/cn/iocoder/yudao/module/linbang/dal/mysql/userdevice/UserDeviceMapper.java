package cn.iocoder.yudao.module.linbang.dal.mysql.userdevice;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo.UserDevicePageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userdevice.UserDeviceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface UserDeviceMapper extends BaseMapperX<UserDeviceDO> {

    default PageResult<UserDeviceDO> selectPage(UserDevicePageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserDeviceDO>()
                .eqIfPresent(UserDeviceDO::getUserId, reqVO.getUserId())
                .inIfPresent(UserDeviceDO::getUserId, userIds)
                .eqIfPresent(UserDeviceDO::getStatus, reqVO.getStatus())
                .likeIfPresent(UserDeviceDO::getDeviceFingerprint, reqVO.getDeviceFingerprint())
                .likeIfPresent(UserDeviceDO::getLastIp, reqVO.getLastIp())
                .betweenIfPresent(UserDeviceDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserDeviceDO::getId));
    }
}
