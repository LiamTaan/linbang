package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 发单预览 Request VO")
@Data
public class AppOrderPreviewReqVO {

    @Schema(description = "类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "340005")
    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @Schema(description = "计价方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "FIXED_PRICE")
    @NotBlank(message = "计价方式不能为空")
    private String pricingMode;

    @Schema(description = "预算金额，单位元", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.00", message = "预算金额不能小于 0")
    private BigDecimal budgetAmount;

    @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "服务人数", example = "2")
    private Integer workerCount;

    @Schema(description = "服务时长/工期说明", example = "2天内完成")
    private String serviceDurationDesc;

    @Schema(description = "需求描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "需求描述不能为空")
    private String requireDesc;

    @Schema(description = "省", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "省不能为空")
    private String province;

    @Schema(description = "市", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "市不能为空")
    private String city;

    @Schema(description = "区", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "区不能为空")
    private String district;

    @Schema(description = "街道")
    private String street;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    @Schema(description = "经度", example = "113.941513")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "22.540503")
    private BigDecimal latitude;

    @Schema(description = "高德 adcode", example = "440305")
    private String adcode;

    @Schema(description = "是否开票", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否开票不能为空")
    private Boolean needInvoice;

    @Schema(description = "是否希望拆单", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否拆单不能为空")
    private Boolean needSplit;

    @Schema(description = "附件文件 ID 列表")
    private List<Long> attachmentFileIds;

    @Schema(description = "价格明细")
    @Valid
    private List<AppOrderCreateReqVO.OrderPriceItemReqVO> priceItems;
}
