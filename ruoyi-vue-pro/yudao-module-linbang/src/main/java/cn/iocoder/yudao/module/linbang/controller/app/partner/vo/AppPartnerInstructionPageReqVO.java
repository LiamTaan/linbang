package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Pattern;

@Schema(description = "用户 App - 合作商会议通知/上级指令分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppPartnerInstructionPageReqVO extends PageParam {

    @Schema(description = OpenApiSchemaConstants.PARTNER_INSTRUCTION_CATEGORY, example = "MEETING_NOTICE")
    @Pattern(regexp = "(?i)^(MEETING_NOTICE|SUPERIOR_INSTRUCTION)$",
            message = "消息分类仅支持 MEETING_NOTICE 或 SUPERIOR_INSTRUCTION")
    private String messageCategory;
}
