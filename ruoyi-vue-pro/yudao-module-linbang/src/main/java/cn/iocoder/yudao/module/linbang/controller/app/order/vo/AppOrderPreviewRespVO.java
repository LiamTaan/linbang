package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 发单预览 Response VO")
@Data
public class AppOrderPreviewRespVO {

    @Schema(description = "预览快照令牌，用于提交时防漂移校验")
    private String previewToken;

    @Schema(description = "类目 ID")
    private Long categoryId;

    @Schema(description = "类目名称")
    private String categoryName;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE)
    private String pricingMode;

    @Schema(description = "计价方式名称", example = "一口价")
    private String pricingModeName;

    @Schema(description = "预算金额，单位元")
    private BigDecimal budgetAmount;

    @Schema(description = "订单应付金额，单位元")
    private BigDecimal orderAmount;

    @Schema(description = "数量。不是全平台统一单位，具体口径由当前类目 quantityUnitLabel 决定")
    private BigDecimal quantity;

    @Schema(description = "当前类目的数量口径，例如 小时/次/台/扇/平方米/单")
    private String quantityUnitLabel;

    @Schema(description = "当前类目是否允许按数量参与拆单；false 时数量仅用于展示和报价辅助")
    private Boolean quantitySplitEnabled;

    @Schema(description = "服务人数")
    private Integer workerCount;

    @Schema(description = "需求描述")
    private String requireDesc;

    @Schema(description = "服务时长/工期说明")
    private String serviceDurationDesc;

    @Schema(description = "开票提示文案")
    private String invoiceImpactReminder;

    @Schema(description = "是否支持开票")
    private Boolean invoiceSupported;

    @Schema(description = "是否支持拆单")
    private Boolean splitSupported;

    @Schema(description = "是否必须勾选协议")
    private Boolean agreementRequired;

    @Schema(description = OpenApiSchemaConstants.AGREEMENT_TYPE)
    private String agreementType;

    @Schema(description = "协议标题")
    private String agreementTitle;

    @Schema(description = "协议内容摘要")
    private String agreementContent;

    @Schema(description = "风险处理策略，若命中内容风控则返回 BLOCK/REVIEW/REPLACE/ALLOW_LOG")
    private String riskStrategy;

    @Schema(description = "是否命中敏感内容")
    private Boolean sensitiveHit;

    @Schema(description = "敏感命中数量")
    private Integer sensitiveHitCount;

    @Schema(description = "敏感命中摘要")
    private List<String> sensitiveHitSummaries;

    @Schema(description = "拆分预览")
    private AppOrderSplitRuleMatchRespVO splitPreview;
}
