package cn.iocoder.yudao.framework.common.util.http;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.SneakyThrows;
import okhttp3.Dns;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP 工具类
 *
 * @author 芋道源码
 */
public class HttpUtils {

    private static final int CONNECT_TIMEOUT_MILLIS = 5_000;
    private static final int READ_TIMEOUT_MILLIS = 10_000;
    private static final int MAX_RESPONSE_BODY_BYTES = 2 * 1024 * 1024;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final OkHttpClient PUBLIC_HTTPS_CLIENT = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
            .followRedirects(false)
            .followSslRedirects(false)
            .proxy(Proxy.NO_PROXY)
            .build();

    /**
     * 编码 URL 参数
     *
     * @param value 参数
     * @return 编码后的参数
     */
    @SneakyThrows
    public static String encodeUtf8(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    }

    /**
     * 解码 URL 参数（query parameter）
     * 注意：此方法会将 + 解码为空格，适用于 query parameter，不适用于 URL path
     *
     * @see #decodeUrlPath(String)
     * @param value 参数
     * @return 解码后的参数
     */
    @SneakyThrows
    public static String decodeUtf8(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
    }

    /**
     * 解码 URL 路径
     * 与 {@link #decodeUtf8(String)} 不同，此方法不会将 + 解码为空格，保持 + 为字面字符
     * 适用于 URL path 部分的解码
     *
     * @param path URL 路径
     * @return 解码后的路径
     */
    @SneakyThrows
    public static String decodeUrlPath(String path) {
        if (StrUtil.isEmpty(path)) {
            return path;
        }
        // 先将 + 替换为 %2B，避免被 URLDecoder 解码为空格
        String encoded = path.replace("+", "%2B");
        return URLDecoder.decode(encoded, StandardCharsets.UTF_8.name());
    }

    /**
     * 编码 URL 路径，按路径段编码，保留 / 分隔符
     *
     * @param path URL 路径，例如 20250602/xxx.pdf
     * @return 编码后的路径
     */
    public static String encodeUrlPath(String path) {
        if (StrUtil.isEmpty(path)) {
            return path;
        }
        String[] segments = path.split(StrUtil.SLASH, -1);
        StringBuilder result = new StringBuilder(path.length());
        for (int i = 0; i < segments.length; i++) {
            if (i > 0) {
                result.append(StrUtil.SLASH);
            }
            result.append(encodeUrlPathSegment(segments[i]));
        }
        return result.toString();
    }

    /**
     * 编码 URL 路径段
     *
     * @param segment URL 路径段
     * @return 编码后的路径段
     */
    public static String encodeUrlPathSegment(String segment) {
        return UriUtils.encodePathSegment(segment, StandardCharsets.UTF_8);
    }

    public static String removeUrlPathQueryAndFragment(String path) {
        if (StrUtil.isEmpty(path)) {
            return path;
        }
        int endIndex = path.length();
        int queryIndex = path.indexOf('?');
        if (queryIndex >= 0) {
            endIndex = queryIndex;
        }
        int fragmentIndex = path.indexOf('#');
        if (fragmentIndex >= 0 && fragmentIndex < endIndex) {
            endIndex = fragmentIndex;
        }
        return path.substring(0, endIndex);
    }

    public static String replaceUrlQuery(String url, String key, String value) {
        UrlBuilder builder = UrlBuilder.of(url, Charset.defaultCharset());
        // 先移除；再添加
        builder.getQuery().remove(key);
        builder.addQuery(key, value);
        return builder.build();
    }

    public static String removeUrlQuery(String url) {
        if (!StrUtil.contains(url, '?')) {
            return url;
        }
        UrlBuilder builder = UrlBuilder.of(url, Charset.defaultCharset());
        // 移除 query、fragment
        builder.setQuery(null);
        builder.setFragment(null);
        return builder.build();
    }

