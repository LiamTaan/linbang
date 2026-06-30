package cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 常见问题分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class HelpFaqPageReqVO extends PageParam {

    @Schema(description = "问题标题关键字", example = "提现")
    private String title;

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_CATEGORY, example = "FUNDS")
    private String categoryCode;

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "创建时间范围")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
