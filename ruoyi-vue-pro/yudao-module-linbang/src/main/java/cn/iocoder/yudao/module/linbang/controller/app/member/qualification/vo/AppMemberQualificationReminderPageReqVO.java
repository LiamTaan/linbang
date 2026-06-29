package cn.iocoder.yudao.module.linbang.controller.app.member.qualification.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 证件提醒分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppMemberQualificationReminderPageReqVO extends PageParam {

    @Schema(description = "已读状态", example = "UNREAD")
    private String readStatus;
}
