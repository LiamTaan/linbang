package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 订单详情 Response VO")
@Data
public class OrderInfoDetailRespVO {

    private Long id;
    private String orderNo;
    private Long userId;
    private Long merchantId;
    private MerchantRespVO merchant;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String pricingMode;
    private BigDecimal budgetAmount;
    private BigDecimal orderAmount;
    private BigDecimal quantity;
    private String serviceDurationDesc;
    private String requireDesc;
    private Long addressId;
    private String province;
    private String city;
    private String district;
    private String street;
    private String detailAddress;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Boolean needInvoice;
    private Boolean needSplit;
    private String splitStatus;
    private Boolean agreementConfirmed;
    private String status;
    private Long payOrderId;
    private OrderPayRecordRespVO payRecord;
    private List<OrderPriceItemRespVO> priceItems;
    private List<OrderAttachmentRespVO> attachments;
    private List<OrderUnitSimpleRespVO> units;
    private List<OrderAcceptRecordRespVO> acceptRecords;
    private List<OrderUnitProofRespVO> proofs;
    private List<OrderRefundRespVO> refunds;
    private List<OrderComplaintRespVO> complaints;
    private List<OrderAppealRespVO> appeals;
    private List<OrderOperateLogRespVO> operateLogs;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @Data
    public static class MerchantRespVO {
        private Long id;
        private String merchantName;
        private String contactName;
        private String contactMobile;
        private String status;
        private String acceptStatus;
        private Integer creditScore;
        private String creditLevel;
    }

    @Data
    public static class OrderPriceItemRespVO {
        private String itemType;
        private String itemName;
        private BigDecimal itemAmount;
        private Integer sortNo;
    }

    @Data
    public static class OrderAttachmentRespVO {
        private Long fileId;
        private String fileType;
        private String fileUrl;
        private Integer sortNo;
    }

    @Data
    public static class OrderUnitSimpleRespVO {
        private Long id;
        private String unitNo;
        private Integer unitSeq;
        private String unitTitle;
        private BigDecimal unitAmount;
        private String splitMode;
        private Long prevUnitId;
        private Boolean isLocked;
        private String lockReason;
        private Long merchantId;
        private String status;
        private LocalDateTime acceptDeadlineTime;
        private LocalDateTime finishTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderUnitProofRespVO {
        private Long id;
        private Long unitId;
        private Long merchantId;
        private Long fileId;
        private String proofType;
        private String proofDesc;
        private LocalDateTime proofTime;
        private BigDecimal longitude;
        private BigDecimal latitude;
    }

    @Data
    public static class OrderPayRecordRespVO {
        private Long id;
        private String merchantOrderId;
        private String subject;
        private Integer price;
        private Integer status;
        private String channelCode;
        private String channelOrderNo;
        private Integer refundPrice;
        private LocalDateTime expireTime;
        private LocalDateTime successTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderAcceptRecordRespVO {
        private Long id;
        private Long unitId;
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
    public static class OrderRefundRespVO {
        private Long id;
        private Long payOrderId;
        private String merchantRefundId;
        private Integer status;
        private String statusName;
        private String auditStatus;
        private String auditRemark;
        private String rejectReason;
        private Integer refundPrice;
        private String reason;
        private String channelErrorMsg;
        private LocalDateTime successTime;
        private LocalDateTime createTime;
    }

    @Data
    public static class OrderComplaintRespVO {
        private Long id;
        private String complaintNo;
        private Long unitId;
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
        private Long unitId;
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
