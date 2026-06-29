package cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 推广内容分页 Request VO")
@Data
public class PromoteContentPageReqVO extends PageParam {

    @Schema(description = "推广员 ID", example = "1001")
    private Long promoterId;

    @Schema(description = "用户 ID", example = "2001")
    private Long userId;

    @Schema(description = "内容标题，支持模糊搜索", example = "暑期保洁活动")
    private String title;

    @Schema(description = "内容状态", example = "PENDING_MANUAL_AUDIT")
    private String status;

    @Schema(description = "系统审核结果", example = "PASS")
    private String systemAuditResult;

    @Schema(description = "人工审核结果", example = "APPROVED")
    private String manualAuditResult;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
