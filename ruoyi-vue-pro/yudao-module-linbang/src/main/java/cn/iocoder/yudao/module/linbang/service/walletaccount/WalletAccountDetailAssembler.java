package cn.iocoder.yudao.module.linbang.service.walletaccount;

import cn.iocoder.yudao.module.linbang.controller.admin.walletaccount.vo.WalletAccountDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class WalletAccountDetailAssembler {

    private WalletAccountDetailAssembler() {
    }

    static WalletAccountDetailRespVO buildDetail(WalletAccountDO account) {
        if (account == null) {
            return null;
        }
        WalletAccountDetailRespVO respVO = new WalletAccountDetailRespVO();
        respVO.setId(account.getId());
        respVO.setUserId(account.getUserId());
        respVO.setRoleCode(account.getRoleCode());
        respVO.setTotalAmount(account.getTotalAmount());
        respVO.setAvailableAmount(account.getAvailableAmount());
        respVO.setFrozenAmount(account.getFrozenAmount());
        respVO.setEscrowAmount(account.getEscrowAmount());
        respVO.setCommissionAmount(account.getCommissionAmount());
        respVO.setStatus(account.getStatus());
        respVO.setCreateTime(account.getCreateTime());
        respVO.setUpdateTime(account.getUpdateTime());
        return respVO;
    }

    static WalletAccountDetailRespVO.MemberUserSimpleRespVO buildUser(MemberUserDO user) {
        if (user == null) {
            return null;
        }
        WalletAccountDetailRespVO.MemberUserSimpleRespVO respVO = new WalletAccountDetailRespVO.MemberUserSimpleRespVO();
        respVO.setId(user.getId());
        respVO.setUserNo(user.getUserNo());
        respVO.setMobile(user.getMobile());
        respVO.setNickname(user.getNickname());
        respVO.setCurrentRoleCode(user.getCurrentRoleCode());
        respVO.setStatus(user.getStatus());
        return respVO;
    }

    static WalletAccountDetailRespVO.WalletBankCardSimpleRespVO buildBankCard(WalletBankCardDO bankCard) {
        if (bankCard == null) {
            return null;
        }
        WalletAccountDetailRespVO.WalletBankCardSimpleRespVO respVO = new WalletAccountDetailRespVO.WalletBankCardSimpleRespVO();
        respVO.setId(bankCard.getId());
        respVO.setUserId(bankCard.getUserId());
        respVO.setBankName(bankCard.getBankName());
        respVO.setBankCode(bankCard.getBankCode());
        respVO.setCardNoMask(bankCard.getCardNoMask());
        respVO.setAccountName(bankCard.getAccountName());
        respVO.setReservedMobile(bankCard.getReservedMobile());
        respVO.setStatus(bankCard.getStatus());
        respVO.setIsDefault(bankCard.getIsDefault());
        respVO.setCreateTime(bankCard.getCreateTime());
        return respVO;
    }

    static List<WalletAccountDetailRespVO.WalletFlowSimpleRespVO> buildFlows(List<WalletFlowDO> flows) {
        if (flows == null || flows.isEmpty()) {
            return Collections.emptyList();
        }
        return flows.stream().map(flow -> {
            WalletAccountDetailRespVO.WalletFlowSimpleRespVO respVO = new WalletAccountDetailRespVO.WalletFlowSimpleRespVO();
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

    static List<WalletAccountDetailRespVO.WalletWithdrawSimpleRespVO> buildWithdraws(List<WalletWithdrawDO> withdraws) {
        if (withdraws == null || withdraws.isEmpty()) {
            return Collections.emptyList();
        }
        return withdraws.stream().map(withdraw -> {
            WalletAccountDetailRespVO.WalletWithdrawSimpleRespVO respVO = new WalletAccountDetailRespVO.WalletWithdrawSimpleRespVO();
            respVO.setId(withdraw.getId());
            respVO.setWithdrawNo(withdraw.getWithdrawNo());
            respVO.setBankCardId(withdraw.getBankCardId());
            respVO.setApplyAmount(withdraw.getApplyAmount());
            respVO.setFeeAmount(withdraw.getFeeAmount());
            respVO.setRealAmount(withdraw.getRealAmount());
            respVO.setStatus(withdraw.getStatus());
            respVO.setAuditStatus(withdraw.getAuditStatus());
            respVO.setAuditRemark(withdraw.getAuditRemark());
            respVO.setRejectReason(withdraw.getRejectReason());
            respVO.setPayTime(withdraw.getPayTime());
            respVO.setCreateTime(withdraw.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    static WalletAccountDetailRespVO.WithdrawStatRespVO buildWithdrawStats(List<WalletWithdrawDO> withdraws) {
        WalletAccountDetailRespVO.WithdrawStatRespVO respVO = new WalletAccountDetailRespVO.WithdrawStatRespVO();
        respVO.setTotalCount(withdraws == null ? 0 : withdraws.size());
        respVO.setTotalApplyAmount(sumAmount(withdraws, null));
        respVO.setPendingCount(countByStatus(withdraws, "PENDING"));
        respVO.setPendingAmount(sumAmount(withdraws, "PENDING"));
        respVO.setSuccessCount(countByStatus(withdraws, "SUCCESS"));
        respVO.setSuccessAmount(sumAmount(withdraws, "SUCCESS"));
        return respVO;
    }

    private static Integer countByStatus(List<WalletWithdrawDO> withdraws, String status) {
        if (withdraws == null || withdraws.isEmpty()) {
            return 0;
        }
        int count = 0;
        for (WalletWithdrawDO withdraw : withdraws) {
            if (status.equalsIgnoreCase(withdraw.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private static BigDecimal sumAmount(List<WalletWithdrawDO> withdraws, String status) {
        if (withdraws == null || withdraws.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (WalletWithdrawDO withdraw : withdraws) {
            if (status == null || status.equalsIgnoreCase(withdraw.getStatus())) {
                total = total.add(withdraw.getApplyAmount() == null ? BigDecimal.ZERO : withdraw.getApplyAmount());
            }
        }
        return total;
    }

}
