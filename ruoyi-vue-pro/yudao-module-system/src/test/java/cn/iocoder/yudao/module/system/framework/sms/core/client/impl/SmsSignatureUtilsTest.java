package cn.iocoder.yudao.module.system.framework.sms.core.client.impl;

import cn.hutool.core.util.HexUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SmsSignatureUtilsTest {

    private static final String CONTENT = "The quick brown fox jumps over the lazy dog";

    @Test
    void testHmacSha1() {
        assertEquals("de7c9b85b8b78aa6bc8a7a36f70a90701c9db4d9",
                HexUtil.encodeHexStr(SmsSignatureUtils.hmacSha1("key", CONTENT)));
    }

    @Test
    void testHmacSha256() {
        assertEquals("f7bc83f430538424b13298e6aa6fb143ef4d59a14946175997479dbc2d1a3cd8",
                SmsSignatureUtils.hmacSha256Hex("key", CONTENT));
    }

}
