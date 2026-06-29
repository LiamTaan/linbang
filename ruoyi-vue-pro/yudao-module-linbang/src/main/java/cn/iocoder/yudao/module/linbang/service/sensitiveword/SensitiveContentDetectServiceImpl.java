package cn.iocoder.yudao.module.linbang.service.sensitiveword;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitivehitlog.SensitiveHitLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveimagescanresult.SensitiveImageScanResultDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.sensitiveword.SensitiveWordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.usersensitivecustomword.UserSensitiveCustomWordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.sensitivehitlog.SensitiveHitLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.sensitiveimagescanresult.SensitiveImageScanResultMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.sensitiveword.SensitiveWordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.usersensitivecustomword.UserSensitiveCustomWordMapper;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.ocr.ImageOcrService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.ocr.OcrExtractResult;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class SensitiveContentDetectServiceImpl implements SensitiveContentDetectService {

    private static final Pattern PHONE_PATTERN = Pattern.compile("(1[3-9]\\d{9})");
    private static final Pattern WECHAT_PATTERN = Pattern.compile("(微信|vx|v信|wechat|微信号)", Pattern.CASE_INSENSITIVE);
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://|www\\.|[a-z0-9-]+\\.(com|cn|net|top|cc|vip|shop))",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern QR_DESC_PATTERN = Pattern.compile("(二维码|扫码|扫一扫)", Pattern.CASE_INSENSITIVE);

    @Resource
    private SensitiveWordMapper sensitiveWordMapper;
    @Resource
    private SensitiveHitLogMapper sensitiveHitLogMapper;
    @Resource
    private ConfigService configService;
    @Resource
    private UserSensitiveCustomWordMapper userSensitiveCustomWordMapper;
    @Resource
    private FileService fileService;
    @Resource
    private SensitiveImageScanResultMapper sensitiveImageScanResultMapper;
    @Resource
    private ImageOcrService imageOcrService;

    @Override
    public SensitiveDetectResult detect(String sceneType, Long userId, String bizType, Long bizId,
                                        String content, List<Long> fileIds) {
        String strategy = resolveStrategy(sceneType);
        String processed = content;
        Set<String> hitWords = new LinkedHashSet<>();
        int hitCount = 0;
        boolean ocrFailed = false;
        boolean reviewRequired = false;
        boolean blockRequired = false;

        if (StrUtil.isNotBlank(content)) {
            List<SensitiveWordDO> platformWords = sensitiveWordMapper.selectList();
            List<UserSensitiveCustomWordDO> customWords = userId == null
                    ? new ArrayList<>()
                    : userSensitiveCustomWordMapper.selectActiveList(userId);
            TextDetectOutcome textOutcome = detectText(sceneType, userId, bizType, bizId, content, strategy,
                    platformWords, customWords);
            processed = textOutcome.getProcessedContent();
            hitWords.addAll(textOutcome.getHitWords());
            hitCount += textOutcome.getHitCount();
            reviewRequired = reviewRequired || textOutcome.isReviewRequired();
            blockRequired = blockRequired || textOutcome.isBlockRequired();
        }

        if (CollUtil.isNotEmpty(fileIds)) {
            List<SensitiveWordDO> platformWords = sensitiveWordMapper.selectList();
            List<UserSensitiveCustomWordDO> customWords = userId == null
                    ? new ArrayList<>()
                    : userSensitiveCustomWordMapper.selectActiveList(userId);
            for (Long fileId : fileIds) {
                if (fileId == null) {
                    continue;
                }
                ImageDetectOutcome imageOutcome = detectImage(sceneType, userId, bizType, bizId, fileId, strategy,
                        platformWords, customWords);
                hitWords.addAll(imageOutcome.getHitWords());
                hitCount += imageOutcome.getHitCount();
                ocrFailed = ocrFailed || imageOutcome.isOcrFailed();
                reviewRequired = reviewRequired || imageOutcome.isReviewRequired();
                blockRequired = blockRequired || imageOutcome.isBlockRequired();
            }
        }

        return SensitiveDetectResult.builder()
                .hit(hitCount > 0)
                .hitCount(hitCount)
                .sceneType(sceneType)
                .strategy(resolveFinalStrategy(sceneType, strategy, hitCount > 0, blockRequired, reviewRequired))
                .processedContent(processed)
                .hitWords(new ArrayList<>(hitWords))
                .ocrFailed(ocrFailed)
                .build();
    }

    private TextDetectOutcome detectText(String sceneType, Long userId, String bizType, Long bizId, String content,
                                         String strategy, List<SensitiveWordDO> platformWords,
                                         List<UserSensitiveCustomWordDO> customWords) {
        String processed = content;
        Set<String> hitWords = new LinkedHashSet<>();
        int hitCount = 0;
        boolean reviewRequired = false;
        boolean blockRequired = false;
        for (SensitiveWordDO word : platformWords) {
            if (!Objects.equals(word.getStatus(), LinbangRiskConstants.STATUS_ENABLE)) {
                continue;
            }
            if (StrUtil.isNotBlank(word.getSceneType()) && !sceneMatches(sceneType, word.getSceneType())) {
                continue;
            }
            if (!containsWord(content, word.getWord())) {
                continue;
            }
            hitCount++;
            hitWords.add(word.getWord());
            String hitStrategy = resolveWordStrategy(sceneType, word.getBlockLevel());
            insertTextHit(sceneType, userId, bizType, bizId, word.getId(), word.getWord(),
                    word.getBlockLevel(), hitStrategy, content);
            if (shouldReplaceWord(word)) {
                processed = processed.replace(word.getWord(), StrUtil.blankToDefault(word.getReplaceText(), "***"));
            }
            reviewRequired = reviewRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(hitStrategy);
            blockRequired = blockRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(hitStrategy);
        }
        for (UserSensitiveCustomWordDO customWord : customWords) {
            if (StrUtil.isNotBlank(customWord.getSceneType()) && !sceneMatches(sceneType, customWord.getSceneType())) {
                continue;
            }
            if (!containsWord(content, customWord.getWord())) {
                continue;
            }
            hitCount++;
            hitWords.add(customWord.getWord());
            insertTextHit(sceneType, userId, bizType, bizId, 0L, customWord.getWord(),
                    "REPLACE", LinbangRiskConstants.SENSITIVE_STRATEGY_REPLACE, content);
            processed = processed.replace(customWord.getWord(), "***");
        }

        BuiltInTextOutcome builtInOutcome = detectBuiltInText(sceneType, userId, bizType, bizId, content, strategy);
        hitWords.addAll(builtInOutcome.getHitWords());
        hitCount += builtInOutcome.getHitCount();
        reviewRequired = reviewRequired || builtInOutcome.isReviewRequired();
        blockRequired = blockRequired || builtInOutcome.isBlockRequired();
        return new TextDetectOutcome(processed, hitWords, hitCount, reviewRequired, blockRequired);
    }

    private BuiltInTextOutcome detectBuiltInText(String sceneType, Long userId, String bizType, Long bizId,
                                                 String content, String strategy) {
        Set<String> hitWords = new LinkedHashSet<>();
        int hitCount = 0;
        boolean reviewRequired = false;
        boolean blockRequired = false;
        String builtInStrategy = resolveHighRiskStrategy(sceneType);
        if (PHONE_PATTERN.matcher(content).find()) {
            insertTextHit(sceneType, userId, bizType, bizId, 0L, "PHONE_CONTACT", "HIGH", builtInStrategy, content);
            hitWords.add("PHONE_CONTACT");
            hitCount++;
            reviewRequired = reviewRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(builtInStrategy);
            blockRequired = blockRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(builtInStrategy);
        }
        if (WECHAT_PATTERN.matcher(content).find()) {
            insertTextHit(sceneType, userId, bizType, bizId, 0L, "WECHAT_CONTACT", "HIGH", builtInStrategy, content);
            hitWords.add("WECHAT_CONTACT");
            hitCount++;
            reviewRequired = reviewRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(builtInStrategy);
            blockRequired = blockRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(builtInStrategy);
        }
        if (URL_PATTERN.matcher(content).find()) {
            insertTextHit(sceneType, userId, bizType, bizId, 0L, "EXTERNAL_LINK", "HIGH", builtInStrategy, content);
            hitWords.add("EXTERNAL_LINK");
            hitCount++;
            reviewRequired = reviewRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(builtInStrategy);
            blockRequired = blockRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(builtInStrategy);
        }
        if (QR_DESC_PATTERN.matcher(content).find()) {
            insertTextHit(sceneType, userId, bizType, bizId, 0L, "QR_GUIDE", "HIGH", builtInStrategy, content);
            hitWords.add("QR_GUIDE");
            hitCount++;
            reviewRequired = reviewRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(builtInStrategy);
            blockRequired = blockRequired || LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(builtInStrategy);
        }
        return new BuiltInTextOutcome(hitWords, hitCount, reviewRequired, blockRequired);
    }

    private ImageDetectOutcome detectImage(String sceneType, Long userId, String bizType, Long bizId,
                                           Long fileId, String strategy,
                                           List<SensitiveWordDO> platformWords,
                                           List<UserSensitiveCustomWordDO> customWords) {
        FileDO file;
        try {
            file = fileService.getFile(fileId);
        } catch (Exception ex) {
            saveImageScanResult(sceneType, userId, bizType, bizId, fileId, null, null,
                    null, null, null, "FAILED", "FILE_NOT_FOUND");
            return new ImageDetectOutcome(0, new ArrayList<>(), true, false, false);
        }
        try {
            byte[] content = fileService.getFileContent(file.getConfigId(), file.getPath());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(content));
            if (image == null) {
                saveImageScanResult(sceneType, userId, bizType, bizId, fileId, file.getUrl(), null,
                        null, null, null, "FAILED", "UNSUPPORTED_IMAGE");
                return new ImageDetectOutcome(0, new ArrayList<>(), true, false, false);
            }
            OcrExtractResult ocrResult = imageOcrService.extractText(content, file.getName(), file.getType());
            String qrContent = decodeQrContent(image);
            Set<String> hitWords = new LinkedHashSet<>();
            int hitCount = 0;
            String maskedFileUrl = file.getUrl();
            boolean reviewRequired = false;
            boolean blockRequired = false;
            if (ocrResult.isSuccess() && StrUtil.isNotBlank(ocrResult.getText())) {
                ImageTextDetectOutcome ocrTextOutcome = detectImageText(sceneType, userId, bizType, bizId,
                        fileId, file.getUrl(), "IMAGE_OCR", ocrResult.getText(), strategy, platformWords, customWords);
                hitWords.addAll(ocrTextOutcome.getHitWords());
                hitCount += ocrTextOutcome.getHitCount();
                reviewRequired = reviewRequired || ocrTextOutcome.isReviewRequired();
                blockRequired = blockRequired || ocrTextOutcome.isBlockRequired();
            }
            if (StrUtil.isNotBlank(qrContent)) {
                ImageTextDetectOutcome qrOutcome = detectImageText(sceneType, userId, bizType, bizId,
                        fileId, file.getUrl(), "QRCODE", qrContent, strategy, platformWords, customWords);
                hitWords.addAll(qrOutcome.getHitWords());
                hitCount += qrOutcome.getHitCount();
                reviewRequired = reviewRequired || qrOutcome.isReviewRequired();
                blockRequired = blockRequired || qrOutcome.isBlockRequired();
            }
            if (hitCount > 0) {
                maskedFileUrl = createMaskedPreviewImage(image, file);
            }
            String finalStrategy = resolveFinalStrategy(sceneType, strategy, hitCount > 0, blockRequired, reviewRequired);
            String scanStatus = hitCount > 0
                    ? resolveScanStatus(finalStrategy)
                    : "PASSED";
            saveImageScanResult(sceneType, userId, bizType, bizId, fileId, file.getUrl(), maskedFileUrl,
                    ocrResult.isSuccess() ? StrUtil.maxLength(ocrResult.getText(), 4000) : null,
                    qrContent, JsonUtils.toJsonString(new ArrayList<>(hitWords)),
                    scanStatus, ocrResult.isSuccess() ? null : ocrResult.getFailureReason());
            if (!ocrResult.isSuccess() && shouldFailOnOcr(sceneType)) {
                return new ImageDetectOutcome(0, new ArrayList<>(), true, false, false);
            }
            return new ImageDetectOutcome(hitCount, new ArrayList<>(hitWords), false, reviewRequired, blockRequired);
        } catch (Exception ex) {
            saveImageScanResult(sceneType, userId, bizType, bizId, fileId, file.getUrl(), null,
                    null, null, null, "FAILED", ex.getMessage());
            return new ImageDetectOutcome(0, new ArrayList<>(), true, false, false);
        }
    }

    private ImageTextDetectOutcome detectImageText(String sceneType, Long userId, String bizType, Long bizId,
                                                   Long fileId, String fileUrl, String contentType, String content,
                                                   String strategy, List<SensitiveWordDO> platformWords,
                                                   List<UserSensitiveCustomWordDO> customWords) {
        TextDetectOutcome textOutcome = detectText(sceneType, userId, bizType, bizId, content, strategy,
                platformWords, customWords);
        if (textOutcome.getHitCount() > 0) {
            sensitiveHitLogMapper.insert(SensitiveHitLogDO.builder()
                    .sceneType(sceneType)
                    .userId(userId)
                    .bizType(bizType)
                    .bizId(bizId)
                    .wordId(0L)
                    .hitWord(contentType)
                    .blockLevel(textOutcome.isBlockRequired() ? "HIGH"
                            : (textOutcome.isReviewRequired() ? "REVIEW" : "REPLACE"))
                    .strategy(resolveFinalStrategy(sceneType, strategy, textOutcome.getHitCount() > 0,
                            textOutcome.isBlockRequired(), textOutcome.isReviewRequired()))
                    .contentType(contentType)
                    .fileId(fileId)
                    .ocrTextSnapshot("IMAGE_OCR".equals(contentType) ? StrUtil.maxLength(content, 2000) : null)
                    .qrContentSnapshot("QRCODE".equals(contentType) ? StrUtil.maxLength(content, 500) : null)
                    .contentSnapshot(StrUtil.maxLength(fileUrl, 500))
                    .build());
        }
        return new ImageTextDetectOutcome(textOutcome.getHitCount(), new ArrayList<>(textOutcome.getHitWords()),
                textOutcome.isReviewRequired(), textOutcome.isBlockRequired());
    }

    private String decodeQrContent(BufferedImage image) {
        try {
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
            Result result = new MultiFormatReader().decode(bitmap);
            return result != null ? result.getText() : null;
        } catch (NotFoundException ex) {
            return null;
        }
    }

    private void insertTextHit(String sceneType, Long userId, String bizType, Long bizId,
                               Long wordId, String hitWord, String blockLevel, String strategy, String content) {
        sensitiveHitLogMapper.insert(SensitiveHitLogDO.builder()
                .sceneType(sceneType)
                .userId(userId)
                .bizType(bizType)
                .bizId(bizId)
                .wordId(wordId)
                .hitWord(hitWord)
                .blockLevel(blockLevel)
                .strategy(strategy)
                .contentType("TEXT")
                .contentSnapshot(StrUtil.maxLength(content, 500))
                .build());
    }

    private void saveImageScanResult(String sceneType, Long userId, String bizType, Long bizId, Long fileId,
                                     String sourceFileUrl, String maskedFileUrl, String ocrText, String qrContent,
                                     String hitWords, String scanStatus, String failureReason) {
        sensitiveImageScanResultMapper.insert(SensitiveImageScanResultDO.builder()
                .sceneType(sceneType)
                .userId(userId)
                .bizType(bizType)
                .bizId(bizId)
                .fileId(fileId)
                .sourceFileUrl(sourceFileUrl)
                .maskedFileUrl(maskedFileUrl)
                .ocrText(ocrText)
                .qrContent(qrContent)
                .hitWords(hitWords)
                .scanStatus(scanStatus)
                .failureReason(StrUtil.maxLength(failureReason, 500))
                .build());
    }

    private String resolveFinalStrategy(String sceneType, String configuredStrategy,
                                        boolean hasHit, boolean blockRequired, boolean reviewRequired) {
        if (blockRequired) {
            return LinbangRiskConstants.SCENE_ORDER_PUBLISH.equals(sceneType)
                    ? LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK
                    : LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW;
        }
        if (reviewRequired) {
            return LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW;
        }
        if (hasHit) {
            return LinbangRiskConstants.SENSITIVE_STRATEGY_REPLACE;
        }
        return configuredStrategy;
    }

    private String resolveScanStatus(String finalStrategy) {
        if (LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equals(finalStrategy)) {
            return "REVIEW";
        }
        if (LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(finalStrategy)) {
            return "BLOCKED";
        }
        return "PASSED";
    }

    private boolean shouldFailOnOcr(String sceneType) {
        String fallbackMode = configService.getConfigByKey(PlatformConfigKeyConstants.OCR_FALLBACK_MODE) == null
                ? null : configService.getConfigByKey(PlatformConfigKeyConstants.OCR_FALLBACK_MODE).getValue();
        if ("ALLOW".equalsIgnoreCase(fallbackMode)) {
            return false;
        }
        return LinbangRiskConstants.SCENE_ORDER_PUBLISH.equals(sceneType);
    }

    private String createMaskedPreviewImage(BufferedImage image, FileDO file) throws Exception {
        BufferedImage masked = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = masked.createGraphics();
        try {
            graphics.drawImage(image, 0, 0, null);
            graphics.setColor(new Color(0, 0, 0, 190));
            graphics.fillRect(0, Math.max(0, image.getHeight() - 96), image.getWidth(), 96);
            graphics.setColor(Color.WHITE);
            graphics.setFont(new Font("SansSerif", Font.BOLD, 22));
            graphics.drawString("内容已脱敏，请联系平台查看原图", 24, Math.max(48, image.getHeight() - 36));
        } finally {
            graphics.dispose();
        }
        byte[] bytes = writeJpeg(masked);
        FileDO maskedFile = fileService.createFileInfo(bytes,
                "masked-" + StrUtil.blankToDefault(file.getName(), "image.jpg"),
                "linbang/sensitive-masked", "image/jpeg");
        return maskedFile.getUrl();
    }

    private byte[] writeJpeg(BufferedImage image) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        try (ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream)) {
            writer.setOutput(imageOutputStream);
            ImageWriteParam params = writer.getDefaultWriteParam();
            if (params.canWriteCompressed()) {
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionQuality(0.85f);
            }
            writer.write(null, new IIOImage(image, null, null), params);
        } finally {
            writer.dispose();
        }
        return outputStream.toByteArray();
    }

    private boolean containsWord(String content, String word) {
        if (StrUtil.isBlank(content) || StrUtil.isBlank(word)) {
            return false;
        }
        return content.toLowerCase(Locale.ROOT).contains(word.toLowerCase(Locale.ROOT));
    }

    private boolean sceneMatches(String sceneType, String wordSceneType) {
        for (String item : wordSceneType.split(",")) {
            if (sceneType.equalsIgnoreCase(StrUtil.trim(item))) {
                return true;
            }
        }
        return false;
    }

    private boolean shouldReplaceWord(SensitiveWordDO word) {
        String blockLevel = StrUtil.blankToDefault(word.getBlockLevel(), "");
        return !LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equalsIgnoreCase(blockLevel)
                && !LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equalsIgnoreCase(blockLevel)
                && !"HIGH".equalsIgnoreCase(blockLevel);
    }

    private String resolveWordStrategy(String sceneType, String blockLevel) {
        if (LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW.equalsIgnoreCase(blockLevel)) {
            return LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW;
        }
        if (LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equalsIgnoreCase(blockLevel)
                || "HIGH".equalsIgnoreCase(blockLevel)) {
            return resolveHighRiskStrategy(sceneType);
        }
        return LinbangRiskConstants.SENSITIVE_STRATEGY_REPLACE;
    }

    private String resolveHighRiskStrategy(String sceneType) {
        return LinbangRiskConstants.SCENE_ORDER_PUBLISH.equals(sceneType)
                ? LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK
                : LinbangRiskConstants.SENSITIVE_STRATEGY_REVIEW;
    }

    private String resolveStrategy(String sceneType) {
        String key;
        switch (sceneType) {
            case LinbangRiskConstants.SCENE_COMMENT:
                key = PlatformConfigKeyConstants.COMMENT_SENSITIVE_STRATEGY;
                break;
            case LinbangRiskConstants.SCENE_MESSAGE:
                key = PlatformConfigKeyConstants.MESSAGE_SENSITIVE_STRATEGY;
                break;
            case LinbangRiskConstants.SCENE_PROMOTE:
                key = PlatformConfigKeyConstants.PROMOTE_SENSITIVE_STRATEGY;
                break;
            case LinbangRiskConstants.SCENE_COMPLAINT:
                key = PlatformConfigKeyConstants.COMPLAINT_SENSITIVE_STRATEGY;
                break;
            case LinbangRiskConstants.SCENE_APPEAL:
                key = PlatformConfigKeyConstants.APPEAL_SENSITIVE_STRATEGY;
                break;
            case LinbangRiskConstants.SCENE_ORDER_PUBLISH:
                key = PlatformConfigKeyConstants.ORDER_PUBLISH_SENSITIVE_STRATEGY;
                break;
            default:
                key = PlatformConfigKeyConstants.MESSAGE_SENSITIVE_STRATEGY;
                break;
        }
        String value = configService.getConfigByKey(key) == null ? null : configService.getConfigByKey(key).getValue();
        if (StrUtil.isBlank(value)) {
            if (LinbangRiskConstants.SCENE_COMMENT.equals(sceneType)) {
                return LinbangRiskConstants.SENSITIVE_STRATEGY_REPLACE;
            }
            if (LinbangRiskConstants.SCENE_COMPLAINT.equals(sceneType)
                    || LinbangRiskConstants.SCENE_APPEAL.equals(sceneType)) {
                return LinbangRiskConstants.SENSITIVE_STRATEGY_ALLOW_LOG;
            }
            return LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK;
        }
        return value;
    }

    private static final class TextDetectOutcome {
        private final String processedContent;
        private final Set<String> hitWords;
        private final int hitCount;
        private final boolean reviewRequired;
        private final boolean blockRequired;

        private TextDetectOutcome(String processedContent, Set<String> hitWords, int hitCount,
                                  boolean reviewRequired, boolean blockRequired) {
            this.processedContent = processedContent;
            this.hitWords = hitWords;
            this.hitCount = hitCount;
            this.reviewRequired = reviewRequired;
            this.blockRequired = blockRequired;
        }

        private String getProcessedContent() {
            return processedContent;
        }

        private Set<String> getHitWords() {
            return hitWords;
        }

        private int getHitCount() {
            return hitCount;
        }

        private boolean isReviewRequired() {
            return reviewRequired;
        }

        private boolean isBlockRequired() {
            return blockRequired;
        }
    }

    private static final class BuiltInTextOutcome {
        private final Set<String> hitWords;
        private final int hitCount;
        private final boolean reviewRequired;
        private final boolean blockRequired;

        private BuiltInTextOutcome(Set<String> hitWords, int hitCount,
                                   boolean reviewRequired, boolean blockRequired) {
            this.hitWords = hitWords;
            this.hitCount = hitCount;
            this.reviewRequired = reviewRequired;
            this.blockRequired = blockRequired;
        }

        private Set<String> getHitWords() {
            return hitWords;
        }

        private int getHitCount() {
            return hitCount;
        }

        private boolean isReviewRequired() {
            return reviewRequired;
        }

        private boolean isBlockRequired() {
            return blockRequired;
        }
    }

    private static final class ImageDetectOutcome {
        private final int hitCount;
        private final List<String> hitWords;
        private final boolean ocrFailed;
        private final boolean reviewRequired;
        private final boolean blockRequired;

        private ImageDetectOutcome(int hitCount, List<String> hitWords, boolean ocrFailed,
                                   boolean reviewRequired, boolean blockRequired) {
            this.hitCount = hitCount;
            this.hitWords = hitWords;
            this.ocrFailed = ocrFailed;
            this.reviewRequired = reviewRequired;
            this.blockRequired = blockRequired;
        }

        private int getHitCount() {
            return hitCount;
        }

        private List<String> getHitWords() {
            return hitWords;
        }

        private boolean isOcrFailed() {
            return ocrFailed;
        }

        private boolean isReviewRequired() {
            return reviewRequired;
        }

        private boolean isBlockRequired() {
            return blockRequired;
        }
    }

    private static final class ImageTextDetectOutcome {
        private final int hitCount;
        private final List<String> hitWords;
        private final boolean reviewRequired;
        private final boolean blockRequired;

        private ImageTextDetectOutcome(int hitCount, List<String> hitWords,
                                       boolean reviewRequired, boolean blockRequired) {
            this.hitCount = hitCount;
            this.hitWords = hitWords;
            this.reviewRequired = reviewRequired;
            this.blockRequired = blockRequired;
        }

        private int getHitCount() {
            return hitCount;
        }

        private List<String> getHitWords() {
            return hitWords;
        }

        private boolean isReviewRequired() {
            return reviewRequired;
        }

        private boolean isBlockRequired() {
            return blockRequired;
        }
    }
}
