package cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 信用记录分页 Request VO")
@Data
public class CreditRecordPageReqVO extends PageParam {

    @Schema(description = "用户关键词，支持用户编号/昵称/手机号")
    private String userKeyword;

    @Schema(description = "服务商ID")
    private Long merchantId;

    @Schema(description = "信用规则ID")
    private Long ruleId;

    @Schema(description = "规则编码")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = OpenApiSchemaConstants.CREDIT_TRIGGER_TYPE, example = "AUTO")
    private String triggerType;

    @Schema(description = OpenApiSchemaConstants.CREDIT_BIZ_TYPE, example = "ORDER")
    private String bizType;

    @Schema(description = "业务 ID，关联订单、单元、投诉、申诉或提现等业务主键", example = "1001")
    private Long bizId;

    @Schema(description = "分值变动")
    private Integer scoreChange;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
