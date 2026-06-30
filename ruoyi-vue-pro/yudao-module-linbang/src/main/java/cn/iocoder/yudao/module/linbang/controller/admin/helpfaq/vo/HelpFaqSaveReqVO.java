package cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Schema(description = "管理后台 - 常见问题新增/修改 Request VO")
@Data
public class HelpFaqSaveReqVO {

    @Schema(description = "常见问题 ID；新增时为空，修改时必填", example = "1")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_CATEGORY, requiredMode = Schema.RequiredMode.REQUIRED, example = "FUNDS")
    @NotBlank(message = "分类编码不能为空")
    @Size(max = 64, message = "分类编码不能超过 64 个字符")
    private String categoryCode;

    @Schema(description = "分类名称，App 按该名称展示常见问题入口", requiredMode = Schema.RequiredMode.REQUIRED, example = "资金与提现")
    @NotBlank(message = "分类名称不能为空")
    @Size(max = 64, message = "分类名称不能超过 64 个字符")
    private String categoryName;

    @Schema(description = "问题标题，App 搜索和详情页展示", requiredMode = Schema.RequiredMode.REQUIRED, example = "提现多久到账？")
    @NotBlank(message = "问题标题不能为空")
    @Size(max = 128, message = "问题标题不能超过 128 个字符")
    private String title;

    @Schema(description = "问题答案正文，说明该分类下的规则、处理路径和注意事项", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问题答案不能为空")
    @Size(max = 4000, message = "问题答案不能超过 4000 个字符")
    private String content;

    @Schema(description = "App 图标标识，前端按标识映射本地图标，例如 fund/order/verify/order_match/voice", example = "fund")
    @Size(max = 64, message = "图标标识不能超过 64 个字符")
    private String icon;

    @Schema(description = "排序号，数值越小越靠前", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "排序号不能为空")
    private Integer sortNo;

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "ENABLE")
    @NotBlank(message = "状态不能为空")
    @Size(max = 32, message = "状态不能超过 32 个字符")
    private String status;
}
