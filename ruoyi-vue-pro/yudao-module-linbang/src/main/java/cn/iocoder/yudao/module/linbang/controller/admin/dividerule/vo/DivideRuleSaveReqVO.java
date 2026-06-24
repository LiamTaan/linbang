package cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo;

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
    private String ruleName;

    @Schema(description = "城市等级")
    private String cityLevel;

    @Schema(description = "类目ID", example = "17167")
    private Long categoryId;

    @Schema(description = "服务商比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "服务商比例不能为空")
    private BigDecimal merchantRate;

    @Schema(description = "平台比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "平台比例不能为空")
    private BigDecimal platformRate;

    @Schema(description = "合作商比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "合作商比例不能为空")
    private BigDecimal partnerRate;

    @Schema(description = "推广员比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "推广员比例不能为空")
    private BigDecimal promoterRate;

    @Schema(description = "个税代扣比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "个税代扣比例不能为空")
    private BigDecimal taxWithholdRate;

    @Schema(description = "最低提现金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "最低提现金额不能为空")
    private BigDecimal minWithdrawAmount;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "状态不能为空")
    private String status;

    @Schema(description = "生效时间")
    private LocalDateTime effectiveTime;

}