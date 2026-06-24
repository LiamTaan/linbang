package cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 信用记录详情 Response VO")
@Data
public class CreditRecordDetailRespVO {

    private Long id;
    private Long userId;
    private Long merchantId;
    private Long ruleId;
    private String ruleCode;
    private String ruleName;
    private Integer scoreChange;
    private Integer beforeScore;
    private Integer afterScore;
    private String triggerType;
    private String bizType;
    private Long bizId;
    private String bizDisplay;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer currentScore;
    private String creditLevel;
    private Integer sameUserRecordCount;
    private Integer sameRuleRecordCount;
    private Integer positiveRecordCount;
    private Integer negativeRecordCount;
    private UserRespVO user;
    private MerchantRespVO merchant;
    private RuleRespVO rule;

    @Data
    public static class UserRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
    }

    @Data
    public static class MerchantRespVO {
        private Long id;
        private Long userId;
        private String merchantName;
        private String contactName;
        private String contactMobile;
        private String status;
        private String acceptStatus;
        private Integer creditScore;
        private String creditLevel;
    }

    @Data
    public static class RuleRespVO {
        private Long id;
        private String ruleCode;
        private String ruleName;
        private Integer scoreChange;
        private String triggerType;
        private String status;
        private LocalDateTime createTime;
    }
}
