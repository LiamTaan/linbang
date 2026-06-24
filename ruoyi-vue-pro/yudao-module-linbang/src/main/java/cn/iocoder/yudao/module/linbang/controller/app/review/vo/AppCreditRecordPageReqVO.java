package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 App - 信用记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppCreditRecordPageReqVO extends PageParam {

    @Schema(description = "规则编码", example = "ORDER_FINISHED_POSITIVE")
    private String ruleCode;

    @Schema(description = "触发类型", example = "AUTO")
    private String triggerType;

    @Schema(description = "业务类型", example = "REVIEW")
    private String bizType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
