package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundCreateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.refund.PayRefundApi;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundCreateReqDTO;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundRespDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_ACCESS_DENIED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_PAY_ORDER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_REFUND_AMOUNT_EXCEED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_REFUND_AMOUNT_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_REFUND_NOTIFY_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_REFUND_STATUS_NOT_ALLOWED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_UNIT_NOT_EXISTS;

@Service
@Validated
public class AppPayRefundServiceImpl implements AppPayRefundService {

    private static final String MERCHANT_REFUND_PREFIX = "LBR";

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private WalletAccountMapper walletAccountMapper;
    @Resource
    private WalletFlowMapper walletFlowMapper;

    @Resource
    private PayOrderService payOrderService;
    @Resource
    private PayAppService payAppService;
    @Resource
    private PayRefundApi payRefundApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRefund(Long authUserId, @Valid AppPayRefundCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        OrderInfoDO order = validateAndGetRefundOrder(authUserId, loginUser.getId(), reqVO.getOrderId());
        OrderUnitDO unit = validateRefundUnit(reqVO.getUnitId(), order.getId());
        validateRefundAmount(reqVO.getApplyAmount(), order, unit);

        PayOrderDO payOrder = payOrderService.getOrder(order.getPayOrderId());
        if (payOrder == null) {
            throw exception(ORDER_PAY_ORDER_NOT_EXISTS);
        }
        if (!PayOrderStatusEnum.isSuccessOrRefund(payOrder.getStatus())) {
            throw exception(ORDER_REFUND_STATUS_NOT_ALLOWED);
        }
        PayAppDO payApp = payAppService.getApp(payOrder.getAppId());
        if (payApp == null) {
            throw exception(ORDER_PAY_ORDER_NOT_EXISTS);
        }

        String merchantRefundId = buildMerchantRefundId(order.getId(), unit != null ? unit.getId() : null);
        Long payRefundId = payRefundApi.createRefund(new PayRefundCreateReqDTO()
                .setAppKey(payApp.getAppKey())
                .setUserIp(ServletUtils.getClientIP())
                .setUserId(loginUser.getId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setMerchantOrderId(payOrder.getMerchantOrderId())
                .setMerchantRefundId(merchantRefundId)
                .setReason(reqVO.getReason())
                .setPrice(reqVO.getApplyAmount().multiply(new BigDecimal("100"))
                        .setScale(0, RoundingMode.HALF_UP).intValue())
                .setNeedAudit(Boolean.TRUE));

        if (!Objects.equals(order.getStatus(), "AFTER_SALE")) {
            orderInfoMapper.updateById(OrderInfoDO.builder().id(order.getId()).status("AFTER_SALE").build());
        }
        saveOperateLog(order.getId(), unit != null ? unit.getId() : null, "REFUND_APPLY", "USER",
                loginUser.getId(), order.getStatus(), "AFTER_SALE", "用户提交退款申请");
        saveRefundTraceFlow(loginUser, order, unit, payRefundId, "REFUND_APPLY", "IN", reqVO.getReason());
        return payRefundId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRefunded(@Valid PayRefundNotifyReqDTO notifyReqDTO) {
        RefundBizRef bizRef = parseRefundBizRef(notifyReqDTO.getMerchantRefundId());
        OrderInfoDO order = orderInfoMapper.selectById(bizRef.orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        OrderUnitDO unit = validateRefundUnit(bizRef.unitId, order.getId());
        PayRefundRespDTO payRefund = payRefundApi.getRefund(notifyReqDTO.getPayRefundId());
        if (payRefund == null
                || !Objects.equals(payRefund.getMerchantRefundId(), notifyReqDTO.getMerchantRefundId())
                || !Objects.equals(payRefund.getMerchantOrderId(), notifyReqDTO.getMerchantOrderId())) {
            throw exception(ORDER_REFUND_NOTIFY_INVALID);
        }

        if (PayRefundStatusEnum.isSuccess(payRefund.getStatus())) {
            handleRefundSuccess(order, unit, payRefund);
            return;
        }
        if (PayRefundStatusEnum.isFailure(payRefund.getStatus())) {
            handleRefundFailure(order, unit, payRefund);
        }
    }

    private void handleRefundSuccess(OrderInfoDO order, OrderUnitDO unit, PayRefundRespDTO payRefund) {
        if (existsRefundFlow(payRefund.getId(), "REFUND_SUCCESS")) {
            return;
        }
        BigDecimal refundAmount = MoneyUtils.fenToYuan(payRefund.getRefundPrice());
        if (unit != null && !Objects.equals(unit.getStatus(), "REFUNDED")) {
            orderUnitMapper.updateById(OrderUnitDO.builder().id(unit.getId()).status("REFUNDED").build());
        } else if (unit == null) {
            markAllUnitsRefunded(order.getId());
        }

        BigDecimal newOrderAmount = nonNegative(order.getOrderAmount().subtract(refundAmount));
        String nextOrderStatus = resolveOrderStatus(order.getId(), newOrderAmount);
        orderInfoMapper.updateById(OrderInfoDO.builder()
                .id(order.getId())
                .orderAmount(newOrderAmount)
                .status(nextOrderStatus)
                .build());
        saveOperateLog(order.getId(), unit != null ? unit.getId() : null, "REFUND_SUCCESS", "SYSTEM", 0L,
                order.getStatus(), nextOrderStatus, "退款成功，订单金额重算");
        saveRefundTraceFlow(resolveRefundUser(order), order, unit, payRefund.getId(), "REFUND_SUCCESS", "IN",
                "退款成功，金额 " + refundAmount);
    }

    private void handleRefundFailure(OrderInfoDO order, OrderUnitDO unit, PayRefundRespDTO payRefund) {
        if (existsRefundFlow(payRefund.getId(), "REFUND_FAILED")) {
            return;
        }
        saveOperateLog(order.getId(), unit != null ? unit.getId() : null, "REFUND_FAILED", "SYSTEM", 0L,
                order.getStatus(), order.getStatus(), "退款失败：" + payRefund.getChannelErrorMsg());
        saveRefundTraceFlow(resolveRefundUser(order), order, unit, payRefund.getId(), "REFUND_FAILED", "OUT",
                "退款失败：" + payRefund.getChannelErrorMsg());
    }

    private String resolveOrderStatus(Long orderId, BigDecimal newOrderAmount) {
        if (newOrderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return "REFUNDED";
        }
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, orderId));
        boolean allRefunded = !units.isEmpty() && units.stream().allMatch(item -> Objects.equals(item.getStatus(), "REFUNDED"));
        return allRefunded ? "REFUNDED" : "AFTER_SALE";
    }

    private void markAllUnitsRefunded(Long orderId) {
        List<OrderUnitDO> units = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getOrderId, orderId));
        for (OrderUnitDO item : units) {
            if (!Objects.equals(item.getStatus(), "REFUNDED")) {
                orderUnitMapper.updateById(OrderUnitDO.builder().id(item.getId()).status("REFUNDED").build());
            }
        }
    }

    private OrderInfoDO validateAndGetRefundOrder(Long authUserId, Long lbUserId, Long orderId) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        if (order == null) {
            throw exception(ORDER_INFO_NOT_EXISTS);
        }
        if (!Objects.equals(order.getUserId(), lbUserId)) {
            throw exception(ORDER_ACCESS_DENIED);
        }
        if (order.getPayOrderId() == null || Objects.equals(order.getStatus(), "CLOSED") || Objects.equals(order.getStatus(), "REFUNDED")) {
            throw exception(ORDER_REFUND_STATUS_NOT_ALLOWED);
        }
        return order;
    }

    private OrderUnitDO validateRefundUnit(Long unitId, Long orderId) {
        if (unitId == null) {
            return null;
        }
        OrderUnitDO unit = orderUnitMapper.selectById(unitId);
        if (unit == null || !Objects.equals(unit.getOrderId(), orderId)) {
            throw exception(ORDER_UNIT_NOT_EXISTS);
        }
        if (Objects.equals(unit.getStatus(), "REFUNDED")) {
            throw exception(ORDER_REFUND_STATUS_NOT_ALLOWED);
        }
        return unit;
    }

    private void validateRefundAmount(BigDecimal applyAmount, OrderInfoDO order, OrderUnitDO unit) {
        if (applyAmount == null || applyAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw exception(ORDER_REFUND_AMOUNT_INVALID);
        }
        BigDecimal limitAmount = unit != null ? unit.getUnitAmount() : order.getOrderAmount();
        if (limitAmount == null || applyAmount.compareTo(limitAmount) > 0) {
            throw exception(ORDER_REFUND_AMOUNT_EXCEED);
        }
    }

    private String buildMerchantRefundId(Long orderId, Long unitId) {
        return MERCHANT_REFUND_PREFIX + "-" + orderId + "-" + (unitId == null ? 0L : unitId) + "-" + IdUtil.getSnowflakeNextIdStr();
    }

    private RefundBizRef parseRefundBizRef(String merchantRefundId) {
        String[] segments = merchantRefundId.split("-");
        if (segments.length < 4 || !Objects.equals(segments[0], MERCHANT_REFUND_PREFIX)) {
            throw exception(ORDER_REFUND_NOTIFY_INVALID);
        }
        try {
            Long orderId = Long.valueOf(segments[1]);
            Long unitId = Long.valueOf(segments[2]);
            return new RefundBizRef(orderId, Objects.equals(unitId, 0L) ? null : unitId);
        } catch (NumberFormatException ex) {
            throw exception(ORDER_REFUND_NOTIFY_INVALID);
        }
    }

    private void saveRefundTraceFlow(MemberUserDO loginUser, OrderInfoDO order, OrderUnitDO unit,
                                     Long payRefundId, String bizType, String flowType, String remark) {
        WalletAccountDO walletAccount = getOrCreateWalletAccount(loginUser);
        walletFlowMapper.insert(WalletFlowDO.builder()
                .flowNo("LBF" + IdUtil.getSnowflakeNextIdStr())
                .userId(loginUser.getId())
                .walletAccountId(walletAccount.getId())
                .bizType(bizType)
                .flowType(flowType)
                .changeAmount(BigDecimal.ZERO)
                .beforeAmount(walletAccount.getAvailableAmount())
                .afterAmount(walletAccount.getAvailableAmount())
                .relatedOrderId(order.getId())
                .relatedUnitId(unit != null ? unit.getId() : null)
                .relatedPayOrderId(order.getPayOrderId())
                .relatedRefundId(payRefundId)
                .remark(remark)
                .createTime(LocalDateTime.now())
                .build());
    }

    private boolean existsRefundFlow(Long payRefundId, String bizType) {
        return walletFlowMapper.selectOne(new LambdaQueryWrapperX<WalletFlowDO>()
                .eq(WalletFlowDO::getRelatedRefundId, payRefundId)
                .eq(WalletFlowDO::getBizType, bizType)) != null;
    }

    private WalletAccountDO getOrCreateWalletAccount(MemberUserDO loginUser) {
        String roleCode = loginUser.getCurrentRoleCode() != null ? loginUser.getCurrentRoleCode() : "USER";
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

    private MemberUserDO resolveRefundUser(OrderInfoDO order) {
        MemberUserDO loginUser = memberUserService.getMemberUser(order.getUserId());
        if (loginUser != null) {
            return loginUser;
        }
        MemberUserDO fallback = new MemberUserDO();
        fallback.setId(order.getUserId());
        fallback.setCurrentRoleCode("USER");
        return fallback;
    }

    private void saveOperateLog(Long orderId, Long unitId, String operateType, String operateRole,
                                Long operateBy, String beforeStatus, String afterStatus, String remark) {
        orderOperateLogMapper.insert(OrderOperateLogDO.builder()
                .orderId(orderId)
                .unitId(unitId)
                .operateType(operateType)
                .operateRole(operateRole)
                .operateBy(operateBy)
                .beforeStatus(beforeStatus)
                .afterStatus(afterStatus)
                .remark(remark)
                .operateTime(LocalDateTime.now())
                .build());
    }

    private BigDecimal nonNegative(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : amount;
    }

    private static final class RefundBizRef {

        private final Long orderId;
        private final Long unitId;

        private RefundBizRef(Long orderId, Long unitId) {
            this.orderId = orderId;
            this.unitId = unitId;
        }

    }

}
