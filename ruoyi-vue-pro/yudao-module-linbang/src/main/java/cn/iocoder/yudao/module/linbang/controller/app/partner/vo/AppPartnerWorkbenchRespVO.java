package cn.iocoder.yudao.module.linbang.controller.app.partner.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 区域合作商工作台 Response VO")
@Data
public class AppPartnerWorkbenchRespVO {

    @Schema(description = "合作商ID")
    private Long partnerId;

    @Schema(description = "合作商名称")
    private String partnerName;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactMobile;

    @Schema(description = "合作商状态：ENABLE 启用、DISABLE 停用", example = "ENABLE")
    private String status;

    @Schema(description = "辖区编码")
    private List<String> regionAdcodes;

    @Schema(description = "待初审入驻数")
    private Long pendingEntryAuditCount;

    @Schema(description = "待处理投诉数")
    private Long pendingComplaintCount;

    @Schema(description = "待处理价格申报数")
    private Long pendingPriceReportCount;

    @Schema(description = "辖区订单数")
    private Long orderCount;

    @Schema(description = "辖区成交额")
    private BigDecimal tradeAmount;

    @Schema(description = "工作台汇总")
    private SummaryRespVO summary;

    @Schema(description = "最近价格申报")
    private List<RecentPriceReportRespVO> recentPriceReports;

    @Data
    public static class SummaryRespVO {

        @Schema(description = "辖区数量")
        private Integer regionCount;

        @Schema(description = "启用辖区数量")
        private Integer enabledRegionCount;

        @Schema(description = "待初审入驻数")
        private Long pendingEntryAuditCount;

        @Schema(description = "待处理投诉数")
        private Long pendingComplaintCount;

        @Schema(description = "待处理价格申报数")
        private Long pendingPriceReportCount;

        @Schema(description = "辖区订单数")
        private Long orderCount;

        @Schema(description = "辖区成交额")
        private BigDecimal tradeAmount;

        @Schema(description = "已通过价格申报数")
        private Integer approvedPriceReportCount;

        @Schema(description = "已驳回价格申报数")
        private Integer rejectedPriceReportCount;
    }

    @Data
    public static class RecentPriceReportRespVO {

        @Schema(description = "主键", example = "1")
        private Long id;

        @Schema(description = "服务商 ID", example = "1001")
        private Long merchantId;

        @Schema(description = "合作商 ID", example = "2001")
        private Long partnerId;

        @Schema(description = "类目 ID", example = "3001")
        private Long categoryId;

        @Schema(description = "区域编码", example = "440305")
        private String regionCode;

        @Schema(description = "建议价格", example = "88.80")
        private BigDecimal suggestedPrice;

        @Schema(description = "备注")
        private String remark;

        @Schema(description = "价格申报状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "PENDING")
        private String status;

        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
