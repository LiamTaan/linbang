package cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Schema(description = "用户 App - 服务商参考价格状态更新 Request VO")
@Data
public class AppMerchantReferencePriceStatusUpdateReqVO {

    @Schema(description = "参考价格 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "参考价格 ID 不能为空")
    private Long id;

    @Schema(description = "状态，仅支持 ENABLE 或 DISABLE", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotBlank(message = "状态不能为空")
    @Pattern(regexp = "^(ENABLE|DISABLE)$", message = "状态仅支持 ENABLE 或 DISABLE")
    private String status;
}

