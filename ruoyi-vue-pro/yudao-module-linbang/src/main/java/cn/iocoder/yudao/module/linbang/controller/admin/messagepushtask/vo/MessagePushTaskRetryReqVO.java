package cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 消息推送任务失败重试 Request VO")
@Data
public class MessagePushTaskRetryReqVO {

    @Schema(description = "推送任务 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "推送任务 ID 不能为空")
    private Long id;
}
