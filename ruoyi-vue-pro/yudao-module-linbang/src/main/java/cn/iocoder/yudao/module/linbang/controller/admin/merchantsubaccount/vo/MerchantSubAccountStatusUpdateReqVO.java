package cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 商户子账号状态更新 Request VO")
@Data
public class MerchantSubAccountStatusUpdateReqVO {

    @Schema(description = "子账号 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "子账号 ID 不能为空")
    private Long id;

    @Schema(description = "状态：ENABLE 启用、DISABLE 禁用", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotBlank(message = "状态不能为空")
    private String status;
}
