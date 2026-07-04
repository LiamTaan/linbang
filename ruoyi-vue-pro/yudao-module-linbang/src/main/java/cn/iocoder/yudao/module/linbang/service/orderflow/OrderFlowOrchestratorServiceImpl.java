package cn.iocoder.yudao.module.linbang.service.orderflow;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.escrowproof.EscrowProofDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.escrowproof.EscrowProofMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.service.finance.LinbangFinanceService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class OrderFlowOrchestratorServiceImpl implements OrderFlowOrchestratorService {

    private static final List<String> FLOW_TERMINAL_DISPATCH_STATUS = Arrays.asList("FLOWED", "EXPIRED");
    private static final List<String> AUTO_REFUND_ACTIVE_STATUS = Arrays.asList("PROCESSING", "SUCCESS", "FAILED");

    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;
    @Resource
    private EscrowProofMapper escrowProofMapper;
    @Resource
    private LinbangFinanceService linbangFinanceService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onOrderPaid(Long orderId) {
        refreshOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onOrderAccepted(Long orderId) {
        refreshOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onOrderServing(Long orderId) {
        refreshOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onUnitFinished(Long orderId) {
        refreshOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onOrderFlowed(Long orderId) {
        refreshOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onRefundSuccess(Long orderId) {
        refreshOrder(orderId);
        repairRefundConsistency(orderId);
        refreshOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onRefundFailed(Long orderId) {
        refreshOrder(orderId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void repairAbnormalOrders() {
        List<OrderInfoDO> orders = orderInfoMapper.selectList(new LambdaQueryWrapperX<OrderInfoDO>()
                .notIn(OrderInfoDO::getStatus, Arrays.asList("PENDING_PAY", "CLOSED", "FINISHED")));
        for (OrderInfoDO order : orders) {
            refreshOrder(order.getId());
            repairRefundConsistency(order.getId());
            refreshOrder(order.getId());
        }
    }

    private void refreshOrder(Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            return;
        }
        List<OrderUnitDO> units = listUnits(orderId);
        repairSplitUnitAssignmentConsistency(order, units);
        units = listUnits(orderId);
        String nextStatus = resolveMainStatus(order, units);
        if (!Objects.equals(order.getStatus(), nextStatus)) {
            orderInfoMapper.updateById(OrderInfoDO.builder()
                    .id(order.getId())
                    .status(nextStatus)
                    .build());
        }
    }

    private void repairRefundConsistency(Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            return;
        }
        List<OrderUnitDO> units = listUnits(orderId);
        for (OrderUnitDO unit : units) {
            if (!isRefundCompletedUnit(unit)) {
                continue;
            }
            if (!Objects.equals(unit.getStatus(), "REFUNDED")) {
                orderUnitMapper.updateById(OrderUnitDO.builder()
                        .id(unit.getId())
                        .status("REFUNDED")
                        .build());
            }
            if (!needsRefundFinanceRepair(order, unit)) {
                continue;
            }
            linbangFinanceService.handleRefundSuccess(order, unit, defaultAmount(unit.getUnitAmount()), unit.getAutoRefundId());
        }
    }

    private void repairSplitUnitAssignmentConsistency(OrderInfoDO order, List<OrderUnitDO> units) {
        if (order == null || units.isEmpty()) {
            return;
        }
        for (OrderUnitDO unit : units) {
            if (unit.getPrevUnitId() == null) {
                continue;
            }
            OrderUnitDO prevUnit = units.stream()
                    .filter(item -> Objects.equals(item.getId(), unit.getPrevUnitId()))
                    .findFirst()
                    .orElse(null);
            if (prevUnit == null || !Objects.equals(prevUnit.getStatus(), "FINISHED")) {
                continue;
            }
            Long inheritMerchantId = prevUnit.getMerchantId() != null ? prevUnit.getMerchantId() : order.getMerchantId();
            if (inheritMerchantId == null) {
                continue;
            }
            if (Arrays.asList("FINISHED", "REFUNDED", "CLOSED", "APPEALING").contains(unit.getStatus())) {
                continue;
            }
            boolean needsAcceptRepair = Arrays.asList("PENDING_CREATE", "PENDING_ACCEPT").contains(unit.getStatus());
            boolean needsMerchantRepair = unit.getMerchantId() == null;
            boolean needsUnlockRepair = Boolean.TRUE.equals(unit.getIsLocked()) || unit.getLockReason() != null;
            boolean needsDispatchRepair = !Objects.equals(unit.getDispatchStatus(), "ACCEPTED");
            if (!needsAcceptRepair && !needsMerchantRepair && !needsUnlockRepair && !needsDispatchRepair) {
                continue;
            }
            LambdaUpdateWrapper<OrderUnitDO> repair = new LambdaUpdateWrapper<OrderUnitDO>()
                    .eq(OrderUnitDO::getId, unit.getId())
                    .set(OrderUnitDO::getMerchantId, inheritMerchantId)
                    .set(OrderUnitDO::getIsLocked, Boolean.FALSE)
                    .set(OrderUnitDO::getLockReason, null);
            if (needsAcceptRepair) {
                repair.set(OrderUnitDO::getStatus, "ACCEPTED")
                        .set(OrderUnitDO::getDispatchStatus, "ACCEPTED")
                        .set(OrderUnitDO::getAcceptDeadlineTime, null);
            } else if (needsDispatchRepair) {
                repair.set(OrderUnitDO::getDispatchStatus, "ACCEPTED");
            }
            orderUnitMapper.update(null, repair);
        }
    }

    private boolean needsRefundFinanceRepair(OrderInfoDO order, OrderUnitDO unit) {
        WalletFlowDO refundFlow = walletFlowMapper.selectOne(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getUserId, order.getUserId())
                .eq(WalletFlowDO::getRelatedOrderId, order.getId())
                .eq(WalletFlowDO::getRelatedUnitId, unit.getId())
                .eq(WalletFlowDO::getBizType, "REFUND_SUCCESS")
                .last("LIMIT 1"));
        EscrowProofDO proof = escrowProofMapper.selectOne(new LambdaQueryWrapperX<EscrowProofDO>()
                .eq(EscrowProofDO::getOrderId, order.getId())
                .eq(EscrowProofDO::getUnitId, unit.getId())
                .orderByDesc(EscrowProofDO::getId)
                .last("LIMIT 1"));
        WalletAccountDO wallet = walletAccountMapper.selectOne(new LambdaQueryWrapperX<WalletAccountDO>()
                .eq(WalletAccountDO::getUserId, order.getUserId())
                .eq(WalletAccountDO::getRoleCode, "USER")
                .last("LIMIT 1"));
        BigDecimal refundAmount = defaultAmount(unit.getUnitAmount());
        boolean proofPending = proof != null && !Objects.equals(proof.getProofStatus(), "REFUNDED");
        boolean walletPending = wallet != null && defaultAmount(wallet.getEscrowAmount()).compareTo(refundAmount) >= 0;
        return refundFlow == null && (proofPending || walletPending);
    }

    private String resolveMainStatus(OrderInfoDO order, List<OrderUnitDO> units) {
        if (Objects.equals(order.getStatus(), "PENDING_PAY") || order.getPayOrderId() == null) {
            return "PENDING_PAY";
        }
        if (units.isEmpty()) {
            return order.getStatus();
        }
        boolean allRefunded = units.stream().allMatch(unit -> Objects.equals(unit.getStatus(), "REFUNDED"));
        if (allRefunded) {
            return "REFUNDED";
        }
        boolean allFinished = units.stream().allMatch(unit -> Objects.equals(unit.getStatus(), "FINISHED"));
        if (allFinished) {
            return "FINISHED";
        }
        boolean hasDispatchablePendingUnit = units.stream().anyMatch(this::isDispatchablePendingUnit);
        boolean hasAfterSaleMarker = units.stream().anyMatch(this::hasAfterSaleMarker);
        if (hasAfterSaleMarker && !hasDispatchablePendingUnit) {
            return "AFTER_SALE";
        }
        if (units.stream().anyMatch(unit -> Objects.equals(unit.getStatus(), "PENDING_CONFIRM"))) {
            return "PENDING_CONFIRM";
        }
        if (units.stream().anyMatch(unit -> Objects.equals(unit.getStatus(), "SERVING"))) {
            return "SERVING";
        }
        if (units.stream().anyMatch(unit -> Objects.equals(unit.getStatus(), "ACCEPTED"))) {
            return "ACCEPTED";
        }
        if (units.stream().anyMatch(unit -> Objects.equals(unit.getStatus(), "PENDING_ACCEPT")
                || Objects.equals(unit.getStatus(), "PENDING_CREATE"))) {
            return "PENDING_ACCEPT";
        }
        return hasAfterSaleMarker ? "AFTER_SALE" : order.getStatus();
    }

    private boolean isDispatchablePendingUnit(OrderUnitDO unit) {
        return Objects.equals(unit.getStatus(), "PENDING_ACCEPT")
                && !Boolean.TRUE.equals(unit.getIsLocked())
                && !isFlowTerminal(unit);
    }

    private boolean hasAfterSaleMarker(OrderUnitDO unit) {
        return Objects.equals(unit.getStatus(), "APPEALING")
                || Objects.equals(unit.getStatus(), "REFUNDED")
                || unit.getFlowTime() != null
                || isFlowTerminal(unit)
                || AUTO_REFUND_ACTIVE_STATUS.contains(unit.getAutoRefundStatus());
    }

    private boolean isRefundCompletedUnit(OrderUnitDO unit) {
        return Objects.equals(unit.getStatus(), "REFUNDED")
                || Objects.equals(unit.getAutoRefundStatus(), "SUCCESS");
    }

    private boolean isFlowTerminal(OrderUnitDO unit) {
        return FLOW_TERMINAL_DISPATCH_STATUS.contains(unit.getDispatchStatus());
    }

    private List<OrderUnitDO> listUnits(Long orderId) {
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, orderId)
                .orderByAsc(OrderUnitDO::getUnitSeq, OrderUnitDO::getId));
        return units != null ? units : Collections.emptyList();
    }

    private BigDecimal defaultAmount(BigDecimal amount) {
        return amount != null ? amount : BigDecimal.ZERO;
    }
}
