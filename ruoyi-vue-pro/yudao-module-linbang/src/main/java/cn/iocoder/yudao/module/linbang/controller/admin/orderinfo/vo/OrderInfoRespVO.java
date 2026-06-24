package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 订单主 Response VO")
@Data
@ExcelIgnoreUnannotated
public class OrderInfoRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11427")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "下单用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20911")
    @ExcelProperty("下单用户ID")
    private Long userId;

    @Schema(description = "下单用户编号")
    @ExcelProperty("下单用户编号")
    private String userNo;

    @Schema(description = "下单用户昵称")
    @ExcelProperty("下单用户昵称")
    private String userNickname;

    @Schema(description = "下单用户手机号")
    @ExcelProperty("下单用户手机号")
    private String userMobile;

    @Schema(description = "服务商ID", example = "22978")
    @ExcelProperty("服务商ID")
    private Long merchantId;

    @Schema(description = "服务商名称")
    @ExcelProperty("服务商名称")
    private String merchantName;

    @Schema(description = "联系人")
    @ExcelProperty("联系人")
    private String merchantContactName;

    @Schema(description = "联系手机")
    @ExcelProperty("联系手机")
    private String merchantContactMobile;

    @Schema(description = "类目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "851")
    @ExcelProperty("类目ID")
    private Long categoryId;

    @Schema(description = "类目名称")
    @ExcelProperty("类目名称")
    private String categoryName;

    @Schema(description = "订单标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("订单标题")
    private String title;

    @Schema(description = "计价方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "计价方式", converter = DictConvert.class)
    @DictFormat("lb_pricing_mode") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String pricingMode;

    @Schema(description = "预算金额")
    @ExcelProperty("预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "订单金额")
    @ExcelProperty("订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "工期描述")
    @ExcelProperty("工期描述")
    private String serviceDurationDesc;

    @Schema(description = "数量")
    @ExcelProperty("数量")
    private BigDecimal quantity;

    @Schema(description = "需求描述")
    @ExcelProperty("需求描述")
    private String requireDesc;

    @Schema(description = "地址ID", example = "30674")
    @ExcelProperty("地址ID")
    private Long addressId;

    @Schema(description = "省")
    @ExcelProperty("省")
    private String province;

    @Schema(description = "市")
    @ExcelProperty("市")
    private String city;

    @Schema(description = "区")
    @ExcelProperty("区")
    private String district;

    @Schema(description = "街道")
    @ExcelProperty("街道")
    private String street;

    @Schema(description = "详细地址")
    @ExcelProperty("详细地址")
    private String detailAddress;

    @Schema(description = "经度")
    @ExcelProperty("经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    @ExcelProperty("纬度")
    private BigDecimal latitude;

    @Schema(description = "是否开票", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否开票")
    private Boolean needInvoice;

    @Schema(description = "是否拆单", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("是否拆单")
    private Boolean needSplit;

    @Schema(description = "拆单状态", example = "2")
    @ExcelProperty("拆单状态")
    private String splitStatus;

    @Schema(description = "协议是否确认", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("协议是否确认")
    private Boolean agreementConfirmed;

    @Schema(description = "支付订单ID", example = "7048")
    @ExcelProperty("支付订单ID")
    private Long payOrderId;

    @Schema(description = "订单状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "订单状态", converter = DictConvert.class)
    @DictFormat("lb_order_status") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
