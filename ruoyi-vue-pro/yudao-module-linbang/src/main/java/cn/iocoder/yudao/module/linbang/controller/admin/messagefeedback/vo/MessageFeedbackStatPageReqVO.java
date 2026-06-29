package cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageFeedbackStatPageReqVO extends PageParam {

    private String sceneCode;

    private String messageCategory;

    private String channelType;

    private Long templateId;

    private Long campaignId;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY)
    private LocalDate[] statDate;
}
