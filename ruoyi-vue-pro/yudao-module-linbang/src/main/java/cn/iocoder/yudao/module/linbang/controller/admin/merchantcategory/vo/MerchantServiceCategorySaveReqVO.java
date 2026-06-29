package cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import javax.validation.constraints.*;
import java.util.List;

@Schema(description = "管理后台 - 服务类目表新增/修改 Request VO")
@Data
public class MerchantServiceCategorySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19322")
    private Long id;

    @Schema(description = "父级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13001")
    @NotNull(message = "父级ID不能为空")
    private Long parentId;

    @Schema(description = "类目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "家电清洗")
    @NotEmpty(message = "类目名称不能为空")
    private String categoryName;

    @Schema(description = "层级，按父类目自动计算")
    private Integer categoryLevel;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sortNo;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
    private String defaultPricingMode;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE_SET)
    @NotEmpty(message = "支持计价方式不能为空")
    private List<@NotBlank(message = "计价方式不能为空") String> supportedPricingModes;

    @Schema(description = "是否支持拆单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否支持拆单不能为空")
    private Boolean supportSplit;

    @Schema(description = "是否支持开票", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否支持开票不能为空")
    private Boolean supportInvoice;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "是否用工类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否用工类不能为空")
    private Boolean laborCategoryFlag;

    @Schema(description = OpenApiSchemaConstants.AGREEMENT_TYPE, requiredMode = Schema.RequiredMode.REQUIRED, example = "TRADE_GUARANTEE")
    @NotBlank(message = "强制协议类型不能为空")
    private String forceAgreementType;

    @Schema(description = "开票影响接单率提醒文案", example = "开票需求通常会降低接单率，请确认是否继续")
    private String invoiceRateReminderText;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @NotEmpty(message = "状态不能为空")
    private String status;

}
