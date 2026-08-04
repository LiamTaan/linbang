package cn.iocoder.yudao.module.linbang.service.match;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantdispatchsetting.MerchantDispatchSettingDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantdispatchsetting.MerchantDispatchSettingMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MerchantDispatchSettingServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private MerchantDispatchSettingServiceImpl merchantDispatchSettingService;
    @Mock
    private MerchantDispatchSettingMapper merchantDispatchSettingMapper;

    @Test
    void getOrCreate_returnsConcurrentInsertAfterDuplicateKey() {
        MerchantDispatchSettingDO concurrent = MerchantDispatchSettingDO.builder()
                .id(8L).merchantId(20L).dispatchEnabled(Boolean.TRUE).build();
        when(merchantDispatchSettingMapper.selectByMerchantId(20L)).thenReturn(null);
        when(merchantDispatchSettingMapper.insert(any(MerchantDispatchSettingDO.class)))
                .thenThrow(new DuplicateKeyException("duplicate merchant setting"));
        when(merchantDispatchSettingMapper.selectByMerchantIdForUpdate(20L)).thenReturn(concurrent);

        assertSame(concurrent, merchantDispatchSettingService.getOrCreate(20L));
    }
}
