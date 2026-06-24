package cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 服务点详情 Response VO")
@Data
public class MerchantServicePointDetailRespVO {

    private Long id;
    private Long merchantId;
    private String pointName;
    private String province;
    private String city;
    private String district;
    private String street;
    private String detailAddress;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private BigDecimal serviceRadiusKm;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private MerchantRespVO merchant;
    private SummaryRespVO summary;
    private List<CategoryRespVO> categories;
    private List<RelatedPointRespVO> relatedPoints;

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
        private Long latestEntryId;
        private String latestEntryNo;
        private String latestEntryStatus;
        private String latestRegionCode;
    }

    @Data
    public static class SummaryRespVO {
        private Integer servicePointCount;
        private Integer enabledServicePointCount;
        private Integer disabledServicePointCount;
        private Integer sameDistrictPointCount;
        private Integer sameCityPointCount;
        private Integer categoryCount;
    }

    @Data
    public static class CategoryRespVO {
        private Long categoryId;
        private String categoryName;
        private Long parentId;
        private Integer categoryLevel;
        private String defaultPricingMode;
        private Boolean supportSplit;
        private Boolean supportInvoice;
    }

    @Data
    public static class RelatedPointRespVO {
        private Long id;
        private String pointName;
        private String province;
        private String city;
        private String district;
        private String street;
        private String detailAddress;
        private BigDecimal serviceRadiusKm;
        private String status;
        private LocalDateTime createTime;
    }
}
