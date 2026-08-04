package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.util.number.MoneyUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.pay.api.refund.PayRefundApi;
import cn.iocoder.yudao.module.pay.api.refund.dto.PayRefundCreateReqDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
@Slf4j
public class AutoFlowRefundServiceImpl implements AutoFlowRefundService {

    private static final String FLOW_REFUND_PREFIX = "LBAF";
    private static final String REFUND_STATUS_NONE = "NONE";
    private static final String REFUND_STATUS_PROCESSING = "PROCESSING";
    private static final String REFUND_STATUS_FAILED = "FAILED";

    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderOperateLogMapper orderOperateLogMapper;
    @Resource
    private PayOrderMapper payOrderMapper;
    @Resource
    private PayRefundMapper payRefundMapper;
    @Resource
    private PayAppService payAppService;
    @Resource
    private PayRefundApi payRefundApi;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public Long createAutoRefund(Long orderId, Long unitId, LocalDateTime flowTime) {
        RefundPreparation preparation = transactionTemplate.execute(
                status -> prepareRefund(orderId, unitId, flowTime));
        if (preparation == null) {
            return null;
        }
        if (!preparation.shouldCreate) {
            if (preparation.unit != null && preparation.payRefundId != null) {
                return linkAndReconcile(preparation, preparation.payRefundId);
            }
            return preparation.payRefundId;
        }

        try {
            Long payRefundId = payRefundApi.createRefund(buildCreateRequest(preparation));
            return linkAndReconcile(preparation, payRefundId);
        } catch (RuntimeException ex) {
            PayRefundDO existedRefund = payRefundMapper.selectByAppIdAndMerchantRefundId(
                    preparation.payApp.getId(), preparation.merchantRefundId);
            if (existedRefund != null) {
                return linkAndReconcile(preparation, existedRefund.getId());
            }
            transactionTemplate.executeWithoutResult(status -> markCreateFailed(preparation));
            throw ex;
        }
    }

    private Long linkAndReconcile(RefundPreparation preparation, Long payRefundId) {
        Long linkedRefundId = transactionTemplate.execute(status -> linkRefund(preparation, payRefundId));
        if (linkedRefundId == null) {
            return null;
        }
        PayRefundDO refund = payRefundMapper.selectById(linkedRefundId);
        if (refund != null && (PayRefundStatusEnum.isSuccess(refund.getStatus())
                || PayRefundStatusEnum.isFailure(refund.getStatus()))) {
            transactionTemplate.executeWithoutResult(status -> handleRefundCallback(refund.getId(),
                    refund.getMerchantRefundId(), refund.getStatus(), null));
        }
        return linkedRefundId;
    }

