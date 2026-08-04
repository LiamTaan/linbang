package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistrationLicenseCleanupServiceTest extends BaseMockitoUnitTest {

    @InjectMocks
    private RegistrationLicenseCleanupService service;
    @Mock
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Mock
    private FileService fileService;

    @Test
    void cleanExpiredOrphans_continuesAfterOneDeleteFailure() throws Exception {
        when(memberUserQualificationMapper.selectUnreferencedFileIds(anyString(),
                any(LocalDateTime.class), anyLong(), anyInt())).thenReturn(Arrays.asList(1L, 2L));
        doThrow(new IllegalStateException("storage unavailable")).when(fileService).deleteFile(1L);

        int cleaned = service.cleanExpiredOrphans();

        assertEquals(1, cleaned);
        verify(fileService).deleteFile(1L);
        verify(fileService).deleteFile(2L);
    }

}
