package cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 投诉分页 Request VO")
@Data
public class ComplaintPageReqVO extends PageParam {

    @Schema(description = "投诉单号")
    private String complaintNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "单元号")
    private String unitNo;

    @Schema(description = "投诉人关键词，支持用户编号/昵称/手机号", example = "13800138000")
    private String complainantUserKeyword;

    @Schema(description = "被投诉人关键词，支持用户编号/昵称/手机号", example = "13800138001")
    private String respondentUserKeyword;

    @Schema(description = "投诉类型", example = "2")
    private String complaintType;

    @Schema(description = "投诉内容")
    private String content;

    @Schema(description = OpenApiSchemaConstants.COMPLAINT_STATUS, example = "PENDING")
    private String status;

    @Schema(description = "处理人")
    private Long handleBy;

    @Schema(description = "处理时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] handleTime;

    @Schema(description = "处理结果")
    private String resultDesc;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