    private RefundPreparation prepareRefund(Long orderId, Long unitId, LocalDateTime flowTime) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        OrderUnitDO unit = orderUnitMapper.selectOneForUpdate(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getId, unitId)
                .eq(OrderUnitDO::getOrderId, orderId));
        if (order == null || unit == null || order.getPayOrderId() == null || unit.getUnitAmount() == null
                || unit.getUnitAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }
        if (unit.getAutoRefundId() != null && !REFUND_STATUS_FAILED.equals(unit.getAutoRefundStatus())) {
            return RefundPreparation.completed(unit.getAutoRefundId());
        }
        PayOrderDO payOrder = payOrderMapper.selectById(order.getPayOrderId());
        if (payOrder == null) {
            return null;
        }
        PayAppDO payApp = payAppService.getApp(payOrder.getAppId());
        if (payApp == null) {
            return null;
        }
        LocalDateTime canonicalFlowTime = unit.getFlowTime() != null ? unit.getFlowTime() : flowTime;
        long flowKey = canonicalFlowTime == null ? 0L : canonicalFlowTime.toEpochSecond(ZoneOffset.UTC);
        String merchantRefundId = FLOW_REFUND_PREFIX + "-" + orderId + "-" + unitId + "-" + flowKey;
        if (REFUND_STATUS_FAILED.equals(unit.getAutoRefundStatus()) && unit.getAutoRefundId() != null) {
            merchantRefundId += "-R" + unit.getAutoRefundId();
        }
        RefundPreparation preparation = new RefundPreparation(order, unit, payOrder, payApp, merchantRefundId,
                null, true);

        PayRefundDO existedRefund = payRefundMapper.selectByAppIdAndMerchantRefundId(payApp.getId(), merchantRefundId);
        if (existedRefund != null) {
            return preparation.withExistingRefund(existedRefund.getId());
        }
        // PROCESSING 且尚未关联退款单，可能是进程在支付单创建前后崩溃。
        // 继续使用确定性的 merchantRefundId 发起，支付模块唯一键负责幂等收敛。
        if (REFUND_STATUS_PROCESSING.equals(unit.getAutoRefundStatus()) && unit.getAutoRefundId() != null) {
            return RefundPreparation.completed(unit.getAutoRefundId());
        }
        if (REFUND_STATUS_PROCESSING.equals(unit.getAutoRefundStatus())) {
            return preparation;
        }
        if (!REFUND_STATUS_NONE.equals(unit.getAutoRefundStatus())
                && !REFUND_STATUS_FAILED.equals(unit.getAutoRefundStatus())) {
            return RefundPreparation.completed(null);
        }
        int updated = orderUnitMapper.update(null, new LambdaUpdateWrapper<OrderUnitDO>()
                .eq(OrderUnitDO::getId, unit.getId())
                .eq(OrderUnitDO::getAutoRefundStatus, unit.getAutoRefundStatus())
                .set(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_PROCESSING)
                .set(OrderUnitDO::getAutoRefundId, null));
        return updated == 0 ? RefundPreparation.completed(null) : preparation;
    }

    private PayRefundCreateReqDTO buildCreateRequest(RefundPreparation preparation) {
        return new PayRefundCreateReqDTO()
                .setAppKey(preparation.payApp.getAppKey())
                .setUserIp(ServletUtils.getClientIP())
                .setUserId(preparation.order.getUserId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setMerchantOrderId(preparation.payOrder.getMerchantOrderId())
                .setMerchantRefundId(preparation.merchantRefundId)
                .setReason("流单自动原路退款")
                .setPrice(refundFen(preparation.unit.getUnitAmount()))
                .setNeedAudit(Boolean.FALSE);
    }

    private Long linkRefund(RefundPreparation preparation, Long payRefundId) {
        OrderUnitDO current = orderUnitMapper.selectOneForUpdate(OrderUnitDO::getId, preparation.unit.getId());
        if (current == null) {
            return payRefundId;
        }
        if (current.getAutoRefundId() != null && !REFUND_STATUS_FAILED.equals(current.getAutoRefundStatus())) {
            return current.getAutoRefundId();
        }
        LambdaUpdateWrapper<OrderUnitDO> updateWrapper = new LambdaUpdateWrapper<OrderUnitDO>()
                .eq(OrderUnitDO::getId, current.getId())
                .set(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_PROCESSING)
                .set(OrderUnitDO::getAutoRefundId, payRefundId);
        if (current.getAutoRefundId() == null) {
            updateWrapper.isNull(OrderUnitDO::getAutoRefundId);
        } else {
            updateWrapper.eq(OrderUnitDO::getAutoRefundId, current.getAutoRefundId())
                    .eq(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_FAILED);
        }
        int updated = orderUnitMapper.update(null, updateWrapper);
        if (updated > 0) {
            orderOperateLogMapper.insert(OrderOperateLogDO.builder()
                    .orderId(preparation.order.getId())
                    .unitId(current.getId())
                    .operateType("AUTO_FLOW_REFUND")
                    .operateRole("SYSTEM")
                    .operateBy(0L)
                    .beforeStatus(current.getStatus())
                    .afterStatus(current.getStatus())
                    .remark("流单自动发起原路退款")
                    .operateTime(LocalDateTime.now())
                    .build());
        }
        return payRefundId;
    }

    private void markCreateFailed(RefundPreparation preparation) {
        orderUnitMapper.update(null, new LambdaUpdateWrapper<OrderUnitDO>()
                .eq(OrderUnitDO::getId, preparation.unit.getId())
                .isNull(OrderUnitDO::getAutoRefundId)
                .eq(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_PROCESSING)
                .set(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_FAILED)
                .set(OrderUnitDO::getFlowReason, "自动退款发起失败，请稍后重试"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundCallback(Long payRefundId, String merchantRefundId, Integer status, String channelErrorMsg) {
        if (merchantRefundId == null || !merchantRefundId.startsWith(FLOW_REFUND_PREFIX)) {
            return;
        }
        OrderUnitDO unit = orderUnitMapper.selectOneForUpdate(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getAutoRefundId, payRefundId)
                .last("LIMIT 1"));
        if (unit == null) {
            return;
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null) {
            return;
        }
        PayRefundDO refund = payRefundMapper.selectById(payRefundId);
        PayOrderDO payOrder = order.getPayOrderId() == null ? null : payOrderMapper.selectById(order.getPayOrderId());
        if (refund == null || !Objects.equals(refund.getMerchantRefundId(), merchantRefundId)
                || payOrder == null || !Objects.equals(refund.getMerchantOrderId(), payOrder.getMerchantOrderId())) {
            return;
        }
        if (unit.getUnitAmount() == null || refund.getRefundPrice() == null
                || refund.getRefundPrice() != refundFen(unit.getUnitAmount())) {
            return;
        }
        if (PayRefundStatusEnum.isSuccess(status)) {
            int updated = orderUnitMapper.update(null, new LambdaUpdateWrapper<OrderUnitDO>()
                    .eq(OrderUnitDO::getId, unit.getId())
                    .eq(OrderUnitDO::getAutoRefundId, payRefundId)
                    .eq(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_PROCESSING)
                    .set(OrderUnitDO::getStatus, "REFUNDED")
                    .set(OrderUnitDO::getAutoRefundStatus, "SUCCESS"));
            if (updated > 0) {
                messagePushDispatchService.dispatchSingleIdempotent("lb_order_flow_refunded", "流单退款结果通知",
                        "ORDER_FLOW_REFUND", unit.getId(), order.getUserId(), "流单退款成功通知",
                        "lb_order_flow_refunded:" + unit.getId() + ":" + payRefundId);
            }
            return;
        }
        if (PayRefundStatusEnum.isFailure(status)) {
            orderUnitMapper.update(null, new LambdaUpdateWrapper<OrderUnitDO>()
                    .eq(OrderUnitDO::getId, unit.getId())
                    .eq(OrderUnitDO::getAutoRefundId, payRefundId)
                    .eq(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_PROCESSING)
                    .set(OrderUnitDO::getAutoRefundStatus, REFUND_STATUS_FAILED)
                    .set(OrderUnitDO::getFlowReason, "自动退款失败，请稍后重试"));
        }
    }

    private int refundFen(BigDecimal amount) {
        return MoneyUtils.yuanToFen(amount);
    }

    @AllArgsConstructor
    private static class RefundPreparation {
        private final OrderInfoDO order;
        private final OrderUnitDO unit;
        private final PayOrderDO payOrder;
        private final PayAppDO payApp;
        private final String merchantRefundId;
        private final Long payRefundId;
        private final boolean shouldCreate;

        private static RefundPreparation completed(Long payRefundId) {
            return new RefundPreparation(null, null, null, null, null, payRefundId, false);
        }

        private RefundPreparation withExistingRefund(Long existedPayRefundId) {
            return new RefundPreparation(order, unit, payOrder, payApp, merchantRefundId,
                    existedPayRefundId, false);
        }
    }
}
