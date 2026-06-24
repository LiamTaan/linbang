package cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
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

    @Schema(description = "类目名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
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

    @Schema(description = "默认计价方式")
    @ExcelProperty("默认计价方式")
    private String defaultPricingMode;

    @Schema(description = "是否支持拆单", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否支持拆单")
    private Boolean supportSplit;

    @Schema(description = "是否支持开票", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否支持开票")
    private Boolean supportInvoice;

    @Schema(description = "风险等级")
    @ExcelProperty("风险等级")
    private String riskLevel;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}