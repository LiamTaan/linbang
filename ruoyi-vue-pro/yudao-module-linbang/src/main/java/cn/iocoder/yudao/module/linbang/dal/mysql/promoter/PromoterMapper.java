package cn.iocoder.yudao.module.linbang.dal.mysql.promoter;

import java.util.Collection;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PromoterMapper extends BaseMapperX<PromoterDO> {

    default PromoterDO selectByUserId(Long userId) {
        return selectOne(PromoterDO::getUserId, userId);
    }

    default PromoterDO selectByInviteCode(String inviteCode) {
        return selectOne(PromoterDO::getInviteCode, inviteCode);
    }

    default PageResult<PromoterDO> selectPage(PromoterPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PromoterDO>()
                .inIfPresent(PromoterDO::getUserId, userIds)
                .likeIfPresent(PromoterDO::getInviteCode, reqVO.getInviteCode())
                .eqIfPresent(PromoterDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PromoterDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PromoterDO::getId));
    }
}
