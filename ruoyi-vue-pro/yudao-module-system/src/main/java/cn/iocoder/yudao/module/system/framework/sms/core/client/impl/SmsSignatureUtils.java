package cn.iocoder.yudao.module.system.framework.sms.core.client.impl;

import cn.hutool.core.util.HexUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

final class SmsSignatureUtils {

    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String HMAC_SHA256 = "HmacSHA256";

    private SmsSignatureUtils() {
    }

    static byte[] hmacSha1(String key, String content) {
        return hmac(HMAC_SHA1, key.getBytes(StandardCharsets.UTF_8), content);
    }

    static byte[] hmacSha256(String key, String content) {
        return hmacSha256(key.getBytes(StandardCharsets.UTF_8), content);
    }

    static byte[] hmacSha256(byte[] key, String content) {
        return hmac(HMAC_SHA256, key, content);
    }

    static String hmacSha256Hex(String key, String content) {
        return HexUtil.encodeHexStr(hmacSha256(key, content));
    }

    private static byte[] hmac(String algorithm, byte[] key, String content) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            mac.init(new SecretKeySpec(key, algorithm));
            return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(algorithm + " 计算失败", e);
        }
    }

}
