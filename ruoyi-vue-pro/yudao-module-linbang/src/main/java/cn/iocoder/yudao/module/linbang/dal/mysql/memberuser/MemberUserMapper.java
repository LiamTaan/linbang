package cn.iocoder.yudao.module.linbang.dal.mysql.memberuser;

import cn.hutool.core.util.StrUtil;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo.*;

/**
 * 用户主表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface MemberUserMapper extends BaseMapperX<MemberUserDO> {

    default MemberUserDO selectByMobile(String mobile) {
        return selectOne(MemberUserDO::getMobile, mobile);
    }

    default List<MemberUserDO> selectListByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return selectBatchIds(ids);
    }

    default List<MemberUserDO> selectListByKeyword(String keyword) {
        if (StrUtil.isBlank(keyword)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<MemberUserDO>()
                .and(wrapper -> wrapper.like(MemberUserDO::getUserNo, keyword)
                        .or()
                        .like(MemberUserDO::getMobile, keyword)
                        .or()
                        .like(MemberUserDO::getNickname, keyword))
                .orderByDesc(MemberUserDO::getId));
    }

    default PageResult<MemberUserDO> selectPage(MemberUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberUserDO>()
                .eqIfPresent(MemberUserDO::getUserNo, reqVO.getUserNo())
                .eqIfPresent(MemberUserDO::getMobile, reqVO.getMobile())
                .likeIfPresent(MemberUserDO::getNickname, reqVO.getNickname())
                .eqIfPresent(MemberUserDO::getAvatar, reqVO.getAvatar())
                .eqIfPresent(MemberUserDO::getGender, reqVO.getGender())
                .eqIfPresent(MemberUserDO::getBirthday, reqVO.getBirthday())
                .eqIfPresent(MemberUserDO::getRegisterSource, reqVO.getRegisterSource())
                .eqIfPresent(MemberUserDO::getCurrentRoleCode, reqVO.getCurrentRoleCode())
                .eqIfPresent(MemberUserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MemberUserDO::getLastLoginTime, reqVO.getLastLoginTime())
                .eqIfPresent(MemberUserDO::getLastLoginIp, reqVO.getLastLoginIp())
                .eqIfPresent(MemberUserDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(MemberUserDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberUserDO::getId));
    }

}
