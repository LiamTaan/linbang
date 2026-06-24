package cn.iocoder.yudao.module.linbang.dal.mysql.orderunit;

import cn.hutool.core.util.StrUtil;
import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo.*;

/**
 * 拆分单元 Mapper
 *
 * @author dawn
 */
@Mapper
public interface OrderUnitMapper extends BaseMapperX<OrderUnitDO> {

    default List<OrderUnitDO> selectListByUnitNo(String unitNo) {
        if (StrUtil.isBlank(unitNo)) {
            return Collections.emptyList();
        }
        return selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .like(OrderUnitDO::getUnitNo, unitNo)
                .orderByDesc(OrderUnitDO::getId));
    }

    default PageResult<OrderUnitDO> selectPage(OrderUnitPageReqVO reqVO, Collection<Long> orderIds,
                                               Collection<Long> prevUnitIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderUnitDO>()
                .inIfPresent(OrderUnitDO::getOrderId, orderIds)
                .eqIfPresent(OrderUnitDO::getUnitNo, reqVO.getUnitNo())
                .eqIfPresent(OrderUnitDO::getUnitSeq, reqVO.getUnitSeq())
                .eqIfPresent(OrderUnitDO::getUnitTitle, reqVO.getUnitTitle())
                .eqIfPresent(OrderUnitDO::getUnitAmount, reqVO.getUnitAmount())
                .eqIfPresent(OrderUnitDO::getSplitMode, reqVO.getSplitMode())
                .inIfPresent(OrderUnitDO::getPrevUnitId, prevUnitIds)
                .eqIfPresent(OrderUnitDO::getIsLocked, reqVO.getIsLocked())
                .eqIfPresent(OrderUnitDO::getLockReason, reqVO.getLockReason())
                .eqIfPresent(OrderUnitDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(OrderUnitDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(OrderUnitDO::getAcceptDeadlineTime, reqVO.getAcceptDeadlineTime())
                .betweenIfPresent(OrderUnitDO::getFinishTime, reqVO.getFinishTime())
                .betweenIfPresent(OrderUnitDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(OrderUnitDO::getId));
    }

}
