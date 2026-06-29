package cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Data
public class MessageTemplatePageReqVO extends PageParam {

    private String templateCode;

    private String templateName;

    private String sceneCode;

    private String messageCategory;

    private String templateType;

    private String channelType;

    private String status;

    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;
}
