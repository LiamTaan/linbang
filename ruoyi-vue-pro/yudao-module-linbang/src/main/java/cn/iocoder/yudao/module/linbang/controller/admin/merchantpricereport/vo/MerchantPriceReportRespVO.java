package cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
public class MerchantPriceReportRespVO {

    @ExcelProperty("主键")
    private Long id;

    @ExcelProperty("服务商ID")
    private Long merchantId;

    @ExcelProperty("服务商名称")
    private String merchantName;

    @ExcelProperty("联系人")
    private String merchantContactName;

    @ExcelProperty("联系手机")
    private String merchantContactMobile;

    @ExcelProperty("合作商ID")
    private Long partnerId;

    @ExcelProperty("合作商名称")
    private String partnerName;

    @ExcelProperty("类目ID")
    private Long categoryId;

    @ExcelProperty("类目名称")
    private String categoryName;

    @ExcelProperty("区域编码")
    private String regionCode;

    @ExcelProperty("建议价格")
    private BigDecimal suggestedPrice;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("状态")
    private String status;

    @ExcelProperty("审核状态")
    private String auditStatus;

    @ExcelProperty("审核备注")
    private String auditRemark;

    @ExcelProperty("驳回原因")
    private String rejectReason;

    @ExcelProperty("审核时间")
    private LocalDateTime auditTime;

    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}
