package cn.iocoder.yudao.module.linbang.controller.admin.sensitiveword.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 敏感词详情 Response VO")
@Data
public class SensitiveWordDetailRespVO {

    private Long id;
    private String word;
    private String wordType;
    private String matchType;
    private String blockLevel;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer sameWordTypeCount;
    private Integer sameMatchTypeCount;
    private Integer sameBlockLevelCount;
    private List<RelatedWordRespVO> relatedWords;

    @Data
    public static class RelatedWordRespVO {
        private Long id;
        private String word;
        private String wordType;
        private String matchType;
        private String blockLevel;
        private String status;
        private LocalDateTime createTime;
    }
}
