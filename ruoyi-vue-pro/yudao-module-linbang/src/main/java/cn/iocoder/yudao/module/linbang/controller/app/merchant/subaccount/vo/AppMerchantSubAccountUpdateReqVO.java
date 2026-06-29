package cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 修改商户子账号 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMerchantSubAccountUpdateReqVO extends AppMerchantSubAccountCreateReqVO {

    @Schema(description = "子账号 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "子账号 ID 不能为空")
    private Long id;
}
