package cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 信用分规则详情 Response VO")
@Data
public class CreditRuleDetailRespVO {

    private Long id;
    private String ruleCode;
    private String ruleName;
    private Integer scoreChange;
    private String triggerType;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer sameTriggerRuleCount;
    private Integer positiveRuleCount;
    private Integer negativeRuleCount;
    private Boolean positiveRule;
    private List<RelatedRuleRespVO> relatedRules;

    @Data
    public static class RelatedRuleRespVO {
        private Long id;
        private String ruleCode;
        private String ruleName;
        private Integer scoreChange;
        private String triggerType;
        private String status;
        private LocalDateTime createTime;
    }
}
