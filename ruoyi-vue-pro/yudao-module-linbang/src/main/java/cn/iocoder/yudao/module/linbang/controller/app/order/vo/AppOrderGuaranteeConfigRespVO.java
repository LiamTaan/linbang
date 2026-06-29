package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 交易保障配置 Response VO")
@Data
public class AppOrderGuaranteeConfigRespVO {

    @Schema(description = "当前交易保障协议版本", example = "v2026.06")
    private String agreementVersion;

    @Schema(description = "交易保障协议标题")
    private String agreementTitle;

    @Schema(description = "交易保障协议文案")
    private String serviceAgreement;

    @Schema(description = "工程托管协议标题")
    private String projectEscrowAgreementTitle;

    @Schema(description = "工程托管协议文案")
    private String projectEscrowAgreement;

    @Schema(description = "发单/接单前防逃单提示文案")
    private String antiEscapeNotice;

}
