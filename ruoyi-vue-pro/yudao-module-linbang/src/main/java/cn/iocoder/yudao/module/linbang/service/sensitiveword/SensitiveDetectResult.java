package cn.iocoder.yudao.module.linbang.service.sensitiveword;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SensitiveDetectResult {

    private boolean hit;

    private int hitCount;

    private String strategy;

    private String sceneType;

    private String processedContent;

    private List<String> hitWords;

    private boolean ocrFailed;
}
