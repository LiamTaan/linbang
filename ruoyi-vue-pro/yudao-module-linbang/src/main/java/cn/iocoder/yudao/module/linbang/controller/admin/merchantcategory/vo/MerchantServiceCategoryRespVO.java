package cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import java.util.List;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 服务类目表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MerchantServiceCategoryRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19322")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "父级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13001")
    @ExcelProperty("父级ID")
    private Long parentId;

    @Schema(description = "类目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "家电清洗")
    @ExcelProperty("类目名称")
    private String categoryName;

    @Schema(description = "层级", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("层级")
    private Integer categoryLevel;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("排序")
    private Integer sortNo;

    @Schema(description = "图标")
    @ExcelProperty("图标")
    private String icon;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
    @ExcelProperty("默认计价方式")
    private String defaultPricingMode;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE_SET)
    private List<String> supportedPricingModes;

    @Schema(description = "是否支持拆单", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否支持拆单")
    private Boolean supportSplit;

    @Schema(description = "是否支持开票", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否支持开票")
    private Boolean supportInvoice;

    @Schema(description = "风险等级")
    @ExcelProperty("风险等级")
    private String riskLevel;

    @Schema(description = "是否用工类")
    private Boolean laborCategoryFlag;

    @Schema(description = OpenApiSchemaConstants.AGREEMENT_TYPE)
    private String forceAgreementType;

    @Schema(description = "开票影响接单率提醒文案")
    private String invoiceRateReminderText;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

}
