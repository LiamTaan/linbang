package cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 申诉分页 Request VO")
@Data
public class AppealPageReqVO extends PageParam {

    @Schema(description = "申诉单号")
    private String appealNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "单元号")
    private String unitNo;

    @Schema(description = "申诉人关键词，支持用户编号/昵称/手机号", example = "13800138000")
    private String userKeyword;

    @Schema(description = "申诉类型", example = "2")
    private String appealType;

    @Schema(description = "申诉内容")
    private String content;

    @Schema(description = OpenApiSchemaConstants.APPEAL_STATUS, example = "PENDING")
    private String status;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "PENDING")
    private String auditStatus;

    @Schema(description = "审核人")
    private Long auditBy;

    @Schema(description = "审核时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] auditTime;

    @Schema(description = "审核备注", example = "你猜")
    private String auditRemark;

    @Schema(description = "驳回原因", example = "不喜欢")
    private String rejectReason;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
