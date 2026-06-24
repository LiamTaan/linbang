package cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 信用分规则 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CreditRuleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "18730")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "规则编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("规则编码")
    private String ruleCode;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("规则名称")
    private String ruleName;

    @Schema(description = "分值变动", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("分值变动")
    private Integer scoreChange;

    @Schema(description = "触发类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("触发类型")
    private String triggerType;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}