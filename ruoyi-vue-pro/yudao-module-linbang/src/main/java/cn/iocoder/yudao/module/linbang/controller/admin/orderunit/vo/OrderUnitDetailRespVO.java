package cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 拆分单元详情 Response VO")
@Data
public class OrderUnitDetailRespVO {

    private Long id;
    private Long orderId;
    private String orderNo;
    private String orderTitle;
    private String orderStatus;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private Long merchantId;
    private String merchantName;
    private String merchantContactName;
    private String merchantContactMobile;
    private String unitNo;
    private Integer unitSeq;
    private String unitTitle;
    private BigDecimal unitAmount;
    private String splitMode;
    private Long prevUnitId;
    private String prevUnitNo;
    private Boolean isLocked;
    private String lockReason;
    private String status;
    private LocalDateTime acceptDeadlineTime;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;
    private List<OrderUnitProofRespVO> proofs;
    private List<OrderAcceptRecordRespVO> acceptRecords;
    private List<OrderComplaintRespVO> complaints;
    private List<OrderAppealRespVO> appeals;
    private List<OrderOperateLogRespVO> operateLogs;

    @Data
    public static class OrderUnitProofRespVO {
        private Long id;
        private Long fileId;
        private Long merchantId;
        private String merchantName;
        private String merchantContactName;
        private String merchantContactMobile;
        private String proofType;
        private String proofDesc;
        private LocalDateTime proofTime;
        private BigDecimal longitude;
        private BigDecimal latitude;
    }

    @Data
    public static class OrderAcceptRecordRespVO {
        private Long id;
        private Long merchantId;
        private String merchantName;
        private String merchantContactName;
        private String merchantContactMobile;
        private LocalDateTime acceptTime;
        private BigDecimal distanceKm;
        private String acceptResult;
        private String remark;
    }

    @Data
    public static class OrderComplaintRespVO {
        private Long id;
        private String complaintNo;
        private Long complainantUserId;
        private Long respondentUserId;
        private String complaintType;
        private String content;
        private String status;
        private String resultDesc;
        private LocalDateTime handleTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderAppealRespVO {
        private Long id;
        private String appealNo;
        private Long userId;
        private String appealType;
        private String content;
        private String status;
        private String auditStatus;
        private String auditRemark;
        private String rejectReason;
        private LocalDateTime auditTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderOperateLogRespVO {
        private Long id;
        private String operateType;
        private String operateRole;
        private Long operateBy;
        private String beforeStatus;
        private String afterStatus;
        private String remark;
        private LocalDateTime operateTime;
    }

}
