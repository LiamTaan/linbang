package cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord;

import java.time.LocalDateTime;
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
                .eqIfPresent(OrderMatchRecordDO::getStageNo, reqVO.getStageNo())
                .eqIfPresent(OrderMatchRecordDO::getPushBatchNo, reqVO.getPushBatchNo())
                .eqIfPresent(OrderMatchRecordDO::getPriorityLayer, reqVO.getPriorityLayer())
                .eqIfPresent(OrderMatchRecordDO::getPriorityPoolFlag, reqVO.getPriorityPoolFlag())
                .eqIfPresent(OrderMatchRecordDO::getCategoryMatchLevel, reqVO.getCategoryMatchLevel())
                .betweenIfPresent(OrderMatchRecordDO::getPushTime, reqVO.getPushTime())
                .betweenIfPresent(OrderMatchRecordDO::getAcceptDeadlineTime, reqVO.getAcceptDeadlineTime())
                .betweenIfPresent(OrderMatchRecordDO::getExpiredTime, reqVO.getExpiredTime())
                .eqIfPresent(OrderMatchRecordDO::getStatus, reqVO.getStatus())
                .eqIfPresent(OrderMatchRecordDO::getFinalResult, reqVO.getFinalResult())
                .orderByDesc(OrderMatchRecordDO::getId));
    }

    default List<OrderMatchRecordDO> selectActiveListByMerchantId(Long merchantId, LocalDateTime now) {
        return selectList(new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .eq(OrderMatchRecordDO::getMerchantId, merchantId)
                .eq(OrderMatchRecordDO::getStatus, "PUSHED")
                .ge(OrderMatchRecordDO::getExpiredTime, now)
                .orderByAsc(OrderMatchRecordDO::getExpiredTime, OrderMatchRecordDO::getId));
    }

    default List<OrderMatchRecordDO> selectListByUnitId(Long unitId) {
        return selectList(new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .eq(OrderMatchRecordDO::getUnitId, unitId)
                .orderByAsc(OrderMatchRecordDO::getStageNo, OrderMatchRecordDO::getId));
    }

    default OrderMatchRecordDO selectByUnitIdAndMerchantIdAndStageNo(Long unitId, Long merchantId, Integer stageNo) {
        return selectOne(new LambdaQueryWrapperX<OrderMatchRecordDO>()
                .eq(OrderMatchRecordDO::getUnitId, unitId)
                .eq(OrderMatchRecordDO::getMerchantId, merchantId)
                .eq(OrderMatchRecordDO::getStageNo, stageNo)
                .last("LIMIT 1"));
    }

}
