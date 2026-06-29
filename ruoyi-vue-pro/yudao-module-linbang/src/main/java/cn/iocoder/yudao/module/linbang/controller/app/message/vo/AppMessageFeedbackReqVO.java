package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AppMessageFeedbackReqVO {

    @NotNull(message = "消息记录 ID 不能为空")
    private Long recordId;
}
