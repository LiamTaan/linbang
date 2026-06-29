package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 优先池冻结/解冻 Request VO")
@Data
public class PriorityPoolFreezeReqVO {

    @Schema(description = "服务商 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "服务商不能为空")
    private Long merchantId;

    @Schema(description = "原因说明", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原因说明不能为空")
    private String reasonRemark;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getReasonRemark() {
        return reasonRemark;
    }

    public void setReasonRemark(String reasonRemark) {
        this.reasonRemark = reasonRemark;
    }
}
