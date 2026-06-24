package cn.iocoder.yudao.module.linbang.controller.admin.memberuser.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 用户详情 Response VO")
@Data
public class MemberUserDetailRespVO {

    private Long id;
    private String userNo;
    private String mobile;
    private String nickname;
    private String avatar;
    private Integer gender;
    private LocalDate birthday;
    private String registerSource;
    private String currentRoleCode;
    private String status;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private String remark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private RealNameRespVO realName;
    private MerchantRespVO merchant;
    private LatestEntryRespVO latestEntry;
    private SummaryRespVO summary;
    private List<QualificationRespVO> qualifications;
    private List<AddressRespVO> addresses;
    private List<CreditRecordRespVO> creditRecords;

    @Data
    public static class RealNameRespVO {
        private Long id;
        private String realName;
        private String idCardNo;
        private String auditStatus;
        private String auditRemark;
        private Long auditBy;
        private LocalDateTime auditTime;
        private String rejectReason;
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
        private String serviceScopeDesc;
    }

    @Data
    public static class LatestEntryRespVO {
        private Long id;
        private Long merchantId;
        private String entryNo;
        private String regionCode;
        private String firstAuditStatus;
        private String finalAuditStatus;
        private String status;
        private String remark;
        private LocalDateTime createTime;
    }

    @Data
    public static class SummaryRespVO {
        private Integer qualificationCount;
        private Integer approvedQualificationCount;
        private Integer rejectedQualificationCount;
        private Integer addressCount;
        private Integer defaultAddressCount;
        private Integer creditRecordCount;
        private Integer latestCreditScore;
        private String latestCreditLevel;
        private Boolean realNameApproved;
        private Boolean merchantBound;
        private Boolean latestEntryApproved;
    }

    @Data
    public static class QualificationRespVO {
        private Long id;
        private String qualificationType;
        private String qualificationName;
        private String qualificationNo;
        private Long fileId;
        private LocalDate validStartDate;
        private LocalDate validEndDate;
        private String auditStatus;
        private String auditRemark;
        private Long auditBy;
        private LocalDateTime auditTime;
        private String rejectReason;
        private LocalDateTime createTime;
    }

    @Data
    public static class AddressRespVO {
        private Long id;
        private String receiverName;
        private String receiverMobile;
        private String province;
        private String city;
        private String district;
        private String street;
        private String detailAddress;
        private BigDecimal longitude;
        private BigDecimal latitude;
        private String adcode;
        private Boolean isDefault;
        private LocalDateTime createTime;
    }

    @Data
    public static class CreditRecordRespVO {
        private Long id;
        private String ruleCode;
        private String ruleName;
        private Integer scoreChange;
        private Integer beforeScore;
        private Integer afterScore;
        private String triggerType;
        private String bizType;
        private Long bizId;
        private String remark;
        private LocalDateTime createTime;
    }
}
