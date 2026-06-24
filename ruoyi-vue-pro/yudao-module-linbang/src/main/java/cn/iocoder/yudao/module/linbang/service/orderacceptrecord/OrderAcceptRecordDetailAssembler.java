package cn.iocoder.yudao.module.linbang.service.orderacceptrecord;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo.OrderAcceptRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

final class OrderAcceptRecordDetailAssembler {

    private OrderAcceptRecordDetailAssembler() {
    }

    static OrderAcceptRecordDetailRespVO build(OrderAcceptRecordDO record, OrderInfoDO order, MemberUserDO orderUser,
                                               OrderUnitDO unit, MerchantInfoDO merchant, List<OrderMatchRecordDO> matchRecords) {
        OrderAcceptRecordDetailRespVO respVO = BeanUtils.toBean(record, OrderAcceptRecordDetailRespVO.class);
        if (order != null) {
            OrderAcceptRecordDetailRespVO.OrderRespVO orderRespVO =
                    BeanUtils.toBean(order, OrderAcceptRecordDetailRespVO.OrderRespVO.class);
            if (orderUser != null) {
                orderRespVO.setUserNo(orderUser.getUserNo());
                orderRespVO.setUserNickname(orderUser.getNickname());
                orderRespVO.setUserMobile(orderUser.getMobile());
            }
            respVO.setOrder(orderRespVO);
        }
        if (unit != null) {
            respVO.setUnit(BeanUtils.toBean(unit, OrderAcceptRecordDetailRespVO.UnitRespVO.class));
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, OrderAcceptRecordDetailRespVO.MerchantRespVO.class));
        }
        respVO.setSummary(buildSummary(matchRecords));
        respVO.setMatchRecords(buildMatchRecords(matchRecords));
        return respVO;
    }

    private static OrderAcceptRecordDetailRespVO.SummaryRespVO buildSummary(List<OrderMatchRecordDO> matchRecords) {
        List<OrderMatchRecordDO> source = matchRecords == null ? Collections.emptyList() : matchRecords;
        OrderAcceptRecordDetailRespVO.SummaryRespVO summary = new OrderAcceptRecordDetailRespVO.SummaryRespVO();
        summary.setMatchRecordCount(source.size());
        summary.setPushedCount((int) source.stream().filter(item -> "PUSHED".equalsIgnoreCase(item.getStatus())).count());
        summary.setAcceptedCount((int) source.stream().filter(item -> "ACCEPTED".equalsIgnoreCase(item.getStatus())).count());
        summary.setRejectedCount((int) source.stream()
                .filter(item -> !"PUSHED".equalsIgnoreCase(item.getStatus()) && !"ACCEPTED".equalsIgnoreCase(item.getStatus()))
                .count());
        summary.setHighestMatchScore(source.stream()
                .map(OrderMatchRecordDO::getMatchScore)
                .filter(item -> item != null)
                .max(Comparator.naturalOrder())
                .orElse(null));
        return summary;
    }

    private static List<OrderAcceptRecordDetailRespVO.MatchRecordRespVO> buildMatchRecords(List<OrderMatchRecordDO> matchRecords) {
        if (matchRecords == null || matchRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return matchRecords.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, OrderAcceptRecordDetailRespVO.MatchRecordRespVO.class))
                .collect(Collectors.toList());
    }
}
