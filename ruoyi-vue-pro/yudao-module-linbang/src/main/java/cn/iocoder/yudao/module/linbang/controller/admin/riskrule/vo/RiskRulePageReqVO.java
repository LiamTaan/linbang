package cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo;

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

    @Schema(description = "规则名称", example = "??")
    private String ruleName;

    @Schema(description = "规则分组")
    private String ruleGroup;

    @Schema(description = "规则值")
    private String ruleValue;

    @Schema(description = "值类型", example = "2")
    private String valueType;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "备注", example = "????")
    private String remark;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}