package cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 订单匹配记录详情 Response VO")
@Data
public class OrderMatchRecordDetailRespVO {

    private Long id;
    private Long orderId;
    private Long unitId;
    private Long merchantId;
    private String matchRuleCode;
    private BigDecimal matchScore;
    private BigDecimal distanceKm;
    private LocalDateTime pushTime;
    private LocalDateTime acceptDeadlineTime;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private OrderRespVO order;
    private UnitRespVO unit;
    private MerchantRespVO merchant;
    private RuleRespVO rule;
    private SummaryRespVO summary;
    private List<AcceptRecordRespVO> acceptRecords;

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
    }

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
    public static class RuleRespVO {
        private Long id;
        private String ruleCode;
        private String ruleName;
        private String ruleGroup;
        private String ruleValue;
        private String valueType;
        private String status;
        private String remark;
    }

    @Data
    public static class SummaryRespVO {
        private Integer acceptRecordCount;
        private Integer acceptedCount;
        private Integer rejectedCount;
        private Boolean deadlineExpired;
        private BigDecimal closestDistanceKm;
    }

    @Data
    public static class AcceptRecordRespVO {
        private Long id;
        private Long orderId;
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
}
