package cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 用户解除限制/解除封禁 Request VO")
@Data
public class MemberUserReleaseRestrictReqVO {

    @Schema(description = "限制记录 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "限制记录不能为空")
    private Long restrictRecordId;

    @Schema(description = "解除备注", requiredMode = Schema.RequiredMode.REQUIRED, example = "人工复核通过，解除限制")
    @NotBlank(message = "解除备注不能为空")
    private String releaseRemark;
}
