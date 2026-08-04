package cn.iocoder.yudao.module.infra.controller.admin.file;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileControllerSecurityTest extends BaseMockitoUnitTest {

    @InjectMocks
    private FileController controller;

    @Mock
    private FileService fileService;
    @Mock
    private HttpServletRequest request;

    @Test
    void getFileContent_rejectsObjectsWithoutCompletedMetadata() throws Exception {
        when(request.getRequestURI()).thenReturn("/admin-api/infra/file/4/get/.pending/private.png");
        FileDO reservation = new FileDO().setConfigId(4L).setPath(".pending/private.png").setSize(-1024L);
        when(fileService.getFileByConfigIdAndPath(4L, ".pending/private.png")).thenReturn(reservation);

        ResponseEntity<byte[]> response = controller.getFileContent(request, 4L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(fileService, never()).getFileContent(anyLong(), anyString(), anyLong());
    }

    @Test
    void getFileContent_readsCompletedMetadataOnly() throws Exception {
        when(request.getRequestURI()).thenReturn("/admin-api/infra/file/4/get/public/file.png");
        FileDO file = new FileDO().setConfigId(4L).setPath("public/file.png")
                .setName("file.png").setType("image/png").setSize(3L);
        when(fileService.getFileByConfigIdAndPath(4L, "public/file.png")).thenReturn(file);
        when(fileService.getFileContent(eq(4L), eq("public/file.png"), anyLong()))
                .thenReturn(new byte[]{1, 2, 3});

        ResponseEntity<byte[]> response = controller.getFileContent(request, 4L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(new byte[]{1, 2, 3}, response.getBody());
    }

}
