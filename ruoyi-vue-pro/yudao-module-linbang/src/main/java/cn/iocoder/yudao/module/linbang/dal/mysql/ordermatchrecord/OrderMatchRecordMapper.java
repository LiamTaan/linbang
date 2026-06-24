package cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo.*;

/**
 * 订单匹配记录 Mapper
 *
 * @author dawn
 */
@Mapper
public interface OrderMatchRecordMapper extends BaseMapperX<OrderMatchRecordDO> {

    default PageResult<OrderMatchRecordDO> selectPage(OrderMatchRecordPageReqVO reqVO, List<Long> matchedOrderIds,
                                                      List<Long> matchedUnitIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .inIfPresent(OrderMatchRecordDO::getOrderId, matchedOrderIds)
                .inIfPresent(OrderMatchRecordDO::getUnitId, matchedUnitIds)
                .eqIfPresent(OrderMatchRecordDO::getMerchantId, reqVO.getMerchantId())
                .eqIfPresent(OrderMatchRecordDO::getMatchRuleCode, reqVO.getMatchRuleCode())
                .eqIfPresent(OrderMatchRecordDO::getMatchScore, reqVO.getMatchScore())
                .eqIfPresent(OrderMatchRecordDO::getDistanceKm, reqVO.getDistanceKm())
                .betweenIfPresent(OrderMatchRecordDO::getPushTime, reqVO.getPushTime())
                .betweenIfPresent(OrderMatchRecordDO::getAcceptDeadlineTime, reqVO.getAcceptDeadlineTime())
                .eqIfPresent(OrderMatchRecordDO::getStatus, reqVO.getStatus())
                .orderByDesc(OrderMatchRecordDO::getId));
    }

}
