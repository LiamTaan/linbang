package cn.iocoder.yudao.module.linbang.service.sensitiveword.ocr;

public interface ImageOcrService {

    OcrExtractResult extractText(byte[] imageBytes, String fileName, String contentType);
}
