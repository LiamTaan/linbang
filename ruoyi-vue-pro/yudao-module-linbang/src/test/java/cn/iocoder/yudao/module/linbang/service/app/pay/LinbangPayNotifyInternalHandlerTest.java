package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.service.walletwithdraw.WalletWithdrawService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayTransferNotifyReqDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.notify.PayNotifyTaskDO;
import cn.iocoder.yudao.module.pay.enums.notify.PayNotifyTypeEnum;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class LinbangPayNotifyInternalHandlerTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LinbangPayNotifyInternalHandler handler;

    @Mock
    private AppLinbangPayOrderService appLinbangPayOrderService;
    @Mock
    private AppPayRefundService appPayRefundService;
    @Mock
    private WalletWithdrawService walletWithdrawService;

    @Test
    void supports_businessNotifyUrls() {
        assertThat(handler.supports(buildTask(PayNotifyTypeEnum.ORDER.getType(),
                "https://api.example.com/app-api/linbang/pay/order/update-paid"))).isTrue();
        assertThat(handler.supports(buildTask(PayNotifyTypeEnum.REFUND.getType(),
                "https://api.example.com/app-api/pay/refund/update-refunded"))).isTrue();
        assertThat(handler.supports(buildTask(PayNotifyTypeEnum.TRANSFER.getType(),
                "https://api.example.com/admin-api/wallet/withdraw/update-transferred"))).isTrue();
    }

    @Test
    void supports_legacyNotifyUrls() {
        assertThat(handler.supports(buildTask(PayNotifyTypeEnum.ORDER.getType(),
                "https://api.example.com/admin-api/pay/notify/order"))).isTrue();
        assertThat(handler.supports(buildTask(PayNotifyTypeEnum.REFUND.getType(),
                "https://api.example.com/admin-api/pay/notify/refund"))).isTrue();
        assertThat(handler.supports(buildTask(PayNotifyTypeEnum.TRANSFER.getType(),
                "https://api.example.com/admin-api/pay/notify/transfer"))).isTrue();
    }

    @Test
    void handle_dispatchesToOrderService() {
        PayOrderNotifyReqDTO request = new PayOrderNotifyReqDTO();

        CommonResult<?> result = handler.handle(buildTask(PayNotifyTypeEnum.ORDER.getType(),
                "https://api.example.com/admin-api/pay/notify/order"), request);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        verify(appLinbangPayOrderService).updatePaid(request);
    }

    @Test
    void handle_dispatchesToRefundService() {
        PayRefundNotifyReqDTO request = new PayRefundNotifyReqDTO();

        CommonResult<?> result = handler.handle(buildTask(PayNotifyTypeEnum.REFUND.getType(),
                "https://api.example.com/app-api/pay/refund/update-refunded"), request);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        verify(appPayRefundService).updateRefunded(request);
    }

    @Test
    void handle_dispatchesToTransferService() {
        PayTransferNotifyReqDTO request = new PayTransferNotifyReqDTO();

        CommonResult<?> result = handler.handle(buildTask(PayNotifyTypeEnum.TRANSFER.getType(),
                "https://api.example.com/admin-api/wallet/withdraw/update-transferred"), request);

        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        verify(walletWithdrawService).updateWalletWithdrawTransferred(request);
    }

    private PayNotifyTaskDO buildTask(Integer type, String notifyUrl) {
        PayNotifyTaskDO task = new PayNotifyTaskDO();
        task.setType(type);
        task.setNotifyUrl(notifyUrl);
        return task;
    }
}
