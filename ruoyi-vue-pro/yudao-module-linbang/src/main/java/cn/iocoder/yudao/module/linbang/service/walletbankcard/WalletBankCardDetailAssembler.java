package cn.iocoder.yudao.module.linbang.service.walletbankcard;

import cn.iocoder.yudao.module.linbang.controller.admin.walletbankcard.vo.WalletBankCardDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;

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
            respVO.setUserMobile(user.getMobile());
        }
        respVO.setBankName(bankCard.getBankName());
        respVO.setBankCode(bankCard.getBankCode());
        respVO.setCardNoEncrypt(bankCard.getCardNoEncrypt());
        respVO.setCardNoMask(bankCard.getCardNoMask());
        respVO.setAccountName(bankCard.getAccountName());
        respVO.setReservedMobile(bankCard.getReservedMobile());
        respVO.setStatus(bankCard.getStatus());
        respVO.setIsDefault(bankCard.getIsDefault());
        respVO.setCreateTime(bankCard.getCreateTime());
        respVO.setUpdateTime(bankCard.getUpdateTime());
        return respVO;
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

    static WalletBankCardDetailRespVO.WithdrawStatRespVO buildWithdrawStats(List<WalletWithdrawDO> withdraws) {
        WalletBankCardDetailRespVO.WithdrawStatRespVO respVO = new WalletBankCardDetailRespVO.WithdrawStatRespVO();
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
