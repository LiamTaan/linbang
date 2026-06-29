package cn.iocoder.yudao.module.linbang.controller.app.merchant.dispatchsetting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "用户 App - 服务商派单设置更新 Request VO")
@Data
public class AppMerchantDispatchSettingUpdateReqVO {

    @Schema(description = "是否参与系统派单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "派单开关不能为空")
    private Boolean dispatchEnabled;

    @Schema(description = "最大接单半径，单位公里", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    @NotNull(message = "最大接单半径不能为空")
    private BigDecimal maxAcceptRadiusKm;

    @Schema(description = "是否开启语音提醒", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "语音提醒开关不能为空")
    private Boolean voiceRemindEnabled;
}
