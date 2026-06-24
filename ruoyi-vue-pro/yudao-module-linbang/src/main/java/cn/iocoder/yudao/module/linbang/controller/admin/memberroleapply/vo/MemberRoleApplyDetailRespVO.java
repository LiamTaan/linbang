package cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberRoleApplyDetailRespVO {

    private Long id;

    private Long userId;

    private String applyRoleCode;

    private String applyReason;

    private String resourceDesc;

    private String auditStatus;

    private String auditRemark;

    private String rejectReason;

    private Long auditBy;

    private LocalDateTime auditTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private UserSummary user;

    private RealNameSummary realName;

    private QualificationSummary latestQualification;

    private PromoterSummary promoter;

    @Data
    public static class UserSummary {
        private Long id;
        private String userNo;
        private String nickname;
        private String mobile;
        private String currentRoleCode;
        private String status;
    }

    @Data
    public static class RealNameSummary {
        private Long id;
        private String realName;
        private String idCardNo;
        private String auditStatus;
    }

    @Data
    public static class QualificationSummary {
        private Long id;
        private String qualificationType;
        private String qualificationName;
        private LocalDate validEndDate;
        private String auditStatus;
    }

    @Data
    public static class PromoterSummary {
        private Long id;
        private String inviteCode;
        private String levelCode;
        private String status;
    }
}
