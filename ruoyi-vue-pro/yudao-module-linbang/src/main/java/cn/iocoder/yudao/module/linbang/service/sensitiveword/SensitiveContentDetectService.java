package cn.iocoder.yudao.module.linbang.service.sensitiveword;

import java.util.List;

public interface SensitiveContentDetectService {

    default SensitiveDetectResult detect(String sceneType, Long userId, String bizType, Long bizId, String content) {
        return detect(sceneType, userId, bizType, bizId, content, null);
    }

    SensitiveDetectResult detect(String sceneType, Long userId, String bizType, Long bizId,
                                 String content, List<Long> fileIds);
}