    /**
     * 拼接 URL
     *
     * copy from Spring Security OAuth2 的 AuthorizationEndpoint 类的 append 方法
     *
     * @param base 基础 URL
     * @param query 查询参数
     * @param keys query 的 key，对应的原本的 key 的映射。例如说 query 里有个 key 是 xx，实际它的 key 是 extra_xx，则通过 keys 里添加这个映射
     * @param fragment URL 的 fragment，即拼接到 # 中
     * @return 拼接后的 URL
     */
    public static String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {
        UriComponentsBuilder template = UriComponentsBuilder.newInstance();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
        URI redirectUri;
        try {
            // assume it's encoded to start with (if it came in over the wire)
            redirectUri = builder.build(true).toUri();
        } catch (Exception e) {
            // ... but allow client registrations to contain hard-coded non-encoded values
            redirectUri = builder.build().toUri();
            builder = UriComponentsBuilder.fromUri(redirectUri);
        }
        template.scheme(redirectUri.getScheme()).port(redirectUri.getPort()).host(redirectUri.getHost())
                .userInfo(redirectUri.getUserInfo()).path(redirectUri.getPath());

        if (fragment) {
            StringBuilder values = new StringBuilder();
            if (redirectUri.getFragment() != null) {
                String append = redirectUri.getFragment();
                values.append(append);
            }
            for (String key : query.keySet()) {
                if (values.length() > 0) {
                    values.append("&");
                }
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                values.append(name).append("={").append(key).append("}");
            }
            if (values.length() > 0) {
                template.fragment(values.toString());
            }
            UriComponents encoded = template.build().expand(query).encode();
            builder.fragment(encoded.getFragment());
        } else {
            for (String key : query.keySet()) {
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                template.queryParam(name, "{" + key + "}");
            }
            template.fragment(redirectUri.getFragment());
            UriComponents encoded = template.build().expand(query).encode();
            builder.query(encoded.getQuery());
        }
        return builder.build().toUriString();
    }

    public static String[] obtainBasicAuthorization(HttpServletRequest request) {
        String clientId;
        String clientSecret;
        // 先从 Header 中获取
        String authorization = request.getHeader("Authorization");
        authorization = StrUtil.subAfter(authorization, "Basic ", true);
        if (StringUtils.hasText(authorization)) {
            authorization = Base64.decodeStr(authorization);
            clientId = StrUtil.subBefore(authorization, ":", false);
            clientSecret = StrUtil.subAfter(authorization, ":", false);
            // 再从 Param 中获取
        } else {
            clientId = request.getParameter("client_id");
            clientSecret = request.getParameter("client_secret");
        }

        // 如果两者非空，则返回
        if (StrUtil.isNotEmpty(clientId) && StrUtil.isNotEmpty(clientSecret)) {
            return new String[]{clientId, clientSecret};
        }
        return null;
    }

    /**
     * HTTP post 请求，基于 {@link cn.hutool.http.HttpUtil} 实现
     *
     * 为什么要封装该方法，因为 HttpUtil 默认封装的方法，没有允许传递 headers 参数
     *
     * @param url URL
     * @param headers 请求头
     * @param requestBody 请求体
     * @return 请求结果
     */
    public static String post(String url, Map<String, String> headers, String requestBody) {
        return post(url, headers, requestBody, MAX_RESPONSE_BODY_BYTES);
    }

    /**
     * HTTP post request with a caller-specific response size limit.
     */
    public static String post(String url, Map<String, String> headers, String requestBody,
                              int maxResponseBodyBytes) {
        validateResponseBodyLimit(maxResponseBodyBytes);
        try (HttpResponse response = secureRequest(HttpRequest.post(url))
                .addHeaders(headers)
                .body(requestBody)
                .execute()) {
            return readLimitedBody(response, maxResponseBodyBytes);
        }
    }

    /**
     * Sends JSON to a public HTTPS destination while pinning the validated DNS result for this connection.
     * This prevents a second DNS lookup from rebinding the host to an internal address.
     */
    public static SecureHttpResponse postPublicHttps(String url, Map<String, String> headers, String requestBody,
                                                     int maxResponseBodyBytes) {
        return postPublicHttps(HttpUrlSecurityUtils.resolvePublicHttpsUrl(url), headers, requestBody,
                maxResponseBodyBytes);
    }

