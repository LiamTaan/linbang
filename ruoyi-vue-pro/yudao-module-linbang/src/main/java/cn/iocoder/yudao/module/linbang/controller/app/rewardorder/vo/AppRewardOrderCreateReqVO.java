package cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 我的悬赏创建 Request VO")
@Data
public class AppRewardOrderCreateReqVO {

    @Schema(description = "悬赏标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "悬赏标题不能为空")
    private String title;

    @Schema(description = "悬赏说明", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "悬赏说明不能为空")
    private String description;

    @Schema(description = "凭证文件 ID 列表 JSON", requiredMode = Schema.RequiredMode.REQUIRED, example = "[11,12]")
    @NotBlank(message = "凭证文件不能为空")
    private String evidenceFileIdsJson;
}
