package cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "用户 App - 创建商户子账号 Request VO")
@Data
public class AppMerchantSubAccountCreateReqVO {

    @Schema(description = "子账号用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Long userId;

    @Schema(description = "子账号手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "13800138000")
    @NotBlank(message = "子账号手机号不能为空")
    private String mobile;

    @Schema(description = "权限编码列表：ORDER_ACCEPT、MERCHANT_MANAGE、SERVICE_POINT_MANAGE", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "权限不能为空")
    private List<String> permissionCodes;

    @Schema(description = "备注", example = "浦东早班接单员")
    private String remark;
}
