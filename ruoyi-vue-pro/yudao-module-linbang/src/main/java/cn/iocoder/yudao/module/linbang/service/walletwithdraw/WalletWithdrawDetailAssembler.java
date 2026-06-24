package cn.iocoder.yudao.module.linbang.service.walletwithdraw;

import cn.iocoder.yudao.module.linbang.controller.admin.walletwithdraw.vo.WalletWithdrawDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class WalletWithdrawDetailAssembler {

    private WalletWithdrawDetailAssembler() {
    }

    static WalletWithdrawDetailRespVO.MemberUserSimpleRespVO buildUser(MemberUserDO user) {
        if (user == null) {
            return null;
        }
        WalletWithdrawDetailRespVO.MemberUserSimpleRespVO respVO = new WalletWithdrawDetailRespVO.MemberUserSimpleRespVO();
        respVO.setId(user.getId());
        respVO.setUserNo(user.getUserNo());
        respVO.setMobile(user.getMobile());
        respVO.setNickname(user.getNickname());
        respVO.setCurrentRoleCode(user.getCurrentRoleCode());
        respVO.setStatus(user.getStatus());
        return respVO;
    }

    static WalletWithdrawDetailRespVO.WalletAccountSimpleRespVO buildWalletAccount(WalletAccountDO account) {
        if (account == null) {
            return null;
        }
        WalletWithdrawDetailRespVO.WalletAccountSimpleRespVO respVO = new WalletWithdrawDetailRespVO.WalletAccountSimpleRespVO();
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

    static WalletWithdrawDetailRespVO.WalletBankCardSimpleRespVO buildBankCard(WalletBankCardDO bankCard) {
        if (bankCard == null) {
            return null;
        }
        WalletWithdrawDetailRespVO.WalletBankCardSimpleRespVO respVO = new WalletWithdrawDetailRespVO.WalletBankCardSimpleRespVO();
        respVO.setId(bankCard.getId());
        respVO.setUserId(bankCard.getUserId());
        respVO.setBankName(bankCard.getBankName());
        respVO.setBankCode(bankCard.getBankCode());
        respVO.setCardNoMask(bankCard.getCardNoMask());
        respVO.setAccountName(bankCard.getAccountName());
        respVO.setReservedMobile(bankCard.getReservedMobile());
        respVO.setStatus(bankCard.getStatus());
        respVO.setIsDefault(bankCard.getIsDefault());
        return respVO;
    }

    static List<WalletWithdrawDetailRespVO.WalletFlowSimpleRespVO> buildFlows(List<WalletFlowDO> flows) {
        if (flows == null || flows.isEmpty()) {
            return Collections.emptyList();
        }
        return flows.stream().map(flow -> {
            WalletWithdrawDetailRespVO.WalletFlowSimpleRespVO respVO = new WalletWithdrawDetailRespVO.WalletFlowSimpleRespVO();
            respVO.setId(flow.getId());
            respVO.setFlowNo(flow.getFlowNo());
            respVO.setUserId(flow.getUserId());
            respVO.setWalletAccountId(flow.getWalletAccountId());
            respVO.setBizType(flow.getBizType());
            respVO.setFlowType(flow.getFlowType());
            respVO.setChangeAmount(flow.getChangeAmount());
            respVO.setBeforeAmount(flow.getBeforeAmount());
            respVO.setAfterAmount(flow.getAfterAmount());
            respVO.setRelatedPayOrderId(flow.getRelatedPayOrderId());
            respVO.setRelatedRefundId(flow.getRelatedRefundId());
            respVO.setRemark(flow.getRemark());
            respVO.setCreateTime(flow.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

}
