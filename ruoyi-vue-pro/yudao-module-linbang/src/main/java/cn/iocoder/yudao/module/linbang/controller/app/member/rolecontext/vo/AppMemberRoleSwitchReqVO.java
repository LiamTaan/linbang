package cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 切换生效角色 Request VO")
@Data
public class AppMemberRoleSwitchReqVO {

    @Schema(description = "目标角色编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "PROMOTER")
    @NotBlank(message = "目标角色不能为空")
    private String targetRoleCode;
}
