package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AppMemberSceneTicketCreateReqVO {

    @Schema(description = "场景，只允许 WEBSOCKET", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "场景不能为空")
    private String scene;

}
