package cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 抢单记录详情 Response VO")
@Data
public class OrderAcceptRecordDetailRespVO {

    private Long id;
    private Long orderId;
    private Long unitId;
    private Long merchantId;
    private LocalDateTime acceptTime;
    private BigDecimal distanceKm;
    private String acceptResult;
    private String remark;
    private OrderRespVO order;
    private UnitRespVO unit;
    private MerchantRespVO merchant;
    private SummaryRespVO summary;
    private List<MatchRecordRespVO> matchRecords;

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
    public static class SummaryRespVO {
        private Integer matchRecordCount;
        private Integer pushedCount;
        private Integer acceptedCount;
        private Integer rejectedCount;
        private BigDecimal highestMatchScore;
    }

    @Data
    public static class MatchRecordRespVO {
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
    }
}
