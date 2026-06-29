package cn.iocoder.yudao.module.linbang.controller.admin.sensitivehitlog.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
public class SensitiveHitLogPageReqVO extends PageParam {

    private String sceneType;
    private Long userId;
    private String userKeyword;
    private String bizType;
    private Long bizId;
    private String strategy;
    private String contentType;
    private Long fileId;
    private String manualAuditResult;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
