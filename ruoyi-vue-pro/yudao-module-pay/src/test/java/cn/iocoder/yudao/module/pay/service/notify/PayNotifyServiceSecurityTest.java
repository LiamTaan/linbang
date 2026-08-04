package cn.iocoder.yudao.module.pay.service.notify;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class PayNotifyServiceSecurityTest {

    @Test
    void sanitizeNotifyLogResponse_handlesNullAndDatabaseLimit() {
        assertEquals("", PayNotifyServiceImpl.sanitizeNotifyLogResponse(null));

        String response = repeat('x', 2048);
        assertEquals(1024, PayNotifyServiceImpl.sanitizeNotifyLogResponse(response).length());
    }

    @Test
    void summarizeNotifyException_doesNotPersistSecretBearingMessages() {
        String summary = PayNotifyServiceImpl.summarizeNotifyException(
                new IllegalStateException("request failed for https://callback.example?token=secret"));

        assertEquals("Notify invocation failed: IllegalStateException", summary);
        assertFalse(summary.contains("secret"));
    }

    private static String repeat(char value, int count) {
        StringBuilder result = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            result.append(value);
        }
        return result.toString();
    }

}
