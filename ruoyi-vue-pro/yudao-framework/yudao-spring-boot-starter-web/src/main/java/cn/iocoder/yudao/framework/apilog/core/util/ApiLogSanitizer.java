package cn.iocoder.yudao.framework.apilog.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * API 日志请求、响应脱敏工具。
 */
@Slf4j
@UtilityClass
public class ApiLogSanitizer {

    private static final String[] SANITIZE_KEYS = new String[]{
            "password", "passwd", "pwd", "oldpassword", "newpassword", "confirmpassword",
            "token", "accesstoken", "refreshtoken", "authorization", "cookie", "sessionid",
            "secret", "clientsecret", "appsecret", "secretkey", "privatekey", "apikey", "credential",
            "code", "smscode", "codesms", "verifycode", "captcha", "phonecode", "logincode", "socialcode",
            "state", "socialstate", "paysign", "signature", "sign", "authcode",
            "bankcardno", "cardno", "accountno", "useraccount", "cvv", "idcard", "idcardno", "idno",
            "mobile", "phone", "reservedmobile", "contactmobile", "email", "openid",
            "username", "realname", "accountname", "tomails", "ccmails", "bccmails",
            "content", "messages", "templateparams"
    };
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static String buildRequestParams(Map<String, ?> query, String body, String[] sanitizeKeys) {
        Map<String, Object> requestParams = new LinkedHashMap<>();
        requestParams.put("query", sanitizeMap(query, sanitizeKeys));
        requestParams.put("body", sanitizeJson(body, sanitizeKeys));
        return JsonUtils.toJsonString(requestParams);
    }

    public static String sanitizeMap(Map<String, ?> map, String[] sanitizeKeys) {
        if (CollUtil.isEmpty(map)) {
            return null;
        }
        Map<String, Object> sanitized = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (!isSanitizeKey(entry.getKey(), sanitizeKeys)) {
                sanitized.put(entry.getKey(), entry.getValue());
            }
        }
        return JsonUtils.toJsonString(sanitized);
    }

    public static String sanitizeJson(String jsonString, String[] sanitizeKeys) {
        if (StrUtil.isEmpty(jsonString)) {
            return null;
        }
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(jsonString);
            sanitizeJsonNode(rootNode, sanitizeKeys);
            return JsonUtils.toJsonString(rootNode);
        } catch (Exception e) {
            log.warn("[sanitizeJson][内容脱敏失败，已省略原始内容，errorType({})]",
                    e.getClass().getSimpleName());
            return null;
        }
    }

    public static String sanitizeCommonResult(CommonResult<?> commonResult, String[] sanitizeKeys) {
        if (commonResult == null) {
            return null;
        }
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(JsonUtils.toJsonString(commonResult));
            sanitizeJsonNode(rootNode.get("data"), sanitizeKeys);
            return JsonUtils.toJsonString(rootNode);
        } catch (Exception e) {
            log.warn("[sanitizeCommonResult][响应内容脱敏失败，已省略原始内容，errorType({})]",
                    e.getClass().getSimpleName());
            return null;
        }
    }

    private static void sanitizeJsonNode(JsonNode node, String[] sanitizeKeys) {
        if (node == null) {
            return;
        }
        if (node.isArray()) {
            for (JsonNode childNode : node) {
                sanitizeJsonNode(childNode, sanitizeKeys);
            }
            return;
        }
        if (!node.isObject()) {
            return;
        }
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            if (isSanitizeKey(entry.getKey(), sanitizeKeys)) {
                iterator.remove();
                continue;
            }
            sanitizeJsonNode(entry.getValue(), sanitizeKeys);
        }
    }

    private static boolean isSanitizeKey(String key, String[] sanitizeKeys) {
        if (key == null) {
            return false;
        }
        String normalizedKey = normalizeSanitizeKey(key);
        for (String sanitizeKey : SANITIZE_KEYS) {
            if (normalizeSanitizeKey(sanitizeKey).equals(normalizedKey)) {
                return true;
            }
        }
        if (sanitizeKeys == null) {
            return false;
        }
        for (String sanitizeKey : sanitizeKeys) {
            if (sanitizeKey != null && normalizeSanitizeKey(sanitizeKey).equals(normalizedKey)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeSanitizeKey(String key) {
        return key.replace("_", "").replace("-", "").replace(".", "").toLowerCase(Locale.ROOT);
    }

}
