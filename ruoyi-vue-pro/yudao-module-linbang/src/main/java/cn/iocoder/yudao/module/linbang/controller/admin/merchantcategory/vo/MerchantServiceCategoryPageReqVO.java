package cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 服务类目表分页 Request VO")
@Data
public class MerchantServiceCategoryPageReqVO extends PageParam {

    @Schema(description = "父级ID", example = "13001")
    private Long parentId;

    @Schema(description = "类目名称", example = "??")
    private String categoryName;

    @Schema(description = "层级")
    private Integer categoryLevel;

    @Schema(description = "排序")
    private Integer sortNo;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "默认计价方式")
    private String defaultPricingMode;

    @Schema(description = "是否支持拆单")
    private Boolean supportSplit;

    @Schema(description = "是否支持开票")
    private Boolean supportInvoice;

    @Schema(description = "风险等级")
    private String riskLevel;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}