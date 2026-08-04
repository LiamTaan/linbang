package cn.iocoder.yudao.module.infra.service.file;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.dal.mysql.file.FileMapper;
import cn.iocoder.yudao.module.infra.framework.file.core.client.FileClient;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileServiceFailureCompensationTest extends BaseMockitoUnitTest {

    @InjectMocks
    private FileServiceImpl service;
    @Mock
    private FileConfigService fileConfigService;
    @Mock
    private FileMapper fileMapper;
    @Mock
    private FileClient fileClient;

    @Test
    void createFileInfo_deletesUploadedObjectWhenMetadataInsertFails() throws Exception {
        byte[] content = ResourceUtil.readBytes("file/erweima.jpg");
        when(fileConfigService.getMasterFileClient()).thenReturn(fileClient);
        when(fileClient.getId()).thenReturn(10L);
        when(fileClient.upload(any(), anyString(), anyString())).thenReturn("https://cdn.example/test.jpg");
        when(fileMapper.insert(any(FileDO.class))).thenThrow(new IllegalStateException("database unavailable"));

        assertThrows(IllegalStateException.class,
                () -> service.createFileInfo(content, "test.jpg", "avatar", "image/jpeg"));

        verify(fileClient).delete(anyString());
    }

    @Test
    void cleanExpiredPendingUploads_continuesAfterObjectDeleteFailure() throws Exception {
        FileDO first = new FileDO().setId(1L).setConfigId(10L).setPath("one.jpg").setSize(-1L);
        FileDO second = new FileDO().setId(2L).setConfigId(10L).setPath("two.jpg").setSize(-1L);
        when(fileMapper.selectExpiredPendingUploads(any(LocalDateTime.class), isNull(), anyInt()))
                .thenReturn(Arrays.asList(first, second));
        when(fileConfigService.getFileClient(10L)).thenReturn(fileClient);
        doThrow(new IllegalStateException("delete failed")).when(fileClient).delete(".pending/one.jpg");
        when(fileMapper.deleteById(2L)).thenReturn(1);

        int cleaned = service.cleanExpiredPendingUploads();

        assertEquals(1, cleaned);
        verify(fileClient).delete(".pending/one.jpg");
        verify(fileClient).delete(".pending/two.jpg");
        verify(fileMapper).deleteById(2L);
    }

    @Test
    void deleteFileList_continuesAfterOneStorageFailure() throws Exception {
        FileDO first = new FileDO().setId(1L).setConfigId(10L).setPath("one.jpg").setSize(1L);
        FileDO second = new FileDO().setId(2L).setConfigId(10L).setPath("two.jpg").setSize(1L);
        when(fileMapper.selectByIds(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(first, second));
        when(fileConfigService.getFileClient(10L)).thenReturn(fileClient);
        doThrow(new IllegalStateException("delete failed")).when(fileClient).delete("one.jpg");

        assertThrows(IllegalStateException.class, () -> service.deleteFileList(Arrays.asList(1L, 2L)));

        verify(fileClient).delete("one.jpg");
        verify(fileClient).delete("two.jpg");
        verify(fileMapper).deleteById(2L);
    }

}
