package cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 投诉详情 Response VO")
@Data
public class ComplaintDetailRespVO {

    private Long id;
    private String complaintNo;
    private Long orderId;
    private Long unitId;
    private Long complainantUserId;
    private Long respondentUserId;
    private String complaintType;
    private String content;
    private String status;
    private Long handleBy;
    private LocalDateTime handleTime;
    private String resultDesc;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private OrderRespVO order;
    private UnitRespVO unit;
    private UserRespVO complainantUser;
    private UserRespVO respondentUser;
    private MerchantRespVO respondentMerchant;
    private SummaryRespVO summary;
    private List<FileRespVO> files;
    private List<RelatedComplaintRespVO> relatedComplaints;
    private List<OperateLogRespVO> operateLogs;

    @Data
    public static class OrderRespVO {
        private Long id;
        private String orderNo;
        private String title;
        private String status;
        private Long userId;
        private String userNo;
        private String userNickname;
        private String userMobile;
        private Long merchantId;
        private BigDecimal orderAmount;
    }

    @Data
    public static class UnitRespVO {
        private Long id;
        private Long orderId;
        private String unitNo;
        private Integer unitSeq;
        private String unitTitle;
        private BigDecimal unitAmount;
        private String status;
        private Boolean isLocked;
        private String lockReason;
        private Long merchantId;
        private LocalDateTime acceptDeadlineTime;
        private LocalDateTime finishTime;
    }

    @Data
    public static class UserRespVO {
        private Long id;
        private String userNo;
        private String mobile;
        private String nickname;
        private String currentRoleCode;
        private String status;
        private LocalDateTime lastLoginTime;
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
    public static class SummaryRespVO {
        private Integer attachmentCount;
        private Integer sameOrderComplaintCount;
        private Integer sameRespondentComplaintCount;
        private Integer pendingCount;
        private Integer processingCount;
        private Integer finishedCount;
        private Integer rejectedCount;
    }

    @Data
    public static class FileRespVO {
        private Long fileId;
    }

    @Data
    public static class RelatedComplaintRespVO {
        private Long id;
        private String complaintNo;
        private Long orderId;
        private Long unitId;
        private String complaintType;
        private String status;
        private String resultDesc;
        private LocalDateTime handleTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OperateLogRespVO {
        private Long id;
        private Long unitId;
        private String operateType;
        private String operateRole;
        private Long operateBy;
        private String beforeStatus;
        private String afterStatus;
        private String remark;
        private LocalDateTime operateTime;
    }
}
