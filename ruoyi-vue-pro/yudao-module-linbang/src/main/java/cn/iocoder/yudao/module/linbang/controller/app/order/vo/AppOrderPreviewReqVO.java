package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

import cn.iocoder.yudao.module.linbang.controller.app.order.vo.AppOrderCreateReqVO.OrderPriceItemReqVO;

import static cn.iocoder.yudao.framework.common.util.number.MoneyUtils.MAX_YUAN_AMOUNT_STR;

@Schema(description = "用户 App - 发单预览 Request VO")
@Data
public class AppOrderPreviewReqVO {

    @Schema(description = "类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "340005")
    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @Schema(description = "计价方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "FIXED_PRICE")
    @NotBlank(message = "计价方式不能为空")
    @Size(max = 32, message = "计价方式不能超过 32 个字符")
    private String pricingMode;

    @Schema(description = "预算金额，单位元，最高 21474836.47 元且最多 2 位小数", requiredMode = Schema.RequiredMode.REQUIRED, example = "500.00")
    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.00", message = "预算金额不能小于 0")
    @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "预算金额超过支付渠道支持上限")
    @Digits(integer = 8, fraction = 2, message = "预算金额最多 8 位整数和 2 位小数")
    private BigDecimal budgetAmount;

    @Schema(description = "数量。不是全平台统一单位，按当前类目 quantityUnitLabel 对应的件/次/小时/台等口径传值；是否参与拆单由类目 quantitySplitEnabled 决定", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于 0")
    @DecimalMax(value = "1000000.00", message = "数量不能超过 1000000")
    @Digits(integer = 7, fraction = 2, message = "数量最多 7 位整数和 2 位小数")
    private BigDecimal quantity;

    @Schema(description = "服务人数", example = "2")
    @Min(value = 1, message = "服务人数必须大于 0")
    @Max(value = 100, message = "服务人数不能超过 100")
    private Integer workerCount;

    @Schema(description = "服务时长/工期说明", example = "2天内完成")
    @Size(max = 64, message = "服务时长说明不能超过 64 个字符")
    private String serviceDurationDesc;

    @Schema(description = "需求描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "需求描述不能为空")
    @Size(max = 5000, message = "需求描述不能超过 5000 个字符")
    private String requireDesc;

    @Schema(description = "省。普通用户预览发单时按当前业务地址或手动填写的跨区地址生效", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "省不能为空")
    @Size(max = 64, message = "省不能超过 64 个字符")
    private String province;

    @Schema(description = "市", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "市不能为空")
    @Size(max = 64, message = "市不能超过 64 个字符")
    private String city;

    @Schema(description = "区", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "区不能为空")
    @Size(max = 64, message = "区不能超过 64 个字符")
    private String district;

    @Schema(description = "街道")
    @Size(max = 64, message = "街道不能超过 64 个字符")
    private String street;

    @Schema(description = "详细地址。预览与下单均以本次确认的服务地址为范围基准", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址不能超过 255 个字符")
    private String detailAddress;

    @Schema(description = "经度", example = "113.941513")
    @DecimalMin(value = "-180.000000", message = "经度不能小于 -180")
    @DecimalMax(value = "180.000000", message = "经度不能大于 180")
    @Digits(integer = 3, fraction = 6, message = "经度最多 3 位整数和 6 位小数")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "22.540503")
    @DecimalMin(value = "-90.000000", message = "纬度不能小于 -90")
    @DecimalMax(value = "90.000000", message = "纬度不能大于 90")
    @Digits(integer = 2, fraction = 6, message = "纬度最多 2 位整数和 6 位小数")
    private BigDecimal latitude;

    @Schema(description = "高德 adcode", example = "440305")
    @Size(max = 12, message = "区域编码不能超过 12 个字符")
    private String adcode;

    @Schema(description = "是否开票", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否开票不能为空")
    private Boolean needInvoice;

    @Schema(description = "是否允许系统应用可选拆单规则。平台金额满 200 元时会按硬性规则自动拆单，不受该字段影响；true 时其余命中规则也会直接生成多个单元", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否拆单不能为空")
    private Boolean needSplit;

    @Schema(description = "附件文件 ID 列表")
    @Size(max = 10, message = "订单附件不能超过 10 个")
    private List<Long> attachmentFileIds;

    @Schema(description = "价格明细")
    @Valid
    @Size(max = 100, message = "价格明细不能超过 100 项")
    private List<@NotNull(message = "价格明细项不能为空") OrderPriceItemReqVO> priceItems;
}
