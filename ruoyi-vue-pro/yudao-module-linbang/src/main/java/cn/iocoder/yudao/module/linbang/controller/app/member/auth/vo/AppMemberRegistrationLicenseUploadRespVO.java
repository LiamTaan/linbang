package cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 企业注册营业执照预上传 Response VO")
@Data
public class AppMemberRegistrationLicenseUploadRespVO {

    @Schema(description = "营业执照文件 ID；后续账号注册时传入 businessLicenseFileId", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1001")
    private Long fileId;

    @Schema(description = "营业执照受控访问地址", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;
}
