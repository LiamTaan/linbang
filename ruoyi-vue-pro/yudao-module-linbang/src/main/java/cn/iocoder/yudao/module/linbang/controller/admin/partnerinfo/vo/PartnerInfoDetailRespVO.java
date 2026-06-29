package cn.iocoder.yudao.module.linbang.controller.admin.partnerinfo.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 区域合作商详情 Response VO")
@Data
public class PartnerInfoDetailRespVO {

    @Schema(description = "合作商 ID", example = "1")
    private Long id;
    @Schema(description = "关联用户 ID", example = "5001")
    private Long userId;
    @Schema(description = "用户编号", example = "LBU123456")
    private String userNo;
    @Schema(description = "用户昵称", example = "华东合作商")
    private String userNickname;
    @Schema(description = "用户手机号", example = "13800138000")
    private String userMobile;
    @Schema(description = "合作商名称", example = "华东区域合作商")
    private String partnerName;
    @Schema(description = "联系人姓名", example = "王经理")
    private String contactName;
    @Schema(description = "联系人手机号", example = "13900139000")
    private String contactMobile;
    @Schema(description = OpenApiSchemaConstants.PARTNER_STATUS, example = "ENABLE")
    private String status;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    @Schema(description = "负责区域行政区划编码列表")
    private List<String> regionAdcodes;
    @Schema(description = "负责区域明细")
    private List<RegionRespVO> regions;
    @Schema(description = "经营数据汇总")
    private SummaryRespVO summary;
    @Schema(description = "最近价格申报记录")
    private List<RecentPriceReportRespVO> recentPriceReports;

    @Data
    public static class RegionRespVO {
        @Schema(description = "区域记录 ID", example = "1")
        private Long id;
        @Schema(description = "省份名称", example = "浙江省")
        private String province;
        @Schema(description = "城市名称", example = "杭州市")
        private String city;
        @Schema(description = "区县名称", example = "西湖区")
        private String district;
        @Schema(description = "行政区划编码", example = "330106")
        private String adcode;
        @Schema(description = OpenApiSchemaConstants.ENABLE_DISABLE_STATUS, example = "ENABLE")
        private String status;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }

    @Data
    public static class SummaryRespVO {
        @Schema(description = "覆盖区域数量", example = "6")
        private Integer regionCount;
        @Schema(description = "启用中的区域数量", example = "5")
        private Integer enabledRegionCount;
        @Schema(description = "待审核入驻数", example = "2")
        private Long pendingEntryAuditCount;
        @Schema(description = "待处理投诉数", example = "1")
        private Long pendingComplaintCount;
        @Schema(description = "待审核价格申报数", example = "3")
        private Long pendingPriceReportCount;
        @Schema(description = "关联订单数", example = "128")
        private Long orderCount;
        @Schema(description = "累计交易额", example = "56800.00")
        private BigDecimal tradeAmount;
        @Schema(description = "已通过价格申报数", example = "18")
        private Integer approvedPriceReportCount;
        @Schema(description = "已驳回价格申报数", example = "2")
        private Integer rejectedPriceReportCount;
    }

    @Data
    public static class RecentPriceReportRespVO {
        @Schema(description = "价格申报 ID", example = "1")
        private Long id;
        @Schema(description = "服务商 ID", example = "8001")
        private Long merchantId;
        @Schema(description = "服务商名称", example = "安心家政")
        private String merchantName;
        @Schema(description = "服务商联系人", example = "张师傅")
        private String merchantContactName;
        @Schema(description = "服务商联系电话", example = "13900139000")
        private String merchantContactMobile;
        @Schema(description = "合作商 ID", example = "1")
        private Long partnerId;
        @Schema(description = "服务类目 ID", example = "101")
        private Long categoryId;
        @Schema(description = "服务类目名称", example = "家电清洗")
        private String categoryName;
        @Schema(description = "区域编码", example = "330106")
        private String regionCode;
        @Schema(description = "建议价格", example = "268.00")
        private BigDecimal suggestedPrice;
        @Schema(description = "申报说明")
        private String remark;
        @Schema(description = "申报状态，按价格申报状态字典展示，常见值如 PENDING 待审核、APPROVED 已通过、REJECTED 已驳回", example = "PENDING")
        private String status;
        @Schema(description = "创建时间")
        private LocalDateTime createTime;
    }
}
