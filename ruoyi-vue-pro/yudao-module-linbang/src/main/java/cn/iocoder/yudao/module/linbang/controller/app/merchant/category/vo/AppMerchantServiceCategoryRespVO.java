package cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 服务类目 Response VO")
@Data
public class AppMerchantServiceCategoryRespVO {

    @Schema(description = "类目 ID", example = "110")
    private Long id;

    @Schema(description = "父级 ID", example = "100")
    private Long parentId;

    @Schema(description = "类目名称", example = "水电维修")
    private String categoryName;

    @Schema(description = "类目层级", example = "2")
    private Integer categoryLevel;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
    private String defaultPricingMode;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE_SET)
    private List<String> supportedPricingModes;

    @Schema(description = "是否支持拆单", example = "false")
    private Boolean supportSplit;

    @Schema(description = "该类目的数量口径，例如 小时/次/台/扇/平方米/单")
    private String quantityUnitLabel;

    @Schema(description = "是否允许按数量参与拆单；关闭后数量仅用于展示和报价辅助")
    private Boolean quantitySplitEnabled;

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_MODE)
    private String splitDefaultMode;

    @Schema(description = "是否工程类", example = "false")
    private Boolean engineeringCategoryFlag;

    @Schema(description = "是否支持开票", example = "true")
    private Boolean supportInvoice;

    @Schema(description = "是否用工类", example = "false")
    private Boolean laborCategoryFlag;

    @Schema(description = OpenApiSchemaConstants.AGREEMENT_TYPE, example = "TRADE_GUARANTEE")
    private String forceAgreementType;

    @Schema(description = "开票影响接单率提醒文案")
    private String invoiceRateReminderText;

    @Schema(description = "下级类目列表，仅一级类目节点返回", implementation = AppMerchantServiceCategoryRespVO.class)
    private List<AppMerchantServiceCategoryRespVO> children;

}
