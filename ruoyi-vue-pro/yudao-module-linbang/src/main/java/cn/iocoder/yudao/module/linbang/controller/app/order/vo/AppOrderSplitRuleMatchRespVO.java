package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 订单拆单规则预览 Response VO")
@Data
public class AppOrderSplitRuleMatchRespVO {

    @Schema(description = "是否命中规则")
    private Boolean matched;

    @Schema(description = "是否需要拆单")
    private Boolean splitRequired;

    @Schema(description = "命中规则 ID")
    private Long ruleId;

    @Schema(description = "命中规则名称")
    private String ruleName;

    @Schema(description = "命中规则编码")
    private String ruleCode;

    @Schema(description = OpenApiSchemaConstants.RULE_MATCH_MODE, example = "ANY")
    private String matchMode;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE, example = "BY_PROGRESS")
    private String splitMode;

    @Schema(description = "单元金额上限，单位元")
    private BigDecimal unitAmountLimit;

    @Schema(description = "预计生成单元数")
    private Integer unitCount;

    @Schema(description = "当前类目的数量口径，例如 小时/次/台/扇/平方米/单")
    private String quantityUnitLabel;

    @Schema(description = "当前类目是否允许按数量参与拆单；false 时数量仅用于展示和报价辅助")
    private Boolean quantitySplitEnabled;

    @Schema(description = "本次拆单触发来源摘要，例如 金额达到阈值、工程类默认拆分、人数达到阈值")
    private List<String> splitTriggerReasons;

    @Schema(description = "拆单规则摘要，说明命中原因、最终单元数、是否因未勾选拆单而仅提示")
    private String splitRuleSummary;

    @Schema(description = "兼容旧前端的规则说明文案，值等同 splitRuleSummary")
    private String ruleDesc;

    @Schema(description = "预计生成的单元列表")
    private List<UnitPreviewRespVO> units;

    @Schema(description = "单元预览")
    @Data
    public static class UnitPreviewRespVO {
        @Schema(description = "单元序号")
        private Integer unitSeq;

        @Schema(description = "单元标题")
        private String unitTitle;

        @Schema(description = "单元类型")
        private String unitType;

        @Schema(description = "单元内容摘要")
        private String unitContent;

        @Schema(description = "单元进度描述")
        private String unitProgress;

        @Schema(description = "单元金额，单位元")
        private BigDecimal unitAmount;

        @Schema(description = "单元服务人数")
        private Integer workerCount;

        @Schema(description = "是否锁定")
        private Boolean locked;

        @Schema(description = "锁定原因")
        private String lockReason;
    }

}
