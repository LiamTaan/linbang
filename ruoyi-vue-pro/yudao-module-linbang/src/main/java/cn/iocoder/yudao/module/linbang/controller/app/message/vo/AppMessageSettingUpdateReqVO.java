package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AppMessageSettingUpdateReqVO {

    @NotNull(message = "语音朗读开关不能为空")
    private Boolean voiceReadEnabled;

    @NotNull(message = "弹窗开关不能为空")
    private Boolean popupEnabled;

    @NotNull(message = "营销开关不能为空")
    private Boolean marketingEnabled;
}
