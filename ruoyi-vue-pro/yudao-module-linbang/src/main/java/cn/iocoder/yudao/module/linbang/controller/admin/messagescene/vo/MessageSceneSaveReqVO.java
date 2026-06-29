package cn.iocoder.yudao.module.linbang.controller.admin.messagescene.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "管理后台 - 消息场景新增/修改 Request VO")
@Data
public class MessageSceneSaveReqVO {

    @Schema(description = "主键", example = "1")
    private Long id;

    @Schema(description = "场景编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORDER_STATUS_CHANGED")
    @NotBlank(message = "场景编码不能为空")
    @Size(max = 64, message = "场景编码不能超过 64 个字符")
    private String sceneCode;

    @Schema(description = "场景名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "订单状态通知")
    @NotBlank(message = "场景名称不能为空")
    @Size(max = 128, message = "场景名称不能超过 128 个字符")
    private String sceneName;

    @Schema(description = "消息分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "ORDER")
    @NotBlank(message = "消息分类不能为空")
    private String messageCategory;

    @Schema(description = "默认渠道，逗号分隔", requiredMode = Schema.RequiredMode.REQUIRED, example = "APP_POPUP,APP_VOICE")
    @NotBlank(message = "默认渠道不能为空")
    private String defaultChannels;

    @Schema(description = "是否强制短信", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否强制短信不能为空")
    private Boolean mandatorySms;

    @Schema(description = "是否支持语音朗读", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "语音朗读开关不能为空")
    private Boolean voiceEnabled;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotBlank(message = "状态不能为空")
    private String status;

    @Schema(description = "业务类型", example = "ORDER")
    private String bizType;

    @Schema(description = "备注")
    private String remark;
}
