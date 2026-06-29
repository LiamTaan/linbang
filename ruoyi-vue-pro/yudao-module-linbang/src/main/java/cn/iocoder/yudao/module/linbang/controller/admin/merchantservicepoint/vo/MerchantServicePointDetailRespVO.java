package cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 服务点详情 Response VO")
@Data
public class MerchantServicePointDetailRespVO {

    @Schema(description = "服务点 ID", example = "1")
    private Long id;
    @Schema(description = "服务商 ID", example = "2001")
    private Long merchantId;
    @Schema(description = "服务点名称", example = "浦东金桥服务点")
    private String pointName;
    @Schema(description = "省", example = "上海市")
    private String province;
    @Schema(description = "市", example = "上海市")
    private String city;
    @Schema(description = "区", example = "浦东新区")
    private String district;
    @Schema(description = "街道", example = "金桥镇")
    private String street;
    @Schema(description = "详细地址", example = "金藏路 258 号")
    private String detailAddress;
    @Schema(description = "经度")
    private BigDecimal longitude;
    @Schema(description = "纬度")
    private BigDecimal latitude;
    @Schema(description = "服务半径（公里）")
    private BigDecimal serviceRadiusKm;
    @Schema(description = OpenApiSchemaConstants.SERVICE_POINT_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "服务类目")
    private List<CategoryRespVO> categories;
    @Schema(description = "同服务商其他服务点")
    private List<RelatedPointRespVO> relatedPoints;

    @Data
    public static class MerchantRespVO {
        @Schema(description = "服务商 ID", example = "2001")
        private Long id;
        @Schema(description = "关联用户 ID", example = "1001")
        private Long userId;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "联系人", example = "李师傅")
        private String contactName;
        @Schema(description = "联系手机", example = "13800138001")
        private String contactMobile;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
        private String acceptStatus;
        @Schema(description = "信用分")
        private Integer creditScore;
        @Schema(description = "信用等级", example = "NORMAL")
        private String creditLevel;
        @Schema(description = "服务范围说明", example = "覆盖浦东新区 15 公里内上门服务")
        private String serviceScopeDesc;
        @Schema(description = "最近入驻申请 ID", example = "1")
        private Long latestEntryId;
        @Schema(description = "最近入驻单号", example = "LBE202606260001")
        private String latestEntryNo;
        @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "APPROVED")
        private String latestEntryStatus;
        @Schema(description = "最近入驻区域编码", example = "310115")
        private String latestRegionCode;
    }

    @Data
    public static class SummaryRespVO {
        @Schema(description = "服务点总数")
        private Integer servicePointCount;
        @Schema(description = "启用中的服务点数")
        private Integer enabledServicePointCount;
        @Schema(description = "停用中的服务点数")
        private Integer disabledServicePointCount;
        @Schema(description = "同区服务点数")
        private Integer sameDistrictPointCount;
        @Schema(description = "同市服务点数")
        private Integer sameCityPointCount;
        @Schema(description = "服务类目数")
        private Integer categoryCount;
    }

    @Data
    public static class CategoryRespVO {
        @Schema(description = "类目 ID", example = "340201")
        private Long categoryId;
        @Schema(description = "类目名称", example = "家电清洗")
        private String categoryName;
        @Schema(description = "父级类目 ID", example = "340200")
        private Long parentId;
        @Schema(description = "类目层级", example = "2")
        private Integer categoryLevel;
        @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
        private String defaultPricingMode;
        @Schema(description = "是否支持拆单")
        private Boolean supportSplit;
        @Schema(description = "是否支持开票")
        private Boolean supportInvoice;
    }

    @Data
    public static class RelatedPointRespVO {
        @Schema(description = "服务点 ID", example = "2")
        private Long id;
        @Schema(description = "服务点名称", example = "浦东张江服务点")
        private String pointName;
        @Schema(description = "省", example = "上海市")
        private String province;
        @Schema(description = "市", example = "上海市")
        private String city;
        @Schema(description = "区", example = "浦东新区")
        private String district;
        @Schema(description = "街道", example = "张江镇")
        private String street;
        @Schema(description = "详细地址", example = "科苑路 88 号")
        private String detailAddress;
        @Schema(description = "服务半径（公里）")
        private BigDecimal serviceRadiusKm;
        @Schema(description = OpenApiSchemaConstants.SERVICE_POINT_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