    public static SecureHttpResponse postPublicHttps(HttpUrlSecurityUtils.ResolvedUrl resolvedUrl,
                                                     Map<String, String> headers, String requestBody,
                                                     int maxResponseBodyBytes) {
        if (resolvedUrl == null) {
            throw new IllegalArgumentException("resolvedUrl must not be null");
        }
        validateResponseBodyLimit(maxResponseBodyBytes);
        Request.Builder requestBuilder = new Request.Builder().url(resolvedUrl.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.header(entry.getKey(), entry.getValue());
            }
        }
        RequestBody body = RequestBody.create(JSON_MEDIA_TYPE, StrUtil.nullToEmpty(requestBody));
        Request request = requestBuilder.post(body).build();
        OkHttpClient client = PUBLIC_HTTPS_CLIENT.newBuilder().dns(createPinnedDns(resolvedUrl)).build();
        try (Response response = client.newCall(request).execute()) {
            return new SecureHttpResponse(response.code(), readLimitedBody(response.body(), maxResponseBodyBytes));
        } catch (IOException ex) {
            throw new IllegalStateException("Public HTTPS request failed", ex);
        }
    }

    static Dns createPinnedDns(HttpUrlSecurityUtils.ResolvedUrl resolvedUrl) {
        return hostname -> {
            if (!normalizeDnsHost(hostname).equals(resolvedUrl.getHost())) {
                throw new UnknownHostException("Unexpected host during pinned HTTPS request");
            }
            return resolvedUrl.getAddresses();
        };
    }

    private static String normalizeDnsHost(String hostname) {
        String normalized = hostname == null ? "" : hostname.toLowerCase(Locale.ROOT);
        while (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    /**
     * HTTP get 请求，基于 {@link cn.hutool.http.HttpUtil} 实现
     *
     * 为什么要封装该方法，因为 HttpUtil 默认封装的方法，没有允许传递 headers 参数
     *
     * @param url URL
     * @param headers 请求头
     * @return 请求结果
     */
    public static String get(String url, Map<String, String> headers) {
        try (HttpResponse response = secureRequest(HttpRequest.get(url))
                .addHeaders(headers)
                .execute()) {
            return readLimitedBody(response);
        }
    }

    private static HttpRequest secureRequest(HttpRequest request) {
        return request.setConnectionTimeout(CONNECT_TIMEOUT_MILLIS)
                .setReadTimeout(READ_TIMEOUT_MILLIS)
                .setFollowRedirects(false);
    }

    private static String readLimitedBody(HttpResponse response) {
        return readLimitedBody(response, MAX_RESPONSE_BODY_BYTES);
    }

    private static String readLimitedBody(HttpResponse response, int maxResponseBodyBytes) {
        long contentLength = response.contentLength();
        if (contentLength > maxResponseBodyBytes) {
            throw new IllegalStateException("HTTP response body exceeds configured limit");
        }
        int initialCapacity = contentLength > 0 ? (int) Math.min(contentLength, 8192L) : 1024;
        try (InputStream input = response.bodyStream();
             ByteArrayOutputStream output = new ByteArrayOutputStream(initialCapacity)) {
            if (input == null) {
                return StrUtil.EMPTY;
            }
            byte[] buffer = new byte[8192];
            int total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > maxResponseBodyBytes) {
                    throw new IllegalStateException("HTTP response body exceeds configured limit");
                }
                output.write(buffer, 0, read);
            }
            return new String(output.toByteArray(), resolveResponseCharset(response));
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read HTTP response body", ex);
        }
    }

    private static String readLimitedBody(ResponseBody responseBody, int maxResponseBodyBytes) throws IOException {
        if (responseBody == null) {
            return StrUtil.EMPTY;
        }
        long contentLength = responseBody.contentLength();
        if (contentLength > maxResponseBodyBytes) {
            throw new IllegalStateException("HTTP response body exceeds configured limit");
        }
        Charset charset = responseBody.contentType() == null ? StandardCharsets.UTF_8
                : responseBody.contentType().charset(StandardCharsets.UTF_8);
        int initialCapacity = contentLength > 0 ? (int) Math.min(contentLength, 8192L) : 1024;
        try (InputStream input = responseBody.byteStream();
             ByteArrayOutputStream output = new ByteArrayOutputStream(initialCapacity)) {
            byte[] buffer = new byte[8192];
            int total = 0;
            int read;
            while ((read = input.read(buffer)) != -1) {
                total += read;
                if (total > maxResponseBodyBytes) {
                    throw new IllegalStateException("HTTP response body exceeds configured limit");
                }
                output.write(buffer, 0, read);
            }
            return new String(output.toByteArray(), charset);
        }
    }

    private static void validateResponseBodyLimit(int maxResponseBodyBytes) {
        if (maxResponseBodyBytes <= 0 || maxResponseBodyBytes > MAX_RESPONSE_BODY_BYTES) {
            throw new IllegalArgumentException("maxResponseBodyBytes must be between 1 and 2 MiB");
        }
    }

    private static Charset resolveResponseCharset(HttpResponse response) {
        if (StrUtil.isNotBlank(response.charset())) {
            try {
                return Charset.forName(response.charset());
            } catch (IllegalArgumentException ignored) {
                // Fall back to UTF-8 for malformed upstream charset declarations.
            }
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * WebSocket URL 切换成 HTTP URL：ws:// → http://；wss:// → https://；其它格式原样保留
     *
     * @param url 原始 URL
     * @return 切换协议后的 URL
     */
    public static String wsUrlToHttp(String url) {
        return StrUtil.startWithIgnoreCase(url, "ws") ? "http" + url.substring(2) : url;
    }

    public static final class SecureHttpResponse {

        private final int statusCode;
        private final String body;

        private SecureHttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getBody() {
            return body;
        }

        public boolean isSuccessful() {
            return statusCode >= 200 && statusCode < 300;
        }
    }

}
