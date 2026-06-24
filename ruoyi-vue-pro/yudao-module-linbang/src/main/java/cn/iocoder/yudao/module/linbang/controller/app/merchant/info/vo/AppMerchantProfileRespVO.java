package cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 当前服务商资料总览 Response VO")
@Data
public class AppMerchantProfileRespVO {

    @Schema(description = "服务商 ID", example = "1")
    private Long merchantId;

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "服务商名称")
    private String merchantName;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系人手机号")
    private String contactMobile;

    @Schema(description = "服务商状态：ENABLE 启用、DISABLE 停用")
    private String merchantStatus;

    @Schema(description = "接单状态：ENABLE 可接单、DISABLE 暂停接单")
    private String acceptStatus;

    @Schema(description = "信用分")
    private Integer creditScore;

    @Schema(description = "信用等级，按信用等级业务字典展示，例如 NORMAL、GOOD、EXCELLENT")
    private String creditLevel;

    @Schema(description = "服务范围说明")
    private String serviceScopeDesc;

    @Schema(description = "入驻申请 ID")
    private Long entryId;

    @Schema(description = "入驻单号")
    private String entryNo;

    @Schema(description = "入驻区域编码")
    private String regionCode;

    @Schema(description = "入驻状态：PENDING 待审核、FIRST_APPROVED 初审通过、APPROVED 终审通过、REJECTED 已驳回")
    private String entryStatus;

    @Schema(description = "初审状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String firstAuditStatus;

    @Schema(description = "终审状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String finalAuditStatus;

    @Schema(description = "服务点数量")
    private Integer servicePointCount;

    @Schema(description = "启用中的服务点数量")
    private Integer enabledServicePointCount;

    @Schema(description = "服务类目列表")
    private List<MerchantCategoryItem> categories;

    @Schema(description = "服务区域预览")
    private List<ServiceAreaItem> serviceAreas;

    @Schema(description = "最近更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "服务类目项")
    @Data
    public static class MerchantCategoryItem {
        @Schema(description = "服务类目 ID")
        private Long categoryId;

        @Schema(description = "服务类目名称")
        private String categoryName;

        @Schema(description = "父类目 ID")
        private Long parentId;

        @Schema(description = "类目层级")
        private Integer categoryLevel;

        @Schema(description = "默认计价方式")
        private String defaultPricingMode;

        @Schema(description = "是否支持拆单")
        private Boolean supportSplit;

        @Schema(description = "是否支持发票")
        private Boolean supportInvoice;
    }

    @Schema(description = "服务区域项")
    @Data
    public static class ServiceAreaItem {
        @Schema(description = "服务点 ID")
        private Long servicePointId;

        @Schema(description = "服务点名称")
        private String pointName;

        @Schema(description = "省")
        private String province;

        @Schema(description = "市")
        private String city;

        @Schema(description = "区")
        private String district;

        @Schema(description = "街道")
        private String street;

        @Schema(description = "详细地址")
        private String detailAddress;

        @Schema(description = "经度")
        private BigDecimal longitude;

        @Schema(description = "纬度")
        private BigDecimal latitude;

        @Schema(description = "服务半径，单位公里")
        private BigDecimal serviceRadiusKm;

        @Schema(description = "服务点状态：ENABLE 启用、DISABLE 停用")
        private String status;
    }
}
