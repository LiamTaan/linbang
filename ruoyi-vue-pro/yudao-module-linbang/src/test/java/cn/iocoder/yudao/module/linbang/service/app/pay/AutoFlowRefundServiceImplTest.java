package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.pay.api.refund.PayRefundApi;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.order.PayOrderDO;
import cn.iocoder.yudao.module.pay.dal.dataobject.refund.PayRefundDO;
import cn.iocoder.yudao.module.pay.dal.mysql.order.PayOrderMapper;
import cn.iocoder.yudao.module.pay.dal.mysql.refund.PayRefundMapper;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import cn.iocoder.yudao.module.pay.service.app.PayAppService;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AutoFlowRefundServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AutoFlowRefundServiceImpl service;

    @Mock
    private OrderInfoMapper orderInfoMapper;
    @Mock
    private OrderUnitMapper orderUnitMapper;
    @Mock
    private OrderOperateLogMapper orderOperateLogMapper;
    @Mock
    private PayOrderMapper payOrderMapper;
    @Mock
    private PayRefundMapper payRefundMapper;
    @Mock
    private PayAppService payAppService;
    @Mock
    private PayRefundApi payRefundApi;
    @Mock
    private MessagePushDispatchService messagePushDispatchService;
    @Mock
    private PlatformTransactionManager transactionManager;

    private final Long orderId = 10L;
    private final Long unitId = 20L;
    private final Long payRefundId = 30L;
    private final LocalDateTime flowTime = LocalDateTime.of(2026, 8, 3, 10, 0);

    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), OrderUnitDO.class);
    }

    @BeforeEach
    void setUpTransactionTemplate() {
        when(transactionManager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        ReflectionTestUtils.setField(service, "transactionTemplate", new TransactionTemplate(transactionManager));
    }

    @Test
    void createAutoRefund_recoversProcessingUnitWithoutLinkedRefund() {
        TestData data = mockPreparationData();
        when(payRefundMapper.selectByAppIdAndMerchantRefundId(eq(data.payApp.getId()), any())).thenReturn(null);
        when(payRefundApi.createRefund(any())).thenReturn(payRefundId);
        when(payRefundMapper.selectById(payRefundId)).thenReturn(null);
        doReturn(data.unit).when(orderUnitMapper).selectOneForUpdate(any(SFunction.class), eq(unitId));

        Long result = service.createAutoRefund(orderId, unitId, flowTime);

        assertEquals(payRefundId, result);
        verify(payRefundApi).createRefund(any());
        verify(orderUnitMapper).update(any(), any());
    }

    @Test
    void createAutoRefund_reconcilesTerminalRefundAfterLinking() {
        TestData data = mockPreparationData();
        String merchantRefundId = "LBAF-" + orderId + "-" + unitId + "-" + flowTime.toEpochSecond(java.time.ZoneOffset.UTC);
        PayRefundDO refund = PayRefundDO.builder()
                .id(payRefundId)
                .merchantOrderId(data.payOrder.getMerchantOrderId())
                .merchantRefundId(merchantRefundId)
                .refundPrice(1000)
                .status(PayRefundStatusEnum.SUCCESS.getStatus())
                .build();
        OrderUnitDO linkedUnit = OrderUnitDO.builder()
                .id(unitId)
                .orderId(orderId)
                .merchantId(50L)
                .unitAmount(new BigDecimal("10.00"))
                .status("FINISHED")
                .autoRefundStatus("PROCESSING")
                .autoRefundId(payRefundId)
                .flowTime(flowTime)
                .build();
        when(payRefundMapper.selectByAppIdAndMerchantRefundId(data.payApp.getId(), merchantRefundId)).thenReturn(refund);
        when(payRefundMapper.selectById(payRefundId)).thenReturn(refund);
        doReturn(data.unit).when(orderUnitMapper).selectOneForUpdate(any(SFunction.class), eq(unitId));
        when(orderUnitMapper.selectOneForUpdate(any(LambdaQueryWrapperX.class)))
                .thenReturn(data.unit, linkedUnit);
        when(orderUnitMapper.update(any(), any())).thenReturn(1);

        Long result = service.createAutoRefund(orderId, unitId, flowTime);

        assertEquals(payRefundId, result);
        verify(payRefundApi, never()).createRefund(any());
        verify(messagePushDispatchService).dispatchSingleIdempotent(
                eq("lb_order_flow_refunded"), any(), eq("ORDER_FLOW_REFUND"), eq(unitId), eq(data.order.getUserId()),
                any(), eq("lb_order_flow_refunded:" + unitId + ":" + payRefundId));
    }

    private TestData mockPreparationData() {
        OrderInfoDO order = OrderInfoDO.builder().id(orderId).userId(40L).payOrderId(60L).build();
        OrderUnitDO unit = OrderUnitDO.builder()
                .id(unitId)
                .orderId(orderId)
                .merchantId(50L)
                .unitAmount(new BigDecimal("10.00"))
                .status("FINISHED")
                .autoRefundStatus("PROCESSING")
                .flowTime(flowTime)
                .build();
        PayOrderDO payOrder = PayOrderDO.builder().id(60L).appId(70L).merchantOrderId("ORDER-60").build();
        PayAppDO payApp = PayAppDO.builder().id(70L).appKey("pay-app").build();
        when(orderInfoMapper.selectById(orderId)).thenReturn(order);
        org.mockito.Mockito.lenient().when(orderUnitMapper.selectOneForUpdate(any(LambdaQueryWrapperX.class))).thenReturn(unit);
        when(payOrderMapper.selectById(60L)).thenReturn(payOrder);
        when(payAppService.getApp(70L)).thenReturn(payApp);
        when(orderUnitMapper.update(any(), any())).thenReturn(1);
        return new TestData(order, unit, payOrder, payApp);
    }

    private static class TestData {
        private final OrderInfoDO order;
        private final OrderUnitDO unit;
        private final PayOrderDO payOrder;
        private final PayAppDO payApp;

        private TestData(OrderInfoDO order, OrderUnitDO unit, PayOrderDO payOrder, PayAppDO payApp) {
            this.order = order;
            this.unit = unit;
            this.payOrder = payOrder;
            this.payApp = payApp;
        }
    }
}
