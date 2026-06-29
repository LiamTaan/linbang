package cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MessageOptimizationRespVO {

    private Long id;

    private String refType;

    private Long templateId;

    private Long campaignId;

    private String sceneCode;

    private String messageCategory;

    private String channelType;

    private LocalDate statStartDate;

    private LocalDate statEndDate;

    private BigDecimal reachRate;

    private BigDecimal clickRate;

    private String optimizationNote;

    private String nextAction;

    private String owner;

    private LocalDateTime deadline;

    private LocalDateTime updateTime;
}
