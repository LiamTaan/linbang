package cn.iocoder.yudao.module.linbang.service.promoter;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.promoter.vo.PromoterDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class PromoterDetailAssembler {

    private PromoterDetailAssembler() {
    }

    static PromoterDetailRespVO build(PromoterDO promoter, MemberUserDO user, List<PromoterRelationDO> relations,
                                      List<CommissionOrderDO> commissionOrders, Map<Long, MemberUserDO> relatedUserMap,
                                      Map<Long, OrderInfoDO> orderMap, Map<Long, OrderUnitDO> unitMap) {
        PromoterDetailRespVO respVO = BeanUtils.toBean(promoter, PromoterDetailRespVO.class);
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, PromoterDetailRespVO.UserRespVO.class));
        }
        respVO.setSummary(buildSummary(relations, commissionOrders));
        respVO.setRecentRelations(buildRecentRelations(relations, relatedUserMap, orderMap));
        respVO.setRecentCommissionOrders(buildRecentCommissionOrders(commissionOrders, relatedUserMap, orderMap, unitMap));
        return respVO;
    }

    private static PromoterDetailRespVO.SummaryRespVO buildSummary(List<PromoterRelationDO> relations,
                                                                   List<CommissionOrderDO> commissionOrders) {
        PromoterDetailRespVO.SummaryRespVO summary = new PromoterDetailRespVO.SummaryRespVO();
        summary.setRelationCount(relations == null ? 0 : relations.size());
        summary.setConvertedRelationCount(relations == null ? 0 : (int) relations.stream()
                .filter(item -> "CONVERTED".equalsIgnoreCase(item.getConvertStatus()))
                .count());
        summary.setPendingCommissionCount(countCommissionByStatus(commissionOrders, "PENDING"));
        summary.setSettledCommissionCount(countCommissionByStatus(commissionOrders, "SETTLED"));
        summary.setInvalidCommissionCount(countCommissionByStatus(commissionOrders, "INVALID"));
        summary.setPendingCommissionAmount(sumCommissionByStatus(commissionOrders, "PENDING"));
        summary.setSettledCommissionAmount(sumCommissionByStatus(commissionOrders, "SETTLED"));
        return summary;
    }

    private static int countCommissionByStatus(List<CommissionOrderDO> commissionOrders, String status) {
        if (commissionOrders == null || commissionOrders.isEmpty()) {
            return 0;
        }
        return (int) commissionOrders.stream()
                .filter(item -> status.equalsIgnoreCase(item.getStatus()))
                .count();
    }

    private static BigDecimal sumCommissionByStatus(List<CommissionOrderDO> commissionOrders, String status) {
        if (commissionOrders == null || commissionOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return commissionOrders.stream()
                .filter(item -> status.equalsIgnoreCase(item.getStatus()))
                .map(CommissionOrderDO::getCommissionAmount)
                .filter(item -> item != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static List<PromoterDetailRespVO.RelationRespVO> buildRecentRelations(List<PromoterRelationDO> relations,
                                                                                  Map<Long, MemberUserDO> relatedUserMap,
                                                                                  Map<Long, OrderInfoDO> orderMap) {
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }
        return relations.stream()
                .limit(10)
                .map(item -> {
                    PromoterDetailRespVO.RelationRespVO respVO = BeanUtils.toBean(item, PromoterDetailRespVO.RelationRespVO.class);
                    MemberUserDO user = relatedUserMap.get(item.getUserId());
                    if (user != null) {
                        respVO.setUserNo(user.getUserNo());
                        respVO.setUserNickname(user.getNickname());
                        respVO.setUserMobile(user.getMobile());
                    }
                    OrderInfoDO order = orderMap.get(item.getFirstOrderId());
                    if (order != null) {
                        respVO.setFirstOrderNo(order.getOrderNo());
                    }
                    return respVO;
                })
                .collect(Collectors.toList());
    }

    private static List<PromoterDetailRespVO.CommissionRespVO> buildRecentCommissionOrders(List<CommissionOrderDO> commissionOrders,
                                                                                           Map<Long, MemberUserDO> relatedUserMap,
                                                                                           Map<Long, OrderInfoDO> orderMap,
                                                                                           Map<Long, OrderUnitDO> unitMap) {
        if (commissionOrders == null || commissionOrders.isEmpty()) {
            return Collections.emptyList();
        }
        return commissionOrders.stream()
                .limit(10)
                .map(item -> {
                    PromoterDetailRespVO.CommissionRespVO respVO = BeanUtils.toBean(item, PromoterDetailRespVO.CommissionRespVO.class);
                    MemberUserDO user = relatedUserMap.get(item.getUserId());
                    if (user != null) {
                        respVO.setUserNo(user.getUserNo());
                        respVO.setUserNickname(user.getNickname());
                        respVO.setUserMobile(user.getMobile());
                    }
                    OrderInfoDO order = orderMap.get(item.getSourceOrderId());
                    if (order != null) {
                        respVO.setSourceOrderNo(order.getOrderNo());
                    }
                    OrderUnitDO unit = unitMap.get(item.getSourceUnitId());
                    if (unit != null) {
                        respVO.setSourceUnitNo(unit.getUnitNo());
                    }
                    return respVO;
                })
                .collect(Collectors.toList());
    }
}
