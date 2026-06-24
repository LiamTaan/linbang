package cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 服务商详情 Response VO")
@Data
public class MerchantInfoDetailRespVO {

    @Schema(description = "主键", example = "966")
    private Long id;

    @Schema(description = "用户ID", example = "20968")
    private Long userId;

    @Schema(description = "服务商名称")
    private String merchantName;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系人手机号")
    private String contactMobile;

    @Schema(description = "服务范围说明")
    private String serviceScopeDesc;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "接单状态")
    private String acceptStatus;

    @Schema(description = "信用分")
    private Integer creditScore;

    @Schema(description = "信用等级")
    private String creditLevel;

    @Schema(description = "最近入驻申请 ID")
    private Long entryId;

    @Schema(description = "最近入驻单号")
    private String entryNo;

    @Schema(description = "最近入驻区域编码")
    private String regionCode;

    @Schema(description = "最近入驻状态")
    private String entryStatus;

    @Schema(description = "最近入驻初审状态")
    private String firstAuditStatus;

    @Schema(description = "最近入驻终审状态")
    private String finalAuditStatus;

    @Schema(description = "服务点总数")
    private Integer servicePointCount;

    @Schema(description = "启用中的服务点数量")
    private Integer enabledServicePointCount;

    @Schema(description = "服务类目列表")
    private List<MerchantCategoryItem> categories;

    @Schema(description = "服务点列表")
    private List<ServicePointItem> servicePoints;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "服务类目项")
    @Data
    public static class MerchantCategoryItem {
        private Long categoryId;
        private String categoryName;
        private Long parentId;
        private Integer categoryLevel;
        private String defaultPricingMode;
        private Boolean supportSplit;
        private Boolean supportInvoice;
    }

    @Schema(description = "服务点项")
    @Data
    public static class ServicePointItem {
        private Long servicePointId;
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
    }

}
