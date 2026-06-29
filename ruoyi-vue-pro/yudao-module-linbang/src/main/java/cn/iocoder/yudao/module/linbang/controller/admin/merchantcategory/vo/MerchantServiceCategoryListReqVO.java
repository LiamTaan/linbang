package cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 服务类目表列表 Request VO")
@Data
public class MerchantServiceCategoryListReqVO {

    @Schema(description = "父级ID", example = "13001")
    private Long parentId;

    @Schema(description = "类目名称", example = "家电清洗")
    private String categoryName;

    @Schema(description = "层级")
    private Integer categoryLevel;

    @Schema(description = "排序")
    private Integer sortNo;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
    private String defaultPricingMode;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE)
    private String supportedPricingMode;

    @Schema(description = "是否支持拆单")
    private Boolean supportSplit;

    @Schema(description = "是否支持开票")
    private Boolean supportInvoice;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "是否用工类")
    private Boolean laborCategoryFlag;

    @Schema(description = OpenApiSchemaConstants.AGREEMENT_TYPE)
    private String forceAgreementType;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
