package cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 信用分规则分页 Request VO")
@Data
public class CreditRulePageReqVO extends PageParam {

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称", example = "李四")
    private String ruleName;

    @Schema(description = "分值变动")
    private Integer scoreChange;

    @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
    private String triggerType;

    @Schema(description = OpenApiSchemaConstants.CREDIT_RULE_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
