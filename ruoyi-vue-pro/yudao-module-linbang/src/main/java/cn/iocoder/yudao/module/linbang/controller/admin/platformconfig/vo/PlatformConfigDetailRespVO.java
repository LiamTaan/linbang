package cn.iocoder.yudao.module.linbang.controller.admin.platformconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 平台配置详情 Response VO")
@Data
public class PlatformConfigDetailRespVO {

    @Schema(description = "配置 ID", example = "1")
    private Long id;
    @Schema(description = "配置分类，例如 app、agreement、platform", example = "app")
    private String category;
    @Schema(description = "配置名称", example = "客服电话")
    private String name;
    @Schema(description = "配置键名，全平台唯一", example = "linbang.app.service-hotline")
    private String key;
    @Schema(description = "配置值；文本、链接、富文本或 JSON 字符串按具体配置项语义使用")
    private String value;
    @Schema(description = "是否前台可见；`true` 表示可被用户端读取", example = "true")
    private Boolean visible;
    @Schema(description = "备注，说明配置用途或维护注意事项")
    private String remark;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "当前分类的统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "同分类下的相关配置项")
    private List<RelatedConfigRespVO> relatedConfigs;

    @Data
    public static class SummaryRespVO {
        @Schema(description = "同分类配置项总数", example = "8")
        private Integer sameCategoryCount;
        @Schema(description = "前台可见配置项数", example = "6")
        private Integer visibleCount;
        @Schema(description = "前台隐藏配置项数", example = "2")
        private Integer hiddenCount;
        @Schema(description = "是否协议类配置分类", example = "false")
        private Boolean agreementCategory;
        @Schema(description = "是否平台基础配置分类", example = "true")
        private Boolean platformCategory;
    }

    @Data
    public static class RelatedConfigRespVO {
        @Schema(description = "配置 ID", example = "2")
        private Long id;
        @Schema(description = "配置名称", example = "客服微信")
        private String name;
        @Schema(description = "配置键名", example = "linbang.app.service-wechat")
        private String key;
        @Schema(description = "是否前台可见", example = "true")
        private Boolean visible;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
