package cn.iocoder.yudao.module.linbang.controller.admin.helpfaq.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 常见问题 Response VO")
@Data
@ExcelIgnoreUnannotated
public class HelpFaqRespVO {

    @Schema(description = "常见问题 ID", example = "1")
    @ExcelProperty("编号")
    private Long id;

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_CATEGORY, example = "FUNDS")
    @ExcelProperty("分类编码")
    private String categoryCode;

    @Schema(description = "分类名称", example = "资金与提现")
    @ExcelProperty("分类名称")
    private String categoryName;

    @Schema(description = "问题标题", example = "提现多久到账？")
    @ExcelProperty("问题标题")
    private String title;

    @Schema(description = "问题答案正文")
    @ExcelProperty("问题答案")
    private String content;

    @Schema(description = "App 图标标识", example = "fund")
    @ExcelProperty("图标")
    private String icon;

    @Schema(description = "排序号，数值越小越靠前", example = "10")
    @ExcelProperty("排序号")
    private Integer sortNo;

    @Schema(description = OpenApiSchemaConstants.HELP_FAQ_STATUS, example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
