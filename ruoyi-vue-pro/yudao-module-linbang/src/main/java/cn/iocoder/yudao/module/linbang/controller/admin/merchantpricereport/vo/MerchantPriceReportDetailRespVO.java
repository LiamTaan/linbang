package cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 价格申报详情 Response VO")
@Data
public class MerchantPriceReportDetailRespVO {

    private Long id;
    private Long merchantId;
    private Long partnerId;
    private Long categoryId;
    private String categoryName;
    private String regionCode;
    private BigDecimal suggestedPrice;
    private String remark;
    private String status;
    private String auditStatus;
    private String auditRemark;
    private String rejectReason;
    private Long auditBy;
    private LocalDateTime auditTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private MerchantRespVO merchant;
    private PartnerRespVO partner;
    private MerchantEntryRespVO merchantEntry;
    private SummaryRespVO summary;
    private List<RelatedReportRespVO> relatedReports;

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
    public static class PartnerRespVO {
        private Long id;
        private String partnerName;
        private String contactName;
        private String contactMobile;
        private String status;
    }

    @Data
    public static class MerchantEntryRespVO {
        private Long id;
        private Long merchantId;
        private Long userId;
        private String userNo;
        private String userNickname;
        private String userMobile;
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
        private Integer totalRelatedCount;
        private Integer pendingCount;
        private Integer approvedCount;
        private Integer rejectedCount;
        private BigDecimal avgSuggestedPrice;
        private BigDecimal minSuggestedPrice;
        private BigDecimal maxSuggestedPrice;
    }

    @Data
    public static class RelatedReportRespVO {
        private Long id;
        private Long merchantId;
        private String merchantName;
        private Long partnerId;
        private String partnerName;
        private Long categoryId;
        private String categoryName;
        private String regionCode;
        private BigDecimal suggestedPrice;
        private String remark;
        private String status;
        private LocalDateTime createTime;
    }
}
