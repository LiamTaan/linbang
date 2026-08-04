package cn.iocoder.yudao.framework.common.util.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link HttpUtils} 的单元测试
 */
public class HttpUtilsTest {

    private HttpServer server;

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Test
    public void testEncodeUrlPath() {
        // 准备参数
        String path = "avatar/中文 100%+文件.jpg";

        // 调用
        String result = HttpUtils.encodeUrlPath(path);

        // 断言
        assertEquals("avatar/%E4%B8%AD%E6%96%87%20100%25+%E6%96%87%E4%BB%B6.jpg", result);
    }

    @Test
    public void testDecodeUrlPath() {
        // 准备参数：+ 是路径字符，不应该按 query parameter 语义解码为空格
        String path = "avatar/%E4%B8%AD%E6%96%87%20100%25+%E6%96%87%E4%BB%B6.jpg";

        // 调用
        String result = HttpUtils.decodeUrlPath(path);

        // 断言
        assertEquals("avatar/中文 100%+文件.jpg", result);
    }

    @Test
    public void testRemoveUrlPathQueryAndFragment() {
        assertEquals("avatar/test.jpg", HttpUtils.removeUrlPathQueryAndFragment("avatar/test.jpg?token=1#preview"));
        assertEquals("avatar/test.jpg", HttpUtils.removeUrlPathQueryAndFragment("avatar/test.jpg#preview?token=1"));
    }

    @Test
    public void testReplaceUrlQuery_replace() {
        // 准备参数
        String url = "https://www.iocoder.cn/path?a=1&b=2";
        // 调用
        String result = HttpUtils.replaceUrlQuery(url, "a", "3");
        // 断言：被替换的 key 会移到末尾，原顺序的其它参数保留
        assertEquals("https://www.iocoder.cn/path?b=2&a=3", result);
    }

    @Test
    public void testReplaceUrlQuery_add() {
        // 准备参数
        String url = "https://www.iocoder.cn/path?a=1";
        // 调用
        String result = HttpUtils.replaceUrlQuery(url, "b", "2");
        // 断言
        assertEquals("https://www.iocoder.cn/path?a=1&b=2", result);
    }

    @Test
    public void testReplaceUrlQuery_noQuery() {
        // 准备参数：原 URL 没有 query
        String url = "https://www.iocoder.cn/path";
        // 调用
        String result = HttpUtils.replaceUrlQuery(url, "a", "1");
        // 断言
        assertEquals("https://www.iocoder.cn/path?a=1", result);
    }

    @Test
    public void testReplaceUrlQuery_emptyValue() {
        // 准备参数：value 为空字符串
        String url = "https://www.iocoder.cn/path?a=1";
        // 调用
        String result = HttpUtils.replaceUrlQuery(url, "a", "");
        // 断言：保留 key，value 为空
        assertEquals("https://www.iocoder.cn/path?a=", result);
    }

    @Test
    public void testGet_doesNotFollowRedirects() throws IOException {
        AtomicInteger finalRequestCount = new AtomicInteger();
        server = createServer();
        server.createContext("/redirect", exchange -> {
            exchange.getResponseHeaders().add("Location", "/final");
            writeResponse(exchange, 302, "redirect");
        });
        server.createContext("/final", exchange -> {
            finalRequestCount.incrementAndGet();
            writeResponse(exchange, 200, "final");
        });
        server.start();

        String body = HttpUtils.get(serverUrl("/redirect"), Collections.emptyMap());

        assertEquals("redirect", body);
        assertEquals(0, finalRequestCount.get());
    }

    @Test
    public void testGet_rejectsOversizedResponse() throws IOException {
        server = createServer();
        server.createContext("/large", exchange -> {
            byte[] body = new byte[2 * 1024 * 1024 + 1];
            exchange.sendResponseHeaders(200, body.length);
            try {
                exchange.getResponseBody().write(body);
            } catch (IOException ignored) {
                // The client can close as soon as it validates Content-Length.
            } finally {
                exchange.close();
            }
        });
        server.start();

        assertThrows(IllegalStateException.class,
                () -> HttpUtils.get(serverUrl("/large"), Collections.emptyMap()));
    }

    @Test
    public void testPost_rejectsCallerSpecificOversizedResponse() throws IOException {
        server = createServer();
        server.createContext("/large-post", exchange -> writeResponse(exchange, 200, "12345"));
        server.start();

        assertThrows(IllegalStateException.class, () -> HttpUtils.post(serverUrl("/large-post"),
                Collections.emptyMap(), "{}", 4));
        assertThrows(IllegalArgumentException.class, () -> HttpUtils.post(serverUrl("/large-post"),
                Collections.emptyMap(), "{}", 0));
    }

    @Test
    public void testCreatePinnedDns_onlyResolvesValidatedHost() throws Exception {
        HttpUrlSecurityUtils.ResolvedUrl resolved =
                HttpUrlSecurityUtils.resolvePublicHttpsUrl("https://8.8.8.8/callback");

        assertEquals(resolved.getAddresses(), HttpUtils.createPinnedDns(resolved).lookup("8.8.8.8"));
        assertEquals(resolved.getAddresses(), HttpUtils.createPinnedDns(resolved).lookup("8.8.8.8."));
        assertThrows(IOException.class,
                () -> HttpUtils.createPinnedDns(resolved).lookup("localhost"));
    }

    private HttpServer createServer() throws IOException {
        return HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    }

    private String serverUrl(String path) {
        return "http://127.0.0.1:" + server.getAddress().getPort() + path;
    }

    private static void writeResponse(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

}
