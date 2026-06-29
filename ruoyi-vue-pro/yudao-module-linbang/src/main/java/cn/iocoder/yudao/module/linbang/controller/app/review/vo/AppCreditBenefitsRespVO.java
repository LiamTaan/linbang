package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 信用等级权益 Response VO")
@Data
public class AppCreditBenefitsRespVO {

    @Schema(description = "当前信用等级", example = "NORMAL")
    private String currentLevelCode;

    @Schema(description = "当前信用等级名称", example = "正常")
    private String currentLevelName;

    @Schema(description = "全部等级权益列表")
    private List<BenefitItem> benefits;

    @Data
    public static class BenefitItem {

        @Schema(description = "等级编码", example = "NORMAL")
        private String levelCode;

        @Schema(description = "等级名称", example = "正常")
        private String levelName;

        @Schema(description = "权益标题", example = "标准排序")
        private String benefitTitle;

        @Schema(description = "权益说明", example = "可参与平台标准排序和正常接单流转")
        private String benefitDesc;

        @Schema(description = "排序号", example = "1")
        private Integer sortNo;
    }
}
