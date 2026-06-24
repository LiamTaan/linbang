package cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 服务商入驻审核 Request VO")
@Data
public class MerchantEntryAuditReqVO {

    @Schema(description = "入驻申请编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "入驻申请编号不能为空")
    private Long id;

    @Schema(description = "审核状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "FIRST_APPROVED")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注", example = "初审通过")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "资质不齐")
    private String rejectReason;

}
