package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, requiredMode = Schema.RequiredMode.REQUIRED, example = "FIXED_PRICE")
    @NotEmpty(message = "计价方式不能为空")
    private String pricingMode;

    @Schema(description = "预算金额")
    private BigDecimal budgetAmount;

    @Schema(description = "订单金额")
    private BigDecimal orderAmount;

    @Schema(description = "服务时长说明，例如 1小时、半天、3天")
    private String serviceDurationDesc;

    @Schema(description = "数量")
    private BigDecimal quantity;

    @Schema(description = "需求描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "需求描述不能为空")
    private String requireDesc;

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

    @Schema(description = OpenApiSchemaConstants.ORDER_SPLIT_STATUS, example = "UNSPLIT")
    private String splitStatus;

    @Schema(description = "协议是否确认", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "协议是否确认不能为空")
    private Boolean agreementConfirmed;

    @Schema(description = "支付订单ID", example = "7048")
    private Long payOrderId;

    @Schema(description = "关联商城消费记录 ID", example = "88001")
    private Long mallConsumeRecordId;

    @Schema(description = "关联商城消费记录编号", example = "MALL202606280001")
    private String mallConsumeRecordNo;

    @Schema(description = "关联商城消费金额，单位元", example = "88.80")
    private BigDecimal mallConsumeAmount;

    @Schema(description = "关联商城消费状态", example = "SUCCESS")
    private String mallConsumeStatus;

    @Schema(description = "推广抵扣金额，单位元", example = "12.50")
    private BigDecimal promoteDeductAmount;

    @Schema(description = "推广抵扣来源类型", example = "PROMOTER")
    private String promoteDeductSourceType;

    @Schema(description = "推广抵扣来源对象 ID", example = "9001")
    private Long promoteDeductSourceId;

    @Schema(description = "推广抵扣来源对象编号", example = "PROMO202606280001")
    private String promoteDeductSourceNo;

    @Schema(description = "抵扣后订单应付金额，单位元", example = "176.50")
    private BigDecimal payableAmountAfterDeduct;

    @Schema(description = OpenApiSchemaConstants.ORDER_STATUS, requiredMode = Schema.RequiredMode.REQUIRED, example = "PENDING_PAY")
    @NotEmpty(message = "订单状态不能为空")
    private String status;

}
