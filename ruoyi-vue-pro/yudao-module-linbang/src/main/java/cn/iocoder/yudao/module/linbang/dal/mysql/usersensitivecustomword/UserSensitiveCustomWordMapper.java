package cn.iocoder.yudao.module.linbang.dal.mysql.usersensitivecustomword;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo.UserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.usersensitivecustomword.UserSensitiveCustomWordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface UserSensitiveCustomWordMapper extends BaseMapperX<UserSensitiveCustomWordDO> {

    default PageResult<UserSensitiveCustomWordDO> selectPage(Long userId, AppUserSensitiveCustomWordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserSensitiveCustomWordDO>()
                .eq(UserSensitiveCustomWordDO::getUserId, userId)
                .likeIfPresent(UserSensitiveCustomWordDO::getWord, reqVO.getWord())
                .eqIfPresent(UserSensitiveCustomWordDO::getStatus, reqVO.getStatus())
                .orderByDesc(UserSensitiveCustomWordDO::getId));
    }

    default List<UserSensitiveCustomWordDO> selectActiveList(Long userId) {
        return selectList(new LambdaQueryWrapperX<UserSensitiveCustomWordDO>()
                .eq(UserSensitiveCustomWordDO::getUserId, userId)
                .eq(UserSensitiveCustomWordDO::getStatus, "ENABLE")
                .orderByDesc(UserSensitiveCustomWordDO::getId));
    }

    default PageResult<UserSensitiveCustomWordDO> selectAdminPage(UserSensitiveCustomWordPageReqVO reqVO, Collection<Long> userIds) {
        LambdaQueryWrapperX<UserSensitiveCustomWordDO> wrapper = new LambdaQueryWrapperX<UserSensitiveCustomWordDO>()
                .likeIfPresent(UserSensitiveCustomWordDO::getWord, reqVO.getWord())
                .eqIfPresent(UserSensitiveCustomWordDO::getSceneType, reqVO.getSceneType())
                .eqIfPresent(UserSensitiveCustomWordDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(UserSensitiveCustomWordDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserSensitiveCustomWordDO::getId);
        if (reqVO.getUserKeyword() != null) {
            if (userIds == null || userIds.isEmpty()) {
                wrapper.eq(UserSensitiveCustomWordDO::getId, -1L);
            } else {
                wrapper.in(UserSensitiveCustomWordDO::getUserId, userIds);
            }
        }
        return selectPage(reqVO, wrapper);
    }
}
