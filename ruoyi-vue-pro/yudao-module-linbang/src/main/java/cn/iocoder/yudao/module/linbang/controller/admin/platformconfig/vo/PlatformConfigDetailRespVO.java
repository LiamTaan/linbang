package cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 平台配置详情 Response VO")
@Data
public class PlatformConfigDetailRespVO {

    private Long id;
    private String category;
    private String name;
    private String key;
    private String value;
    private Boolean visible;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private SummaryRespVO summary;
    private List<RelatedConfigRespVO> relatedConfigs;

    @Data
    public static class SummaryRespVO {
        private Integer sameCategoryCount;
        private Integer visibleCount;
        private Integer hiddenCount;
        private Boolean agreementCategory;
        private Boolean platformCategory;
    }

    @Data
    public static class RelatedConfigRespVO {
        private Long id;
        private String name;
        private String key;
        private Boolean visible;
        private String remark;
        private LocalDateTime createTime;
    }
}
