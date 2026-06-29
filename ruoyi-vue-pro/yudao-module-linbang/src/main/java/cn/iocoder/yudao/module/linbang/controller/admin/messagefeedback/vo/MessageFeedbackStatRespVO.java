package cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MessageFeedbackStatRespVO {

    private Long id;

    private LocalDate statDate;

    private String sceneCode;

    private String messageCategory;

    private Long templateId;

    private Long campaignId;

    private Long pushTaskId;

    private String channelType;

    private Integer plannedAudienceCount;

    private Integer reachedCount;

    private Integer clickedCount;

    private Integer readCount;

    private Integer voicePlayedCount;

    private BigDecimal reachRate;

    private BigDecimal clickRate;

    private BigDecimal readRate;
}
