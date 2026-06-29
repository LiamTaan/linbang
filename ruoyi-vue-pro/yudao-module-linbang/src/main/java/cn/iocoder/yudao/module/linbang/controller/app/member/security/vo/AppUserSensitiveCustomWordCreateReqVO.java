package cn.iocoder.yudao.module.linbang.controller.app.member.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 新增用户自定义脱敏词 Request VO")
@Data
public class AppUserSensitiveCustomWordCreateReqVO {

    @Schema(description = "关键词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "关键词不能为空")
    private String word;

    @Schema(description = "适用场景", example = "ORDER_PUBLISH")
    private String sceneType;

    @Schema(description = "备注")
    private String remark;
}
