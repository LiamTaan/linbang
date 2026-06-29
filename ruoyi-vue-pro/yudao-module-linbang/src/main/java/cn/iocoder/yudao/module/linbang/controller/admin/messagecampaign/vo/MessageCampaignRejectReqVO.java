package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 消息投放活动驳回 Request VO")
@Data
public class MessageCampaignRejectReqVO {

    @Schema(description = "投放活动 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "投放活动 ID 不能为空")
    private Long id;

    @Schema(description = "驳回原因", requiredMode = Schema.RequiredMode.REQUIRED, example = "定向条件过宽，需要缩小区域")
    @NotBlank(message = "驳回原因不能为空")
    private String rejectReason;
}
