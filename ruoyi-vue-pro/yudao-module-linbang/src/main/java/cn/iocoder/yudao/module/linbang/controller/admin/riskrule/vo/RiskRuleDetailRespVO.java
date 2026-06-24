package cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 风控规则详情 Response VO")
@Data
public class RiskRuleDetailRespVO {

    private Long id;
    private String ruleCode;
    private String ruleName;
    private String ruleGroup;
    private String ruleValue;
    private String valueType;
    private String status;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer hitCount;
    private Integer pendingEventCount;
    private Integer userBlacklistCount;
    private List<RiskEventSimpleRespVO> recentEvents;
    private List<BlacklistSimpleRespVO> relatedBlacklists;

    @Data
    public static class RiskEventSimpleRespVO {
        private Long id;
        private String bizType;
        private Long bizId;
        private MemberUserSimpleRespVO user;
        private String riskType;
        private String riskLevel;
        private String hitRuleCode;
        private String status;
        private Long handleBy;
        private LocalDateTime handleTime;
        private String remark;
        private LocalDateTime createTime;
    }

    @Data
    public static class BlacklistSimpleRespVO {
        private Long id;
        private Long userId;
        private String blackType;
        private String reason;
        private String status;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private LocalDateTime createTime;
        private MemberUserSimpleRespVO user;
    }

    @Data
    public static class MemberUserSimpleRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
    }
}
