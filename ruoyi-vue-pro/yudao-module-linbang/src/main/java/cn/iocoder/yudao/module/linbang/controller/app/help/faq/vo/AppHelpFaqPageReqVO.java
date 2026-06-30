package cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;

@Schema(description = "用户 App - 常见问题查询 Request VO")
@Data
public class AppHelpFaqPageReqVO {

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_CATEGORY, example = "FUNDS")
    @Size(max = 64, message = "分类编码不能超过 64 个字符")
    private String categoryCode;

    @Schema(description = "搜索关键字，会匹配问题标题和答案正文", example = "提现")
    @Size(max = 64, message = "搜索关键字不能超过 64 个字符")
    private String keyword;
}
