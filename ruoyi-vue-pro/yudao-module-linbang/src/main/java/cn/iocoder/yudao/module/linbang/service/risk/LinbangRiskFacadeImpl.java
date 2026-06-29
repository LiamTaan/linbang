package cn.iocoder.yudao.module.linbang.service.risk;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userfrozenfundrecord.UserFrozenFundRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userriskrelation.UserRiskRelationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord.UserRestrictRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.blacklist.BlacklistMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.riskevent.RiskEventMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userfrozenfundrecord.UserFrozenFundRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userriskrelation.UserRiskRelationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userrestrictrecord.UserRestrictRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.service.memberqualification.MemberQualificationExpiryService;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget;
import cn.iocoder.yudao.module.linbang.service.punishlog.PunishLogWriteService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_ACCEPT_RESTRICTED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_ACCOUNT_BLOCKED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PUBLISH_RESTRICTED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_REAL_NAME_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SELF_DEAL_BLOCKED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_FROZEN_FUND_RECORD_NOT_EXISTS;

@Service
public class LinbangRiskFacadeImpl implements LinbangRiskFacade {

    @Resource
    private ConfigService configService;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private BlacklistMapper blacklistMapper;
    @Resource
    private UserRestrictRecordMapper userRestrictRecordMapper;
    @Resource
    private UserRiskRelationMapper userRiskRelationMapper;
    @Resource
    private RiskEventMapper riskEventMapper;
    @Resource
    private CreditRecordService creditRecordService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private UserFrozenFundRecordMapper userFrozenFundRecordMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private MemberQualificationExpiryService memberQualificationExpiryService;
    @Resource
    private PunishLogWriteService punishLogWriteService;

    @Override
    public void validateBeforeCreateOrder(Long authUserId, MemberUserDO loginUser) {
        validateRealName(loginUser.getId());
        validateBlacklist(loginUser.getId());
        if (getActiveRestrict(loginUser.getId(), LinbangRiskConstants.RESTRICT_TYPE_PUBLISH) != null) {
            throw exception(ORDER_PUBLISH_RESTRICTED);
        }
    }

    @Override
    public void fillOrderDeposit(OrderInfoDO order) {
        int thresholdFen = getIntConfig(PlatformConfigKeyConstants.LARGE_ORDER_DEPOSIT_THRESHOLD_FEN, 0);
        int depositFen = getIntConfig(PlatformConfigKeyConstants.LARGE_ORDER_DEPOSIT_AMOUNT_FEN, 0);
        BigDecimal amountFen = order.getOrderAmount() == null ? BigDecimal.ZERO : order.getOrderAmount().multiply(new BigDecimal("100"));
        if (thresholdFen > 0 && amountFen.compareTo(new BigDecimal(thresholdFen)) >= 0) {
            order.setDepositRequired(Boolean.TRUE);
            order.setDepositAmount(new BigDecimal(depositFen).divide(new BigDecimal("100")));
            order.setDepositPayStatus(LinbangRiskConstants.DEPOSIT_PAY_STATUS_UNPAID);
            return;
        }
        order.setDepositRequired(Boolean.FALSE);
        order.setDepositAmount(BigDecimal.ZERO);
        order.setDepositPayStatus(LinbangRiskConstants.DEPOSIT_PAY_STATUS_NOT_REQUIRED);
    }

