package cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 订单拆单规则 Response VO")
@Data
public class OrderSplitRuleRespVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = OpenApiSchemaConstants.RULE_MATCH_MODE, example = "ANY")
    private String matchMode;

    @Schema(description = "类目 ID")
    private Long categoryId;

    @Schema(description = "类目名称")
    private String categoryName;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE_SET)
    private List<String> applicablePricingModes;

    @Schema(description = "触发最小订单金额，单位元")
    private BigDecimal minOrderAmount;

    @Schema(description = "触发最小数量")
    private BigDecimal minQuantity;

    @Schema(description = "触发最小服务人数")
    private Integer minWorkerCount;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "BY_PROGRESS")
    private String splitMode;

    @Schema(description = "默认拆分单元数")
    private Integer defaultUnitCount;

    @Schema(description = "单元金额上限，单位元")
    private BigDecimal unitAmountLimit;

    @Schema(description = "拆分单元模板")
    private Map<String, String> unitTemplate;

    @Schema(description = "优先级排序号")
    private Integer sortNo;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
