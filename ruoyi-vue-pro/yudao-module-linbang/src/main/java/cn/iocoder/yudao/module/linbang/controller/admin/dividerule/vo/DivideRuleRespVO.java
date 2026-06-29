package cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 分账规则 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DivideRuleRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11825")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("规则名称")
    private String ruleName;

    @Schema(description = OpenApiSchemaConstants.CITY_LEVEL, example = "TIER_1")
    @ExcelProperty("城市等级")
    private String cityLevel;

    @Schema(description = "类目ID", example = "17167")
    @ExcelProperty("类目ID")
    private Long categoryId;

    @Schema(description = "类目名称")
    @ExcelProperty("类目名称")
    private String categoryName;

    @Schema(description = "服务商比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("服务商比例")
    private BigDecimal merchantRate;

    @Schema(description = "平台比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("平台比例")
    private BigDecimal platformRate;

    @Schema(description = "合作商比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("合作商比例")
    private BigDecimal partnerRate;

    @Schema(description = "推广员比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("推广员比例")
    private BigDecimal promoterRate;

    @Schema(description = "个税代扣比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("个税代扣比例")
    private BigDecimal taxWithholdRate;

    @Schema(description = "最低提现金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("最低提现金额")
    private BigDecimal minWithdrawAmount;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @ExcelProperty("状态")
    private String status;

    @Schema(description = "生效时间")
    @ExcelProperty("生效时间")
    private LocalDateTime effectiveTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
