package cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SensitiveHitLogRespVO {

    private Long id;
    private String sceneType;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private String bizType;
    private Long bizId;
    private String hitWord;
    private String blockLevel;
    private String strategy;
    private String contentType;
    private Long fileId;
    private String ocrTextSnapshot;
    private String qrContentSnapshot;
    private String manualAuditResult;
    private String contentSnapshot;
    private LocalDateTime createTime;
}
