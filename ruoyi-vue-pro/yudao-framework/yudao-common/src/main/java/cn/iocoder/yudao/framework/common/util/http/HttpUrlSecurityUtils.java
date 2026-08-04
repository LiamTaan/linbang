package cn.iocoder.yudao.framework.common.util.http;

import java.net.InetAddress;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Validation helpers for server-side HTTP destinations.
 */
public final class HttpUrlSecurityUtils {

    private HttpUrlSecurityUtils() {
    }

    /**
     * Validates an HTTPS URL and rejects destinations that resolve to non-public address space.
     *
     * @param value URL to validate
     * @return normalized ASCII URL
     * @throws IllegalArgumentException when the URL is not a public HTTPS destination
     */
    public static String validatePublicHttpsUrl(String value) {
        return resolvePublicHttpsUrl(value).getUrl();
    }

    /**
     * Validates and resolves an HTTPS URL so the caller can pin the exact public addresses for the connection.
     */
    public static ResolvedUrl resolvePublicHttpsUrl(String value) {
        if (value == null) {
            throw new IllegalArgumentException("URL must not be null");
        }
        String url = value.trim();
        if (url.isEmpty() || containsControlCharacter(url) || url.indexOf('\\') >= 0) {
            throw new IllegalArgumentException("URL contains invalid characters");
        }

        URI uri;
        try {
            uri = URI.create(url);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("URL is malformed", ex);
        }
        if (uri.isOpaque() || !"https".equalsIgnoreCase(uri.getScheme()) || uri.getUserInfo() != null
                || uri.getFragment() != null || uri.getPort() == 0 || uri.getPort() < -1 || uri.getPort() > 65535) {
            throw new IllegalArgumentException("Only public HTTPS URLs without credentials or fragments are allowed");
        }

        String host = normalizeHost(uri.getHost());
        if (host.isEmpty() || "localhost".equals(host) || host.endsWith(".localhost")) {
            throw new IllegalArgumentException("URL host is not allowed");
        }
        InetAddress[] addresses;
        try {
            addresses = InetAddress.getAllByName(host);
        } catch (Exception ex) {
            throw new IllegalArgumentException("URL host cannot be resolved", ex);
        }
        if (addresses.length == 0) {
            throw new IllegalArgumentException("URL host cannot be resolved");
        }
        for (InetAddress address : addresses) {
            if (!isPublicAddress(address)) {
                throw new IllegalArgumentException("URL host resolves to a non-public address");
            }
        }
        return new ResolvedUrl(uri.toASCIIString(), host,
                Collections.unmodifiableList(Arrays.asList(addresses.clone())));
    }

    public static boolean isPublicAddress(InetAddress address) {
        if (address == null || address.isAnyLocalAddress() || address.isLoopbackAddress()
                || address.isLinkLocalAddress() || address.isSiteLocalAddress() || address.isMulticastAddress()) {
            return false;
        }
        byte[] bytes = address.getAddress();
        if (bytes.length == 4) {
            return isPublicIpv4(bytes);
        }
        if (bytes.length != 16 || (bytes[0] & 0xfe) == 0xfc) {
            return false;
        }
        if (isIpv4MappedAddress(bytes)) {
            return isPublicIpv4(new byte[]{bytes[12], bytes[13], bytes[14], bytes[15]});
        }
        if (isIpv4CompatibleAddress(bytes)) {
            return false;
        }
        int first = bytes[0] & 0xff;
        int second = bytes[1] & 0xff;
        int third = bytes[2] & 0xff;
        int fourth = bytes[3] & 0xff;
        // Public IPv6 unicast space is currently allocated from 2000::/3.
        if ((first & 0xe0) != 0x20) {
            return false;
        }
        // Block translation, discard, transition, benchmarking, ORCHID, and documentation prefixes.
        return !(first == 0x00 && second == 0x64 && third == 0xff && fourth == 0x9b)
                && !(first == 0x01 && second == 0x00 && third == 0x00 && fourth == 0x00)
                && !(first == 0x20 && second == 0x01 && third == 0x00 && fourth == 0x00)
                && !(first == 0x20 && second == 0x01 && third == 0x00 && fourth == 0x02)
                && !(first == 0x20 && second == 0x01 && third == 0x0d && fourth == 0xb8)
                && !(first == 0x20 && second == 0x01 && third == 0x00 && (fourth & 0xf0) == 0x10)
                && !(first == 0x20 && second == 0x01 && third == 0x00 && (fourth & 0xf0) == 0x20)
                && !(first == 0x20 && second == 0x02);
    }

    private static String normalizeHost(String host) {
        if (host == null) {
            return "";
        }
        String normalized = host.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("[") && normalized.endsWith("]")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }
        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private static boolean containsControlCharacter(String value) {
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch <= 0x1f || ch == 0x7f) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPublicIpv4(byte[] bytes) {
        int first = bytes[0] & 0xff;
        int second = bytes[1] & 0xff;
        int third = bytes[2] & 0xff;
        return first != 0 && first < 224
                && !(first == 100 && second >= 64 && second <= 127)
                && !(first == 192 && second == 0 && (third == 0 || third == 2))
                && !(first == 192 && second == 88 && third == 99)
                && !(first == 198 && (second == 18 || second == 19))
                && !(first == 198 && second == 51 && third == 100)
                && !(first == 203 && second == 0 && third == 113);
    }

    private static boolean isIpv4MappedAddress(byte[] bytes) {
        for (int i = 0; i < 10; i++) {
            if (bytes[i] != 0) {
                return false;
            }
        }
        return (bytes[10] & 0xff) == 0xff && (bytes[11] & 0xff) == 0xff;
    }

    private static boolean isIpv4CompatibleAddress(byte[] bytes) {
        for (int i = 0; i < 12; i++) {
            if (bytes[i] != 0) {
                return false;
            }
        }
        return true;
    }

    public static final class ResolvedUrl {

        private final String url;
        private final String host;
        private final List<InetAddress> addresses;

        private ResolvedUrl(String url, String host, List<InetAddress> addresses) {
            this.url = url;
            this.host = host;
            this.addresses = addresses;
        }

        public String getUrl() {
            return url;
        }

        public String getHost() {
            return host;
        }

        public List<InetAddress> getAddresses() {
            return addresses;
        }
    }

}
