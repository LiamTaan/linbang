package cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 协议文案 Response VO")
@Data
public class AppAgreementRespVO {

    @Schema(description = "服务协议")
    private String serviceAgreement;

    @Schema(description = "隐私协议")
    private String privacyAgreement;
}
