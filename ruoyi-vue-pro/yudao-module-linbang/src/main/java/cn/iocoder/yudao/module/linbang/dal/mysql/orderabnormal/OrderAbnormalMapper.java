package cn.iocoder.yudao.module.linbang.dal.mysql.orderabnormal;

import cn.hutool.core.util.StrUtil;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo.*;

/**
 * 异常订单 Mapper
 *
 * @author dawn
 */
@Mapper
public interface OrderAbnormalMapper extends BaseMapperX<OrderAbnormalDO> {

    default List<OrderAbnormalDO> selectListByAbnormalNo(String abnormalNo) {
        if (StrUtil.isBlank(abnormalNo)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<OrderAbnormalDO>()
                .like(OrderAbnormalDO::getAbnormalNo, abnormalNo)
                .orderByDesc(OrderAbnormalDO::getId));
    }

    default PageResult<OrderAbnormalDO> selectPage(OrderAbnormalPageReqVO reqVO, Collection<Long> orderIds,
                                                   Collection<Long> unitIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderAbnormalDO>()
                .inIfPresent(OrderAbnormalDO::getOrderId, orderIds)
                .inIfPresent(OrderAbnormalDO::getUnitId, unitIds)
                .eqIfPresent(OrderAbnormalDO::getAbnormalNo, reqVO.getAbnormalNo())
                .eqIfPresent(OrderAbnormalDO::getAbnormalType, reqVO.getAbnormalType())
                .eqIfPresent(OrderAbnormalDO::getRiskLevel, reqVO.getRiskLevel())
                .eqIfPresent(OrderAbnormalDO::getHitRuleCode, reqVO.getHitRuleCode())
                .eqIfPresent(OrderAbnormalDO::getHandleStatus, reqVO.getHandleStatus())
                .eqIfPresent(OrderAbnormalDO::getHandleBy, reqVO.getHandleBy())
                .betweenIfPresent(OrderAbnormalDO::getHandleTime, reqVO.getHandleTime())
                .eqIfPresent(OrderAbnormalDO::getRemark, reqVO.getRemark())
                .betweenIfPresent(OrderAbnormalDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OrderAbnormalDO::getId));
    }

}
