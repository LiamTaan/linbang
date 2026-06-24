package cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 评价分页 Request VO")
@Data
public class ReviewCommentPageReqVO extends PageParam {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "单元号")
    private String unitNo;

    @Schema(description = "评价发起人关键词，支持用户编号/昵称/手机号", example = "13800138000")
    private String fromUserKeyword;

    @Schema(description = "评价目标用户关键词，支持用户编号/昵称/手机号", example = "13800138001")
    private String toUserKeyword;

    @Schema(description = "星级")
    private Integer starLevel;

    @Schema(description = "评价内容")
    private String content;

    @Schema(description = "是否自动评价")
    private Boolean isAutoReview;

    @Schema(description = "状态", example = "2")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
