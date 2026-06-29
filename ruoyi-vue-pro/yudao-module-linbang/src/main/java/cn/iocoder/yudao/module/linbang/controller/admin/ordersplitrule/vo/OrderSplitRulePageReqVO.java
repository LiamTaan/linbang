package cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 订单拆单规则分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderSplitRulePageReqVO extends PageParam {

    @Schema(description = "规则名称", example = "金额超 200 自动拆单")
    private String ruleName;

    @Schema(description = "规则编码", example = "AMOUNT_GE_200")
    private String ruleCode;

    @Schema(description = OpenApiSchemaConstants.RULE_MATCH_MODE, example = "ANY")
    private String matchMode;

    @Schema(description = "类目 ID", example = "340005")
    private Long categoryId;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "HOURLY")
    private String pricingMode;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "BY_PROGRESS")
    private String splitMode;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
