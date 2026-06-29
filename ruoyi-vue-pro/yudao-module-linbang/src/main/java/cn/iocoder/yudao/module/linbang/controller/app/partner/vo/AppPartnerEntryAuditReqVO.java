package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 合作商入驻初审 Request VO")
@Data
public class AppPartnerEntryAuditReqVO {

    @Schema(description = "入驻申请 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "入驻申请编号不能为空")
    private Long id;

    @Schema(description = "初审结果：APPROVED 通过、REJECTED 驳回", requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "初审结果不能为空")
    private String auditStatus;

    @Schema(description = "初审意见", example = "辖区资质材料齐全，同意进入平台终审")
    private String auditRemark;

    @Schema(description = "驳回原因，驳回时必填", example = "辖区经营范围不匹配")
    private String rejectReason;
}
