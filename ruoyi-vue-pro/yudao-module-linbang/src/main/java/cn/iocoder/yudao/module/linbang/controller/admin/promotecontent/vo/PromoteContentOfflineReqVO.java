package cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 推广内容下架 Request VO")
@Data
public class PromoteContentOfflineReqVO {

    @Schema(description = "内容 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "内容 ID 不能为空")
    private Long id;

    @Schema(description = "下架原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "下架原因不能为空")
    private String offlineReason;

    @Schema(description = "处罚动作：DEMOTE/RESTRICT_PROMOTE/DISABLE_PROMOTER/SCORE")
    private String penaltyAction;

    @Schema(description = "扣分值，动作为 SCORE 时使用")
    private Integer scoreChange;

    @Schema(description = "处罚原因")
    private String penaltyReason;
}
