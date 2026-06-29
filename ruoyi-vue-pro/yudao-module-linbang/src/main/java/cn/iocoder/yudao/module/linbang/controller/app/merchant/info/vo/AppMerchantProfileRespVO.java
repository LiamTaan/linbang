package cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = "服务商名称", example = "邻帮家政浦东店")
    private String merchantName;

    @Schema(description = "联系人", example = "张三")
    private String contactName;

    @Schema(description = "联系人手机号", example = "13800138000")
    private String contactMobile;

    @Schema(description = "服务商状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String merchantStatus;

    @Schema(description = "接单状态：ENABLE 可接单、DISABLE 暂停接单", example = "ENABLE")
    private String acceptStatus;

    @Schema(description = "信用分")
    private Integer creditScore;

    @Schema(description = "信用等级，按信用等级业务字典展示，例如 WARNING、NORMAL、EXCELLENT、DISABLED")
    private String creditLevel;

    @Schema(description = "综合评分，按金额加权平均，保留两位小数", example = "4.67")
    private BigDecimal compositeScore;

    @Schema(description = "评价总数", example = "20")
    private Integer reviewCount;

    @Schema(description = "好评率，百分比保留两位小数", example = "90.00")
    private BigDecimal positiveRate;

    @Schema(description = "是否在好评优先池", example = "true")
    private Boolean inPositivePriorityPool;

    @Schema(description = "服务范围说明", example = "覆盖浦东新区 5 公里内家庭保洁与维修")
    private String serviceScopeDesc;

    @Schema(description = "入驻申请 ID")
    private Long entryId;

    @Schema(description = "入驻单号")
    private String entryNo;

    @Schema(description = "入驻区域编码")
    private String regionCode;

    @Schema(description = "入驻状态：PENDING 待审核、FIRST_APPROVED 初审通过、APPROVED 终审通过、REJECTED 已驳回", example = "APPROVED")
    private String entryStatus;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String firstAuditStatus;

    @Schema(description = OpenApiSchemaConstants.AUDIT_STATUS, example = "APPROVED")
    private String finalAuditStatus;

    @Schema(description = "统一进度状态：PENDING_FIRST_AUDIT、PENDING_FINAL_AUDIT、APPROVED_WAIT_BANK_CARD、APPROVED_ENABLED、REJECTED", example = "APPROVED_WAIT_BANK_CARD")
    private String progressStatus;

    @Schema(description = "当前阶段名称", example = "终审通过，待绑定银行卡")
    private String currentStageName;

    @Schema(description = "当前阶段时间")
    private LocalDateTime currentStageTime;

    @Schema(description = "驳回原因")
    private String rejectReason;

    @Schema(description = "当前阻塞原因")
    private String onboardingBlockedReason;

    @Schema(description = "是否已开通接单", example = "false")
    private Boolean acceptEnabled;

    @Schema(description = "当前操作者是否主账号", example = "true")
    private Boolean mainAccountOperator;

    @Schema(description = "当前操作者权限编码列表，仅子账号有值")
    private List<String> operatorPermissionCodes;

    @Schema(description = "当前操作者可见服务点范围，仅子账号有值")
    private List<Long> visibleServicePointIds;

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

        @Schema(description = "服务类目名称", example = "家庭保洁")
        private String categoryName;

        @Schema(description = "父类目 ID")
        private Long parentId;

        @Schema(description = "类目层级")
        private Integer categoryLevel;

        @Schema(description = OpenApiSchemaConstants.PRICING_MODE, example = "FIXED_PRICE")
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

        @Schema(description = "服务点名称", example = "浦东服务点")
        private String pointName;

        @Schema(description = "省", example = "上海市")
        private String province;

        @Schema(description = "市", example = "上海市")
        private String city;

        @Schema(description = "区", example = "浦东新区")
        private String district;

        @Schema(description = "街道", example = "陆家嘴街道")
        private String street;

        @Schema(description = "详细地址", example = "世纪大道 1 号")
        private String detailAddress;

        @Schema(description = "经度", example = "121.500000")
        private BigDecimal longitude;

        @Schema(description = "纬度", example = "31.230000")
        private BigDecimal latitude;

        @Schema(description = "服务半径，单位公里", example = "5.00")
        private BigDecimal serviceRadiusKm;

        @Schema(description = "服务点状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
        private String status;

        @Schema(description = "当前操作者是否可管理该服务点", example = "true")
        private Boolean manageable;
    }
}
