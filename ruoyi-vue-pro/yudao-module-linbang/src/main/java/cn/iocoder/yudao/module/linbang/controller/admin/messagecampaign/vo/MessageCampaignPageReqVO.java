package cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageCampaignPageReqVO extends PageParam {

    private String campaignName;

    private String sourceType;

    private String auditStatus;

    private String executeStatus;

    private String targetMode;

    private String sceneCode;

    private String messageCategory;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
