package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import cn.iocoder.yudao.framework.common.validation.Mobile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 企业注册营业执照预上传 Request VO")
@Data
public class AppMemberRegistrationLicenseUploadReqVO {

    @Schema(description = "注册手机号；必须与后续账号注册请求一致", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "13800138000")
    @Mobile
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "注册短信验证码；仅校验，本接口不消费验证码", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1234")
    @NotBlank(message = "短信验证码不能为空")
    private String smsCode;

    @Schema(description = "营业执照图片，单文件最大 20 MiB", requiredMode = Schema.RequiredMode.REQUIRED,
            type = "string", format = "binary")
    @NotNull(message = "营业执照图片不能为空")
    private MultipartFile file;
}
