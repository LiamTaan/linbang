package cn.iocoder.yudao.module.linbang.controller.app.help.faq.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 常见问题 Response VO")
@Data
public class AppHelpFaqRespVO {

    @Schema(description = "常见问题 ID", example = "1")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_CATEGORY, example = "FUNDS")
    private String categoryCode;

    @Schema(description = "分类名称，App 常见问题列表入口展示", example = "资金与提现")
    private String categoryName;

    @Schema(description = "问题标题，App 搜索结果和详情页展示", example = "提现多久到账？")
    private String title;

    @Schema(description = "问题答案正文，说明处理规则、路径和注意事项")
    private String content;

    @Schema(description = "App 图标标识，前端按标识映射本地图标", example = "fund")
    private String icon;

    @Schema(description = "排序号，数值越小越靠前", example = "10")
    private Integer sortNo;
}
