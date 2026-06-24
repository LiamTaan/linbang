package cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 风控规则表新增/修改 Request VO")
@Data
public class RiskRuleSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32687")
    private Long id;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规则编码不能为空")
    private String ruleCode;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @NotEmpty(message = "规则名称不能为空")
    private String ruleName;

    @Schema(description = "规则分组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规则分组不能为空")
    private String ruleGroup;

    @Schema(description = "规则值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规则值不能为空")
    private String ruleValue;

    @Schema(description = "值类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "值类型不能为空")
    private String valueType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "备注", example = "????")
    private String remark;

}