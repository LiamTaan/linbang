package cn.iocoder.yudao.module.linbang.service.app.wallet;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardDefaultReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppBankCardUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletBillPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletBillRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletFlowRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.wallet.vo.AppWalletWithdrawRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditlevelbenefit.CreditLevelBenefitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditLevelBenefitService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

@Service
@Validated
public class AppWalletServiceImpl implements AppWalletService {

    private static final BigDecimal MIN_WITHDRAW_AMOUNT = new BigDecimal("10.00");

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private PromoterMapper promoterMapper;
    @Resource
    private CreditLevelBenefitService creditLevelBenefitService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

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
        respVO.setMinWithdrawAmount(MIN_WITHDRAW_AMOUNT);
        respVO.setRealNameVerified(isRealNameVerified(loginUser.getId()));
        PromoterDO promoter = promoterMapper.selectByUserId(loginUser.getId());
        respVO.setPendingPromoteIncome(promoter == null ? BigDecimal.ZERO
                : Optional.ofNullable(promoter.getAvailableCommissionAmount()).orElse(BigDecimal.ZERO));
        respVO.setTotalPromoteIncome(promoter == null ? BigDecimal.ZERO
                : Optional.ofNullable(promoter.getTotalCommissionAmount()).orElse(BigDecimal.ZERO));
        WalletBankCardDO defaultCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                .eq(WalletBankCardDO::getUserId, loginUser.getId())
                .eq(WalletBankCardDO::getIsDefault, Boolean.TRUE)
                .last("LIMIT 1"));
        if (defaultCard != null) {
            AppWalletAccountRespVO.DefaultBankCardRespVO cardRespVO = new AppWalletAccountRespVO.DefaultBankCardRespVO();
            cardRespVO.setId(defaultCard.getId());
            cardRespVO.setBankName(defaultCard.getBankName());
            cardRespVO.setCardNoMask(defaultCard.getCardNoMask());
            cardRespVO.setAccountName(defaultCard.getAccountName());
            respVO.setDefaultBankCard(cardRespVO);
        }
        ArrayList<AppWalletAccountRespVO.BenefitItemRespVO> benefits = new ArrayList<>();
        for (CreditLevelBenefitDO item : creditLevelBenefitService.getEnabledBenefits()) {
            AppWalletAccountRespVO.BenefitItemRespVO benefit = new AppWalletAccountRespVO.BenefitItemRespVO();
            benefit.setBenefitType("CREDIT_LEVEL");
            benefit.setBenefitTitle(item.getBenefitTitle());
            benefit.setBenefitDesc(item.getBenefitDesc());
            benefits.add(benefit);
            if (benefits.size() >= 4) {
                break;
            }
        }
        respVO.setBenefits(benefits);
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
        ArrayList<AppBankCardRespVO> list = new ArrayList<>();
        for (WalletBankCardDO bankCard : pageResult.getList()) {
            list.add(convertBankCard(bankCard));
        }
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public AppBankCardRespVO getBankCard(Long authUserId, Long id) {
        return convertBankCard(getValidatedBankCard(authUserId, id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWithdraw(Long authUserId, @Valid AppWalletWithdrawCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        WalletAccountDO walletAccount = getOrCreateWalletAccount(authUserId, loginUser);
        walletAccount = walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, walletAccount.getId());
        if (walletAccount == null || !"ENABLE".equals(walletAccount.getStatus())) {
            throw exception(WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH);
        }
        if (!isRealNameVerified(loginUser.getId())) {
            throw exception(ORDER_REAL_NAME_REQUIRED);
        }
        if (reqVO.getApplyAmount() == null || reqVO.getApplyAmount().compareTo(MIN_WITHDRAW_AMOUNT) < 0
                || reqVO.getApplyAmount().compareTo(MoneyUtils.MAX_YUAN_AMOUNT) > 0
                || reqVO.getApplyAmount().stripTrailingZeros().scale() > 2) {
            throw exception(WALLET_WITHDRAW_AMOUNT_INVALID);
        }
        if (walletAccount.getAvailableAmount().compareTo(reqVO.getApplyAmount()) < 0) {
            throw exception(WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH);
        }

        WalletBankCardDO bankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                .eq(WalletBankCardDO::getId, reqVO.getBankCardId())
                .eq(WalletBankCardDO::getUserId, loginUser.getId())
                .eq(WalletBankCardDO::getStatus, "ENABLE"));
        if (bankCard == null || StrUtil.isBlank(bankCard.getTransferAccount())) {
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
        int updated = walletAccountMapper.update(null, new LambdaUpdateWrapper<WalletAccountDO>()
                .eq(WalletAccountDO::getId, walletAccount.getId())
                .eq(WalletAccountDO::getStatus, "ENABLE")
                .ge(WalletAccountDO::getAvailableAmount, reqVO.getApplyAmount())
                .set(WalletAccountDO::getAvailableAmount, afterAvailable)
                .set(WalletAccountDO::getFrozenAmount, afterFrozen));
        if (updated == 0) {
            throw exception(WALLET_AVAILABLE_AMOUNT_NOT_ENOUGH);
        }

        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(loginUser.getId())
                .walletAccountId(walletAccount.getId())
                .bizType("WITHDRAW_APPLY")
                .flowType("OUT")
                .changeAmount(reqVO.getApplyAmount().negate())
                .beforeAmount(beforeAvailable)
                .afterAmount(afterAvailable)
                .relatedWithdrawId(withdraw.getId())
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
        ArrayList<AppWalletWithdrawRespVO> list = new ArrayList<>();
        for (WalletWithdrawDO withdraw : pageResult.getList()) {
            list.add(convertWithdraw(withdraw));
        }
        return new PageResult<>(list, pageResult.getTotal());
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
        return convertWithdraw(withdraw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBankCard(Long authUserId, @Valid AppBankCardCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        lockBankCardOwner(loginUser.getId());
        resetDefaultBankCardIfNeeded(loginUser.getId(), reqVO.getIsDefault());
        WalletBankCardDO bankCard = WalletBankCardDO.builder()
                .userId(loginUser.getId())
                .bankName(reqVO.getBankName())
                .bankCode(reqVO.getBankCode())
                .cardNoEncrypt(DigestUtil.sha256Hex(reqVO.getCardNo()))
                .transferAccount(reqVO.getCardNo())
                .cardNoMask(maskCardNo(reqVO.getCardNo()))
                .accountName(reqVO.getAccountName())
                .bankProvince(reqVO.getBankProvince())
                .bankCity(reqVO.getBankCity())
                .reservedMobile(reqVO.getReservedMobile())
                .status("ENABLE")
                .isDefault(reqVO.getIsDefault())
                .build();
        walletBankCardMapper.insert(bankCard);
        enableMerchantAcceptIfReady(loginUser.getId());
        return bankCard.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBankCard(Long authUserId, @Valid AppBankCardUpdateReqVO reqVO) {
        WalletBankCardDO bankCard = getValidatedBankCard(authUserId, reqVO.getId());
        lockBankCardOwner(bankCard.getUserId());
        resetDefaultBankCardIfNeeded(bankCard.getUserId(), reqVO.getIsDefault());
        walletBankCardMapper.updateById(WalletBankCardDO.builder()
                .id(bankCard.getId())
                .bankName(reqVO.getBankName())
                .bankCode(reqVO.getBankCode())
                .accountName(reqVO.getAccountName())
                .bankProvince(reqVO.getBankProvince())
                .bankCity(reqVO.getBankCity())
                .reservedMobile(reqVO.getReservedMobile())
                .isDefault(reqVO.getIsDefault())
                .build());
        enableMerchantAcceptIfReady(bankCard.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultBankCard(Long authUserId, @Valid AppBankCardDefaultReqVO reqVO) {
        WalletBankCardDO bankCard = getValidatedBankCard(authUserId, reqVO.getId());
        lockBankCardOwner(bankCard.getUserId());
        resetDefaultBankCardIfNeeded(bankCard.getUserId(), Boolean.TRUE);
        walletBankCardMapper.updateById(WalletBankCardDO.builder()
                .id(bankCard.getId())
                .isDefault(Boolean.TRUE)
                .build());
        enableMerchantAcceptIfReady(bankCard.getUserId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBankCard(Long authUserId, Long id) {
        WalletBankCardDO bankCard = getValidatedBankCard(authUserId, id);
        lockBankCardOwner(bankCard.getUserId());
        long pendingWithdrawCount = walletWithdrawMapper.selectCount(new LambdaQueryWrapperX<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getBankCardId, bankCard.getId())
                .in(WalletWithdrawDO::getAuditStatus, "PENDING", "APPROVED")
                .in(WalletWithdrawDO::getStatus, "PENDING", "PROCESSING"));
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
        ArrayList<AppWalletFlowRespVO> list = new ArrayList<>();
        for (WalletFlowDO walletFlow : pageResult.getList()) {
            list.add(convertWalletFlow(walletFlow));
        }
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PageResult<AppWalletBillRespVO> getWalletBillPage(Long authUserId, AppWalletBillPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        LambdaQueryWrapperX<WalletFlowDO> queryWrapper = new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getUserId, loginUser.getId())
                .betweenIfPresent(WalletFlowDO::getCreateTime, reqVO.getCreateTime());
        applyBillTypeFilter(queryWrapper, reqVO.getBillType());
        applyBillStatusFilter(queryWrapper, reqVO.getBizStatus());
        queryWrapper.orderByDesc(WalletFlowDO::getCreateTime, WalletFlowDO::getId);
        PageResult<WalletFlowDO> pageResult = walletFlowMapper.selectPage(reqVO, queryWrapper);
        List<AppWalletBillRespVO> list = pageResult.getList().stream()
                .map(this::convertWalletBill)
                .collect(Collectors.toList());
        return new PageResult<>(list, pageResult.getTotal());
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
        return convertWalletFlow(walletFlow);
    }

    private WalletAccountDO getOrCreateWalletAccount(Long authUserId, MemberUserDO loginUser) {
        String roleCode = resolveRoleCode(authUserId, loginUser);
        WalletAccountDO walletAccount = walletAccountMapper.selectByUserIdAndRoleCode(loginUser.getId(), roleCode);
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
        try {
            walletAccountMapper.insert(walletAccount);
            return walletAccount;
        } catch (DuplicateKeyException ex) {
            WalletAccountDO concurrent = walletAccountMapper.selectByUserIdAndRoleCodeForUpdate(
                    loginUser.getId(), roleCode);
            if (concurrent == null) {
                throw ex;
            }
            return concurrent;
        }
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

    private void lockBankCardOwner(Long userId) {
        MemberUserDO owner = memberUserMapper.selectOneForUpdate(MemberUserDO::getId, userId);
        if (owner == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
    }

    private void enableMerchantAcceptIfReady(Long userId) {
        MerchantEntryDO latestEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, userId)
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        if (latestEntry == null
                || !"APPROVED_WAIT_BANK_CARD".equals(latestEntry.getProgressStatus())
                || !"APPROVED".equals(latestEntry.getFinalAuditStatus())) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        merchantEntryMapper.updateById(MerchantEntryDO.builder()
                .id(latestEntry.getId())
                .progressStatus("APPROVED_ENABLED")
                .currentStageName("已开通接单权限")
                .currentStageTime(now)
                .acceptEnabled(Boolean.TRUE)
                .bankCardRequired(Boolean.TRUE)
                .onboardingBlockedReason(null)
                .build());
        if (latestEntry.getMerchantId() != null) {
            merchantInfoMapper.updateById(MerchantInfoDO.builder()
                    .id(latestEntry.getMerchantId())
                    .status("ENABLE")
                    .acceptStatus("ENABLE")
                    .build());
        }
        memberUserService.updateMemberUserRole(userId, "MERCHANT");
        messagePushDispatchService.dispatchSingle("lb_merchant_accept_enabled", "接单权限开通提醒", "MERCHANT_ENTRY",
                latestEntry.getId(), userId, "用户绑卡后自动开通接单权限");
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

    private boolean isRealNameVerified(Long userId) {
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(userId);
        return realName != null && "APPROVED".equalsIgnoreCase(realName.getAuditStatus());
    }

    private AppBankCardRespVO convertBankCard(WalletBankCardDO bankCard) {
        AppBankCardRespVO respVO = BeanUtils.toBean(bankCard, AppBankCardRespVO.class);
        respVO.setTransferEnabled(StrUtil.isNotBlank(bankCard.getTransferAccount())
                && "ENABLE".equalsIgnoreCase(bankCard.getStatus()));
        respVO.setReservedMobile(maskMobile(bankCard.getReservedMobile()));
        return respVO;
    }

    private String maskMobile(String mobile) {
        if (StrUtil.isBlank(mobile) || mobile.length() < 7) {
            return mobile == null ? null : "******";
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(mobile.length() - 4);
    }

    private AppWalletWithdrawRespVO convertWithdraw(WalletWithdrawDO withdraw) {
        AppWalletWithdrawRespVO respVO = BeanUtils.toBean(withdraw, AppWalletWithdrawRespVO.class);
        respVO.setExpectedArrivalDesc("审核通过后预计 T+1 到账");
        return respVO;
    }

    private AppWalletFlowRespVO convertWalletFlow(WalletFlowDO walletFlow) {
        AppWalletFlowRespVO respVO = BeanUtils.toBean(walletFlow, AppWalletFlowRespVO.class);
        respVO.setBizLabel(resolveBizLabel(walletFlow.getBizType()));
        respVO.setTaxAmount(BigDecimal.ZERO);
        return respVO;
    }

    private AppWalletBillRespVO convertWalletBill(WalletFlowDO walletFlow) {
        AppWalletBillRespVO respVO = new AppWalletBillRespVO();
        respVO.setId(walletFlow.getId());
        respVO.setBillType(resolveBillType(walletFlow.getBizType()));
        respVO.setBillTitle(resolveBizLabel(walletFlow.getBizType()));
        respVO.setBillSummary(StrUtil.blankToDefault(walletFlow.getRemark(), resolveBizLabel(walletFlow.getBizType())));
        respVO.setBizType(walletFlow.getBizType());
        respVO.setBizStatus(resolveBillBizStatus(walletFlow.getBizType()));
        respVO.setAmountDirection(walletFlow.getChangeAmount() != null && walletFlow.getChangeAmount().compareTo(BigDecimal.ZERO) < 0 ? "OUT" : "IN");
        respVO.setAmount(walletFlow.getChangeAmount() == null ? BigDecimal.ZERO : walletFlow.getChangeAmount().abs());
        respVO.setBeforeAmount(walletFlow.getBeforeAmount());
        respVO.setAfterAmount(walletFlow.getAfterAmount());
        respVO.setRelatedOrderId(walletFlow.getRelatedOrderId());
        respVO.setRelatedUnitId(walletFlow.getRelatedUnitId());
        respVO.setRelatedRefundId(walletFlow.getRelatedRefundId());
        respVO.setRelatedWithdrawId(resolveRelatedWithdrawId(walletFlow));
        respVO.setBizNo(walletFlow.getFlowNo());
        respVO.setRemark(walletFlow.getRemark());
        respVO.setCreateTime(walletFlow.getCreateTime());
        return respVO;
    }

    private String resolveBizLabel(String bizType) {
        if ("ORDER_PAY".equalsIgnoreCase(bizType)) {
            return "托管锁定";
        }
        if ("SETTLEMENT_UNLOCK".equalsIgnoreCase(bizType)) {
            return "结算解锁";
        }
        if ("WITHDRAW_APPLY".equalsIgnoreCase(bizType)) {
            return "提现冻结";
        }
        if ("WITHDRAW_SUCCESS".equalsIgnoreCase(bizType)) {
            return "提现到账";
        }
        if ("WITHDRAW_FAILED".equalsIgnoreCase(bizType)) {
            return "提现退回";
        }
        if ("WITHDRAW_RETRY_FREEZE".equalsIgnoreCase(bizType)) {
            return "提现重试冻结";
        }
        if ("REFUND_SUCCESS".equalsIgnoreCase(bizType)) {
            return "退款冲减";
        }
        return bizType;
    }

    private String resolveBillType(String bizType) {
        if ("ORDER_PAY".equalsIgnoreCase(bizType)) {
            return "ORDER";
        }
        if ("SETTLEMENT_UNLOCK".equalsIgnoreCase(bizType)) {
            return "SETTLEMENT";
        }
        if ("WITHDRAW_APPLY".equalsIgnoreCase(bizType)
                || "WITHDRAW_SUCCESS".equalsIgnoreCase(bizType)
                || "WITHDRAW_FAILED".equalsIgnoreCase(bizType)
                || "WITHDRAW_RETRY_FREEZE".equalsIgnoreCase(bizType)) {
            return "WITHDRAW";
        }
        if ("REFUND_SUCCESS".equalsIgnoreCase(bizType)) {
            return "REFUND";
        }
        return "ADJUST";
    }

    private String resolveBillBizStatus(String bizType) {
        if ("WITHDRAW_APPLY".equalsIgnoreCase(bizType)) {
            return "PENDING";
        }
        if ("WITHDRAW_FAILED".equalsIgnoreCase(bizType)) {
            return "FAILED";
        }
        return "SUCCESS";
    }

    private Long resolveRelatedWithdrawId(WalletFlowDO walletFlow) {
        if (!"WITHDRAW_APPLY".equalsIgnoreCase(walletFlow.getBizType())
                && !"WITHDRAW_SUCCESS".equalsIgnoreCase(walletFlow.getBizType())
                && !"WITHDRAW_FAILED".equalsIgnoreCase(walletFlow.getBizType())
                && !"WITHDRAW_RETRY_FREEZE".equalsIgnoreCase(walletFlow.getBizType())) {
            return null;
        }
        return walletFlow.getRelatedWithdrawId();
    }

    private void applyBillTypeFilter(LambdaQueryWrapperX<WalletFlowDO> queryWrapper, String billType) {
        if (StrUtil.isBlank(billType)) {
            return;
        }
        if ("ORDER".equalsIgnoreCase(billType)) {
            queryWrapper.eq(WalletFlowDO::getBizType, "ORDER_PAY");
        } else if ("SETTLEMENT".equalsIgnoreCase(billType)) {
            queryWrapper.eq(WalletFlowDO::getBizType, "SETTLEMENT_UNLOCK");
        } else if ("WITHDRAW".equalsIgnoreCase(billType)) {
            queryWrapper.in(WalletFlowDO::getBizType, "WITHDRAW_APPLY", "WITHDRAW_SUCCESS", "WITHDRAW_FAILED",
                    "WITHDRAW_RETRY_FREEZE");
        } else if ("REFUND".equalsIgnoreCase(billType)) {
            queryWrapper.eq(WalletFlowDO::getBizType, "REFUND_SUCCESS");
        } else {
            queryWrapper.notIn(WalletFlowDO::getBizType, "ORDER_PAY", "SETTLEMENT_UNLOCK", "WITHDRAW_APPLY",
                    "WITHDRAW_SUCCESS", "WITHDRAW_FAILED", "WITHDRAW_RETRY_FREEZE", "REFUND_SUCCESS");
        }
    }

    private void applyBillStatusFilter(LambdaQueryWrapperX<WalletFlowDO> queryWrapper, String bizStatus) {
        if (StrUtil.isBlank(bizStatus)) {
            return;
        }
        if ("PENDING".equalsIgnoreCase(bizStatus)) {
            queryWrapper.eq(WalletFlowDO::getBizType, "WITHDRAW_APPLY");
        } else if ("FAILED".equalsIgnoreCase(bizStatus)) {
            queryWrapper.eq(WalletFlowDO::getBizType, "WITHDRAW_FAILED");
        } else if ("SUCCESS".equalsIgnoreCase(bizStatus)) {
            queryWrapper.notIn(WalletFlowDO::getBizType, "WITHDRAW_APPLY", "WITHDRAW_FAILED");
        }
    }
}
