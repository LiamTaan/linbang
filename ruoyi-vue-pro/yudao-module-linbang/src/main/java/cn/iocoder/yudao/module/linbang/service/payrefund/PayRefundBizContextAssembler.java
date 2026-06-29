package cn.iocoder.yudao.module.linbang.service.payrefund;

import cn.iocoder.yudao.module.linbang.controller.admin.payrefund.vo.PayRefundBizContextRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class PayRefundBizContextAssembler {

    private PayRefundBizContextAssembler() {
    }

    static PayRefundBizContextRespVO buildBase(PayRefundDO refund, Long orderId, Long unitId) {
        PayRefundBizContextRespVO respVO = new PayRefundBizContextRespVO();
        respVO.setPayRefundId(refund.getId());
        respVO.setMerchantRefundId(refund.getMerchantRefundId());
        respVO.setOrderId(orderId);
        respVO.setUnitId(unitId);
        return respVO;
    }

    static PayRefundBizContextRespVO.OrderSimpleRespVO buildOrder(OrderInfoDO order) {
        if (order == null) {
            return null;
        }
        PayRefundBizContextRespVO.OrderSimpleRespVO respVO = new PayRefundBizContextRespVO.OrderSimpleRespVO();
        respVO.setId(order.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setUserId(order.getUserId());
        respVO.setMerchantId(order.getMerchantId());
        respVO.setPricingMode(order.getPricingMode());
        respVO.setOrderAmount(order.getOrderAmount());
        respVO.setSplitStatus(order.getSplitStatus());
        respVO.setStatus(order.getStatus());
        respVO.setPayOrderId(order.getPayOrderId());
        respVO.setCreateTime(order.getCreateTime());
        return respVO;
    }

    static PayRefundBizContextRespVO.OrderUnitSimpleRespVO buildUnit(OrderUnitDO unit) {
        if (unit == null) {
            return null;
        }
        PayRefundBizContextRespVO.OrderUnitSimpleRespVO respVO = new PayRefundBizContextRespVO.OrderUnitSimpleRespVO();
        respVO.setId(unit.getId());
        respVO.setOrderId(unit.getOrderId());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setUnitSeq(unit.getUnitSeq());
        respVO.setUnitTitle(unit.getUnitTitle());
        respVO.setUnitAmount(unit.getUnitAmount());
        respVO.setIsLocked(unit.getIsLocked());
        respVO.setLockReason(unit.getLockReason());
        respVO.setMerchantId(unit.getMerchantId());
        respVO.setStatus(unit.getStatus());
        respVO.setCreateTime(unit.getCreateTime());
        return respVO;
    }

    static List<PayRefundBizContextRespVO.WalletFlowSimpleRespVO> buildFlows(List<WalletFlowDO> flows) {
        if (flows == null || flows.isEmpty()) {
            return Collections.emptyList();
        }
        return flows.stream().map(flow -> {
            PayRefundBizContextRespVO.WalletFlowSimpleRespVO respVO = new PayRefundBizContextRespVO.WalletFlowSimpleRespVO();
            respVO.setId(flow.getId());
            respVO.setFlowNo(flow.getFlowNo());
            respVO.setUserId(flow.getUserId());
            respVO.setWalletAccountId(flow.getWalletAccountId());
            respVO.setBizType(flow.getBizType());
            respVO.setFlowType(flow.getFlowType());
            respVO.setChangeAmount(flow.getChangeAmount());
            respVO.setBeforeAmount(flow.getBeforeAmount());
            respVO.setAfterAmount(flow.getAfterAmount());
            respVO.setRelatedOrderId(flow.getRelatedOrderId());
            respVO.setRelatedUnitId(flow.getRelatedUnitId());
            respVO.setRelatedPayOrderId(flow.getRelatedPayOrderId());
            respVO.setRelatedRefundId(flow.getRelatedRefundId());
            respVO.setRemark(flow.getRemark());
            respVO.setCreateTime(flow.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    static List<PayRefundBizContextRespVO.ComplaintSimpleRespVO> buildComplaints(List<ComplaintDO> complaints) {
        if (complaints == null || complaints.isEmpty()) {
            return Collections.emptyList();
        }
        return complaints.stream().map(complaint -> {
            PayRefundBizContextRespVO.ComplaintSimpleRespVO respVO = new PayRefundBizContextRespVO.ComplaintSimpleRespVO();
            respVO.setId(complaint.getId());
            respVO.setComplaintNo(complaint.getComplaintNo());
            respVO.setOrderId(complaint.getOrderId());
            respVO.setUnitId(complaint.getUnitId());
            respVO.setComplainantUserId(complaint.getComplainantUserId());
            respVO.setRespondentUserId(complaint.getRespondentUserId());
            respVO.setComplaintType(complaint.getComplaintType());
            respVO.setContent(complaint.getContent());
            respVO.setStatus(complaint.getStatus());
            respVO.setResultDesc(complaint.getResultDesc());
            respVO.setHandleTime(complaint.getHandleTime());
            respVO.setCreateTime(complaint.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    static List<PayRefundBizContextRespVO.AppealSimpleRespVO> buildAppeals(List<AppealDO> appeals) {
        if (appeals == null || appeals.isEmpty()) {
            return Collections.emptyList();
        }
        return appeals.stream().map(appeal -> {
            PayRefundBizContextRespVO.AppealSimpleRespVO respVO = new PayRefundBizContextRespVO.AppealSimpleRespVO();
            respVO.setId(appeal.getId());
            respVO.setAppealNo(appeal.getAppealNo());
            respVO.setOrderId(appeal.getOrderId());
            respVO.setUnitId(appeal.getUnitId());
            respVO.setUserId(appeal.getUserId());
            respVO.setAppealType(appeal.getAppealType());
            respVO.setContent(appeal.getContent());
            respVO.setStatus(appeal.getStatus());
            respVO.setAuditStatus(appeal.getAuditStatus());
            respVO.setAuditRemark(appeal.getAuditRemark());
            respVO.setRejectReason(appeal.getRejectReason());
            respVO.setAuditTime(appeal.getAuditTime());
            respVO.setCreateTime(appeal.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    static List<PayRefundBizContextRespVO.OrderOperateLogSimpleRespVO> buildLogs(List<OrderOperateLogDO> logs) {
        if (logs == null || logs.isEmpty()) {
            return Collections.emptyList();
        }
        return logs.stream().map(log -> {
            PayRefundBizContextRespVO.OrderOperateLogSimpleRespVO respVO = new PayRefundBizContextRespVO.OrderOperateLogSimpleRespVO();
            respVO.setId(log.getId());
            respVO.setOrderId(log.getOrderId());
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
