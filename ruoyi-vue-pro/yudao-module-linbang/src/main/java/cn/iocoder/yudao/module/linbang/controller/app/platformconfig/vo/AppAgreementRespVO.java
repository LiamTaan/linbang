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

    @Schema(description = "注册协议版本", example = "v2026.06")
    private String registerAgreementVersion;

    @Schema(description = "注册协议标题")
    private String registerAgreementTitle;

    @Schema(description = "注册协议内容")
    private String registerAgreementContent;

    @Schema(description = "受益人连带责任说明")
    private String beneficiaryLiabilityNotice;
}
