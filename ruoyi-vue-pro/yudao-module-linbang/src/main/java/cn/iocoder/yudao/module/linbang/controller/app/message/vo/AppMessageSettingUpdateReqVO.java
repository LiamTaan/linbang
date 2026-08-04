package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "用户 App - 消息中心 更新 Request VO")
public class AppMessageSettingUpdateReqVO {

    @NotNull(message = "语音朗读开关不能为空")
    @Schema(description = "是否启用消息语音朗读：true 启用、false 关闭")
    private Boolean voiceReadEnabled;

    @NotNull(message = "弹窗开关不能为空")
    @Schema(description = "是否允许站内弹窗：true 允许、false 禁止")
    private Boolean popupEnabled;

    @NotNull(message = "营销开关不能为空")
    @Schema(description = "是否接收营销消息：true 接收、false 不接收")
    private Boolean marketingEnabled;
}
