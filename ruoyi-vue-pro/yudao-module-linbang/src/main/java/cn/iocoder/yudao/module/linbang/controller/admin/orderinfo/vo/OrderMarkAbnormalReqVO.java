package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 订单标记异常 Request VO")
@Data
public class OrderMarkAbnormalReqVO {

    @Schema(description = "订单编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单编号不能为空")
    private Long orderId;

    @Schema(description = "单元编号", example = "1")
    private Long unitId;

    @Schema(description = "异常类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "MANUAL")
    @NotBlank(message = "异常类型不能为空")
    private String abnormalType;

    @Schema(description = "风险等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "HIGH")
    @NotBlank(message = "风险等级不能为空")
    private String riskLevel;

    @Schema(description = "命中规则编码", example = "SPLIT_AMOUNT_THRESHOLD")
    private String hitRuleCode;

    @Schema(description = "备注", example = "人工巡检发现异常")
    private String remark;

}
