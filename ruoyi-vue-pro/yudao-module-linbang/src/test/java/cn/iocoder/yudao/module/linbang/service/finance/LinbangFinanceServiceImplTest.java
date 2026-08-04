package cn.iocoder.yudao.module.linbang.service.finance;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletflow.WalletFlowDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletaccount.WalletAccountMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletflow.WalletFlowMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinbangFinanceServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LinbangFinanceServiceImpl service;

    @Mock
    private WalletAccountMapper walletAccountMapper;
    @Mock
    private WalletFlowMapper walletFlowMapper;

    @Test
    void handleRefundSuccess_isIdempotentAfterRefundFlowExists() {
        Long walletId = 10L;
        Long refundId = 20L;
        WalletAccountDO wallet = WalletAccountDO.builder()
                .id(walletId)
                .userId(1L)
                .roleCode("USER")
                .escrowAmount(new BigDecimal("100.00"))
                .totalAmount(new BigDecimal("100.00"))
                .build();
        OrderInfoDO order = OrderInfoDO.builder().id(30L).userId(1L).build();

        when(walletAccountMapper.selectByUserIdAndRoleCode(1L, "USER")).thenReturn(wallet);
        doReturn(wallet).when(walletAccountMapper)
                .selectOneForUpdate(any(SFunction.class), eq(walletId));
        when(walletFlowMapper.selectOne(any(LambdaQueryWrapperX.class)))
                .thenReturn(WalletFlowDO.builder().id(40L).build());

        service.handleRefundSuccess(order, null, new BigDecimal("10.00"), refundId);

        verify(walletAccountMapper, never()).updateById(any(WalletAccountDO.class));
        verify(walletFlowMapper, never()).insert(any(WalletFlowDO.class));
    }
}
