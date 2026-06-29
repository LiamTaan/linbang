package cn.iocoder.yudao.module.linbang.controller.app.reminder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 修改提醒 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppReminderUpdateReqVO extends AppReminderCreateReqVO {

    @Schema(description = "提醒 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "提醒 ID 不能为空")
    private Long id;
}
