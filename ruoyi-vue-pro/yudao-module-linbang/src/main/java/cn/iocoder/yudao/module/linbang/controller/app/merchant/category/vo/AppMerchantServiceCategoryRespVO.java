package cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

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

    @Schema(description = "默认计价方式", example = "ONE_PRICE")
    private String defaultPricingMode;

    @Schema(description = "是否支持拆单", example = "false")
    private Boolean supportSplit;

    @Schema(description = "是否支持开票", example = "true")
    private Boolean supportInvoice;

}
