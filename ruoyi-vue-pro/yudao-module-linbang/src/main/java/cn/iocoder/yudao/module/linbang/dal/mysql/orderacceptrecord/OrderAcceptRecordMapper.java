package cn.iocoder.yudao.module.linbang.dal.mysql.orderacceptrecord;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo.*;

/**
 * 抢单记录 Mapper
 *
 * @author dawn
 */
@Mapper
public interface OrderAcceptRecordMapper extends BaseMapperX<OrderAcceptRecordDO> {

    default PageResult<OrderAcceptRecordDO> selectPage(OrderAcceptRecordPageReqVO reqVO, List<Long> matchedOrderIds,
                                                       List<Long> matchedUnitIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderAcceptRecordDO>()
                .inIfPresent(OrderAcceptRecordDO::getOrderId, matchedOrderIds)
                .inIfPresent(OrderAcceptRecordDO::getUnitId, matchedUnitIds)
                .eqIfPresent(OrderAcceptRecordDO::getMerchantId, reqVO.getMerchantId())
                .betweenIfPresent(OrderAcceptRecordDO::getAcceptTime, reqVO.getAcceptTime())
                .eqIfPresent(OrderAcceptRecordDO::getDistanceKm, reqVO.getDistanceKm())
                .eqIfPresent(OrderAcceptRecordDO::getAcceptResult, reqVO.getAcceptResult())
                .eqIfPresent(OrderAcceptRecordDO::getRemark, reqVO.getRemark())
                .orderByDesc(OrderAcceptRecordDO::getId));
    }

}
