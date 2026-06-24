package cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 帮助反馈 Response VO")
@Data
public class AppHelpFeedbackRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "反馈分类")
    private String feedbackType;

    @Schema(description = "反馈内容")
    private String content;

    @Schema(description = "联系电话")
    private String contactMobile;

    @Schema(description = "附件地址")
    private String attachmentUrls;

    @Schema(description = "反馈处理状态：PENDING 待处理、PROCESSING 处理中、FINISHED 已完结", example = "PENDING")
    private String status;

    @Schema(description = "处理备注")
    private String handleRemark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
