package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
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
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AutoFlowRefundServiceImpl implements AutoFlowRefundService {

    private static final String FLOW_REFUND_PREFIX = "LBAF";

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAutoRefund(Long orderId, Long unitId, LocalDateTime flowTime) {
        OrderInfoDO order = orderInfoMapper.selectById(orderId);
        OrderUnitDO unit = orderUnitMapper.selectById(unitId);
        if (order == null || unit == null || order.getPayOrderId() == null) {
            return null;
        }
        if (unit.getAutoRefundId() != null) {
            return unit.getAutoRefundId();
        }
        PayOrderDO payOrder = payOrderMapper.selectById(order.getPayOrderId());
        if (payOrder == null) {
            return null;
        }
        PayAppDO payApp = payAppService.getApp(payOrder.getAppId());
        if (payApp == null) {
            return null;
        }
        String merchantRefundId = FLOW_REFUND_PREFIX + "-" + orderId + "-" + unitId + "-" + flowTime.toLocalDate() + "-" + IdUtil.getSnowflakeNextIdStr();
        Long payRefundId = payRefundApi.createRefund(new PayRefundCreateReqDTO()
                .setAppKey(payApp.getAppKey())
                .setUserIp(ServletUtils.getClientIP())
                .setUserId(order.getUserId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setMerchantOrderId(payOrder.getMerchantOrderId())
                .setMerchantRefundId(merchantRefundId)
                .setReason("流单自动原路退款")
                .setPrice(unit.getUnitAmount().multiply(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP).intValue())
                .setNeedAudit(Boolean.FALSE));
        orderUnitMapper.updateById(OrderUnitDO.builder()
                .id(unitId)
                .autoRefundStatus("PROCESSING")
                .autoRefundId(payRefundId)
                .build());
        orderOperateLogMapper.insert(OrderOperateLogDO.builder()
                .orderId(orderId)
                .unitId(unitId)
                .operateType("AUTO_FLOW_REFUND")
                .operateRole("SYSTEM")
                .operateBy(0L)
                .beforeStatus(unit.getStatus())
                .afterStatus(unit.getStatus())
                .remark("流单自动发起原路退款")
                .operateTime(LocalDateTime.now())
                .build());
        return payRefundId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRefundCallback(Long payRefundId, String merchantRefundId, Integer status, String channelErrorMsg) {
        if (merchantRefundId == null || !merchantRefundId.startsWith(FLOW_REFUND_PREFIX)) {
            return;
        }
        OrderUnitDO unit = orderUnitMapper.selectOne(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getAutoRefundId, payRefundId)
                .last("LIMIT 1"));
        if (unit == null) {
            return;
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (PayRefundStatusEnum.isSuccess(status)) {
            orderUnitMapper.updateById(OrderUnitDO.builder()
                    .id(unit.getId())
                    .status("REFUNDED")
                    .autoRefundStatus("SUCCESS")
                    .build());
            messagePushDispatchService.dispatchSingleIdempotent("lb_order_flow_refunded", "流单退款结果通知",
                    "ORDER_FLOW_REFUND", unit.getId(), order.getUserId(), "流单退款成功通知",
                    "lb_order_flow_refunded:" + unit.getId() + ":" + payRefundId);
            return;
        }
        if (PayRefundStatusEnum.isFailure(status)) {
            orderUnitMapper.updateById(OrderUnitDO.builder()
                    .id(unit.getId())
                    .autoRefundStatus("FAILED")
                    .flowReason(channelErrorMsg)
                    .build());
        }
    }
}
