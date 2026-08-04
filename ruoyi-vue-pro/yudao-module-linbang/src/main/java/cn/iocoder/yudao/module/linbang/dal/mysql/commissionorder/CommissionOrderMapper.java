package cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface CommissionOrderMapper extends BaseMapperX<CommissionOrderDO> {

    @Select("SELECT COALESCE(SUM(commission_amount), 0) FROM lb_commission_order "
            + "WHERE promoter_id = #{promoterId} AND status = #{status} AND deleted = 0")
    BigDecimal selectAmountSumByPromoterIdAndStatus(@Param("promoterId") Long promoterId,
                                                    @Param("status") String status);

    @Select({
            "<script>",
            "SELECT COALESCE(SUM(commission_amount), 0) FROM lb_commission_order",
            "WHERE promoter_id = #{promoterId} AND deleted = 0",
            "AND user_id IN",
            "<foreach collection='userIds' item='userId' open='(' separator=',' close=')'>#{userId}</foreach>",
            "</script>"
    })
    BigDecimal selectAmountSumByPromoterIdAndUserIds(@Param("promoterId") Long promoterId,
                                                     @Param("userIds") List<Long> userIds);

    default PageResult<CommissionOrderDO> selectPage(CommissionOrderPageReqVO reqVO, List<Long> promoterIds,
                                                     List<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CommissionOrderDO>()
                .inIfPresent(CommissionOrderDO::getPromoterId, promoterIds)
                .inIfPresent(CommissionOrderDO::getUserId, userIds)
                .eqIfPresent(CommissionOrderDO::getCommissionType, reqVO.getCommissionType())
                .eqIfPresent(CommissionOrderDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CommissionOrderDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CommissionOrderDO::getId));
    }

    default PageResult<CommissionOrderDO> selectAppPage(Long promoterId, AppCommissionPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CommissionOrderDO>()
                .eq(CommissionOrderDO::getPromoterId, promoterId)
                .eqIfPresent(CommissionOrderDO::getStatus, reqVO.getStatus())
                .orderByDesc(CommissionOrderDO::getId));
    }
}
