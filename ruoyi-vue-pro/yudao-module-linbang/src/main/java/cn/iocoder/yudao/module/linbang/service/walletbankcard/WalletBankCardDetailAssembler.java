package cn.iocoder.yudao.module.linbang.service.walletbankcard;

import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.WalletBankCardDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawStatDTO;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class WalletBankCardDetailAssembler {

    private WalletBankCardDetailAssembler() {
    }

    static WalletBankCardDetailRespVO buildDetail(WalletBankCardDO bankCard, MemberUserDO user) {
        if (bankCard == null) {
            return null;
        }
        WalletBankCardDetailRespVO respVO = new WalletBankCardDetailRespVO();
        respVO.setId(bankCard.getId());
        respVO.setUserId(bankCard.getUserId());
        if (user != null) {
            respVO.setUserNo(user.getUserNo());
            respVO.setUserNickname(user.getNickname());
            respVO.setUserMobile(maskMobile(user.getMobile()));
        }
        respVO.setBankName(bankCard.getBankName());
        respVO.setBankCode(bankCard.getBankCode());
        respVO.setCardNoMask(bankCard.getCardNoMask());
        respVO.setAccountName(bankCard.getAccountName());
        respVO.setBankProvince(bankCard.getBankProvince());
        respVO.setBankCity(bankCard.getBankCity());
        respVO.setReservedMobile(maskMobile(bankCard.getReservedMobile()));
        respVO.setStatus(bankCard.getStatus());
        respVO.setIsDefault(bankCard.getIsDefault());
        respVO.setCreateTime(bankCard.getCreateTime());
        respVO.setUpdateTime(bankCard.getUpdateTime());
        return respVO;
    }

    private static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 7) {
            return mobile == null ? null : "******";
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    static List<WalletBankCardDetailRespVO.WalletAccountSimpleRespVO> buildWalletAccounts(List<WalletAccountDO> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            return Collections.emptyList();
        }
        return accounts.stream().map(account -> {
            WalletBankCardDetailRespVO.WalletAccountSimpleRespVO respVO = new WalletBankCardDetailRespVO.WalletAccountSimpleRespVO();
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
        }).collect(Collectors.toList());
    }

    static List<WalletBankCardDetailRespVO.WalletWithdrawSimpleRespVO> buildWithdraws(List<WalletWithdrawDO> withdraws) {
        if (withdraws == null || withdraws.isEmpty()) {
            return Collections.emptyList();
        }
        return withdraws.stream().map(withdraw -> {
            WalletBankCardDetailRespVO.WalletWithdrawSimpleRespVO respVO = new WalletBankCardDetailRespVO.WalletWithdrawSimpleRespVO();
            respVO.setId(withdraw.getId());
            respVO.setWithdrawNo(withdraw.getWithdrawNo());
            respVO.setWalletAccountId(withdraw.getWalletAccountId());
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

    static WalletBankCardDetailRespVO.WithdrawStatRespVO buildWithdrawStats(WalletWithdrawStatDTO stat) {
        WalletBankCardDetailRespVO.WithdrawStatRespVO respVO = new WalletBankCardDetailRespVO.WithdrawStatRespVO();
        respVO.setTotalCount(stat == null || stat.getTotalCount() == null ? 0 : stat.getTotalCount());
        respVO.setTotalApplyAmount(stat == null || stat.getTotalApplyAmount() == null
                ? BigDecimal.ZERO : stat.getTotalApplyAmount());
        respVO.setPendingCount(stat == null || stat.getPendingCount() == null ? 0 : stat.getPendingCount());
        respVO.setPendingAmount(stat == null || stat.getPendingAmount() == null
                ? BigDecimal.ZERO : stat.getPendingAmount());
        respVO.setSuccessCount(stat == null || stat.getSuccessCount() == null ? 0 : stat.getSuccessCount());
        respVO.setSuccessAmount(stat == null || stat.getSuccessAmount() == null
                ? BigDecimal.ZERO : stat.getSuccessAmount());
        return respVO;
    }

}
