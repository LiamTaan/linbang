package cn.iocoder.yudao.module.linbang.controller.admin.helpfeedback.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@Schema(description = "管理后台 - 帮助反馈 分页查询 Request VO")
public class HelpFeedbackPageReqVO extends PageParam {

    @Schema(description = "用户筛选关键词，可匹配用户编号、昵称或手机号")
    private String userKeyword;

    @Schema(description = "反馈分类，按平台帮助反馈分类字典展示，例如功能建议、异常反馈、投诉建议")
    private String feedbackType;

    @Schema(description = "联系人手机号")
    private String contactMobile;

    @Schema(description = "反馈处理状态筛选：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结")
    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    @Schema(description = "记录创建时间")
    private LocalDateTime[] createTime;
}
