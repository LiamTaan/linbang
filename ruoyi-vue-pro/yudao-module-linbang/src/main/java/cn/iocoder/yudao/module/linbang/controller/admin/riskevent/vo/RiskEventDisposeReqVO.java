package cn.iocoder.yudao.module.linbang.controller.admin.riskevent.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 风险事件处置 Request VO")
@Data
public class RiskEventDisposeReqVO {

    @Schema(description = "风险事件 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "风险事件 ID 不能为空")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.RISK_EVENT_DISPOSE_ACTION, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "CONFIRM_VIOLATION")
    @NotBlank(message = "处置动作不能为空")
    private String disposeAction;

    @Schema(description = "处置备注，记录误判解除、处罚说明、冻结或解冻原因", example = "人工核实后确认违规，执行限制接单和冻结资金")
    private String disposeRemark;
}
