package cn.iocoder.yudao.module.linbang.controller.admin.promoteappeal.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 推广申诉分页 Request VO")
@Data
public class PromoteAppealPageReqVO extends PageParam {

    @Schema(description = "推广员 ID", example = "1001")
    private Long promoterId;

    @Schema(description = "用户 ID", example = "2001")
    private Long userId;

    @Schema(description = "推广内容 ID", example = "3001")
    private Long contentId;

    @Schema(description = "申诉状态", example = "PENDING")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
