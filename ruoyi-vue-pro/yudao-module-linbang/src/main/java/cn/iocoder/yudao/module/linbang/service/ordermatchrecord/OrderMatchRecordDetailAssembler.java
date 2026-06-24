package cn.iocoder.yudao.module.linbang.service.ordermatchrecord;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo.OrderMatchRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderacceptrecord.OrderAcceptRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

final class OrderMatchRecordDetailAssembler {

    private OrderMatchRecordDetailAssembler() {
    }

    static OrderMatchRecordDetailRespVO build(OrderMatchRecordDO record, OrderInfoDO order, MemberUserDO orderUser,
                                              OrderUnitDO unit, MerchantInfoDO merchant, RiskRuleDO rule,
                                              List<OrderAcceptRecordDO> acceptRecords) {
        OrderMatchRecordDetailRespVO respVO = BeanUtils.toBean(record, OrderMatchRecordDetailRespVO.class);
        if (order != null) {
            OrderMatchRecordDetailRespVO.OrderRespVO orderRespVO =
                    BeanUtils.toBean(order, OrderMatchRecordDetailRespVO.OrderRespVO.class);
            if (orderUser != null) {
                orderRespVO.setUserNo(orderUser.getUserNo());
                orderRespVO.setUserNickname(orderUser.getNickname());
                orderRespVO.setUserMobile(orderUser.getMobile());
            }
            respVO.setOrder(orderRespVO);
        }
        if (unit != null) {
            respVO.setUnit(BeanUtils.toBean(unit, OrderMatchRecordDetailRespVO.UnitRespVO.class));
        }
        if (merchant != null) {
            respVO.setMerchant(BeanUtils.toBean(merchant, OrderMatchRecordDetailRespVO.MerchantRespVO.class));
        }
        if (rule != null) {
            respVO.setRule(BeanUtils.toBean(rule, OrderMatchRecordDetailRespVO.RuleRespVO.class));
        }
        respVO.setSummary(buildSummary(record, acceptRecords));
        respVO.setAcceptRecords(buildAcceptRecords(acceptRecords, merchant));
        return respVO;
    }

    private static OrderMatchRecordDetailRespVO.SummaryRespVO buildSummary(OrderMatchRecordDO record,
                                                                            List<OrderAcceptRecordDO> acceptRecords) {
        List<OrderAcceptRecordDO> source = acceptRecords == null ? Collections.emptyList() : acceptRecords;
        OrderMatchRecordDetailRespVO.SummaryRespVO summary = new OrderMatchRecordDetailRespVO.SummaryRespVO();
        summary.setAcceptRecordCount(source.size());
        summary.setAcceptedCount((int) source.stream().filter(item -> "ACCEPTED".equalsIgnoreCase(item.getAcceptResult())).count());
        summary.setRejectedCount((int) source.stream().filter(item -> !"ACCEPTED".equalsIgnoreCase(item.getAcceptResult())).count());
        summary.setDeadlineExpired(record.getAcceptDeadlineTime() != null && record.getAcceptDeadlineTime().isBefore(LocalDateTime.now()));
        summary.setClosestDistanceKm(source.stream()
                .map(OrderAcceptRecordDO::getDistanceKm)
                .filter(item -> item != null)
                .min(Comparator.naturalOrder())
                .orElse(null));
        return summary;
    }

    private static List<OrderMatchRecordDetailRespVO.AcceptRecordRespVO> buildAcceptRecords(List<OrderAcceptRecordDO> acceptRecords,
                                                                                             MerchantInfoDO merchant) {
        if (acceptRecords == null || acceptRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return acceptRecords.stream()
                .limit(10)
                .map(item -> {
                    OrderMatchRecordDetailRespVO.AcceptRecordRespVO respVO =
                            BeanUtils.toBean(item, OrderMatchRecordDetailRespVO.AcceptRecordRespVO.class);
                    if (merchant != null) {
                        respVO.setMerchantName(merchant.getMerchantName());
                        respVO.setMerchantContactName(merchant.getContactName());
                        respVO.setMerchantContactMobile(merchant.getContactMobile());
                    }
                    return respVO;
                })
                .collect(Collectors.toList());
    }
}
