package cn.iocoder.yudao.module.linbang.controller.app.message.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppMessageCampaignPageReqVO extends PageParam {

    private String auditStatus;

    private String executeStatus;
}
