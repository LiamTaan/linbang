package cn.iocoder.yudao.module.linbang.service.sensitiveword.ocr;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class ImageOcrServiceImpl implements ImageOcrService {

    private static final String PROVIDER_LOCAL_DISABLED = "LOCAL_DISABLED";
    private static final String PROVIDER_REMOTE_GENERIC = "REMOTE_GENERIC";

    @Resource
    private ConfigService configService;

    @Override
    public OcrExtractResult extractText(byte[] imageBytes, String fileName, String contentType) {
        String provider = getConfigValue(PlatformConfigKeyConstants.OCR_PROVIDER);
        if (StrUtil.isBlank(provider) || PROVIDER_LOCAL_DISABLED.equalsIgnoreCase(provider)) {
            return OcrExtractResult.builder()
                    .success(false)
                    .failureReason("OCR_PROVIDER_NOT_CONFIGURED")
                    .build();
        }
        if (PROVIDER_REMOTE_GENERIC.equalsIgnoreCase(provider)) {
            return callGenericRemoteProvider(imageBytes, fileName, contentType);
        }
        return OcrExtractResult.builder()
                .success(false)
                .failureReason("OCR_PROVIDER_UNSUPPORTED")
                .build();
    }

    private OcrExtractResult callGenericRemoteProvider(byte[] imageBytes, String fileName, String contentType) {
        String endpoint = getConfigValue("linbang.ocr.generic.endpoint");
        if (StrUtil.isBlank(endpoint)) {
            return OcrExtractResult.builder()
                    .success(false)
                    .failureReason("OCR_ENDPOINT_NOT_CONFIGURED")
                    .build();
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("fileName", fileName);
        payload.put("contentType", contentType);
        payload.put("imageBase64", Base64.encode(imageBytes));
        String apiKey = getConfigValue("linbang.ocr.generic.api-key");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        if (StrUtil.isNotBlank(apiKey)) {
            headers.put("Authorization", "Bearer " + apiKey);
        }
        try {
            String response = HttpUtils.post(endpoint, headers, JsonUtils.toJsonString(payload));
            JsonNode root = JsonUtils.parseTree(response);
            boolean success = root.path("success").asBoolean(false);
            if (!success) {
                return OcrExtractResult.builder()
                        .success(false)
                        .rawResponse(StrUtil.maxLength(response, 2000))
                        .failureReason(StrUtil.blankToDefault(JsonUtils.getText(root, "message"), "OCR_REMOTE_FAILED"))
                        .build();
            }
            String text = firstNonBlank(JsonUtils.getText(root, "text"), JsonUtils.getText(root.path("data"), "text"));
            return OcrExtractResult.builder()
                    .success(true)
                    .text(StrUtil.nullToEmpty(text))
                    .rawResponse(StrUtil.maxLength(response, 2000))
                    .build();
        } catch (Exception ex) {
            return OcrExtractResult.builder()
                    .success(false)
                    .failureReason(StrUtil.blankToDefault(ex.getMessage(), "OCR_REMOTE_EXCEPTION"))
                    .build();
        }
    }

    private String getConfigValue(String key) {
        return configService.getConfigByKey(key) == null ? null : configService.getConfigByKey(key).getValue();
    }

    private String firstNonBlank(String primary, String secondary) {
        return StrUtil.isNotBlank(primary) ? primary : secondary;
    }
}
