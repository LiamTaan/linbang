package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 消息投放活动审核 Request VO")
@Data
public class MessageCampaignAuditReqVO {

    @Schema(description = "投放活动 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "投放活动 ID 不能为空")
    private Long id;

    @Schema(description = "审核备注", example = "定向人群和文案已确认")
    @NotBlank(message = "审核备注不能为空")
    private String auditRemark;
}
