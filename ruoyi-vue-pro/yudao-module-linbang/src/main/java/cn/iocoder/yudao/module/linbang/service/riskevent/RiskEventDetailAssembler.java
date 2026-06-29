package cn.iocoder.yudao.module.linbang.service.riskevent;

import cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo.RiskEventDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskrule.RiskRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class RiskEventDetailAssembler {

    private RiskEventDetailAssembler() {
    }

    static RiskEventDetailRespVO.RiskRuleSimpleRespVO buildRiskRule(RiskRuleDO riskRule) {
        if (riskRule == null) {
            return null;
        }
        RiskEventDetailRespVO.RiskRuleSimpleRespVO respVO = new RiskEventDetailRespVO.RiskRuleSimpleRespVO();
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

    static RiskEventDetailRespVO.OrderSimpleRespVO buildOrder(OrderInfoDO order, MemberUserDO orderUser,
                                                              MerchantInfoDO merchant) {
        if (order == null) {
            return null;
        }
        RiskEventDetailRespVO.OrderSimpleRespVO respVO = new RiskEventDetailRespVO.OrderSimpleRespVO();
        respVO.setId(order.getId());
        respVO.setOrderNo(order.getOrderNo());
        respVO.setStatus(order.getStatus());
        respVO.setUserId(order.getUserId());
        if (orderUser != null) {
            respVO.setUserNo(orderUser.getUserNo());
            respVO.setUserNickname(orderUser.getNickname());
            respVO.setUserMobile(orderUser.getMobile());
        }
        respVO.setMerchantId(order.getMerchantId());
        if (merchant != null) {
            respVO.setMerchantName(merchant.getMerchantName());
            respVO.setMerchantContactName(merchant.getContactName());
            respVO.setMerchantContactMobile(merchant.getContactMobile());
        }
        return respVO;
    }

    static RiskEventDetailRespVO.OrderUnitSimpleRespVO buildUnit(OrderUnitDO unit, MerchantInfoDO merchant) {
        if (unit == null) {
            return null;
        }
        RiskEventDetailRespVO.OrderUnitSimpleRespVO respVO = new RiskEventDetailRespVO.OrderUnitSimpleRespVO();
        respVO.setId(unit.getId());
        respVO.setOrderId(unit.getOrderId());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setUnitSeq(unit.getUnitSeq());
        respVO.setUnitTitle(unit.getUnitTitle());
        respVO.setStatus(unit.getStatus());
        respVO.setIsLocked(unit.getIsLocked());
        respVO.setLockReason(unit.getLockReason());
        respVO.setMerchantId(unit.getMerchantId());
        if (merchant != null) {
            respVO.setMerchantName(merchant.getMerchantName());
            respVO.setMerchantContactName(merchant.getContactName());
            respVO.setMerchantContactMobile(merchant.getContactMobile());
        }
        return respVO;
    }

    static RiskEventDetailRespVO.OrderAbnormalSimpleRespVO buildAbnormal(OrderAbnormalDO abnormal) {
        if (abnormal == null) {
            return null;
        }
        RiskEventDetailRespVO.OrderAbnormalSimpleRespVO respVO = new RiskEventDetailRespVO.OrderAbnormalSimpleRespVO();
        respVO.setId(abnormal.getId());
        respVO.setOrderId(abnormal.getOrderId());
        respVO.setUnitId(abnormal.getUnitId());
        respVO.setAbnormalNo(abnormal.getAbnormalNo());
        respVO.setAbnormalType(abnormal.getAbnormalType());
        respVO.setRiskLevel(abnormal.getRiskLevel());
        respVO.setHandleStatus(abnormal.getHandleStatus());
        respVO.setHandleBy(abnormal.getHandleBy());
        respVO.setHandleTime(abnormal.getHandleTime());
        respVO.setRemark(abnormal.getRemark());
        respVO.setCreateTime(abnormal.getCreateTime());
        return respVO;
    }

    static RiskEventDetailRespVO.ComplaintSimpleRespVO buildComplaint(ComplaintDO complaint) {
        if (complaint == null) {
            return null;
        }
        RiskEventDetailRespVO.ComplaintSimpleRespVO respVO = new RiskEventDetailRespVO.ComplaintSimpleRespVO();
        respVO.setId(complaint.getId());
        respVO.setComplaintNo(complaint.getComplaintNo());
        respVO.setOrderId(complaint.getOrderId());
        respVO.setUnitId(complaint.getUnitId());
        respVO.setComplainantUserId(complaint.getComplainantUserId());
        respVO.setRespondentUserId(complaint.getRespondentUserId());
        respVO.setComplaintType(complaint.getComplaintType());
        respVO.setStatus(complaint.getStatus());
        respVO.setHandleTime(complaint.getHandleTime());
        respVO.setResultDesc(complaint.getResultDesc());
        respVO.setCreateTime(complaint.getCreateTime());
        return respVO;
    }

    static RiskEventDetailRespVO.AppealSimpleRespVO buildAppeal(AppealDO appeal) {
        if (appeal == null) {
            return null;
        }
        RiskEventDetailRespVO.AppealSimpleRespVO respVO = new RiskEventDetailRespVO.AppealSimpleRespVO();
        respVO.setId(appeal.getId());
        respVO.setAppealNo(appeal.getAppealNo());
        respVO.setOrderId(appeal.getOrderId());
        respVO.setUnitId(appeal.getUnitId());
        respVO.setUserId(appeal.getUserId());
        respVO.setAppealType(appeal.getAppealType());
        respVO.setStatus(appeal.getStatus());
        respVO.setAuditStatus(appeal.getAuditStatus());
        respVO.setAuditBy(appeal.getAuditBy());
        respVO.setAuditTime(appeal.getAuditTime());
        respVO.setAuditRemark(appeal.getAuditRemark());
        respVO.setRejectReason(appeal.getRejectReason());
        respVO.setCreateTime(appeal.getCreateTime());
        return respVO;
    }

    static RiskEventDetailRespVO.WithdrawSimpleRespVO buildWithdraw(WalletWithdrawDO withdraw) {
        if (withdraw == null) {
            return null;
        }
        RiskEventDetailRespVO.WithdrawSimpleRespVO respVO = new RiskEventDetailRespVO.WithdrawSimpleRespVO();
        respVO.setId(withdraw.getId());
        respVO.setWithdrawNo(withdraw.getWithdrawNo());
        respVO.setUserId(withdraw.getUserId());
        respVO.setWalletAccountId(withdraw.getWalletAccountId());
        respVO.setBankCardId(withdraw.getBankCardId());
        respVO.setApplyAmount(withdraw.getApplyAmount());
        respVO.setFeeAmount(withdraw.getFeeAmount());
        respVO.setRealAmount(withdraw.getRealAmount());
        respVO.setStatus(withdraw.getStatus());
        respVO.setAuditStatus(withdraw.getAuditStatus());
        respVO.setAuditBy(withdraw.getAuditBy());
        respVO.setAuditTime(withdraw.getAuditTime());
        respVO.setAuditRemark(withdraw.getAuditRemark());
        respVO.setRejectReason(withdraw.getRejectReason());
        respVO.setPayTime(withdraw.getPayTime());
        respVO.setCreateTime(withdraw.getCreateTime());
        return respVO;
    }

    static List<RiskEventDetailRespVO.OrderOperateLogRespVO> buildLogs(List<OrderOperateLogDO> logs) {
        if (logs == null || logs.isEmpty()) {
            return Collections.emptyList();
        }
        return logs.stream().map(log -> {
            RiskEventDetailRespVO.OrderOperateLogRespVO respVO = new RiskEventDetailRespVO.OrderOperateLogRespVO();
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
