package cn.iocoder.yudao.module.linbang.service.finance;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderdividerecord.OrderDivideRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.dividerule.DivideRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderdividerecord.OrderDivideRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import cn.iocoder.yudao.module.linbang.service.escrowproof.EscrowProofService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferRespDTO;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.module.linbang.constants.FinanceDisplayConstants.WITHDRAW_TRANSFER_FAILURE_REASON;

@Service
@Validated
public class LinbangFinanceServiceImpl implements LinbangFinanceService {

    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;
    @Resource
    private DivideRuleMapper divideRuleMapper;
    @Resource
    private OrderDivideRecordMapper orderDivideRecordMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private EscrowProofService escrowProofService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderPaid(OrderInfoDO order, Long payOrderId) {
        if (order == null || order.getUserId() == null || order.getId() == null) {
            return;
        }
        WalletAccountDO walletAccount = getOrCreateWalletAccount(order.getUserId(), "USER");
        walletAccount = walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, walletAccount.getId());
        if (walletAccount == null || existsOrderPaidFlow(order.getId(), payOrderId)) {
            return;
        }
        BigDecimal orderAmount = defaultAmount(order.getOrderAmount());
        if (orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal beforeEscrow = defaultAmount(walletAccount.getEscrowAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .totalAmount(defaultAmount(walletAccount.getTotalAmount()).add(orderAmount))
                .escrowAmount(beforeEscrow.add(orderAmount))
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(order.getUserId())
                .walletAccountId(walletAccount.getId())
                .bizType("ORDER_PAY")
                .flowType("IN")
                .changeAmount(orderAmount)
                .beforeAmount(beforeEscrow)
                .afterAmount(beforeEscrow.add(orderAmount))
                .relatedOrderId(order.getId())
                .relatedPayOrderId(payOrderId)
                .remark("订单支付完成，资金进入托管")
                .createTime(LocalDateTime.now())
                .build());
        createEscrowProofs(order, orderAmount);
        createDivideRecords(order, null, orderAmount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleUnitFinished(OrderInfoDO order, OrderUnitDO unit) {
        if (order == null || unit == null || unit.getMerchantId() == null) {
            return;
        }
        Long merchantUserId = resolveMerchantUserId(unit.getMerchantId());
        if (merchantUserId == null) {
            return;
        }
        WalletAccountDO merchantWallet = getOrCreateWalletAccount(merchantUserId, "MERCHANT");
        merchantWallet = walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, merchantWallet.getId());
        if (walletFlowMapper.selectOne(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getBizType, "SETTLEMENT_UNLOCK")
                .eq(WalletFlowDO::getRelatedUnitId, unit.getId())) != null) {
            return;
        }
        BigDecimal unitAmount = defaultAmount(unit.getUnitAmount());
        WalletAccountDO userWallet = getOrCreateWalletAccount(order.getUserId(), "USER");
        userWallet = walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, userWallet.getId());
        BigDecimal beforeEscrow = userWallet == null ? BigDecimal.ZERO : defaultAmount(userWallet.getEscrowAmount());
        BigDecimal beforeTotal = userWallet == null ? BigDecimal.ZERO : defaultAmount(userWallet.getTotalAmount());
        if (userWallet == null || unitAmount.compareTo(BigDecimal.ZERO) <= 0
                || beforeEscrow.compareTo(unitAmount) < 0
                || beforeTotal.compareTo(unitAmount) < 0) {
            throw new IllegalStateException("托管余额不足，拒绝解锁结算");
        }
        BigDecimal beforeAvailable = defaultAmount(merchantWallet.getAvailableAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(merchantWallet.getId())
                .totalAmount(defaultAmount(merchantWallet.getTotalAmount()).add(unitAmount))
                .availableAmount(beforeAvailable.add(unitAmount))
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(merchantUserId)
                .walletAccountId(merchantWallet.getId())
                .bizType("SETTLEMENT_UNLOCK")
                .flowType("IN")
                .changeAmount(unitAmount)
                .beforeAmount(beforeAvailable)
                .afterAmount(beforeAvailable.add(unitAmount))
                .relatedOrderId(order.getId())
                .relatedUnitId(unit.getId())
                .remark("单元完成，资金解锁为可提现")
                .createTime(LocalDateTime.now())
                .build());

        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(userWallet.getId())
                .escrowAmount(beforeEscrow.subtract(unitAmount))
                .totalAmount(beforeTotal.subtract(unitAmount))
                .build());
        escrowProofService.unlockProof(order.getId(), unit.getId(), "单元完成并确认，释放托管金额");
        messagePushDispatchService.dispatchSingle("lb_escrow_unlocked", "托管解锁通知", "ORDER",
                order.getId(), order.getUserId(), "单元完成后提醒用户托管资金已解锁");
    }

    private Long resolveMerchantUserId(Long merchantId) {
        if (merchantId == null) {
            return null;
        }
        MerchantInfoDO merchant = merchantInfoMapper.selectById(merchantId);
        return merchant != null ? merchant.getUserId() : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundSuccess(OrderInfoDO order, OrderUnitDO unit, BigDecimal refundAmount, Long payRefundId) {
        if (order == null) {
            return;
        }
        WalletAccountDO userWallet = getOrCreateWalletAccount(order.getUserId(), "USER");
        userWallet = walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, userWallet.getId());
        if (userWallet == null || existsRefundFlow(order.getId(), unit != null ? unit.getId() : null)) {
            return;
        }
        BigDecimal realRefundAmount = defaultAmount(refundAmount);
        BigDecimal beforeEscrow = defaultAmount(userWallet.getEscrowAmount());
        BigDecimal beforeTotal = defaultAmount(userWallet.getTotalAmount());
        if (realRefundAmount.compareTo(BigDecimal.ZERO) <= 0
                || beforeEscrow.compareTo(realRefundAmount) < 0
                || beforeTotal.compareTo(realRefundAmount) < 0) {
            throw new IllegalStateException("托管余额不足，拒绝入账退款");
        }
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(userWallet.getId())
                .escrowAmount(beforeEscrow.subtract(realRefundAmount))
                .totalAmount(beforeTotal.subtract(realRefundAmount))
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(order.getUserId())
                .walletAccountId(userWallet.getId())
                .bizType("REFUND_SUCCESS")
                .flowType("OUT")
                .changeAmount(realRefundAmount.negate())
                .beforeAmount(beforeEscrow)
                .afterAmount(nonNegative(beforeEscrow.subtract(realRefundAmount)))
                .relatedOrderId(order.getId())
                .relatedUnitId(unit != null ? unit.getId() : null)
                .relatedRefundId(payRefundId)
                .remark("退款成功，冲减托管金额")
                .createTime(LocalDateTime.now())
                .build());
        escrowProofService.refundProof(order.getId(), unit != null ? unit.getId() : null, "退款成功，释放托管");
        updateDivideRecordsRefunded(order.getId(), unit != null ? unit.getId() : null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWithdrawTransferSuccess(WalletAccountDO walletAccount, Long withdrawId, PayTransferRespDTO transfer) {
        if (walletAccount == null || transfer == null) {
            return;
        }
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectOneForUpdate(WalletWithdrawDO::getId, withdrawId);
        if (withdraw == null) {
            return;
        }
        int withdrawUpdated = walletWithdrawMapper.update(null, new LambdaUpdateWrapper<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getId, withdraw.getId())
                .eq(WalletWithdrawDO::getStatus, "PROCESSING")
                .set(WalletWithdrawDO::getStatus, "SUCCESS")
                .set(WalletWithdrawDO::getAuditStatus, "APPROVED")
                .set(WalletWithdrawDO::getPayTime, transfer.getSuccessTime())
                .set(WalletWithdrawDO::getPayTransferId, transfer.getId())
                .set(WalletWithdrawDO::getPayTransferNo, transfer.getNo())
                .set(WalletWithdrawDO::getTransferErrorMsg, null));
        if (withdrawUpdated == 0) {
            return;
        }
        walletAccount = walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, walletAccount.getId());
        BigDecimal realAmount = defaultAmount(withdraw.getRealAmount());
        BigDecimal beforeFrozen = defaultAmount(walletAccount.getFrozenAmount());
        BigDecimal beforeTotal = defaultAmount(walletAccount.getTotalAmount());
        if (realAmount.compareTo(BigDecimal.ZERO) <= 0 || beforeFrozen.compareTo(realAmount) < 0
                || beforeTotal.compareTo(realAmount) < 0) {
            throw new IllegalStateException("钱包冻结余额不足，拒绝提现成功入账");
        }
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .frozenAmount(beforeFrozen.subtract(realAmount))
                .totalAmount(beforeTotal.subtract(realAmount))
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(withdraw.getUserId())
                .walletAccountId(walletAccount.getId())
                .bizType("WITHDRAW_SUCCESS")
                .relatedWithdrawId(withdraw.getId())
                .flowType("OUT")
                .changeAmount(realAmount.negate())
                .beforeAmount(beforeFrozen)
                .afterAmount(nonNegative(beforeFrozen.subtract(realAmount)))
                .remark("提现打款成功")
                .createTime(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWithdrawTransferFailed(WalletAccountDO walletAccount, Long withdrawId, PayTransferRespDTO transfer) {
        if (walletAccount == null || transfer == null) {
            return;
        }
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectOneForUpdate(WalletWithdrawDO::getId, withdrawId);
        if (withdraw == null) {
            return;
        }
        int withdrawUpdated = walletWithdrawMapper.update(null, new LambdaUpdateWrapper<WalletWithdrawDO>()
                .eq(WalletWithdrawDO::getId, withdraw.getId())
                .eq(WalletWithdrawDO::getStatus, "PROCESSING")
                .set(WalletWithdrawDO::getStatus, "FAILED")
                .set(WalletWithdrawDO::getPayTransferId, transfer.getId())
                .set(WalletWithdrawDO::getPayTransferNo, transfer.getNo())
                .set(WalletWithdrawDO::getTransferErrorMsg, WITHDRAW_TRANSFER_FAILURE_REASON));
        if (withdrawUpdated == 0) {
            return;
        }
        walletAccount = walletAccountMapper.selectOneForUpdate(WalletAccountDO::getId, walletAccount.getId());
        BigDecimal realAmount = defaultAmount(withdraw.getRealAmount());
        BigDecimal beforeFrozen = defaultAmount(walletAccount.getFrozenAmount());
        BigDecimal beforeAvailable = defaultAmount(walletAccount.getAvailableAmount());
        if (realAmount.compareTo(BigDecimal.ZERO) <= 0 || beforeFrozen.compareTo(realAmount) < 0) {
            throw new IllegalStateException("钱包冻结余额不足，拒绝退回提现金额");
        }
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .frozenAmount(beforeFrozen.subtract(realAmount))
                .availableAmount(beforeAvailable.add(realAmount))
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(withdraw.getUserId())
                .walletAccountId(walletAccount.getId())
                .bizType("WITHDRAW_FAILED")
                .relatedWithdrawId(withdraw.getId())
                .flowType("IN")
                .changeAmount(realAmount)
                .beforeAmount(beforeAvailable)
                .afterAmount(beforeAvailable.add(realAmount))
                .remark("提现打款失败，退回可提现金额")
                .createTime(LocalDateTime.now())
                .build());
    }

    private void createDivideRecords(OrderInfoDO order, OrderUnitDO unit, BigDecimal baseAmount) {
        DivideRuleDO rule = divideRuleMapper.selectOne(new LambdaQueryWrapperX<DivideRuleDO>()
                .eq(DivideRuleDO::getCategoryId, order.getCategoryId())
                .eq(DivideRuleDO::getStatus, "ENABLE")
                .le(DivideRuleDO::getEffectiveTime, LocalDateTime.now())
                .orderByDesc(DivideRuleDO::getEffectiveTime, DivideRuleDO::getId)
                .last("LIMIT 1"));
        if (rule == null) {
            return;
        }
        boolean licensed = hasApprovedLicense(order.getMerchantId());
        BigDecimal merchantRate = licensed ? rule.getMerchantRate()
                : nonNegative(defaultAmount(rule.getMerchantRate()).subtract(defaultAmount(rule.getTaxWithholdRate())));
        List<OrderDivideRecordDO> records = new ArrayList<>();
        records.add(buildRecord(order, unit, rule, "MERCHANT", order.getMerchantId(), merchantRate,
                calc(baseAmount, merchantRate), BigDecimal.ZERO));
        records.add(buildRecord(order, unit, rule, "PLATFORM", 0L, rule.getPlatformRate(), calc(baseAmount, rule.getPlatformRate()), BigDecimal.ZERO));
        records.add(buildRecord(order, unit, rule, "PARTNER", null, rule.getPartnerRate(), calc(baseAmount, rule.getPartnerRate()), BigDecimal.ZERO));
        records.add(buildRecord(order, unit, rule, "PROMOTER", null, rule.getPromoterRate(), calc(baseAmount, rule.getPromoterRate()), BigDecimal.ZERO));
        if (!licensed) {
            BigDecimal taxAmount = calc(baseAmount, rule.getTaxWithholdRate());
            if (taxAmount.compareTo(BigDecimal.ZERO) > 0) {
                records.add(buildRecord(order, unit, rule, "TAX", order.getMerchantId(), rule.getTaxWithholdRate(), taxAmount, taxAmount));
            }
        }
        records.forEach(orderDivideRecordMapper::insert);
    }

    private void updateDivideRecordsRefunded(Long orderId, Long unitId) {
        List<OrderDivideRecordDO> records = unitId != null ? orderDivideRecordMapper.selectListByUnitId(unitId)
                : orderDivideRecordMapper.selectListByOrderId(orderId);
        for (OrderDivideRecordDO record : records) {
            orderDivideRecordMapper.updateById(OrderDivideRecordDO.builder()
                    .id(record.getId())
                    .settleStatus("REFUNDED")
                    .remark(StrUtil.blankToDefault(record.getRemark(), "") + "；退款回滚")
                    .build());
        }
    }

    private OrderDivideRecordDO buildRecord(OrderInfoDO order, OrderUnitDO unit, DivideRuleDO rule, String targetType,
                                            Long targetBizId, BigDecimal rate, BigDecimal amount, BigDecimal taxAmount) {
        return OrderDivideRecordDO.builder()
                .divideNo("LBDR" + IdUtil.getSnowflakeNextIdStr())
                .orderId(order.getId())
                .unitId(unit != null ? unit.getId() : null)
                .divideRuleId(rule.getId())
                .targetType(targetType)
                .targetBizId(targetBizId)
                .divideRate(defaultAmount(rate))
                .divideAmount(amount)
                .taxAmount(taxAmount)
                .settleStatus("PENDING")
                .cityLevel(rule.getCityLevel())
                .categoryId(order.getCategoryId())
                .remark("订单分账预分配")
                .build();
    }

    private boolean hasApprovedLicense(Long merchantId) {
        if (merchantId == null) {
            return false;
        }
        MemberUserQualificationDO qualification = memberUserQualificationMapper.selectOne(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, merchantId)
                .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                .like(MemberUserQualificationDO::getQualificationName, "营业执照")
                .last("LIMIT 1"));
        return qualification != null;
    }

    private WalletAccountDO getOrCreateWalletAccount(Long userId, String roleCode) {
        WalletAccountDO walletAccount = walletAccountMapper.selectByUserIdAndRoleCode(userId, roleCode);
        if (walletAccount != null) {
            return walletAccount;
        }
        walletAccount = WalletAccountDO.builder()
                .userId(userId)
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
            WalletAccountDO concurrent = walletAccountMapper.selectByUserIdAndRoleCodeForUpdate(userId, roleCode);
            if (concurrent == null) {
                throw ex;
            }
            return concurrent;
        }
    }

    private void createEscrowProofs(OrderInfoDO order, BigDecimal orderAmount) {
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, order.getId())
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
        BigDecimal unitTotal = units.stream()
                .map(OrderUnitDO::getUnitAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean validUnitAmounts = !units.isEmpty()
                && units.stream().allMatch(unit -> unit.getUnitAmount() != null
                && unit.getUnitAmount().compareTo(BigDecimal.ZERO) > 0)
                && unitTotal.compareTo(orderAmount) == 0;
        if (!validUnitAmounts) {
            escrowProofService.createLockedProof(order.getId(), null, order.getUserId(), order.getMerchantId(),
                    orderAmount, "订单支付成功，待服务完成后解锁");
            return;
        }
        for (OrderUnitDO unit : units) {
            escrowProofService.createLockedProof(order.getId(), unit.getId(), order.getUserId(), unit.getMerchantId(),
                    unit.getUnitAmount(), "订单单元支付成功，待对应单元完成后解锁");
        }
    }

    private boolean existsOrderPaidFlow(Long orderId, Long payOrderId) {
        LambdaQueryWrapperX<WalletFlowDO> query = new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getRelatedOrderId, orderId)
                .eq(WalletFlowDO::getBizType, "ORDER_PAY")
                .eq(WalletFlowDO::getFlowType, "IN")
                .last("LIMIT 1");
        if (payOrderId == null) {
            query.isNull(WalletFlowDO::getRelatedPayOrderId);
        } else {
            query.eq(WalletFlowDO::getRelatedPayOrderId, payOrderId);
        }
        return walletFlowMapper.selectOne(query) != null;
    }

    private boolean existsRefundFlow(Long orderId, Long unitId) {
        LambdaQueryWrapperX<WalletFlowDO> query = new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getRelatedOrderId, orderId)
                .eq(WalletFlowDO::getBizType, "REFUND_SUCCESS")
                .eq(WalletFlowDO::getFlowType, "OUT")
                .last("LIMIT 1");
        if (unitId == null) {
            query.isNull(WalletFlowDO::getRelatedUnitId);
        } else {
            query.eq(WalletFlowDO::getRelatedUnitId, unitId);
        }
        return walletFlowMapper.selectOne(query) != null;
    }

    private BigDecimal calc(BigDecimal amount, BigDecimal rate) {
        if (amount == null || rate == null) {
            return BigDecimal.ZERO;
        }
        return amount.multiply(rate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private BigDecimal nonNegative(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : amount;
    }
}
