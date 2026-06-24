package cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 服务类目表新增/修改 Request VO")
@Data
public class MerchantServiceCategorySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19322")
    private Long id;

    @Schema(description = "父级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13001")
    @NotNull(message = "父级ID不能为空")
    private Long parentId;

    @Schema(description = "类目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @NotEmpty(message = "类目名称不能为空")
    private String categoryName;

    @Schema(description = "层级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "层级不能为空")
    private Integer categoryLevel;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    private Integer sortNo;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "默认计价方式")
    private String defaultPricingMode;

    @Schema(description = "是否支持拆单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否支持拆单不能为空")
    private Boolean supportSplit;

    @Schema(description = "是否支持开票", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否支持开票不能为空")
    private Boolean supportInvoice;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态不能为空")
    private String status;

}