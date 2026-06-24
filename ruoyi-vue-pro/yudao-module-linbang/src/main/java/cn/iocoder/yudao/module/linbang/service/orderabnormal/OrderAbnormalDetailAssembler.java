package cn.iocoder.yudao.module.linbang.service.orderabnormal;

import cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo.OrderAbnormalDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class OrderAbnormalDetailAssembler {

    private OrderAbnormalDetailAssembler() {
    }

    static OrderAbnormalDetailRespVO.OrderSimpleRespVO buildOrder(OrderInfoDO order) {
        if (order == null) {
            return null;
        }
        OrderAbnormalDetailRespVO.OrderSimpleRespVO respVO = new OrderAbnormalDetailRespVO.OrderSimpleRespVO();
        respVO.setId(order.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setTitle(order.getTitle());
        respVO.setStatus(order.getStatus());
        respVO.setUserId(order.getUserId());
        respVO.setMerchantId(order.getMerchantId());
        return respVO;
    }

    static OrderAbnormalDetailRespVO.OrderUnitSimpleRespVO buildUnit(OrderUnitDO unit) {
        if (unit == null) {
            return null;
        }
        OrderAbnormalDetailRespVO.OrderUnitSimpleRespVO respVO = new OrderAbnormalDetailRespVO.OrderUnitSimpleRespVO();
        respVO.setId(unit.getId());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setUnitSeq(unit.getUnitSeq());
        respVO.setUnitTitle(unit.getUnitTitle());
        respVO.setStatus(unit.getStatus());
        respVO.setIsLocked(unit.getIsLocked());
        respVO.setLockReason(unit.getLockReason());
        respVO.setMerchantId(unit.getMerchantId());
        return respVO;
    }

    static OrderAbnormalDetailRespVO.RiskRuleSimpleRespVO buildRiskRule(RiskRuleDO riskRule) {
        if (riskRule == null) {
            return null;
        }
        OrderAbnormalDetailRespVO.RiskRuleSimpleRespVO respVO = new OrderAbnormalDetailRespVO.RiskRuleSimpleRespVO();
        respVO.setId(riskRule.getId());
        respVO.setRuleCode(riskRule.getRuleCode());
        respVO.setRuleName(riskRule.getRuleName());
        respVO.setRuleGroup(riskRule.getRuleGroup());
        respVO.setRuleValue(riskRule.getRuleValue());
        respVO.setValueType(riskRule.getValueType());
        respVO.setStatus(riskRule.getStatus());
        respVO.setRemark(riskRule.getRemark());
        return respVO;
    }

    static List<OrderAbnormalDetailRespVO.OrderOperateLogRespVO> buildLogs(List<OrderOperateLogDO> logs) {
        if (logs == null || logs.isEmpty()) {
            return Collections.emptyList();
        }
        return logs.stream().map(log -> {
            OrderAbnormalDetailRespVO.OrderOperateLogRespVO respVO = new OrderAbnormalDetailRespVO.OrderOperateLogRespVO();
            respVO.setId(log.getId());
            respVO.setUnitId(log.getUnitId());
            respVO.setOperateType(log.getOperateType());
            respVO.setOperateRole(log.getOperateRole());
            respVO.setOperateBy(log.getOperateBy());
            respVO.setBeforeStatus(log.getBeforeStatus());
            respVO.setAfterStatus(log.getAfterStatus());
            respVO.setRemark(log.getRemark());
            respVO.setOperateTime(log.getOperateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

}
