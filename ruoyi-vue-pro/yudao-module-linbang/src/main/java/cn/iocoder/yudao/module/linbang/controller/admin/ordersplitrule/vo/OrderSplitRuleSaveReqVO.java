package cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 订单拆单规则新增/修改 Request VO")
@Data
public class OrderSplitRuleSaveReqVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "金额超 200 自动拆单")
    @NotBlank(message = "规则名称不能为空")
    private String ruleName;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "AMOUNT_GE_200")
    @NotBlank(message = "规则编码不能为空")
    private String ruleCode;

    @Schema(description = OpenApiSchemaConstants.RULE_MATCH_MODE, requiredMode = Schema.RequiredMode.REQUIRED, example = "ANY")
    @NotBlank(message = "命中模式不能为空")
    private String matchMode;

    @Schema(description = "命中类目 ID。命中当前类目及其所有子类目", example = "340005")
    private Long categoryId;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE_SET)
    private List<String> applicablePricingModes;

    @Schema(description = "触发最小订单金额，单位元", example = "200.00")
    @DecimalMin(value = "0.00", message = "触发最小订单金额不能小于 0")
    private BigDecimal minOrderAmount;

    @Schema(description = "触发最小数量，例如 >=5 件时触发", example = "5")
    @DecimalMin(value = "0.00", message = "触发最小数量不能小于 0")
    private BigDecimal minQuantity;

    @Schema(description = "触发最小服务人数，例如 >=2 人时触发", example = "2")
    @Min(value = 1, message = "触发最小服务人数必须大于 0")
    private Integer minWorkerCount;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, requiredMode = Schema.RequiredMode.REQUIRED, example = "BY_PROGRESS")
    @NotBlank(message = "拆分方式不能为空")
    private String splitMode;

    @Schema(description = "默认拆分单元数。命中规则但无法从金额/数量/人数推导出更多单元时，按该数量兜底", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "默认拆分单元数不能为空")
    @Min(value = 1, message = "默认拆分单元数必须大于 0")
    private Integer defaultUnitCount;

    @Schema(description = "单元金额上限，单位元", requiredMode = Schema.RequiredMode.REQUIRED, example = "200.00")
    @NotNull(message = "单元金额上限不能为空")
    @DecimalMin(value = "0.01", message = "单元金额上限必须大于 0")
    private BigDecimal unitAmountLimit;

    @Schema(description = "拆分单元模板，建议包含 titlePrefix、contentTemplate、lockReasonTemplate 等键")
    private Map<String, String> unitTemplate;

    @Schema(description = "优先级排序号，越小越优先", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "排序号不能为空")
    private Integer sortNo;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "备注", example = "工程类默认拆 3 单元")
    private String remark;

}
