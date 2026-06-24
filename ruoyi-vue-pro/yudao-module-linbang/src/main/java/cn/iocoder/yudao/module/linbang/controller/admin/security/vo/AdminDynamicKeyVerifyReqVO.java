package cn.iocoder.yudao.module.linbang.controller.admin.security.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "管理后台 - 动态密钥校验 Request VO")
@Data
public class AdminDynamicKeyVerifyReqVO {

    @Schema(description = "动态密钥口令", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "动态密钥不能为空")
    private String password;
}
