package cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 服务点更新 Request VO")
@Data
public class AppMerchantServicePointUpdateReqVO extends AppMerchantServicePointCreateReqVO {

    @Schema(description = "服务点 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "服务点 ID 不能为空")
    private Long id;

}
