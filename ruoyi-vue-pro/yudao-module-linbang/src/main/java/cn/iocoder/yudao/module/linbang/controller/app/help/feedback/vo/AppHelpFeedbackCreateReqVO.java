package cn.iocoder.yudao.module.linbang.controller.app.help.feedback.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - 帮助反馈新增 Request VO")
@Data
public class AppHelpFeedbackCreateReqVO {

    @Schema(description = "反馈分类", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "反馈分类不能为空")
    @Size(max = 64, message = "反馈分类不能超过 64 个字符")
    private String feedbackType;

    @Schema(description = "反馈内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "反馈内容不能为空")
    @Size(max = 2000, message = "反馈内容不能超过 2000 个字符")
    private String content;

    @Schema(description = "联系电话")
    @Size(max = 20, message = "联系电话不能超过 20 个字符")
    private String contactMobile;

    @Schema(description = "附件地址，多个以逗号分隔")
    @Size(max = 1000, message = "附件地址不能超过 1000 个字符")
    private String attachmentUrls;
}
