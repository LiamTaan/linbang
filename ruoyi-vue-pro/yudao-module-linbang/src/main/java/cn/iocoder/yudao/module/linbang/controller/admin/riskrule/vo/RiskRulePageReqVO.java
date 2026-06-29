package cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 风控规则表分页 Request VO")
@Data
public class RiskRulePageReqVO extends PageParam {

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称", example = "用户单日发单次数上限")
    private String ruleName;

    @Schema(description = "规则分组")
    private String ruleGroup;

    @Schema(description = "规则值")
    private String ruleValue;

    @Schema(description = OpenApiSchemaConstants.RISK_RULE_VALUE_TYPE, example = "INTEGER")
    private String valueType;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "备注", example = "超过阈值时转人工复核")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
