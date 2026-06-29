package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 智能匹配策略 Response VO")
@Data
public class MatchStrategyRespVO {

    @Schema(description = "策略 ID", example = "1")
    private Long id;

    @Schema(description = "策略编码")
    private String strategyCode;

    @Schema(description = "策略名称")
    private String strategyName;

    @Schema(description = "阶段配置 JSON")
    private String stageConfigJson;

    @Schema(description = "最大阶段数")
    private Integer maxStageCount;

    @Schema(description = "最大扩圈半径，单位公里")
    private BigDecimal maxRadiusKm;

    @Schema(description = "流单建议模板")
    private String flowAdviceTemplate;

    @Schema(description = "是否自动退款")
    private Boolean autoRefundEnabled;

    @Schema(description = "自动退款重试次数")
    private Integer autoRefundRetryTimes;

    @Schema(description = "状态")
    private String status;
}
