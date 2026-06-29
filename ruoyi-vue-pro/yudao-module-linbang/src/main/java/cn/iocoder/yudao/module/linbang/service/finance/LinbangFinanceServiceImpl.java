package cn.iocoder.yudao.module.linbang.service.finance;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.dividerule.DivideRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderdividerecord.OrderDivideRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.dividerule.DivideRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderdividerecord.OrderDivideRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import cn.iocoder.yudao.module.linbang.service.escrowproof.EscrowProofService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferRespDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private EscrowProofService escrowProofService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderPaid(OrderInfoDO order, Long payOrderId) {
        WalletAccountDO walletAccount = getOrCreateWalletAccount(order.getUserId(), "USER");
        BigDecimal orderAmount = defaultAmount(order.getOrderAmount());
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
        escrowProofService.createLockedProof(order.getId(), null, order.getUserId(), order.getMerchantId(),
                orderAmount, "订单支付成功，待服务完成后解锁");
        createDivideRecords(order, null, orderAmount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleUnitFinished(OrderInfoDO order, OrderUnitDO unit) {
        if (order == null || unit == null || unit.getMerchantId() == null) {
            return;
        }
        WalletAccountDO merchantWallet = getOrCreateWalletAccount(unit.getMerchantId(), "MERCHANT");
        BigDecimal unitAmount = defaultAmount(unit.getUnitAmount());
        BigDecimal beforeAvailable = defaultAmount(merchantWallet.getAvailableAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(merchantWallet.getId())
                .totalAmount(defaultAmount(merchantWallet.getTotalAmount()).add(unitAmount))
                .availableAmount(beforeAvailable.add(unitAmount))
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(unit.getMerchantId())
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

        WalletAccountDO userWallet = getOrCreateWalletAccount(order.getUserId(), "USER");
        BigDecimal beforeEscrow = defaultAmount(userWallet.getEscrowAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(userWallet.getId())
                .escrowAmount(nonNegative(beforeEscrow.subtract(unitAmount)))
                .build());
        escrowProofService.unlockProof(order.getId(), null, "单元完成并确认，释放托管金额");
        messagePushDispatchService.dispatchSingle("lb_escrow_unlocked", "托管解锁通知", "ORDER",
                order.getId(), order.getUserId(), "单元完成后提醒用户托管资金已解锁");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundSuccess(OrderInfoDO order, OrderUnitDO unit, BigDecimal refundAmount) {
        if (order == null) {
            return;
        }
        WalletAccountDO userWallet = getOrCreateWalletAccount(order.getUserId(), "USER");
        BigDecimal realRefundAmount = defaultAmount(refundAmount);
        BigDecimal beforeEscrow = defaultAmount(userWallet.getEscrowAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(userWallet.getId())
                .escrowAmount(nonNegative(beforeEscrow.subtract(realRefundAmount)))
                .totalAmount(nonNegative(defaultAmount(userWallet.getTotalAmount()).subtract(realRefundAmount)))
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
                .remark("退款成功，冲减托管金额")
                .createTime(LocalDateTime.now())
                .build());
        escrowProofService.refundProof(order.getId(), unit != null ? unit.getId() : null, "退款成功，释放托管");
        updateDivideRecordsRefunded(order.getId(), unit != null ? unit.getId() : null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleWithdrawTransferSuccess(WalletAccountDO walletAccount, Long withdrawId, PayTransferRespDTO transfer) {
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(withdrawId);
        if (withdraw == null || "SUCCESS".equals(withdraw.getStatus())) {
            return;
        }
        BigDecimal realAmount = defaultAmount(withdraw.getRealAmount());
        BigDecimal beforeFrozen = defaultAmount(walletAccount.getFrozenAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .frozenAmount(nonNegative(beforeFrozen.subtract(realAmount)))
                .totalAmount(nonNegative(defaultAmount(walletAccount.getTotalAmount()).subtract(realAmount)))
                .build());
        walletWithdrawMapper.updateById(WalletWithdrawDO.builder()
                .id(withdraw.getId())
                .status("SUCCESS")
                .auditStatus("APPROVED")
                .payTime(transfer.getSuccessTime())
                .payTransferId(transfer.getId())
                .payTransferNo(transfer.getNo())
                .transferErrorMsg(null)
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(withdraw.getUserId())
                .walletAccountId(walletAccount.getId())
                .bizType("WITHDRAW_SUCCESS")
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
        WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(withdrawId);
        if (withdraw == null || "FAILED".equals(withdraw.getStatus())) {
            return;
        }
        BigDecimal realAmount = defaultAmount(withdraw.getRealAmount());
        BigDecimal beforeFrozen = defaultAmount(walletAccount.getFrozenAmount());
        BigDecimal beforeAvailable = defaultAmount(walletAccount.getAvailableAmount());
        walletAccountMapper.updateById(WalletAccountDO.builder()
                .id(walletAccount.getId())
                .frozenAmount(nonNegative(beforeFrozen.subtract(realAmount)))
                .availableAmount(beforeAvailable.add(realAmount))
                .build());
        walletWithdrawMapper.updateById(WalletWithdrawDO.builder()
                .id(withdraw.getId())
                .status("FAILED")
                .payTransferId(transfer.getId())
                .payTransferNo(transfer.getNo())
                .transferErrorMsg(StrUtil.blankToDefault(transfer.getChannelErrorMsg(), "打款失败"))
                .build());
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(withdraw.getUserId())
                .walletAccountId(walletAccount.getId())
                .bizType("WITHDRAW_FAILED")
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
                .orderByDesc(DivideRuleDO::getEffectiveTime, DivideRuleDO::getId)
                .last("LIMIT 1"));
        if (rule == null) {
            return;
        }
        List<OrderDivideRecordDO> records = new ArrayList<>();
        records.add(buildRecord(order, unit, rule, "MERCHANT", order.getMerchantId(), rule.getMerchantRate(), calc(baseAmount, rule.getMerchantRate()), BigDecimal.ZERO));
        records.add(buildRecord(order, unit, rule, "PLATFORM", 0L, rule.getPlatformRate(), calc(baseAmount, rule.getPlatformRate()), BigDecimal.ZERO));
        records.add(buildRecord(order, unit, rule, "PARTNER", null, rule.getPartnerRate(), calc(baseAmount, rule.getPartnerRate()), BigDecimal.ZERO));
        records.add(buildRecord(order, unit, rule, "PROMOTER", null, rule.getPromoterRate(), calc(baseAmount, rule.getPromoterRate()), BigDecimal.ZERO));
        if (!hasApprovedLicense(order.getMerchantId())) {
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
        WalletAccountDO walletAccount = walletAccountMapper.selectOne(new LambdaQueryWrapperX<WalletAccountDO>()
                .eq(WalletAccountDO::getUserId, userId)
                .eq(WalletAccountDO::getRoleCode, roleCode));
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
        walletAccountMapper.insert(walletAccount);
        return walletAccount;
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
