package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 服务商评分摘要 Response VO")
@Data
public class AppMerchantReviewSummaryRespVO {

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "综合评分，按金额加权平均，保留两位小数", example = "4.67")
    private BigDecimal compositeScore;

    @Schema(description = "评价总数", example = "20")
    private Integer reviewCount;

    @Schema(description = "好评数（4-5 星）", example = "18")
    private Integer positiveReviewCount;

    @Schema(description = "中评数（3 星）", example = "1")
    private Integer neutralReviewCount;

    @Schema(description = "差评数（1-2 星）", example = "1")
    private Integer negativeReviewCount;

    @Schema(description = "好评率，百分比保留两位小数", example = "90.00")
    private BigDecimal positiveRate;

    @Schema(description = "是否在好评优先池", example = "true")
    private Boolean inPositivePriorityPool;
}
