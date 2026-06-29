package cn.iocoder.yudao.module.linbang.controller.app.merchant.dispatchsetting.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "用户 App - 服务商派单设置 Response VO")
@Data
public class AppMerchantDispatchSettingRespVO {

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "是否参与系统派单")
    private Boolean dispatchEnabled;

    @Schema(description = "最大接单半径，单位公里")
    private BigDecimal maxAcceptRadiusKm;

    @Schema(description = "是否开启语音提醒")
    private Boolean voiceRemindEnabled;
}
