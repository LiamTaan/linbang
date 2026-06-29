package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_WORD_TYPE, example = "ILLEGAL")
    private String wordType;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_MATCH_TYPE, example = "CONTAINS")
    private String matchType;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_BLOCK_LEVEL, example = "BLOCK")
    private String blockLevel;

    @Schema(description = "适用场景，多个场景用英文逗号分隔", example = "MESSAGE")
    private String sceneType;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
