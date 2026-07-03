package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 删除交付凭证 Request VO")
@Data
public class AppDeliveryProofDeleteReqVO {

    @Schema(description = "凭证 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "凭证 ID 不能为空")
    private Long proofId;
}
