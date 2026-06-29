package cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

@Schema(description = "用户 App - 服务商接单状态更新 Request VO")
@Data
public class AppMerchantAcceptStatusUpdateReqVO {

    @Schema(description = "接单状态，仅支持 ENABLE 或 DISABLE", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotBlank(message = "接单状态不能为空")
    @Pattern(regexp = "^(ENABLE|DISABLE)$", message = "接单状态仅支持 ENABLE 或 DISABLE")
    private String acceptStatus;

    @Schema(description = "是否参与系统派单")
    private Boolean dispatchEnabled;

    @Schema(description = "最大接单半径，单位公里", example = "5")
    private BigDecimal maxAcceptRadiusKm;

    @Schema(description = "是否开启语音提醒")
    private Boolean voiceRemindEnabled;

}
