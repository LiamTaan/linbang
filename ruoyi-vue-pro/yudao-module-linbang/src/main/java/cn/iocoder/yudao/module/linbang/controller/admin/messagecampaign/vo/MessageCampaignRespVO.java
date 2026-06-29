package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageCampaignRespVO {

    private Long id;

    private String campaignName;

    private String sourceType;

    private String auditStatus;

    private String executeStatus;

    private String targetMode;

    private String sceneCode;

    private String messageCategory;

    private Integer plannedAudienceCount;

    private Integer reachedCount;

    private Integer clickedCount;

    private Integer readCount;

    private Integer voicePlayedCount;

    private LocalDateTime scheduleTime;

    private LocalDateTime executeTime;

    private LocalDateTime createTime;
}
