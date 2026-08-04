package cn.iocoder.yudao.module.linbang.service.sensitiveword.ocr;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageOcrServiceImplTest {

    @Test
    void validateRemoteEndpointShouldOnlyAllowPublicHttpsTargets() throws Exception {
        assertEquals("https://8.8.8.8/ocr", ImageOcrServiceImpl.validateRemoteEndpoint("https://8.8.8.8/ocr"));

        assertThrows(Exception.class, () -> ImageOcrServiceImpl.validateRemoteEndpoint("http://8.8.8.8/ocr"));
        assertThrows(Exception.class, () -> ImageOcrServiceImpl.validateRemoteEndpoint("https://127.0.0.1/ocr"));
        assertThrows(Exception.class, () -> ImageOcrServiceImpl.validateRemoteEndpoint("https://10.0.0.1/ocr"));
        assertThrows(Exception.class, () -> ImageOcrServiceImpl.validateRemoteEndpoint("https://169.254.169.254/ocr"));
        assertThrows(Exception.class, () -> ImageOcrServiceImpl.validateRemoteEndpoint("https://100.64.0.1/ocr"));
        assertThrows(Exception.class, () -> ImageOcrServiceImpl.validateRemoteEndpoint("https://[fc00::1]/ocr"));
    }

    @Test
    void reservedAndDocumentationAddressesShouldNotBePublic() throws Exception {
        assertFalse(ImageOcrServiceImpl.isPublicAddress(InetAddress.getByName("192.0.2.1")));
        assertFalse(ImageOcrServiceImpl.isPublicAddress(InetAddress.getByName("198.51.100.1")));
        assertFalse(ImageOcrServiceImpl.isPublicAddress(InetAddress.getByName("203.0.113.1")));
        assertFalse(ImageOcrServiceImpl.isPublicAddress(InetAddress.getByName("2001:db8::1")));
        assertFalse(ImageOcrServiceImpl.isPublicAddress(InetAddress.getByName("2002:0a00:0001::1")));
        assertTrue(ImageOcrServiceImpl.isPublicAddress(InetAddress.getByName("8.8.8.8")));
        assertTrue(ImageOcrServiceImpl.isPublicAddress(InetAddress.getByName("2001:4860:4860::8888")));
    }
}
