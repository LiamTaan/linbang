package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 发送站内消息 Request VO")
@Data
public class AppMessageSendReqVO {

    @Schema(description = "接收用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1002")
    @NotNull(message = "接收用户不能为空")
    private Long receiverUserId;

    @Schema(description = "消息标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "订单沟通")
    @NotBlank(message = "消息标题不能为空")
    private String title;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "消息内容不能为空")
    private String content;
}
