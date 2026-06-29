package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveimagescanresult.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
public class SensitiveImageScanResultPageReqVO extends PageParam {

    private String sceneType;
    private Long userId;
    private String bizType;
    private Long bizId;
    private String scanStatus;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
