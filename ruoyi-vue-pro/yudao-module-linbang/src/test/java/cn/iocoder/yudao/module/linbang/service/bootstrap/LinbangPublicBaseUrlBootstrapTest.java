package cn.iocoder.yudao.module.linbang.service.bootstrap;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileConfigDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileConfigMapper;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.framework.file.core.client.local.LocalFileClientConfig;
import cn.iocoder.yudao.module.infra.service.file.FileConfigService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LinbangPublicBaseUrlBootstrapTest extends BaseMockitoUnitTest {

    @InjectMocks
    private LinbangPublicBaseUrlBootstrap bootstrap;
    @Mock
    private FileConfigMapper fileConfigMapper;
    @Mock
    private FileConfigService fileConfigService;
    @Mock
    private FileMapper fileMapper;

    @Test
    void runShouldSyncFilesWhenConfigDomainIsUnchanged() {
        FileConfigDO config = buildLocalConfig("https://files.example.com");
        FileDO file = new FileDO().setId(10L).setConfigId(1L).setPath("2026/test.png")
                .setUrl("https://old.example.com/admin-api/infra/file/1/get/2026/test.png");
        prepareBaseUrl();
        when(fileConfigMapper.selectByMaster()).thenReturn(config);
        when(fileMapper.selectListByConfigIdAfterId(1L, null, 500))
                .thenReturn(Collections.singletonList(file));
        when(fileMapper.updateBatch(anyCollection(), eq(500))).thenReturn(true);

        bootstrap.run(null);

        verify(fileConfigService, never()).updateFileConfig(org.mockito.ArgumentMatchers.any());
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<FileDO>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(fileMapper).updateBatch(captor.capture(), eq(500));
        FileDO update = captor.getValue().iterator().next();
        assertEquals("https://files.example.com/admin-api/infra/file/1/get/2026/test.png", update.getUrl());
    }

    @Test
    void runShouldResumeFileSyncAfterPreviousFailure() {
        FileConfigDO config = buildLocalConfig("https://old.example.com");
        FileDO file = new FileDO().setId(10L).setConfigId(1L).setPath("2026/test.png")
                .setUrl("https://old.example.com/admin-api/infra/file/1/get/2026/test.png");
        prepareBaseUrl();
        when(fileConfigMapper.selectByMaster()).thenReturn(config);
        when(fileMapper.selectListByConfigIdAfterId(1L, null, 500))
                .thenReturn(Collections.singletonList(file), Collections.singletonList(file));
        when(fileMapper.updateBatch(anyCollection(), eq(500)))
                .thenThrow(new IllegalStateException("first batch failed"))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> bootstrap.run(null));
        bootstrap.run(null);

        verify(fileConfigService, times(1)).updateFileConfig(org.mockito.ArgumentMatchers.any());
        verify(fileMapper, times(2)).updateBatch(anyCollection(), eq(500));
    }

    private void prepareBaseUrl() {
        ReflectionTestUtils.setField(bootstrap, "publicBaseUrl", "https://files.example.com");
        ReflectionTestUtils.setField(bootstrap, "serverPort", 48080);
    }

    private FileConfigDO buildLocalConfig(String domain) {
        LocalFileClientConfig clientConfig = new LocalFileClientConfig();
        clientConfig.setBasePath("D:/files");
        clientConfig.setDomain(domain);
        return FileConfigDO.builder()
                .id(1L)
                .name("local")
                .storage(10)
                .master(true)
                .config(clientConfig)
                .build();
    }
}
