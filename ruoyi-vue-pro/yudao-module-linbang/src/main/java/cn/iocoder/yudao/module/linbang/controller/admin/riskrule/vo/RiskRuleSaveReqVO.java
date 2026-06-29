package cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "用户单日发单次数上限")
    @NotEmpty(message = "规则名称不能为空")
    private String ruleName;

    @Schema(description = "规则分组", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规则分组不能为空")
    private String ruleGroup;

    @Schema(description = "规则值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规则值不能为空")
    private String ruleValue;

    @Schema(description = OpenApiSchemaConstants.RISK_RULE_VALUE_TYPE, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "INTEGER")
    @NotEmpty(message = "值类型不能为空")
    private String valueType;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "备注", example = "超过阈值时转人工复核")
    private String remark;

}
