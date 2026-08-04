package cn.iocoder.yudao.module.linbang.dal.mysql.promoter;

import java.util.Collection;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface PromoterMapper extends BaseMapperX<PromoterDO> {

    @Update("UPDATE lb_promoter SET total_commission_amount = GREATEST(COALESCE(total_commission_amount, 0) + #{totalDelta}, 0), "
            + "available_commission_amount = GREATEST(COALESCE(available_commission_amount, 0) + #{availableDelta}, 0) "
            + "WHERE id = #{id} AND deleted = 0")
    int updateCommissionAmounts(@Param("id") Long id,
                                @Param("totalDelta") BigDecimal totalDelta,
                                @Param("availableDelta") BigDecimal availableDelta);

    default PromoterDO selectByUserId(Long userId) {
        return selectOne(PromoterDO::getUserId, userId);
    }

    default PromoterDO selectByUserIdForUpdate(Long userId) {
        return selectOne(new LambdaQueryWrapperX<PromoterDO>()
                .eq(PromoterDO::getUserId, userId)
                .last("FOR UPDATE"));
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
