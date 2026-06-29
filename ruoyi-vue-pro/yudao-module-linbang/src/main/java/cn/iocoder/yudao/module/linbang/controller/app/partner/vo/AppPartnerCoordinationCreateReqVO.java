package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 合作商发起协调 Request VO")
@Data
public class AppPartnerCoordinationCreateReqVO {

    @Schema(description = "纠纷类型：COMPLAINT / APPEAL", requiredMode = Schema.RequiredMode.REQUIRED, example = "COMPLAINT")
    @NotBlank(message = "纠纷类型不能为空")
    private String disputeType;

    @Schema(description = "纠纷主键 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "纠纷主键不能为空")
    private Long disputeId;

    @Schema(description = "协调意见", requiredMode = Schema.RequiredMode.REQUIRED, example = "已联系双方，建议按平台规则补齐凭证")
    @NotBlank(message = "协调意见不能为空")
    private String coordinationRemark;

    @Schema(description = "是否升级平台终审", example = "true")
    private Boolean escalateToPlatform;

    @Schema(description = "升级平台终审备注", example = "双方无法达成一致，请平台终审")
    private String escalateRemark;
}
