package cn.iocoder.yudao.framework.encrypt;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * 各种 API 加解密的测试类：不是单测，而是方便大家生成密钥、加密、解密等操作。
 *
 * @author 芋道源码
 */
@SuppressWarnings("ConstantValue")
public class ApiEncryptTest {

    @Test
    public void testGenerateAsymmetric() {
        String asymmetricAlgorithm = AsymmetricAlgorithm.RSA.getValue();
//        String asymmetricAlgorithm = "SM2";
//        String asymmetricAlgorithm = SM4.ALGORITHM_NAME;
//        String asymmetricAlgorithm = SymmetricAlgorithm.AES.getValue();
        String requestClientKey = null;
        String requestServerKey = null;
        String responseClientKey = null;
        String responseServerKey = null;
        if (Objects.equals(asymmetricAlgorithm, AsymmetricAlgorithm.RSA.getValue())) {
            // 请求的密钥
            RSA requestRsa = SecureUtil.rsa();
            requestClientKey = requestRsa.getPublicKeyBase64();
            requestServerKey = requestRsa.getPrivateKeyBase64();
            // 响应的密钥
            RSA responseRsa = new RSA();
            responseClientKey = responseRsa.getPrivateKeyBase64();
            responseServerKey = responseRsa.getPublicKeyBase64();
        } else if (Objects.equals(asymmetricAlgorithm, SymmetricAlgorithm.AES.getValue())) {
            // 使用 16 位密钥，兼容未安装 Unlimited Strength Policy 的 JDK 8。
            // 请求的密钥（前后端密钥一致）
            requestClientKey = RandomUtil.randomNumbers(16);
            requestServerKey = requestClientKey;
            // 响应的密钥（前后端密钥一致）
            responseClientKey = RandomUtil.randomNumbers(16);
            responseServerKey = responseClientKey;
        }

        assertNotNull(requestClientKey);
        assertNotNull(requestServerKey);
        assertNotNull(responseClientKey);
        assertNotNull(responseServerKey);
    }

    @Test
    public void testEncrypt_aes() {
        String key = "5254911138989348";
        String body = "{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"password\": \"admin123\",\n" +
                "  \"uuid\": \"3acd87a09a4f48fb9118333780e94883\",\n" +
                "  \"code\": \"1024\"\n" +
                "}";
        String encrypt = SecureUtil.aes(StrUtil.utf8Bytes(key))
                .encryptBase64(body);
        assertNotEquals(body, encrypt);
        assertFalse(encrypt.contains("admin123"));
    }

    @Test
    public void testEncrypt_rsa() {
        String key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCls2rIpnGdYnLFgz1XU13GbNQ5DloyPpvW00FPGjqn5Z6JpK+kDtVlnkhwR87iRrE5Vf2WNqRX6vzbLSgveIQY8e8oqGCb829myjf1MuI+ZzN4ghf/7tEYhZJGPI9AbfxFqBUzm+kR3/HByAI22GLT96WM26QiMK8n3tIP/yiLswIDAQAB";
        String body = "{\n" +
                "  \"username\": \"admin\",\n" +
                "  \"password\": \"admin123\",\n" +
                "  \"uuid\": \"3acd87a09a4f48fb9118333780e94883\",\n" +
                "  \"code\": \"1024\"\n" +
                "}";
        String encrypt = SecureUtil.rsa(null, key)
                .encryptBase64(body, KeyType.PublicKey);
        assertNotEquals(body, encrypt);
        assertFalse(encrypt.contains("admin123"));
    }

}