    @Override
    public void validateBeforeAcceptOrder(Long authUserId, MemberUserDO loginUser, MerchantInfoDO merchant, OrderInfoDO order) {
        validateRealName(loginUser.getId());
        validateBlacklist(loginUser.getId());
        if (getActiveRestrict(loginUser.getId(), LinbangRiskConstants.RESTRICT_TYPE_ACCEPT) != null) {
            throw exception(ORDER_ACCEPT_RESTRICTED);
        }
        if (!Objects.equals(merchant.getStatus(), LinbangRiskConstants.STATUS_ENABLE)
                || !Objects.equals(merchant.getAcceptStatus(), LinbangRiskConstants.STATUS_ENABLE)) {
            throw exception(ORDER_ACCEPT_RESTRICTED);
        }
        validateMerchantEntry(merchant);
        validateBankCardBound(merchant.getUserId());
        if (!memberQualificationExpiryService.canMerchantAccept(merchant.getUserId())) {
            throw exception(ORDER_ACCEPT_RESTRICTED);
        }
        if (Objects.equals(order.getUserId(), loginUser.getId())) {
            applySelfDealPenalty(loginUser, merchant, order);
            throw exception(ORDER_SELF_DEAL_BLOCKED);
        }
        UserRiskRelationDO relation = userRiskRelationMapper.selectActive(order.getUserId(), loginUser.getId());
        if (relation != null) {
            createRiskEvent("ORDER", order.getId(), LinbangRiskConstants.RISK_TYPE_RELATED_ACCOUNT,
                    LinbangRiskConstants.RISK_LEVEL_HIGH, relation.getRelationType(),
                    "下单人与接单人命中关联账号规则", order.getUserId() + "," + loginUser.getId());
            throw exception(ORDER_ACCOUNT_BLOCKED);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderCancelled(MemberUserDO loginUser, OrderInfoDO order, String cancelReason) {
        long cancelCount = orderInfoMapper.selectCount(new LambdaQueryWrapperX<OrderInfoDO>()
                .eq(OrderInfoDO::getUserId, loginUser.getId())
                .eq(OrderInfoDO::getStatus, "CLOSED"));
        int limitCount = getIntConfig(PlatformConfigKeyConstants.ORDER_CANCEL_LIMIT_COUNT, 3);
        if (cancelCount < limitCount) {
            return;
        }
        int restrictHours = getIntConfig(PlatformConfigKeyConstants.ORDER_CANCEL_RESTRICT_HOURS, 24);
        UserRestrictRecordDO record = UserRestrictRecordDO.builder()
                .userId(loginUser.getId())
                .restrictType(LinbangRiskConstants.RESTRICT_TYPE_PUBLISH)
                .status(LinbangRiskConstants.RESTRICT_STATUS_ACTIVE)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(restrictHours))
                .sourceRuleCode("ORDER_CANCEL_LIMIT")
                .sourceBizType("ORDER")
                .sourceBizId(order.getId())
                .reason(StrUtil.blankToDefault(cancelReason, "取消订单次数达到限制"))
                .build();
        userRestrictRecordMapper.insert(record);
        punishLogWriteService.createPunishLog(loginUser.getId(),
                "RESTRICT_" + LinbangRiskConstants.RESTRICT_TYPE_PUBLISH, record.getStatus(), record.getReason(),
                record.getSourceBizType(), record.getSourceBizId(), "USER_RESTRICT_RECORD", record.getId(),
                null, record.getCreateTime(), record.getStartTime(), record.getEndTime(), null);
        createRiskEvent("ORDER", order.getId(), LinbangRiskConstants.RISK_TYPE_CANCEL_LIMIT,
                LinbangRiskConstants.RISK_LEVEL_MEDIUM, "ORDER_CANCEL_LIMIT",
                "用户取消订单次数达到限制", String.valueOf(loginUser.getId()));
        messagePushDispatchService.dispatchBatch(null, "取消订单限制通知", "SINGLE_USER",
                "ORDER_CANCEL_LIMIT", order.getId(), "系统自动处罚通知",
                Arrays.asList(new MessagePushDispatchTarget(loginUser.getId(), order.getId(), "cancel-limit-" + order.getId())));
    }

    @Override
    public UserRestrictRecordDO getActiveRestrict(Long userId, String restrictType) {
        return userRestrictRecordMapper.selectActive(userId, restrictType, LocalDateTime.now());
    }

    @Override
    public Long createRiskEvent(String bizType, Long bizId, String riskType, String riskLevel,
                                String hitRuleCode, String remark, String relatedUserIds) {
        RiskEventDO event = RiskEventDO.builder()
                .bizType(bizType)
                .bizId(bizId)
                .riskType(riskType)
                .riskLevel(riskLevel)
                .hitRuleCode(hitRuleCode)
                .status(LinbangRiskConstants.RISK_STATUS_PENDING)
                .disposeStatus(LinbangRiskConstants.RISK_DISPOSE_STATUS_PENDING)
                .remark(remark)
                .relatedUserIds(relatedUserIds)
                .build();
        riskEventMapper.insert(event);
        return event.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applySelfDealPenalty(MemberUserDO user, MerchantInfoDO merchant, OrderInfoDO order) {
        createRiskEvent("ORDER", order.getId(), LinbangRiskConstants.RISK_TYPE_SELF_DEAL,
                LinbangRiskConstants.RISK_LEVEL_HIGH, "SELF_DEAL",
                "命中自接自单拦截", user.getId() + "," + order.getUserId());
        Integer score = getIntConfig(PlatformConfigKeyConstants.SELF_DEAL_PENISH_SCORE, 10);
        creditRecordService.applyCreditRule(user.getId(), merchant.getId(), "SELF_DEAL_PENALTY",
                "ORDER", order.getId(), "命中自接自单，扣减 " + score + " 分");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long freezeUserFunds(Long userId, String sourceBizType, Long sourceBizId, BigDecimal amount, String reason) {
        WalletAccountDO wallet = walletAccountMapper.selectOne(new LambdaQueryWrapperX<WalletAccountDO>()
                .eq(WalletAccountDO::getUserId, userId)
                .orderByDesc(WalletAccountDO::getId)
                .last("LIMIT 1"));
        if (wallet == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        BigDecimal beforeAvailable = defaultBigDecimal(wallet.getAvailableAmount());
        BigDecimal beforeFrozen = defaultBigDecimal(wallet.getFrozenAmount());
        BigDecimal freezeAmount = amount.min(beforeAvailable);
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(wallet.getId())
                .availableAmount(beforeAvailable.subtract(freezeAmount))
                .frozenAmount(beforeFrozen.add(freezeAmount))
                .build());
        UserFrozenFundRecordDO record = UserFrozenFundRecordDO.builder()
                .userId(userId)
                .walletAccountId(wallet.getId())
                .frozenAmount(freezeAmount)
                .releasedAmount(BigDecimal.ZERO)
                .status(LinbangRiskConstants.FROZEN_STATUS_ACTIVE)
                .sourceBizType(sourceBizType)
                .sourceBizId(sourceBizId)
                .reason(reason)
                .build();
        userFrozenFundRecordMapper.insert(record);
        punishLogWriteService.createPunishLog(userId, LinbangRiskConstants.RISK_DISPOSE_ACTION_FREEZE,
                record.getStatus(), reason, sourceBizType, sourceBizId, "USER_FROZEN_FUND_RECORD", record.getId(),
                null, record.getCreateTime(), record.getCreateTime(), null, null);
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(userId)
                .walletAccountId(wallet.getId())
                .bizType(sourceBizType)
                .flowType("FREEZE")
                .changeAmount(freezeAmount.negate())
                .beforeAmount(beforeAvailable)
                .afterAmount(beforeAvailable.subtract(freezeAmount))
                .remark(reason)
                .build());
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseFrozenFunds(Long recordId, Long releasedBy, String releaseRemark) {
        UserFrozenFundRecordDO record = userFrozenFundRecordMapper.selectById(recordId);
        if (record == null) {
            throw exception(USER_FROZEN_FUND_RECORD_NOT_EXISTS);
        }
        if (!Objects.equals(record.getStatus(), LinbangRiskConstants.FROZEN_STATUS_ACTIVE)) {
            return;
        }
        WalletAccountDO wallet = walletAccountMapper.selectById(record.getWalletAccountId());
        if (wallet != null) {
            BigDecimal released = defaultBigDecimal(record.getFrozenAmount())
                    .subtract(defaultBigDecimal(record.getReleasedAmount()));
            walletAccountMapper.updateById(WalletAccountDO.builder()
                    .id(wallet.getId())
                    .availableAmount(defaultBigDecimal(wallet.getAvailableAmount()).add(released))
                    .frozenAmount(defaultBigDecimal(wallet.getFrozenAmount()).subtract(released))
                    .build());
        }
        userFrozenFundRecordMapper.updateById(UserFrozenFundRecordDO.builder()
                .id(recordId)
                .status(LinbangRiskConstants.FROZEN_STATUS_RELEASED)
                .releasedAmount(record.getFrozenAmount())
                .releasedBy(releasedBy)
                .releasedTime(LocalDateTime.now())
                .releaseRemark(releaseRemark)
                .build());
        punishLogWriteService.releasePunishLog("USER_FROZEN_FUND_RECORD", recordId,
                LinbangRiskConstants.FROZEN_STATUS_RELEASED, releasedBy, LocalDateTime.now(), releaseRemark);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserToBlacklist(Long userId, String blackType, String reason, LocalDateTime endTime) {
        BlacklistDO active = blacklistMapper.selectOne(new LambdaQueryWrapperX<BlacklistDO>()
                .eq(BlacklistDO::getUserId, userId)
                .eq(BlacklistDO::getBlackType, blackType)
                .eq(BlacklistDO::getStatus, LinbangRiskConstants.STATUS_ENABLE)
                .last("LIMIT 1"));
        if (active != null) {
            return;
        }
        blacklistMapper.insert(BlacklistDO.builder()
                .userId(userId)
                .blackType(blackType)
                .reason(reason)
                .startTime(LocalDateTime.now())
                .endTime(endTime)
                .status(LinbangRiskConstants.STATUS_ENABLE)
                .build());
        BlacklistDO latest = blacklistMapper.selectOne(new LambdaQueryWrapperX<BlacklistDO>()
                .eq(BlacklistDO::getUserId, userId)
                .eq(BlacklistDO::getBlackType, blackType)
                .orderByDesc(BlacklistDO::getId)
                .last("LIMIT 1"));
        if (latest != null) {
            punishLogWriteService.createPunishLog(userId, "BLACKLIST_" + blackType, latest.getStatus(), reason,
                    "USER", userId, "BLACKLIST_RECORD", latest.getId(),
                    null, latest.getCreateTime(), latest.getStartTime(), latest.getEndTime(), null);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRestrictRecord(Long userId, String restrictType, String sourceRuleCode, String sourceBizType,
                                     Long sourceBizId, String reason, LocalDateTime endTime) {
        UserRestrictRecordDO active = getActiveRestrict(userId, restrictType);
        if (active != null) {
            return active.getId();
        }
        UserRestrictRecordDO record = UserRestrictRecordDO.builder()
                .userId(userId)
                .restrictType(restrictType)
                .status(LinbangRiskConstants.RESTRICT_STATUS_ACTIVE)
                .startTime(LocalDateTime.now())
                .endTime(endTime)
                .sourceRuleCode(sourceRuleCode)
                .sourceBizType(sourceBizType)
                .sourceBizId(sourceBizId)
                .reason(reason)
                .build();
        userRestrictRecordMapper.insert(record);
        punishLogWriteService.createPunishLog(userId, "RESTRICT_" + restrictType, record.getStatus(), reason,
                sourceBizType, sourceBizId, "USER_RESTRICT_RECORD", record.getId(),
                null, record.getCreateTime(), record.getStartTime(), record.getEndTime(), null);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseRestrictRecordByBiz(String sourceBizType, Long sourceBizId, String restrictType,
                                           Long releasedBy, String releaseRemark) {
        List<UserRestrictRecordDO> records = userRestrictRecordMapper.selectList(new LambdaQueryWrapperX<UserRestrictRecordDO>()
                .eqIfPresent(UserRestrictRecordDO::getSourceBizType, sourceBizType)
                .eqIfPresent(UserRestrictRecordDO::getSourceBizId, sourceBizId)
                .eqIfPresent(UserRestrictRecordDO::getRestrictType, restrictType)
                .eq(UserRestrictRecordDO::getStatus, LinbangRiskConstants.RESTRICT_STATUS_ACTIVE));
        for (UserRestrictRecordDO record : records) {
            userRestrictRecordMapper.updateById(UserRestrictRecordDO.builder()
                    .id(record.getId())
                    .status(LinbangRiskConstants.RESTRICT_STATUS_RELEASED)
                    .releasedBy(releasedBy)
                    .releasedTime(LocalDateTime.now())
                    .releaseRemark(releaseRemark)
                    .build());
            punishLogWriteService.releasePunishLog("USER_RESTRICT_RECORD", record.getId(),
                    LinbangRiskConstants.RESTRICT_STATUS_RELEASED, releasedBy, LocalDateTime.now(), releaseRemark);
        }
    }

    @Override
    public void syncPunishLogs() {
        punishLogWriteService.syncHistoricalLogs();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markDepositPaid(PayOrderNotifyReqDTO notifyReqDTO) {
        if (notifyReqDTO == null || StrUtil.isBlank(notifyReqDTO.getMerchantOrderId())
                || !notifyReqDTO.getMerchantOrderId().startsWith("DEPOSIT:")) {
            return false;
        }
        Long orderId = Long.valueOf(StrUtil.subAfter(notifyReqDTO.getMerchantOrderId(), "DEPOSIT:", false));
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .depositPayOrderId(notifyReqDTO.getPayOrderId())
                .depositPayStatus(LinbangRiskConstants.DEPOSIT_PAY_STATUS_PAID)
                .depositPaidTime(LocalDateTime.now())
                .build());
        return true;
    }

    private void validateRealName(Long userId) {
        boolean require = Boolean.parseBoolean(getStringConfig(PlatformConfigKeyConstants.ORDER_RECHECK_REALNAME_ENABLE, "true"));
        if (!require) {
            return;
        }
        MemberUserRealNameDO realName = memberUserRealNameMapper.selectByUserId(userId);
        if (realName == null || !Objects.equals(realName.getAuditStatus(), "APPROVED")) {
            throw exception(ORDER_REAL_NAME_REQUIRED);
        }
    }

    private void validateBlacklist(Long userId) {
        BlacklistDO blacklist = blacklistMapper.selectOne(new LambdaQueryWrapperX<BlacklistDO>()
                .eq(BlacklistDO::getUserId, userId)
                .eq(BlacklistDO::getStatus, LinbangRiskConstants.STATUS_ENABLE)
                .le(BlacklistDO::getStartTime, LocalDateTime.now())
                .and(wrapper -> wrapper.isNull(BlacklistDO::getEndTime).or().ge(BlacklistDO::getEndTime, LocalDateTime.now()))
                .last("LIMIT 1"));
        if (blacklist != null) {
            throw exception(ORDER_ACCOUNT_BLOCKED);
        }
    }

    private void validateMerchantEntry(MerchantInfoDO merchant) {
        MerchantEntryDO entry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getMerchantId, merchant.getId())
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        if (entry == null || !Boolean.TRUE.equals(entry.getAcceptEnabled())
                || !Objects.equals(entry.getProgressStatus(), "APPROVED_ENABLED")) {
            throw exception(ORDER_ACCEPT_RESTRICTED);
        }
    }

    private void validateBankCardBound(Long userId) {
        boolean hasBankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO>()
                .eq(cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO::getUserId, userId)
                .eq(cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO::getStatus, LinbangRiskConstants.STATUS_ENABLE)
                .last("LIMIT 1")) != null;
        if (!hasBankCard) {
            throw exception(ORDER_ACCEPT_RESTRICTED);
        }
    }

    private int getIntConfig(String key, int defaultValue) {
        try {
            return Integer.parseInt(getStringConfig(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private String getStringConfig(String key, String defaultValue) {
        return configService.getConfigByKey(key) == null || StrUtil.isBlank(configService.getConfigByKey(key).getValue())
                ? defaultValue : configService.getConfigByKey(key).getValue();
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
