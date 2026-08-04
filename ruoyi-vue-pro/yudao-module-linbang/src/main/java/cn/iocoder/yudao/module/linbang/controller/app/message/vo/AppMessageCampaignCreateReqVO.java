package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Schema(description = "用户 App - 消息中心 创建 Request VO")
public class AppMessageCampaignCreateReqVO {

    @NotBlank(message = "投放名称不能为空")
    @Schema(description = "消息投放活动名称")
    private String campaignName;

    @Schema(description = "目标高德行政区划编码列表；为空表示不按区域限制")
    private String targetRegionCodes;

    @Schema(description = "目标服务分类 ID 列表；为空表示不按分类限制")
    private String targetCategoryIds;

    @Schema(description = "目标用户角色编码列表；为空表示不按角色限制")
    private String targetRoleCodes;

    @Schema(description = "允许投放的时间窗口列表，例如 09:00-12:00；为空表示不限制")
    private String deliveryTimeWindows;

    @NotBlank(message = "内容不能为空")
    @Schema(description = "发送时固化的消息正文快照")
    private String contentSnapshot;
}
