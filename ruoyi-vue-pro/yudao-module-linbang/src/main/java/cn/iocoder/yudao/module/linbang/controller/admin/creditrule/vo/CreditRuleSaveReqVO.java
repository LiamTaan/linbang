package cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 信用分规则新增/修改 Request VO")
@Data
public class CreditRuleSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "18730")
    private Long id;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规则编码不能为空")
    private String ruleCode;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "规则名称不能为空")
    private String ruleName;

    @Schema(description = "分值变动", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分值变动不能为空")
    private Integer scoreChange;

    @Schema(description = "触发类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "触发类型不能为空")
    private String triggerType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotEmpty(message = "状态不能为空")
    private String status;

}