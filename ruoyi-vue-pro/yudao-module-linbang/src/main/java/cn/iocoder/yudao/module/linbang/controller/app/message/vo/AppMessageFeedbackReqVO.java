package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(description = "用户 App - 消息中心 Request VO")
public class AppMessageFeedbackReqVO {

    @NotNull(message = "消息记录 ID 不能为空")
    @Schema(description = "消息发送记录 ID")
    private Long recordId;
}
