package cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "用户 App - 服务商参考价格新增 Request VO")
@Data
public class AppMerchantReferencePriceCreateReqVO {

    @Schema(description = "服务类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "服务类目不能为空")
    private Long categoryId;

    @Schema(description = "价格单位文案", requiredMode = Schema.RequiredMode.REQUIRED, example = "元/次")
    @NotBlank(message = "价格单位不能为空")
    private String priceUnitLabel;

    @Schema(description = "参考最低价", requiredMode = Schema.RequiredMode.REQUIRED, example = "88.00")
    @NotNull(message = "参考最低价不能为空")
    @DecimalMin(value = "0.00", message = "参考最低价不能小于 0")
    private BigDecimal referencePriceMin;

    @Schema(description = "参考最高价", requiredMode = Schema.RequiredMode.REQUIRED, example = "188.00")
    @NotNull(message = "参考最高价不能为空")
    @DecimalMin(value = "0.00", message = "参考最高价不能小于 0")
    private BigDecimal referencePriceMax;

    @Schema(description = "参考价格说明", example = "基础上门费已含，超出部分按现场情况另议")
    private String referencePriceDesc;
}

