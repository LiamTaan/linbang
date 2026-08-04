package cn.iocoder.yudao.module.pay.service.notify;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PayNotifyRouteHelperTest {

    private static final String ROUTE = "/app-api/linbang/pay/order/update-paid";

    @Test
    void matches_acceptsExactPathAndContextPath() {
        assertTrue(PayNotifyRouteHelper.matches("http://127.0.0.1:48080" + ROUTE, ROUTE));
        assertTrue(PayNotifyRouteHelper.matches("https://api.example.com/gateway" + ROUTE + "/", ROUTE));
    }

    @Test
    void matches_rejectsDeceptiveOrMalformedUrls() {
        assertFalse(PayNotifyRouteHelper.matches("https://evil.example/callback?next=" + ROUTE, ROUTE));
        assertFalse(PayNotifyRouteHelper.matches("https://evil.example/callback#" + ROUTE, ROUTE));
        assertFalse(PayNotifyRouteHelper.matches("https://user@evil.example" + ROUTE, ROUTE));
        assertFalse(PayNotifyRouteHelper.matches("javascript:" + ROUTE, ROUTE));
        assertFalse(PayNotifyRouteHelper.matches("not-a-url" + ROUTE, ROUTE));
    }

}
