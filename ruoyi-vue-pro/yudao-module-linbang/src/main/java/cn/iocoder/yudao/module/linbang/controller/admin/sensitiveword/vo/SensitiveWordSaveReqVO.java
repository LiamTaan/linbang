package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 敏感词表新增/修改 Request VO")
@Data
public class SensitiveWordSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "18533")
    private Long id;

    @Schema(description = "关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "关键词不能为空")
    private String word;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_WORD_TYPE, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ILLEGAL")
    @NotEmpty(message = "词库类型不能为空")
    private String wordType;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_MATCH_TYPE, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "CONTAINS")
    @NotEmpty(message = "匹配方式不能为空")
    private String matchType;

    @Schema(description = OpenApiSchemaConstants.SENSITIVE_BLOCK_LEVEL, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "BLOCK")
    @NotEmpty(message = "拦截级别不能为空")
    private String blockLevel;

    @Schema(description = "适用场景，多个场景用英文逗号分隔，例如 MESSAGE,COMMENT,PROMOTE", example = "MESSAGE,COMMENT")
    private String sceneType;

    @Schema(description = "命中后替换文案，过滤模式下用于替换敏感词内容", example = "***")
    private String replaceText;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @NotEmpty(message = "状态不能为空")
    private String status;

}
