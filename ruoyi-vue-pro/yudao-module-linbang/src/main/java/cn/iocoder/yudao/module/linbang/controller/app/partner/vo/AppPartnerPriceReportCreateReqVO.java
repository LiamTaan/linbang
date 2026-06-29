package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "用户 App - 合作商价格申报创建 Request VO")
@Data
public class AppPartnerPriceReportCreateReqVO {

    @Schema(description = "服务商 ID", example = "1001")
    private Long merchantId;

    @Schema(description = "类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3001")
    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @Schema(description = "区域编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "440305")
    @NotBlank(message = "区域编码不能为空")
    private String regionCode;

    @Schema(description = "建议价格，单位元", requiredMode = Schema.RequiredMode.REQUIRED, example = "128.00")
    @NotNull(message = "建议价格不能为空")
    private BigDecimal suggestedPrice;

    @Schema(description = "申报备注", example = "本辖区人工成本偏高，建议上调")
    private String remark;
}
