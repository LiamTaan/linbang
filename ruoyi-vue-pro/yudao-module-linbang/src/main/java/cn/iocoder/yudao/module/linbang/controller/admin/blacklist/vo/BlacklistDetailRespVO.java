package cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 黑名单详情 Response VO")
@Data
public class BlacklistDetailRespVO {

    private Long id;
    private Long userId;
    private String blackType;
    private String reason;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private LocalDateTime createTime;
    private MemberUserSimpleRespVO user;
    private List<RiskEventSimpleRespVO> recentRiskEvents;

    @Data
    public static class MemberUserSimpleRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
        private LocalDateTime lastLoginTime;
        private String lastLoginIp;
    }

    @Data
    public static class RiskEventSimpleRespVO {
        private Long id;
        private String bizType;
        private Long bizId;
        private String riskType;
        private String riskLevel;
        private String hitRuleCode;
        private String status;
        private Long handleBy;
        private LocalDateTime handleTime;
        private String remark;
        private LocalDateTime createTime;
    }

}
