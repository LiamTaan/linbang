package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 消息记录分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMessageRecordPageReqVO extends PageParam {

    @Schema(description = "发送状态")
    private String sendStatus;
}
