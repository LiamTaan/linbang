package cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 服务商接单状态 Response VO")
@Data
public class AppMerchantAcceptStatusRespVO {

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "服务商状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String merchantStatus;

    @Schema(description = "接单状态：ENABLE 可接单、DISABLE 暂停接单", example = "DISABLE")
    private String acceptStatus;

}
