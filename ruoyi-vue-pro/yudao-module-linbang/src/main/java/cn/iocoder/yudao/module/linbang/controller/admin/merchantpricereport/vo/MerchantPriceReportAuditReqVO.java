package cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 价格申报审核 Request VO")
@Data
public class MerchantPriceReportAuditReqVO {

    @Schema(description = "申报编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "申报编号不能为空")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.PRICE_REPORT_AUDIT_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "APPROVED")
    @NotBlank(message = "审核状态不能为空")
    private String auditStatus;

    @Schema(description = "审核备注", example = "价格合理")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "价格偏离区间")
    private String rejectReason;
}
