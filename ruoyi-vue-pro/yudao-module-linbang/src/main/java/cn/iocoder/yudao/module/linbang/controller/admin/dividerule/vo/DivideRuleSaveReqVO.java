package cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分账规则新增/修改 Request VO")
@Data
public class DivideRuleSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11825")
    private Long id;

    @Schema(description = "规则名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "规则名称不能为空")
    @Size(max = 64, message = "规则名称不能超过 64 个字符")
    private String ruleName;

    @Schema(description = OpenApiSchemaConstants.CITY_LEVEL, example = "TIER_1")
    @NotBlank(message = "城市等级不能为空")
    @Pattern(regexp = "[A-Z][A-Z0-9_]{1,31}", message = "城市等级格式不正确")
    private String cityLevel;

    @Schema(description = "类目ID", example = "17167")
    @NotNull(message = "服务类目不能为空")
    private Long categoryId;

    @Schema(description = "服务商比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "服务商比例不能为空")
    @DecimalMin(value = "0", message = "服务商比例不能小于 0")
    @DecimalMax(value = "100", message = "服务商比例不能大于 100")
    @Digits(integer = 3, fraction = 4, message = "服务商比例最多保留 4 位小数")
    private BigDecimal merchantRate;

    @Schema(description = "平台比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "平台比例不能为空")
    @DecimalMin(value = "0", message = "平台比例不能小于 0")
    @DecimalMax(value = "100", message = "平台比例不能大于 100")
    @Digits(integer = 3, fraction = 4, message = "平台比例最多保留 4 位小数")
    private BigDecimal platformRate;

    @Schema(description = "合作商比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "合作商比例不能为空")
    @DecimalMin(value = "0", message = "合作商比例不能小于 0")
    @DecimalMax(value = "100", message = "合作商比例不能大于 100")
    @Digits(integer = 3, fraction = 4, message = "合作商比例最多保留 4 位小数")
    private BigDecimal partnerRate;

    @Schema(description = "推广员比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "推广员比例不能为空")
    @DecimalMin(value = "0", message = "推广员比例不能小于 0")
    @DecimalMax(value = "100", message = "推广员比例不能大于 100")
    @Digits(integer = 3, fraction = 4, message = "推广员比例最多保留 4 位小数")
    private BigDecimal promoterRate;

    @Schema(description = "个税代扣比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "个税代扣比例不能为空")
    @DecimalMin(value = "0", message = "个税代扣比例不能小于 0")
    @DecimalMax(value = "100", message = "个税代扣比例不能大于 100")
    @Digits(integer = 3, fraction = 4, message = "个税代扣比例最多保留 4 位小数")
    private BigDecimal taxWithholdRate;

    @Schema(description = "最低提现金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "最低提现金额不能为空")
    @DecimalMin(value = "0", message = "最低提现金额不能小于 0")
    @Digits(integer = 16, fraction = 2, message = "最低提现金额最多保留 2 位小数")
    private BigDecimal minWithdrawAmount;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, requiredMode = Schema.RequiredMode.REQUIRED,
            example = "ENABLE")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;

}
