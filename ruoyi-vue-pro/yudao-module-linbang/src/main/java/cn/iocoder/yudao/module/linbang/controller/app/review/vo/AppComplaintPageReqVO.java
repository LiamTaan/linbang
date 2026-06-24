package cn.iocoder.yudao.module.linbang.controller.app.review.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "用户 App - 投诉分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppComplaintPageReqVO extends PageParam {

    @Schema(description = "投诉状态筛选：PENDING 待受理、PROCESSING 处理中、FINISHED 已完结、REJECTED 已驳回", example = "PENDING")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
