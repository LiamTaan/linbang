package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 创建订单 Request VO")
@Data
public class AppOrderCreateReqVO {

    @Schema(description = "类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @Schema(description = "订单标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "订单标题不能为空")
    private String title;

    @Schema(description = "计价方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "计价方式不能为空")
    private String pricingMode;

    @Schema(description = "预算金额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.00", message = "预算金额不能小于 0")
    private BigDecimal budgetAmount;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "工期描述")
    private String serviceDurationDesc;

    @Schema(description = "需求描述")
    private String requireDesc;

    @Schema(description = "地址 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "地址不能为空")
    private Long addressId;

    @Schema(description = "是否需要发票", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否需要发票不能为空")
    private Boolean needInvoice;

    @Schema(description = "是否需要拆单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否需要拆单不能为空")
    private Boolean needSplit;

    @Schema(description = "是否确认协议", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "请确认服务协议")
    private Boolean agreementConfirmed;

    @Schema(description = "附件文件 ID 列表")
    private List<Long> attachmentFileIds;

    @Schema(description = "价格明细")
    @Valid
    private List<OrderPriceItemReqVO> priceItems;

    @Schema(description = "用户 App - 订单价格项")
    @Data
    public static class OrderPriceItemReqVO {

        @Schema(description = "明细类型", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "明细类型不能为空")
        private String itemType;

        @Schema(description = "明细名称", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "明细名称不能为空")
        private String itemName;

        @Schema(description = "明细金额", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "明细金额不能为空")
        @DecimalMin(value = "0.00", message = "明细金额不能小于 0")
        private BigDecimal itemAmount;

        @Schema(description = "排序号")
        private Integer sortNo;
    }
}
