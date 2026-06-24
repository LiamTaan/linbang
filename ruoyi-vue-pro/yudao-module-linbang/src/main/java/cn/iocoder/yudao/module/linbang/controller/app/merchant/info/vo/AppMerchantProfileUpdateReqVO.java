package cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - 服务商资料更新 Request VO")
@Data
public class AppMerchantProfileUpdateReqVO {

    @Schema(description = "服务商名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "LinBang Services")
    @NotBlank(message = "服务商名称不能为空")
    @Size(max = 64, message = "服务商名称不能超过 64 个字符")
    private String merchantName;

    @Schema(description = "联系人", requiredMode = Schema.RequiredMode.REQUIRED, example = "Alice")
    @NotBlank(message = "联系人不能为空")
    @Size(max = 32, message = "联系人不能超过 32 个字符")
    private String contactName;

    @Schema(description = "联系人手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "联系人手机号不能为空")
    @Size(max = 20, message = "联系人手机号不能超过 20 个字符")
    private String contactMobile;

    @Schema(description = "服务范围说明", example = "Home repair and cleaning")
    @Size(max = 500, message = "服务范围说明不能超过 500 个字符")
    private String serviceScopeDesc;

}
