package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 敏感词表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SensitiveWordRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "18533")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("关键词")
    private String word;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_WORD_TYPE, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ILLEGAL")
    @ExcelProperty("词库类型")
    private String wordType;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_MATCH_TYPE, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "CONTAINS")
    @ExcelProperty("匹配方式")
    private String matchType;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_BLOCK_LEVEL, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "BLOCK")
    @ExcelProperty("拦截级别")
    private String blockLevel;

    @Schema(description = "适用场景，多个场景用英文逗号分隔", example = "MESSAGE,COMMENT")
    @ExcelProperty("适用场景")
    private String sceneType;

    @Schema(description = "替换文案，过滤模式下用于替换敏感词内容", example = "***")
    @ExcelProperty("替换文案")
    private String replaceText;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
