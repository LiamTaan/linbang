package cn.iocoder.yudao.module.linbang.controller.admin.dividerule.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 分账规则详情 Response VO")
@Data
public class DivideRuleDetailRespVO {

    @Schema(description = "主键", example = "300001")
    private Long id;

    @Schema(description = "规则名称", example = "一线城市标准分账")
    private String ruleName;

    @Schema(description = OpenApiSchemaConstants.CITY_LEVEL, example = "TIER_1")
    private String cityLevel;

    @Schema(description = "类目 ID", example = "340201")
    private Long categoryId;

    @Schema(description = "类目名称", example = "家电清洗")
    private String categoryName;

    @Schema(description = "服务商分账比例")
    private BigDecimal merchantRate;

    @Schema(description = "平台分账比例")
    private BigDecimal platformRate;

    @Schema(description = "合作商分账比例")
    private BigDecimal partnerRate;

    @Schema(description = "推广员分账比例")
    private BigDecimal promoterRate;

    @Schema(description = "个税代扣比例")
    private BigDecimal taxWithholdRate;

    @Schema(description = "最低提现金额")
    private BigDecimal minWithdrawAmount;

    @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = "规则生效时间")
    private LocalDateTime effectiveTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "总分账比例，通常应与 1.0000 对齐")
    private BigDecimal totalRate;

    @Schema(description = "平台外可分配收益比例，通常为服务商/合作商/推广员合计")
    private BigDecimal incomeRate;

    @Schema(description = "比例是否平衡，true 表示分账比例校验通过")
    private Boolean rateBalanced;

}
