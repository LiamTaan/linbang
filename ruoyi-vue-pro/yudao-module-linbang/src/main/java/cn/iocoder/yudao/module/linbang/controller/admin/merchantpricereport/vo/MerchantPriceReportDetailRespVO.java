package cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 价格申报详情 Response VO")
@Data
public class MerchantPriceReportDetailRespVO {

    @Schema(description = "主键 ID")
    private Long id;
    @Schema(description = "服务商 ID，关联服务商档案")
    private Long merchantId;
    @Schema(description = "区域合作商 ID，关联合作商档案")
    private Long partnerId;
    @Schema(description = "服务分类 ID，关联服务分类")
    private Long categoryId;
    @Schema(description = "服务分类名称")
    private String categoryName;
    @Schema(description = "业务所属高德行政区划编码")
    private String regionCode;
    @Schema(description = "服务商建议价格，单位：元，保留两位小数")
    private BigDecimal suggestedPrice;
    @Schema(description = "业务备注")
    private String remark;
    @Schema(description = "价格申报状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String status;
    @Schema(description = "价格申报审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String auditStatus;
    @Schema(description = "审核备注")
    private String auditRemark;
    @Schema(description = "审核驳回原因")
    private String rejectReason;
    @Schema(description = "审核人后台用户 ID")
    private Long auditBy;
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
    @Schema(description = "记录最后更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "关联服务商摘要")
    private MerchantRespVO merchant;
    @Schema(description = "关联区域合作商摘要")
    private PartnerRespVO partner;
    @Schema(description = "关联服务商入驻申请摘要")
    private MerchantEntryRespVO merchantEntry;
    @Schema(description = "业务统计摘要")
    private SummaryRespVO summary;
    @Schema(description = "同区域、同分类的关联价格申报列表")
    private List<RelatedReportRespVO> relatedReports;

    @Data
    @Schema(description = "管理后台 - 服务商摘要 Response VO")
    public static class MerchantRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "联系人姓名")
        private String contactName;
        @Schema(description = "联系人手机号")
        private String contactMobile;
        @Schema(description = "服务商状态：ENABLE 启用、DISABLE 停用")
        private String status;
        @Schema(description = "服务商接单状态：ENABLE 可接单、DISABLE 暂停接单")
        private String acceptStatus;
        @Schema(description = "当前信用分")
        private Integer creditScore;
        @Schema(description = "信用等级编码，按平台信用等级规则展示")
        private String creditLevel;
    }

    @Data
    @Schema(description = "管理后台 - 区域合作商摘要 Response VO")
    public static class PartnerRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "区域合作商名称")
        private String partnerName;
        @Schema(description = "联系人姓名")
        private String contactName;
        @Schema(description = "联系人手机号")
        private String contactMobile;
        @Schema(description = "区域合作商状态：ENABLE 启用、DISABLE 停用")
        private String status;
    }

    @Data
    @Schema(description = "管理后台 - 入驻申请摘要 Response VO")
    public static class MerchantEntryRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "服务商 ID，关联服务商档案")
        private Long merchantId;
        @Schema(description = "平台用户 ID，关联用户档案")
        private Long userId;
        @Schema(description = "平台用户业务编号")
        private String userNo;
        @Schema(description = "用户昵称")
        private String userNickname;
        @Schema(description = "用户手机号")
        private String userMobile;
        @Schema(description = "服务商入驻申请业务编号")
        private String entryNo;
        @Schema(description = "业务所属高德行政区划编码")
        private String regionCode;
        @Schema(description = "入驻初审状态：PENDING 待初审、APPROVED 已通过、REJECTED 已驳回")
        private String firstAuditStatus;
        @Schema(description = "入驻终审状态：PENDING 待终审、APPROVED 已通过、REJECTED 已驳回")
        private String finalAuditStatus;
        @Schema(description = "入驻状态：PENDING 待审核、FIRST_APPROVED 初审通过、APPROVED 终审通过、REJECTED 已驳回")
        private String status;
        @Schema(description = "业务备注")
        private String remark;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }

    @Data
    @Schema(description = "管理后台 - 统计摘要 Response VO")
    public static class SummaryRespVO {
        @Schema(description = "同区域、同分类的关联价格申报总数，单位：条")
        private Integer totalRelatedCount;
        @Schema(description = "待审核的关联记录数量，单位：条")
        private Integer pendingCount;
        @Schema(description = "已审核通过的关联记录数量，单位：条")
        private Integer approvedCount;
        @Schema(description = "审核驳回的关联记录数量，单位：条")
        private Integer rejectedCount;
        @Schema(description = "关联申报的平均建议价格，单位：元，保留两位小数")
        private BigDecimal avgSuggestedPrice;
        @Schema(description = "关联申报的最低建议价格，单位：元，保留两位小数")
        private BigDecimal minSuggestedPrice;
        @Schema(description = "关联申报的最高建议价格，单位：元，保留两位小数")
        private BigDecimal maxSuggestedPrice;
    }

    @Data
    @Schema(description = "管理后台 - 关联价格申报摘要 Response VO")
    public static class RelatedReportRespVO {
        @Schema(description = "主键 ID")
        private Long id;
        @Schema(description = "服务商 ID，关联服务商档案")
        private Long merchantId;
        @Schema(description = "服务商名称")
        private String merchantName;
        @Schema(description = "区域合作商 ID，关联合作商档案")
        private Long partnerId;
        @Schema(description = "区域合作商名称")
        private String partnerName;
        @Schema(description = "服务分类 ID，关联服务分类")
        private Long categoryId;
        @Schema(description = "服务分类名称")
        private String categoryName;
        @Schema(description = "业务所属高德行政区划编码")
        private String regionCode;
        @Schema(description = "服务商建议价格，单位：元，保留两位小数")
        private BigDecimal suggestedPrice;
        @Schema(description = "业务备注")
        private String remark;
        @Schema(description = "关联价格申报状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
        private String status;
        @Schema(description = "记录创建时间")
        private LocalDateTime createTime;
    }
}
