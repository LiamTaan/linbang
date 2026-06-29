package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "用户 App - 订单拆单规则预览 Request VO")
@Data
public class AppOrderSplitRuleMatchReqVO {

    @Schema(description = "类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "340501")
    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "CONTRACT")
    private String pricingMode;

    @Schema(description = "预估订单金额，单位元", requiredMode = Schema.RequiredMode.REQUIRED, example = "398.00")
    @NotNull(message = "预估订单金额不能为空")
    @DecimalMin(value = "0.00", message = "预估订单金额不能小于 0")
    private BigDecimal budgetAmount;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "服务人数", example = "2")
    @Min(value = 1, message = "服务人数必须大于 0")
    private Integer workerCount;

    @Schema(description = "需求描述，仅用于生成预览单元标题", example = "安装三组灯具并调试")
    private String requireDesc;

}
