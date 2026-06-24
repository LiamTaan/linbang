package cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 风控规则表 Response VO")
@Data
@ExcelIgnoreUnannotated
public class RiskRuleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "32687")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("规则编码")
    private String ruleCode;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "??")
    @ExcelProperty("规则名称")
    private String ruleName;

    @Schema(description = "规则分组", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("规则分组")
    private String ruleGroup;

    @Schema(description = "规则值", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("规则值")
    private String ruleValue;

    @Schema(description = "值类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("值类型")
    private String valueType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "备注", example = "????")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}