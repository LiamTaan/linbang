package cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@Schema(description = "管理后台 - 服务商价格申报 Response VO")
public class MerchantPriceReportRespVO {

    @ExcelProperty("主键")
    @Schema(description = "主键 ID")
    private Long id;

    @ExcelProperty("服务商ID")
    @Schema(description = "服务商 ID，关联服务商档案")
    private Long merchantId;

    @ExcelProperty("服务商名称")
    @Schema(description = "服务商名称")
    private String merchantName;

    @ExcelProperty("联系人")
    @Schema(description = "服务商联系人姓名")
    private String merchantContactName;

    @ExcelProperty("联系手机")
    @Schema(description = "服务商联系人手机号")
    private String merchantContactMobile;

    @ExcelProperty("合作商ID")
    @Schema(description = "区域合作商 ID，关联合作商档案")
    private Long partnerId;

    @ExcelProperty("合作商名称")
    @Schema(description = "区域合作商名称")
    private String partnerName;

    @ExcelProperty("类目ID")
    @Schema(description = "服务分类 ID，关联服务分类")
    private Long categoryId;

    @ExcelProperty("类目名称")
    @Schema(description = "服务分类名称")
    private String categoryName;

    @ExcelProperty("区域编码")
    @Schema(description = "业务所属高德行政区划编码")
    private String regionCode;

    @ExcelProperty("建议价格")
    @Schema(description = "服务商建议价格，单位：元，保留两位小数")
    private BigDecimal suggestedPrice;

    @ExcelProperty("备注")
    @Schema(description = "业务备注")
    private String remark;

    @ExcelProperty("状态")
    @Schema(description = "价格申报状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String status;

    @ExcelProperty("审核状态")
    @Schema(description = "价格申报审核状态：PENDING 待审核、APPROVED 已通过、REJECTED 已驳回")
    private String auditStatus;

    @ExcelProperty("审核备注")
    @Schema(description = "审核备注")
    private String auditRemark;

    @ExcelProperty("驳回原因")
    @Schema(description = "审核驳回原因")
    private String rejectReason;

    @ExcelProperty("审核时间")
    @Schema(description = "审核时间")
    private LocalDateTime auditTime;

    @ExcelProperty("创建时间")
    @Schema(description = "记录创建时间")
    private LocalDateTime createTime;
}
