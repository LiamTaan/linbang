package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 敏感词表分页 Request VO")
@Data
public class SensitiveWordPageReqVO extends PageParam {

    @Schema(description = "关键词")
    private String word;

    @Schema(description = "词库类型", example = "1")
    private String wordType;

    @Schema(description = "匹配方式", example = "2")
    private String matchType;

    @Schema(description = "拦截级别")
    private String blockLevel;

    @Schema(description = "状态", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}