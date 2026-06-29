package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SensitiveImageScanResultRespVO {

    private Long id;
    private String sceneType;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private String bizType;
    private Long bizId;
    private Long fileId;
    private String sourceFileUrl;
    private String maskedFileUrl;
    private String ocrText;
    private String qrContent;
    private String hitWords;
    private String scanStatus;
    private String failureReason;
    private LocalDateTime createTime;
}
