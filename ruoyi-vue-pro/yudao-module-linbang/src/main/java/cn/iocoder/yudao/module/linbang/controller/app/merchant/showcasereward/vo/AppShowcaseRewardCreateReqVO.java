package cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(description = "用户 App - 服务商晒单悬赏申请 Request VO")
@Data
public class AppShowcaseRewardCreateReqVO {

    @Schema(description = "案例标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "案例标题不能为空")
    private String title;

    @Schema(description = "案例说明", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "案例说明不能为空")
    private String description;

    @Schema(description = "凭证文件 ID 列表 JSON", requiredMode = Schema.RequiredMode.REQUIRED, example = "[11,12]")
    @NotBlank(message = "凭证文件不能为空")
    private String evidenceFileIdsJson;
}
