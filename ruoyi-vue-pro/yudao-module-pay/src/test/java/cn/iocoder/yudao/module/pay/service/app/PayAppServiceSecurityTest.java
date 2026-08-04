package cn.iocoder.yudao.module.pay.service.app;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.pay.controller.admin.app.vo.PayAppCreateReqVO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.dal.mysql.app.PayAppMapper;
import cn.iocoder.yudao.module.pay.service.notify.PayNotifyInternalHandler;
import cn.iocoder.yudao.module.pay.service.order.PayOrderService;
import cn.iocoder.yudao.module.pay.service.refund.PayRefundService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Collections;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertServiceException;
import static cn.iocoder.yudao.module.pay.enums.ErrorCodeConstants.APP_NOTIFY_URL_INVALID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PayAppServiceSecurityTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PayAppServiceImpl appService;

    @Mock
    private PayAppMapper appMapper;
    @Mock
    private PayOrderService orderService;
    @Mock
    private PayRefundService refundService;
    @Mock
    private ObjectProvider<PayNotifyInternalHandler> internalHandlerProvider;

    @Test
    void createApp_rejectsExternalPrivateNetworkCallback() {
        when(internalHandlerProvider.iterator())
                .thenReturn(Collections.<PayNotifyInternalHandler>emptyList().iterator());
        PayAppCreateReqVO request = buildRequest("https://127.0.0.1/private-callback");

        assertServiceException(() -> appService.createApp(request), APP_NOTIFY_URL_INVALID, "支付结果");
    }

    @Test
    void createApp_allowsRegisteredInternalCallback() {
        PayNotifyInternalHandler handler = org.mockito.Mockito.mock(PayNotifyInternalHandler.class);
        when(handler.supports(any())).thenReturn(true);
        when(internalHandlerProvider.iterator()).thenReturn(Collections.singletonList(handler).iterator());
        PayAppCreateReqVO request = buildRequest(
                "http://127.0.0.1:48080/app-api/linbang/pay/order/update-paid");

        appService.createApp(request);

        verify(appMapper).insert(any(PayAppDO.class));
    }

    private static PayAppCreateReqVO buildRequest(String orderNotifyUrl) {
        PayAppCreateReqVO request = new PayAppCreateReqVO();
        request.setAppKey("linbang-app");
        request.setName("LinBang");
        request.setStatus(0);
        request.setOrderNotifyUrl(orderNotifyUrl);
        request.setRefundNotifyUrl("https://8.8.8.8/refund-callback");
        return request;
    }

}
