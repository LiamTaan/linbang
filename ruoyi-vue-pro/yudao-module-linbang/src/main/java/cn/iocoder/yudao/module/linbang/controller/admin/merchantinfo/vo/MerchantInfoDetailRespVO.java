package cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = "服务商名称", example = "安心家政")
    private String merchantName;

    @Schema(description = "联系人", example = "李师傅")
    private String contactName;

    @Schema(description = "联系人手机号", example = "13800138001")
    private String contactMobile;

    @Schema(description = "服务范围说明", example = "覆盖浦东新区 15 公里内上门服务")
    private String serviceScopeDesc;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_STATUS, example = "ENABLE")
    private String status;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ACCEPT_STATUS, example = "ENABLE")
    private String acceptStatus;

    @Schema(description = "是否参与系统自动派单")
    private Boolean dispatchEnabled;

    @Schema(description = "信用分", example = "100")
    private Integer creditScore;

    @Schema(description = "信用等级", example = "NORMAL")
    private String creditLevel;

    @Schema(description = "综合评分，按已生效评价金额加权平均，保留 2 位小数")
    private BigDecimal compositeScore;

    @Schema(description = "好评率，取值 0-100，保留 2 位小数")
    private BigDecimal positiveRate;

    @Schema(description = "是否在好评优先池中")
    private Boolean inPositivePriorityPool;

    @Schema(description = "最近入驻申请 ID")
    private Long entryId;

    @Schema(description = "最近入驻单号")
    private String entryNo;

    @Schema(description = "最近入驻区域编码")
    private String regionCode;

    @Schema(description = OpenApiSchemaConstants.MERCHANT_ENTRY_STATUS, example = "APPROVED")
    private String entryStatus;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String firstAuditStatus;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
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

    @Schema(description = "服务点项")
    @Data
    public static class ServicePointItem {
        @Schema(description = "服务点 ID", example = "1001")
        private Long servicePointId;
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
    }

}
