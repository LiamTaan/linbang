package cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 区域合作商详情 Response VO")
@Data
public class PartnerInfoDetailRespVO {

    private Long id;
    private Long userId;
    private String userNo;
    private String userNickname;
    private String userMobile;
    private String partnerName;
    private String contactName;
    private String contactMobile;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> regionAdcodes;
    private List<RegionRespVO> regions;
    private SummaryRespVO summary;
    private List<RecentPriceReportRespVO> recentPriceReports;

    @Data
    public static class RegionRespVO {
        private Long id;
        private String province;
        private String city;
        private String district;
        private String adcode;
        private String status;
        private LocalDateTime createTime;
    }

    @Data
    public static class SummaryRespVO {
        private Integer regionCount;
        private Integer enabledRegionCount;
        private Long pendingEntryAuditCount;
        private Long pendingComplaintCount;
        private Long pendingPriceReportCount;
        private Long orderCount;
        private BigDecimal tradeAmount;
        private Integer approvedPriceReportCount;
        private Integer rejectedPriceReportCount;
    }

    @Data
    public static class RecentPriceReportRespVO {
        private Long id;
        private Long merchantId;
        private String merchantName;
        private String merchantContactName;
        private String merchantContactMobile;
        private Long partnerId;
        private Long categoryId;
        private String categoryName;
        private String regionCode;
        private BigDecimal suggestedPrice;
        private String remark;
        private String status;
        private LocalDateTime createTime;
    }
}
