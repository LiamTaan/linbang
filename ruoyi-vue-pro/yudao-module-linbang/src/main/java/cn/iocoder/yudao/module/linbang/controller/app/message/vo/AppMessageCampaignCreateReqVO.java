package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AppMessageCampaignCreateReqVO {

    @NotBlank(message = "投放名称不能为空")
    private String campaignName;

    private String targetRegionCodes;

    private String targetCategoryIds;

    private String targetRoleCodes;

    private String deliveryTimeWindows;

    @NotBlank(message = "内容不能为空")
    private String contentSnapshot;
}
