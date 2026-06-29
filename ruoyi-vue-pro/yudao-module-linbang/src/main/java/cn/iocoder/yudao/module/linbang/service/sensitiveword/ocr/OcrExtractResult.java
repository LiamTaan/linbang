package cn.iocoder.yudao.module.linbang.service.sensitiveword.ocr;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrExtractResult {

    private boolean success;

    private String text;

    private String rawResponse;

    private String failureReason;
}
