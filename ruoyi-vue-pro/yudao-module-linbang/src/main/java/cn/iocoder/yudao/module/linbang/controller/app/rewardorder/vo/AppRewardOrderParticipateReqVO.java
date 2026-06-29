package cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "用户 App - 参与悬赏 Request VO")
@Data
public class AppRewardOrderParticipateReqVO {

    @Schema(description = "悬赏 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "悬赏 ID 不能为空")
    private Long rewardOrderId;

    @Schema(description = "参与说明", example = "我可以提供本地转介绍资源")
    @Size(max = 255, message = "参与说明长度不能超过 255 个字符")
    private String participateRemark;
}
