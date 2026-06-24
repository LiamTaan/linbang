package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 绑定邀请码 Request VO")
@Data
public class AppPromoteInviteCodeBindReqVO {

    @Schema(description = "邀请码", requiredMode = Schema.RequiredMode.REQUIRED, example = "LB44C2C5E8")
    @NotBlank(message = "邀请码不能为空")
    private String inviteCode;
}
