package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 消息投放新增/修改 Request VO")
@Data
public class MessageCampaignSaveReqVO {

    private Long id;

    @NotBlank(message = "投放名称不能为空")
    private String campaignName;

    @NotBlank(message = "来源类型不能为空")
    private String sourceType;

    @NotBlank(message = "目标模式不能为空")
    private String targetMode;

    private String targetRegionCodes;

    private String targetCategoryIds;

    private String targetRoleCodes;

    private String deliveryTimeWindows;

    private LocalDateTime scheduleTime;

    @NotBlank(message = "场景编码不能为空")
    private String sceneCode;

    @NotBlank(message = "消息分类不能为空")
    private String messageCategory;

    private String bizType;

    private Long bizId;

    @NotNull(message = "内容快照不能为空")
    private String contentSnapshot;
}
