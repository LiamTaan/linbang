package cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 信用分规则详情 Response VO")
@Data
public class CreditRuleDetailRespVO {

    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "规则唯一编码")
    private String ruleCode;
    @Schema(description = "规则名称")
    private String ruleName;
    @Schema(description = "信用分变动值；正数加分、负数扣分")
    private Integer scoreChange;
    @Schema(description = "信用规则触发类型：AUTO 系统自动触发、MANUAL 人工调整")
    private String triggerType;
    @Schema(description = "信用规则状态：ENABLE 启用、DISABLE 停用")
    private String status;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
    @Schema(description = "记录最后更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "相同触发类型的信用规则数量，单位：条")
    private Integer sameTriggerRuleCount;
    @Schema(description = "增加信用分的规则数量，单位：条")
    private Integer positiveRuleCount;
    @Schema(description = "扣减信用分的规则数量，单位：条")
    private Integer negativeRuleCount;
    @Schema(description = "是否为加分规则：true 加分、false 扣分或不加分")
    private Boolean positiveRule;
    @Schema(description = "同触发类型的关联信用规则列表")
    private List<RelatedRuleRespVO> relatedRules;

    @Data
    @Schema(description = "管理后台 - 关联信用规则摘要 Response VO")
    public static class RelatedRuleRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "规则唯一编码")
        private String ruleCode;
        @Schema(description = "规则名称")
        private String ruleName;
        @Schema(description = "信用分变动值；正数加分、负数扣分")
        private Integer scoreChange;
        @Schema(description = "信用规则触发类型：AUTO 系统自动触发、MANUAL 人工调整")
        private String triggerType;
        @Schema(description = "关联信用规则状态：ENABLE 启用、DISABLE 停用")
        private String status;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }
}
