package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

import static cn.iocoder.yudao.framework.common.util.number.MoneyUtils.MAX_YUAN_AMOUNT_STR;

@Schema(description = "用户 App - 订单拆单规则预览 Request VO")
@Data
public class AppOrderSplitRuleMatchReqVO {

    @Schema(description = "类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "340501")
    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "CONTRACT")
    @Size(max = 32, message = "计价方式不能超过 32 个字符")
    private String pricingMode;

    @Schema(description = "预估订单金额，单位元，最高 21474836.47 元且最多 2 位小数", requiredMode = Schema.RequiredMode.REQUIRED, example = "398.00")
    @NotNull(message = "预估订单金额不能为空")
    @DecimalMin(value = "0.00", message = "预估订单金额不能小于 0")
    @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "预估订单金额超过支付渠道支持上限")
    @Digits(integer = 8, fraction = 2, message = "预估订单金额最多 8 位整数和 2 位小数")
    private BigDecimal budgetAmount;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于 0")
    @DecimalMax(value = "1000000.00", message = "数量不能超过 1000000")
    @Digits(integer = 7, fraction = 2, message = "数量最多 7 位整数和 2 位小数")
    private BigDecimal quantity;

    @Schema(description = "服务人数", example = "2")
    @Min(value = 1, message = "服务人数必须大于 0")
    @Max(value = 100, message = "服务人数不能超过 100")
    private Integer workerCount;

    @Schema(description = "需求描述，仅用于生成预览单元标题", example = "安装三组灯具并调试")
    @Size(max = 5000, message = "需求描述不能超过 5000 个字符")
    private String requireDesc;

}
