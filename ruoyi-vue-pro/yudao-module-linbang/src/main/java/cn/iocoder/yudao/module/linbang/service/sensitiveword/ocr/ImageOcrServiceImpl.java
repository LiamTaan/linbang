package cn.iocoder.yudao.module.linbang.service.sensitiveword.ocr;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUrlSecurityUtils;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageOcrServiceImpl implements ImageOcrService {

    private static final String PROVIDER_LOCAL_DISABLED = "LOCAL_DISABLED";
    private static final String PROVIDER_REMOTE_GENERIC = "REMOTE_GENERIC";
    private static final int MAX_RESPONSE_BYTES = 1024 * 1024;

    @Resource
    private ConfigService configService;

    @Override
    public OcrExtractResult extractText(byte[] imageBytes, String fileName, String contentType) {
        if (imageBytes == null || imageBytes.length == 0 || imageBytes.length > FileService.MAX_FILE_SIZE_BYTES) {
            return failure("OCR_IMAGE_INVALID");
        }
        String provider = getConfigValue(PlatformConfigKeyConstants.OCR_PROVIDER);
        if (StrUtil.isBlank(provider) || PROVIDER_LOCAL_DISABLED.equalsIgnoreCase(provider)) {
            return failure("OCR_PROVIDER_NOT_CONFIGURED");
        }
        if (PROVIDER_REMOTE_GENERIC.equalsIgnoreCase(provider)) {
            return callGenericRemoteProvider(imageBytes, fileName, contentType);
        }
        return failure("OCR_PROVIDER_UNSUPPORTED");
    }

    private OcrExtractResult callGenericRemoteProvider(byte[] imageBytes, String fileName, String contentType) {
        String endpointValue = getConfigValue(PlatformConfigKeyConstants.OCR_GENERIC_ENDPOINT);
        if (StrUtil.isBlank(endpointValue)) {
            return failure("OCR_ENDPOINT_NOT_CONFIGURED");
        }
        HttpUrlSecurityUtils.ResolvedUrl endpoint;
        try {
            endpoint = HttpUrlSecurityUtils.resolvePublicHttpsUrl(endpointValue);
        } catch (Exception ex) {
            return failure("OCR_ENDPOINT_INVALID");
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("fileName", StrUtil.maxLength(StrUtil.nullToEmpty(fileName), 255));
        payload.put("contentType", StrUtil.maxLength(StrUtil.nullToEmpty(contentType), 128));
        payload.put("imageBase64", Base64.encode(imageBytes));
        String apiKey = getConfigValue(PlatformConfigKeyConstants.OCR_GENERIC_API_KEY);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        if (StrUtil.isNotBlank(apiKey)) {
            headers.put("Authorization", "Bearer " + apiKey);
        }
        try {
            HttpUtils.SecureHttpResponse httpResponse = HttpUtils.postPublicHttps(endpoint, headers,
                    JsonUtils.toJsonString(payload), MAX_RESPONSE_BYTES);
            if (!httpResponse.isSuccessful()) {
                return failure("OCR_REMOTE_HTTP_ERROR");
            }
            String response = httpResponse.getBody();
            JsonNode root = JsonUtils.parseTree(response);
            if (root == null) {
                return failure("OCR_REMOTE_RESPONSE_INVALID");
            }
            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                return OcrExtractResult.builder()
                        .success(false)
                        .rawResponse(StrUtil.maxLength(response, 2000))
                        .failureReason("OCR_REMOTE_FAILED")
                        .build();
            }
            String text = firstNonBlank(JsonUtils.getText(root, "text"), JsonUtils.getText(root.path("data"), "text"));
            return OcrExtractResult.builder()
                    .success(true)
                    .text(StrUtil.nullToEmpty(text))
                    .rawResponse(StrUtil.maxLength(response, 2000))
                    .build();
        } catch (Exception ex) {
            return failure("OCR_REMOTE_EXCEPTION");
        }
    }

    private String getConfigValue(String key) {
        ConfigDO config = configService.getConfigByKey(key);
        return config == null ? null : config.getValue();
    }

    private String firstNonBlank(String primary, String secondary) {
        return StrUtil.isNotBlank(primary) ? primary : secondary;
    }

    static String validateRemoteEndpoint(String endpoint) throws Exception {
        return HttpUrlSecurityUtils.validatePublicHttpsUrl(endpoint);
    }

    static boolean isPublicAddress(InetAddress address) {
        return HttpUrlSecurityUtils.isPublicAddress(address);
    }

    private OcrExtractResult failure(String reason) {
        return OcrExtractResult.builder().success(false).failureReason(reason).build();
    }
}
