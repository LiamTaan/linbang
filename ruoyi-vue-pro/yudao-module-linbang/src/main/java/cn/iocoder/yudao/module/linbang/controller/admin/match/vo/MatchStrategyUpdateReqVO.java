package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 智能匹配策略更新 Request VO")
@Data
public class MatchStrategyUpdateReqVO {

    @Schema(description = "策略编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "策略编码不能为空")
    private String strategyCode;

    @Schema(description = "策略名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "策略名称不能为空")
    private String strategyName;

    @Schema(description = "阶段配置 JSON", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "阶段配置不能为空")
    private String stageConfigJson;

    @Schema(description = "最大阶段数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "最大阶段数不能为空")
    private Integer maxStageCount;

    @Schema(description = "最大扩圈半径，单位公里", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "最大扩圈半径不能为空")
    private BigDecimal maxRadiusKm;

    @Schema(description = "流单建议模板", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "流单建议模板不能为空")
    private String flowAdviceTemplate;

    @Schema(description = "平台级自动派单总开关", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "自动派单总开关不能为空")
    private Boolean autoDispatchEnabled;

    @Schema(description = "是否自动退款", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "自动退款开关不能为空")
    private Boolean autoRefundEnabled;

    @Schema(description = "自动退款重试次数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "自动退款重试次数不能为空")
    private Integer autoRefundRetryTimes;
}
