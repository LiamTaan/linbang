package cn.iocoder.yudao.module.linbang.service.app.wallet;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardDefaultReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AppWalletServiceImpl implements AppWalletService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;

    @Override
    public AppWalletAccountRespVO getWalletAccount(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        WalletAccountDO walletAccount = getOrCreateWalletAccount(authUserId, loginUser);
        AppWalletAccountRespVO respVO = new AppWalletAccountRespVO();
        respVO.setId(walletAccount.getId());
        respVO.setTotalAmount(walletAccount.getTotalAmount());
        respVO.setAvailableAmount(walletAccount.getAvailableAmount());
        respVO.setFrozenAmount(walletAccount.getFrozenAmount());
        respVO.setEscrowAmount(walletAccount.getEscrowAmount());
        respVO.setCommissionAmount(walletAccount.getCommissionAmount());
        respVO.setStatus(walletAccount.getStatus());
        return respVO;
    }

    @Override
    public PageResult<AppBankCardRespVO> getBankCardPage(Long authUserId, AppBankCardPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<WalletBankCardDO> pageResult = walletBankCardMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<WalletBankCardDO>()
                        .eq(WalletBankCardDO::getUserId, loginUser.getId())
                        .eqIfPresent(WalletBankCardDO::getStatus, reqVO.getStatus())
                        .orderByDesc(WalletBankCardDO::getIsDefault)
                        .orderByDesc(WalletBankCardDO::getId));
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), AppBankCardRespVO.class), pageResult.getTotal());
    }

    @Override
    public AppBankCardRespVO getBankCard(Long authUserId, Long id) {
        return BeanUtils.toBean(getValidatedBankCard(authUserId, id), AppBankCardRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWithdraw(Long authUserId, @Valid AppWalletWithdrawCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        WalletAccountDO walletAccount = getOrCreateWalletAccount(authUserId, loginUser);
        if (reqVO.getApplyAmount() == null || reqVO.getApplyAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(WALLET_WITHDRAW_AMOUNT_INVALID);
        }
        if (walletAccount.getAvailableAmount().compareTo(reqVO.getApplyAmount()) < 0) {
            throw exception(WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH);
        }

        WalletBankCardDO bankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                .eq(WalletBankCardDO::getId, reqVO.getBankCardId())
                .eq(WalletBankCardDO::getUserId, loginUser.getId())
                .eq(WalletBankCardDO::getStatus, "ENABLE"));
        if (bankCard == null) {
            throw exception(WALLET_BANK_CARD_INVALID);
        }

        WalletWithdrawDO withdraw = WalletWithdrawDO.builder()
                .withdrawNo("LBW" + IdUtil.getSnowflakeNextIdStr())
                .userId(loginUser.getId())
                .walletAccountId(walletAccount.getId())
                .bankCardId(bankCard.getId())
                .applyAmount(reqVO.getApplyAmount())
                .feeAmount(BigDecimal.ZERO)
                .realAmount(reqVO.getApplyAmount())
                .status("PENDING")
                .auditStatus("PENDING")
                .build();
        walletWithdrawMapper.insert(withdraw);

        BigDecimal beforeAvailable = walletAccount.getAvailableAmount();
        BigDecimal beforeFrozen = walletAccount.getFrozenAmount();
        BigDecimal afterAvailable = beforeAvailable.subtract(reqVO.getApplyAmount());
        BigDecimal afterFrozen = beforeFrozen.add(reqVO.getApplyAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .availableAmount(afterAvailable)
                .frozenAmount(afterFrozen)
                .build());

        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(loginUser.getId())
                .walletAccountId(walletAccount.getId())
                .bizType("WITHDRAW_APPLY")
                .flowType("OUT")
                .changeAmount(reqVO.getApplyAmount().negate())
                .beforeAmount(beforeAvailable)
                .afterAmount(afterAvailable)
                .remark("用户提交提现申请")
                .createTime(LocalDateTime.now())
                .build());
        return withdraw.getId();
    }

    @Override
    public PageResult<AppWalletWithdrawRespVO> getWithdrawPage(Long authUserId, AppWalletWithdrawPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<WalletWithdrawDO> pageResult = walletWithdrawMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<WalletWithdrawDO>()
                        .eq(WalletWithdrawDO::getUserId, loginUser.getId())
                        .eqIfPresent(WalletWithdrawDO::getStatus, reqVO.getStatus())
                        .eqIfPresent(WalletWithdrawDO::getAuditStatus, reqVO.getAuditStatus())
                        .betweenIfPresent(WalletWithdrawDO::getCreateTime, reqVO.getCreateTime())
                        .orderByDesc(WalletWithdrawDO::getId));
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), AppWalletWithdrawRespVO.class), pageResult.getTotal());
    }

    @Override
    public AppWalletWithdrawRespVO getWithdraw(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectOne(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getId, id)
                .eq(WalletWithdrawDO::getUserId, loginUser.getId()));
        if (withdraw == null) {
            throw exception(WALLET_WITHDRAW_NOT_EXISTS);
        }
        return BeanUtils.toBean(withdraw, AppWalletWithdrawRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBankCard(Long authUserId, @Valid AppBankCardCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        resetDefaultBankCardIfNeeded(loginUser.getId(), reqVO.getIsDefault());
        WalletBankCardDO bankCard = WalletBankCardDO.builder()
                .userId(loginUser.getId())
                .bankName(reqVO.getBankName())
                .bankCode(reqVO.getBankCode())
                .cardNoEncrypt(DigestUtil.sha256Hex(reqVO.getCardNo()))
                .cardNoMask(maskCardNo(reqVO.getCardNo()))
                .accountName(reqVO.getAccountName())
                .reservedMobile(StrUtil.blankToDefault(reqVO.getReservedMobile(), loginUser.getMobile()))
                .status("ENABLE")
                .isDefault(reqVO.getIsDefault())
                .build();
        walletBankCardMapper.insert(bankCard);
        return bankCard.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBankCard(Long authUserId, @Valid AppBankCardUpdateReqVO reqVO) {
        WalletBankCardDO bankCard = getValidatedBankCard(authUserId, reqVO.getId());
        resetDefaultBankCardIfNeeded(bankCard.getUserId(), reqVO.getIsDefault());
        walletBankCardMapper.updateById(WalletBankCardDO.builder()
                .id(bankCard.getId())
                .bankName(reqVO.getBankName())
                .bankCode(reqVO.getBankCode())
                .accountName(reqVO.getAccountName())
                .reservedMobile(reqVO.getReservedMobile())
                .isDefault(reqVO.getIsDefault())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultBankCard(Long authUserId, @Valid AppBankCardDefaultReqVO reqVO) {
        WalletBankCardDO bankCard = getValidatedBankCard(authUserId, reqVO.getId());
        resetDefaultBankCardIfNeeded(bankCard.getUserId(), Boolean.TRUE);
        walletBankCardMapper.updateById(WalletBankCardDO.builder()
                .id(bankCard.getId())
                .isDefault(Boolean.TRUE)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBankCard(Long authUserId, Long id) {
        WalletBankCardDO bankCard = getValidatedBankCard(authUserId, id);
        long pendingWithdrawCount = walletWithdrawMapper.selectCount(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getBankCardId, bankCard.getId())
                .in(WalletWithdrawDO::getAuditStatus, "PENDING", "APPROVED")
                .in(WalletWithdrawDO::getStatus, "PENDING", "APPROVED"));
        if (pendingWithdrawCount > 0) {
            throw exception(WALLET_BANK_CARD_INVALID);
        }
        walletBankCardMapper.deleteById(bankCard.getId());
        if (Boolean.TRUE.equals(bankCard.getIsDefault())) {
            WalletBankCardDO nextDefaultCard = walletBankCardMapper.selectOne(new LambdaQueryWrapper<WalletBankCardDO>()
                    .eq(WalletBankCardDO::getUserId, bankCard.getUserId())
                    .orderByDesc(WalletBankCardDO::getId)
                    .last("LIMIT 1"));
            if (nextDefaultCard != null) {
                walletBankCardMapper.updateById(WalletBankCardDO.builder()
                        .id(nextDefaultCard.getId())
                        .isDefault(Boolean.TRUE)
                        .build());
            }
        }
    }

    @Override
    public PageResult<AppWalletFlowRespVO> getWalletFlowPage(Long authUserId, AppWalletFlowPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<WalletFlowDO> pageResult = walletFlowMapper.selectPage(reqVO,
                new LambdaQueryWrapperX<WalletFlowDO>()
                        .eq(WalletFlowDO::getUserId, loginUser.getId())
                        .eqIfPresent(WalletFlowDO::getBizType, reqVO.getBizType())
                        .eqIfPresent(WalletFlowDO::getFlowType, reqVO.getFlowType())
                        .betweenIfPresent(WalletFlowDO::getCreateTime, reqVO.getCreateTime())
                        .orderByDesc(WalletFlowDO::getId));
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), AppWalletFlowRespVO.class), pageResult.getTotal());
    }

    @Override
    public AppWalletFlowRespVO getWalletFlow(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        WalletFlowDO walletFlow = walletFlowMapper.selectOne(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getId, id)
                .eq(WalletFlowDO::getUserId, loginUser.getId()));
        if (walletFlow == null) {
            throw exception(WALLET_FLOW_NOT_EXISTS);
        }
        return BeanUtils.toBean(walletFlow, AppWalletFlowRespVO.class);
    }

    private WalletAccountDO getOrCreateWalletAccount(Long authUserId, MemberUserDO loginUser) {
        String roleCode = resolveRoleCode(authUserId, loginUser);
        WalletAccountDO walletAccount = walletAccountMapper.selectOne(new LambdaQueryWrapperX<WalletAccountDO>()
                .eq(WalletAccountDO::getUserId, loginUser.getId())
                .eq(WalletAccountDO::getRoleCode, roleCode));
        if (walletAccount != null) {
            return walletAccount;
        }
        walletAccount = WalletAccountDO.builder()
                .userId(loginUser.getId())
                .roleCode(roleCode)
                .totalAmount(BigDecimal.ZERO)
                .availableAmount(BigDecimal.ZERO)
                .frozenAmount(BigDecimal.ZERO)
                .escrowAmount(BigDecimal.ZERO)
                .commissionAmount(BigDecimal.ZERO)
                .status("ENABLE")
                .build();
        walletAccountMapper.insert(walletAccount);
        return walletAccount;
    }

    private String resolveRoleCode(Long authUserId, MemberUserDO loginUser) {
        if ("MERCHANT".equalsIgnoreCase(loginUser.getCurrentRoleCode())) {
            return "MERCHANT";
        }
        MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, authUserId)
                .eq(MerchantInfoDO::getStatus, "ENABLE"));
        return merchant != null ? "MERCHANT" : Optional.ofNullable(loginUser.getCurrentRoleCode()).orElse("USER");
    }

    private String maskCardNo(String cardNo) {
        if (cardNo == null || cardNo.length() <= 4) {
            return cardNo;
        }
        String last4 = cardNo.substring(cardNo.length() - 4);
        String first4 = cardNo.substring(0, Math.min(cardNo.length(), 4));
        return first4 + " **** **** " + last4;
    }

    private void resetDefaultBankCardIfNeeded(Long userId, Boolean isDefault) {
        if (!Boolean.TRUE.equals(isDefault)) {
            return;
        }
        walletBankCardMapper.update(null, new LambdaUpdateWrapper<WalletBankCardDO>()
                .eq(WalletBankCardDO::getUserId, userId)
                .set(WalletBankCardDO::getIsDefault, Boolean.FALSE));
    }

    private WalletBankCardDO getValidatedBankCard(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        WalletBankCardDO bankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                .eq(WalletBankCardDO::getId, id)
                .eq(WalletBankCardDO::getUserId, loginUser.getId()));
        if (bankCard == null) {
            throw exception(WALLET_BANK_CARD_NOT_EXISTS);
        }
        return bankCard;
    }
}
