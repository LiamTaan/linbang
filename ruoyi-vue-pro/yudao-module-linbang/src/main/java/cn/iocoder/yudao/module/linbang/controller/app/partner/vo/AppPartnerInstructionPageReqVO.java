package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "用户 App - 合作商会议通知/上级指令分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPartnerInstructionPageReqVO extends PageParam {

    @Schema(description = "消息分类，仅支持 MEETING_NOTICE / SUPERIOR_INSTRUCTION", example = "MEETING_NOTICE")
    private String messageCategory;
}
