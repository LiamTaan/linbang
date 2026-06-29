package cn.iocoder.yudao.module.linbang.service.walletflow;

import cn.iocoder.yudao.module.linbang.controller.admin.walletflow.vo.WalletFlowDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;

final class WalletFlowDetailAssembler {

    private WalletFlowDetailAssembler() {
    }

    static WalletFlowDetailRespVO buildDetail(WalletFlowDO flow, MemberUserDO user) {
        if (flow == null) {
            return null;
        }
        WalletFlowDetailRespVO respVO = new WalletFlowDetailRespVO();
        respVO.setId(flow.getId());
        respVO.setFlowNo(flow.getFlowNo());
        respVO.setUserId(flow.getUserId());
        if (user != null) {
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(user.getMobile());
        }
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
    }

    static WalletFlowDetailRespVO.WalletAccountSimpleRespVO buildWalletAccount(WalletAccountDO account) {
        if (account == null) {
            return null;
        }
        WalletFlowDetailRespVO.WalletAccountSimpleRespVO respVO = new WalletFlowDetailRespVO.WalletAccountSimpleRespVO();
        respVO.setId(account.getId());
        respVO.setUserId(account.getUserId());
        respVO.setRoleCode(account.getRoleCode());
        respVO.setTotalAmount(account.getTotalAmount());
        respVO.setAvailableAmount(account.getAvailableAmount());
        respVO.setFrozenAmount(account.getFrozenAmount());
        respVO.setEscrowAmount(account.getEscrowAmount());
        respVO.setCommissionAmount(account.getCommissionAmount());
        respVO.setStatus(account.getStatus());
        return respVO;
    }

    static WalletFlowDetailRespVO.OrderSimpleRespVO buildOrder(OrderInfoDO order) {
        if (order == null) {
            return null;
        }
        WalletFlowDetailRespVO.OrderSimpleRespVO respVO = new WalletFlowDetailRespVO.OrderSimpleRespVO();
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

    static WalletFlowDetailRespVO.OrderUnitSimpleRespVO buildUnit(OrderUnitDO unit, MerchantInfoDO merchant) {
        if (unit == null) {
            return null;
        }
        WalletFlowDetailRespVO.OrderUnitSimpleRespVO respVO = new WalletFlowDetailRespVO.OrderUnitSimpleRespVO();
        respVO.setId(unit.getId());
        respVO.setOrderId(unit.getOrderId());
        respVO.setUnitNo(unit.getUnitNo());
        respVO.setUnitSeq(unit.getUnitSeq());
        respVO.setUnitTitle(unit.getUnitTitle());
        respVO.setUnitAmount(unit.getUnitAmount());
        respVO.setIsLocked(unit.getIsLocked());
        respVO.setLockReason(unit.getLockReason());
        respVO.setMerchantId(unit.getMerchantId());
        if (merchant != null) {
            respVO.setMerchantName(merchant.getMerchantName());
            respVO.setMerchantContactName(merchant.getContactName());
            respVO.setMerchantContactMobile(merchant.getContactMobile());
        }
        respVO.setStatus(unit.getStatus());
        respVO.setCreateTime(unit.getCreateTime());
        return respVO;
    }

    static WalletFlowDetailRespVO.PayOrderSimpleRespVO buildPayOrder(PayOrderDO payOrder) {
        if (payOrder == null) {
            return null;
        }
        WalletFlowDetailRespVO.PayOrderSimpleRespVO respVO = new WalletFlowDetailRespVO.PayOrderSimpleRespVO();
        respVO.setId(payOrder.getId());
        respVO.setMerchantOrderId(payOrder.getMerchantOrderId());
        respVO.setSubject(payOrder.getSubject());
        respVO.setPrice(payOrder.getPrice());
        respVO.setStatus(payOrder.getStatus());
        respVO.setChannelCode(payOrder.getChannelCode());
        respVO.setChannelOrderNo(payOrder.getChannelOrderNo());
        respVO.setNo(payOrder.getNo());
        respVO.setRefundPrice(payOrder.getRefundPrice());
        respVO.setSuccessTime(payOrder.getSuccessTime());
        respVO.setCreateTime(payOrder.getCreateTime());
        return respVO;
    }

    static WalletFlowDetailRespVO.PayRefundSimpleRespVO buildRefund(PayRefundDO refund) {
        if (refund == null) {
            return null;
        }
        WalletFlowDetailRespVO.PayRefundSimpleRespVO respVO = new WalletFlowDetailRespVO.PayRefundSimpleRespVO();
        respVO.setId(refund.getId());
        respVO.setMerchantRefundId(refund.getMerchantRefundId());
        respVO.setMerchantOrderId(refund.getMerchantOrderId());
        respVO.setStatus(refund.getStatus());
        respVO.setAuditStatus(refund.getAuditStatus());
        respVO.setAuditRemark(refund.getAuditRemark());
        respVO.setRejectReason(refund.getRejectReason());
        respVO.setRefundPrice(refund.getRefundPrice());
        respVO.setReason(refund.getReason());
        respVO.setSuccessTime(refund.getSuccessTime());
        respVO.setCreateTime(refund.getCreateTime());
        return respVO;
    }

}
