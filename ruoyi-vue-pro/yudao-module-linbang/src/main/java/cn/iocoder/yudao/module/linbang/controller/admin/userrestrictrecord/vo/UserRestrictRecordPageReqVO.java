package cn.iocoder.yudao.module.linbang.controller.admin.userrestrictrecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 用户限制记录分页 Request VO")
@Data
public class UserRestrictRecordPageReqVO extends PageParam {

    private Long userId;

    @Schema(description = "用户关键词")
    private String userKeyword;

    @Schema(description = "限制类型")
    private String restrictType;

    @Schema(description = "状态")
    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
