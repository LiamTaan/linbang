package cn.iocoder.yudao.module.linbang.controller.admin.promoterpenaltyrecord.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 推广员处罚记录分页 Request VO")
@Data
public class PromoterPenaltyRecordPageReqVO extends PageParam {

    @Schema(description = "推广员 ID", example = "1001")
    private Long promoterId;

    @Schema(description = "处罚动作", example = "DEMOTE")
    private String penaltyAction;

    @Schema(description = "状态", example = "ACTIVE")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
