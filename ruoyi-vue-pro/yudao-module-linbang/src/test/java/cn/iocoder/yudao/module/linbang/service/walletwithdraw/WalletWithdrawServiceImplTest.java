package cn.iocoder.yudao.module.linbang.service.walletwithdraw;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import cn.iocoder.yudao.module.linbang.service.finance.LinbangFinanceService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.pay.api.transfer.PayTransferApi;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferRespDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.app.PayAppDO;
import cn.iocoder.yudao.module.pay.enums.transfer.PayTransferStatusEnum;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletWithdrawServiceImplTest extends BaseMockitoUnitTest {

    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), WalletWithdrawDO.class);
    }

    @InjectMocks
    private WalletWithdrawServiceImpl service;

    @Mock
    private WalletWithdrawMapper walletWithdrawMapper;
    @Mock
    private WalletAccountMapper walletAccountMapper;
    @Mock
    private PayTransferApi payTransferApi;
    @Mock
    private LinbangFinanceService linbangFinanceService;
    @Mock
    private MessagePushDispatchService messagePushDispatchService;

    @Test
    void recoverTransferAfterSubmitException_linksAcceptedTransferAndKeepsFundsFrozen() {
        WalletWithdrawDO withdraw = buildWithdraw();
        PayAppDO payApp = PayAppDO.builder().id(2L).appKey("pay-app").build();
        PayTransferRespDTO transfer = new PayTransferRespDTO();
        transfer.setId(99L);
        transfer.setMerchantTransferId(String.valueOf(withdraw.getId()));
        transfer.setPrice(1000);
        transfer.setStatus(PayTransferStatusEnum.WAITING.getStatus());
        when(payTransferApi.getTransferByMerchantTransferId(payApp.getAppKey(), String.valueOf(withdraw.getId())))
                .thenReturn(transfer);
        when(walletWithdrawMapper.update(isNull(), any())).thenReturn(1);

        Long result = ReflectionTestUtils.invokeMethod(service, "recoverTransferAfterSubmitException",
                withdraw, payApp, new RuntimeException("submit timeout"));

        assertEquals(transfer.getId(), result);
        ArgumentCaptor<LambdaUpdateWrapper<WalletWithdrawDO>> wrapperCaptor = ArgumentCaptor.forClass(LambdaUpdateWrapper.class);
        verify(walletWithdrawMapper).update(isNull(), wrapperCaptor.capture());
        assertTrue(wrapperCaptor.getValue().getSqlSet().contains("pay_transfer_id"));
        verify(linbangFinanceService, never()).handleWithdrawTransferFailed(any(), any(), any());
        verify(walletAccountMapper, never()).selectById(any());
    }

    @Test
    void recoverTransferAfterSubmitException_reconcilesSuccessfulTransferAndNotifiesIdempotently() {
        WalletWithdrawDO withdraw = buildWithdraw();
        PayAppDO payApp = PayAppDO.builder().id(2L).appKey("pay-app").build();
        WalletAccountDO walletAccount = WalletAccountDO.builder().id(withdraw.getWalletAccountId()).build();
        PayTransferRespDTO transfer = new PayTransferRespDTO();
        transfer.setId(99L);
        transfer.setMerchantTransferId(String.valueOf(withdraw.getId()));
        transfer.setPrice(1000);
        transfer.setStatus(PayTransferStatusEnum.SUCCESS.getStatus());
        when(payTransferApi.getTransferByMerchantTransferId(payApp.getAppKey(), String.valueOf(withdraw.getId())))
                .thenReturn(transfer);
        when(walletAccountMapper.selectById(withdraw.getWalletAccountId())).thenReturn(walletAccount);
        when(walletWithdrawMapper.update(isNull(), any())).thenReturn(1);

        Long result = ReflectionTestUtils.invokeMethod(service, "recoverTransferAfterSubmitException",
                withdraw, payApp, new RuntimeException("submit timeout"));

        assertEquals(transfer.getId(), result);
        verify(linbangFinanceService).handleWithdrawTransferSuccess(walletAccount, withdraw.getId(), transfer);
        verify(messagePushDispatchService).dispatchSingleIdempotent(
                eq("lb_withdraw_arrived"), eq("提现到账通知"), eq("WITHDRAW"), eq(withdraw.getId()),
                eq(withdraw.getUserId()), eq("提现打款成功后通知申请人"),
                eq("lb_withdraw_arrived:" + withdraw.getId() + ":" + transfer.getId()));
    }

    @Test
    void recoverTransferAfterSubmitException_reconcilesClosedTransferAndNotifiesIdempotently() {
        WalletWithdrawDO withdraw = buildWithdraw();
        PayAppDO payApp = PayAppDO.builder().id(2L).appKey("pay-app").build();
        WalletAccountDO walletAccount = WalletAccountDO.builder().id(withdraw.getWalletAccountId()).build();
        PayTransferRespDTO transfer = new PayTransferRespDTO();
        transfer.setId(100L);
        transfer.setMerchantTransferId(String.valueOf(withdraw.getId()));
        transfer.setPrice(1000);
        transfer.setStatus(PayTransferStatusEnum.CLOSED.getStatus());
        when(payTransferApi.getTransferByMerchantTransferId(payApp.getAppKey(), String.valueOf(withdraw.getId())))
                .thenReturn(transfer);
        when(walletAccountMapper.selectById(withdraw.getWalletAccountId())).thenReturn(walletAccount);
        when(walletWithdrawMapper.update(isNull(), any())).thenReturn(1);

        Long result = ReflectionTestUtils.invokeMethod(service, "recoverTransferAfterSubmitException",
                withdraw, payApp, new RuntimeException("submit timeout"));

        assertEquals(transfer.getId(), result);
        verify(linbangFinanceService).handleWithdrawTransferFailed(walletAccount, withdraw.getId(), transfer);
        verify(messagePushDispatchService).dispatchSingleIdempotent(
                eq("lb_withdraw_failed"), eq("提现失败通知"), eq("WITHDRAW"), eq(withdraw.getId()),
                eq(withdraw.getUserId()), eq("提现打款失败后通知申请人"),
                eq("lb_withdraw_failed:" + withdraw.getId() + ":" + transfer.getId()));
    }

    @Test
    void recoverTransferAfterSubmitException_keepsFundsFrozenWhenLookupIsUncertain() {
        WalletWithdrawDO withdraw = buildWithdraw();
        PayAppDO payApp = PayAppDO.builder().id(2L).appKey("pay-app").build();
        when(payTransferApi.getTransferByMerchantTransferId(eq(payApp.getAppKey()), eq(String.valueOf(withdraw.getId()))))
                .thenThrow(new IllegalStateException("lookup timeout"));

        Long result = ReflectionTestUtils.invokeMethod(service, "recoverTransferAfterSubmitException",
                withdraw, payApp, new RuntimeException("submit timeout"));

        assertNull(result);
        ArgumentCaptor<LambdaUpdateWrapper<WalletWithdrawDO>> wrapperCaptor = ArgumentCaptor.forClass(LambdaUpdateWrapper.class);
        verify(walletWithdrawMapper).update(isNull(), wrapperCaptor.capture());
        assertTrue(wrapperCaptor.getValue().getSqlSet().contains("transfer_error_msg"));
        verify(linbangFinanceService, never()).handleWithdrawTransferFailed(any(), any(), any());
        verify(walletAccountMapper, never()).selectById(any());
    }

    @Test
    void recoverTransferAfterSubmitException_keepsFundsFrozenWhenTransferIsNotYetVisible() {
        WalletWithdrawDO withdraw = buildWithdraw();
        PayAppDO payApp = PayAppDO.builder().id(2L).appKey("pay-app").build();
        when(payTransferApi.getTransferByMerchantTransferId(payApp.getAppKey(), String.valueOf(withdraw.getId())))
                .thenReturn(null);

        Long result = ReflectionTestUtils.invokeMethod(service, "recoverTransferAfterSubmitException",
                withdraw, payApp, new RuntimeException("submit timeout"));

        assertNull(result);
        verify(linbangFinanceService, never()).handleWithdrawTransferFailed(any(), any(), any());
        verify(walletAccountMapper, never()).selectById(any());
        verify(walletWithdrawMapper).update(isNull(), any());
    }

    @Test
    void linkPayTransfer_rejectsConflictingAssociation() {
        WalletWithdrawDO withdraw = buildWithdraw();
        WalletWithdrawDO latest = buildWithdraw();
        latest.setPayTransferId(88L);
        when(walletWithdrawMapper.update(isNull(), any())).thenReturn(0);
        when(walletWithdrawMapper.selectById(withdraw.getId())).thenReturn(latest);

        assertThrows(IllegalStateException.class,
                () -> ReflectionTestUtils.invokeMethod(service, "linkPayTransfer", withdraw, 99L));

        verify(walletWithdrawMapper, times(2)).update(isNull(), any());
    }

    private WalletWithdrawDO buildWithdraw() {
        return WalletWithdrawDO.builder()
                .id(1L)
                .withdrawNo("WD-1")
                .userId(3L)
                .walletAccountId(4L)
                .bankCardId(5L)
                .realAmount(new BigDecimal("10.00"))
                .status("PROCESSING")
                .auditStatus("APPROVED")
                .build();
    }
}
