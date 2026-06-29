package cn.iocoder.yudao.module.linbang.controller.app.promote.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 推广申诉提交 Request VO")
@Data
public class AppPromoteAppealCreateReqVO {

    @Schema(description = "推广内容 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "推广内容 ID 不能为空")
    private Long contentId;

    @Schema(description = "申诉原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "申诉原因不能为空")
    private String appealReason;
}
