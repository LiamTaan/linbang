package cn.iocoder.yudao.framework.common.util.http;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpUrlSecurityUtilsTest {

    @Test
    void validatePublicHttpsUrl_allowsPublicTargets() throws Exception {
        assertEquals("https://8.8.8.8/callback",
                HttpUrlSecurityUtils.validatePublicHttpsUrl("https://8.8.8.8/callback"));
        assertEquals("https://[2001:4860:4860::8888]/callback",
                HttpUrlSecurityUtils.validatePublicHttpsUrl("https://[2001:4860:4860::8888]/callback"));

        HttpUrlSecurityUtils.ResolvedUrl resolved =
                HttpUrlSecurityUtils.resolvePublicHttpsUrl("https://8.8.8.8/callback");
        assertEquals("8.8.8.8", resolved.getHost());
        assertEquals(InetAddress.getByName("8.8.8.8"), resolved.getAddresses().get(0));
    }

    @Test
    void validatePublicHttpsUrl_rejectsUnsafeTargets() {
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("http://8.8.8.8/callback"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://user:pass@8.8.8.8/callback"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://8.8.8.8/callback#fragment"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://localhost/callback"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://127.0.0.1/callback"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://2130706433/callback"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://10.0.0.1/callback"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://169.254.169.254/latest/meta-data"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://[fc00::1]/callback"));
        assertThrows(IllegalArgumentException.class,
                () -> HttpUrlSecurityUtils.validatePublicHttpsUrl("https://8.8.8.8/callback\r\nX-Test: value"));
    }

    @Test
    void isPublicAddress_rejectsReservedRanges() throws Exception {
        assertFalse(HttpUrlSecurityUtils.isPublicAddress(InetAddress.getByName("192.0.2.1")));
        assertFalse(HttpUrlSecurityUtils.isPublicAddress(InetAddress.getByName("198.51.100.1")));
        assertFalse(HttpUrlSecurityUtils.isPublicAddress(InetAddress.getByName("203.0.113.1")));
        assertFalse(HttpUrlSecurityUtils.isPublicAddress(InetAddress.getByName("2001:db8::1")));
        assertFalse(HttpUrlSecurityUtils.isPublicAddress(InetAddress.getByName("2002:0a00:0001::1")));
        assertTrue(HttpUrlSecurityUtils.isPublicAddress(InetAddress.getByName("8.8.8.8")));
        assertTrue(HttpUrlSecurityUtils.isPublicAddress(InetAddress.getByName("2001:4860:4860::8888")));
    }

}
