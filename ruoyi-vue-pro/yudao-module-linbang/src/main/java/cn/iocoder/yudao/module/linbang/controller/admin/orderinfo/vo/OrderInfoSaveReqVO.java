package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 订单主新增/修改 Request VO")
@Data
public class OrderInfoSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "11427")
    private Long id;

    @Schema(description = "订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;

    @Schema(description = "下单用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20911")
    @NotNull(message = "下单用户ID不能为空")
    private Long userId;

    @Schema(description = "服务商ID", example = "22978")
    private Long merchantId;

    @Schema(description = "类目ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "851")
    @NotNull(message = "类目ID不能为空")
    private Long categoryId;

    @Schema(description = "订单标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "订单标题不能为空")
    private String title;

    @Schema(description = "计价方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "计价方式不能为空")
    private String pricingMode;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "工期描述")
    private String serviceDurationDesc;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "需求描述")
    private String requireDesc;

    @Schema(description = "地址ID", example = "30674")
    private Long addressId;

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

    @Schema(description = "是否开票", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否开票不能为空")
    private Boolean needInvoice;

    @Schema(description = "是否拆单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否拆单不能为空")
    private Boolean needSplit;

    @Schema(description = "拆单状态", example = "2")
    private String splitStatus;

    @Schema(description = "协议是否确认", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "协议是否确认不能为空")
    private Boolean agreementConfirmed;

    @Schema(description = "支付订单ID", example = "7048")
    private Long payOrderId;

    @Schema(description = "订单状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotEmpty(message = "订单状态不能为空")
    private String status;

}