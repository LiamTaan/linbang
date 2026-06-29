package cn.iocoder.yudao.module.linbang.controller.admin.match.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 流单退款重试 Request VO")
@Data
public class OrderFlowRetryRefundReqVO {

    @Schema(description = "单元 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单元不能为空")
    private Long unitId;

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
