package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 敏感词详情 Response VO")
@Data
public class SensitiveWordDetailRespVO {

    @Schema(description = "主键", example = "320001")
    private Long id;
    @Schema(description = "关键词", example = "私彩")
    private String word;
    @Schema(description = OpenApiSchemaConstants.SENSITIVE_WORD_TYPE, example = "ILLEGAL")
    private String wordType;
    @Schema(description = OpenApiSchemaConstants.SENSITIVE_MATCH_TYPE, example = "CONTAINS")
    private String matchType;
    @Schema(description = OpenApiSchemaConstants.SENSITIVE_BLOCK_LEVEL, example = "BLOCK")
    private String blockLevel;
    @Schema(description = "适用场景，多个场景用英文逗号分隔", example = "MESSAGE,COMMENT")
    private String sceneType;
    @Schema(description = "替换文案，过滤模式下用于替换敏感词内容", example = "***")
    private String replaceText;
    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "同词库类型词条数")
    private Integer sameWordTypeCount;
    @Schema(description = "同匹配方式词条数")
    private Integer sameMatchTypeCount;
    @Schema(description = "同拦截级别词条数")
    private Integer sameBlockLevelCount;
    @Schema(description = "相关词条")
    private List<RelatedWordRespVO> relatedWords;

    @Data
    public static class RelatedWordRespVO {
        @Schema(description = "词条 ID", example = "320002")
        private Long id;
        @Schema(description = "关键词", example = "刷单")
        private String word;
        @Schema(description = OpenApiSchemaConstants.SENSITIVE_WORD_TYPE, example = "RISK")
        private String wordType;
        @Schema(description = OpenApiSchemaConstants.SENSITIVE_MATCH_TYPE, example = "CONTAINS")
        private String matchType;
        @Schema(description = OpenApiSchemaConstants.SENSITIVE_BLOCK_LEVEL, example = "REVIEW")
        private String blockLevel;
        @Schema(description = "适用场景，多个场景用英文逗号分隔", example = "PROMOTE")
        private String sceneType;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
