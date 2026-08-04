package cn.iocoder.yudao.framework.apilog.core.filter;

import cn.iocoder.yudao.framework.apilog.core.util.ApiLogSanitizer;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiAccessLogFilterTest {

    @Test
    void sanitizeJson_removesSensitiveFieldsCaseInsensitively() {
        String payload = "{\"mobile\":\"13800138000\",\"smsCode\":\"123456\","
                + "\"nested\":{\"ACCESS_TOKEN\":\"secret-token\",\"name\":\"visible\"}}";

        String result = ReflectionTestUtils.invokeMethod(ApiAccessLogFilter.class,
                "sanitizeJson", payload, new String[0]);

        assertFalse(result.contains("13800138000"));
        assertFalse(result.contains("123456"));
        assertFalse(result.contains("secret-token"));
        assertTrue(result.contains("visible"));
    }

    @Test
    void sanitizeMap_doesNotMutateInputAndHonorsCustomKeys() {
        Map<String, String> input = new LinkedHashMap<>();
        input.put("Password", "secret");
        input.put("customSecret", "custom-secret");
        input.put("pageNo", "1");

        String result = ReflectionTestUtils.invokeMethod(ApiAccessLogFilter.class,
                "sanitizeMap", input, new String[]{"customSecret"});

        assertEquals(3, input.size());
        assertFalse(result.contains("secret"));
        assertTrue(result.contains("pageNo"));
    }

    @Test
    void sanitizeJson_omitsMalformedPayload() {
        String result = ReflectionTestUtils.invokeMethod(ApiAccessLogFilter.class,
                "sanitizeJson", "{\"password\":", new String[0]);

        assertNull(result);
    }

    @Test
    void buildRequestParams_removesSensitiveQueryAndBodyFields() {
        Map<String, String> query = new LinkedHashMap<>();
        query.put("authorization", "Bearer secret-token");
        query.put("pageNo", "1");

        String result = ApiLogSanitizer.buildRequestParams(query,
                "{\"bank_card_no\":\"6222000000000000\",\"content\":\"private\",\"visible\":\"yes\"}", null);

        assertFalse(result.contains("secret-token"));
        assertFalse(result.contains("6222000000000000"));
        assertFalse(result.contains("private"));
        assertTrue(result.contains("pageNo"));
        assertTrue(result.contains("visible"));
    }

}
