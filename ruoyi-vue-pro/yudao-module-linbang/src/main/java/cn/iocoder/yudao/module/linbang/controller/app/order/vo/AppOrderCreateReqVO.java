package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

import static cn.iocoder.yudao.framework.common.util.number.MoneyUtils.MAX_YUAN_AMOUNT_STR;

@Schema(description = "用户 App - 创建订单 Request VO")
@Data
public class AppOrderCreateReqVO {

    @Schema(description = "类目 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "类目不能为空")
    private Long categoryId;

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, requiredMode = Schema.RequiredMode.REQUIRED, example = "FIXED_PRICE")
    @NotBlank(message = "计价方式不能为空")
    @Size(max = 32, message = "计价方式不能超过 32 个字符")
    private String pricingMode;

    @Schema(description = "预算金额，单位元，最高 21474836.47 元且最多 2 位小数；当未传 priceItems 时，系统默认按该金额作为订单应付金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "199.00")
    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.00", message = "预算金额不能小于 0")
    @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "预算金额超过支付渠道支持上限")
    @Digits(integer = 8, fraction = 2, message = "预算金额最多 8 位整数和 2 位小数")
    private BigDecimal budgetAmount;

    @Schema(description = "数量。不是全平台统一单位，按当前类目 quantityUnitLabel 对应的件/次/小时/台等口径传值；是否参与拆单由类目 quantitySplitEnabled 决定", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于 0")
    @DecimalMax(value = "1000000.00", message = "数量不能超过 1000000")
    @Digits(integer = 7, fraction = 2, message = "数量最多 7 位整数和 2 位小数")
    private BigDecimal quantity;

    @Schema(description = "服务人数。用于多人服务拆单规则匹配，范围 1 到 100，默认可传 1", example = "2")
    @Min(value = 1, message = "服务人数必须大于 0")
    @Max(value = 100, message = "服务人数不能超过 100")
    private Integer workerCount;

    @Schema(description = "服务时长/工期说明，仅用于展示，例如 1小时、半天、今天内、3天内上门", example = "1小时")
    @Size(max = 64, message = "服务时长说明不能超过 64 个字符")
    private String serviceDurationDesc;

    @Schema(description = "具体需求描述，同时作为订单标题来源。首页发布需求场景无需再单独传 title", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "具体需求描述不能为空")
    @Size(max = 5000, message = "具体需求描述不能超过 5000 个字符")
    private String requireDesc;

    @Schema(description = "省。普通用户无固定归属区域，本次发单按当前业务地址或用户手动填写的跨区地址生效", requiredMode = Schema.RequiredMode.REQUIRED, example = "广东省")
    @NotBlank(message = "省不能为空")
    @Size(max = 64, message = "省不能超过 64 个字符")
    private String province;

    @Schema(description = "市", requiredMode = Schema.RequiredMode.REQUIRED, example = "深圳市")
    @NotBlank(message = "市不能为空")
    @Size(max = 64, message = "市不能超过 64 个字符")
    private String city;

    @Schema(description = "区", requiredMode = Schema.RequiredMode.REQUIRED, example = "南山区")
    @NotBlank(message = "区不能为空")
    @Size(max = 64, message = "区不能超过 64 个字符")
    private String district;

    @Schema(description = "街道", example = "粤海街道")
    @Size(max = 64, message = "街道不能超过 64 个字符")
    private String street;

    @Schema(description = "详细地址。订单所属区域以本次创建时确认的服务地址为准，不以账号默认地址永久绑定", requiredMode = Schema.RequiredMode.REQUIRED, example = "科技园南区 XX 大厦 1201")
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 255, message = "详细地址不能超过 255 个字符")
    private String detailAddress;

    @Schema(description = "经度，前端已拿到坐标时可直接传；不传时后端会根据地址尝试解析", example = "113.941513")
    @DecimalMin(value = "-180.000000", message = "经度不能小于 -180")
    @DecimalMax(value = "180.000000", message = "经度不能大于 180")
    @Digits(integer = 3, fraction = 6, message = "经度最多 3 位整数和 6 位小数")
    private BigDecimal longitude;

    @Schema(description = "纬度，前端已拿到坐标时可直接传；不传时后端会根据地址尝试解析", example = "22.540503")
    @DecimalMin(value = "-90.000000", message = "纬度不能小于 -90")
    @DecimalMax(value = "90.000000", message = "纬度不能大于 90")
    @Digits(integer = 2, fraction = 6, message = "纬度最多 2 位整数和 6 位小数")
    private BigDecimal latitude;

    @Schema(description = "区域编码，高德 adcode，便于提升地址解析准确度", example = "440305")
    @Size(max = 12, message = "区域编码不能超过 12 个字符")
    private String adcode;

    @Schema(description = "是否需要发票。当前仅作为订单标记字段保存", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否需要发票不能为空")
    private Boolean needInvoice;

    @Schema(description = "是否允许系统应用可选拆单规则。平台金额满 200 元时会按硬性规则自动拆单，不受该字段影响；true 时其余命中规则也会真正生成多个单元", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否需要拆单不能为空")
    private Boolean needSplit;

    @Schema(description = "是否确认协议。必须传 true 才允许创建订单", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "请确认服务协议")
    private Boolean agreementConfirmed;

    @Schema(description = "协议版本。后端会按该版本做快照保存", requiredMode = Schema.RequiredMode.REQUIRED, example = "v2026.06")
    @NotBlank(message = "协议版本不能为空")
    @Size(max = 64, message = "协议版本不能超过 64 个字符")
    private String agreementVersion;

    @Schema(description = "预览快照令牌。必须由预览接口返回", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "预览快照不能为空")
    @Size(max = 4096, message = "预览快照长度不合法")
    private String previewToken;

    @Schema(description = "是否确认防逃单提醒。发单前必须传 true", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "请确认防逃单提醒")
    private Boolean antiEscapeConfirmed;

    @Schema(description = "附件文件 ID 列表。需先调用 /app-api/infra/file/presigned-url 直传文件，再调用 /app-api/infra/file/create 获取 fileId，最后把 fileId 传到这里", example = "[101,102]")
    @Size(max = 10, message = "订单附件不能超过 10 个")
    private List<Long> attachmentFileIds;

    @Schema(description = "价格明细。传了以后，系统会按各项 itemAmount 求和作为订单应付金额；不传则默认使用 budgetAmount")
    @Valid
    @Size(max = 100, message = "价格明细不能超过 100 项")
    private List<@NotNull(message = "价格明细项不能为空") OrderPriceItemReqVO> priceItems;

    @Schema(description = "用户 App - 订单价格项")
    @Data
    public static class OrderPriceItemReqVO {

        @Schema(description = "明细类型，按价格项类型字典展示，例如 LABOR 人工费、MATERIAL 材料费", requiredMode = Schema.RequiredMode.REQUIRED, example = "LABOR")
        @NotBlank(message = "明细类型不能为空")
        @Size(max = 32, message = "明细类型不能超过 32 个字符")
        private String itemType;

        @Schema(description = "明细名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "人工费")
        @NotBlank(message = "明细名称不能为空")
        @Size(max = 64, message = "明细名称不能超过 64 个字符")
        private String itemName;

        @Schema(description = "明细金额，单位元，最高 21474836.47 元且最多 2 位小数", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.00")
        @NotNull(message = "明细金额不能为空")
        @DecimalMin(value = "0.00", message = "明细金额不能小于 0")
        @DecimalMax(value = MAX_YUAN_AMOUNT_STR, message = "明细金额超过支付渠道支持上限")
        @Digits(integer = 8, fraction = 2, message = "明细金额最多 8 位整数和 2 位小数")
        private BigDecimal itemAmount;

        @Schema(description = "排序号；不传时后端按列表顺序自动补 1、2、3 ...", example = "1")
        @Min(value = 0, message = "排序号不能小于 0")
        @Max(value = 100000, message = "排序号不能超过 100000")
        private Integer sortNo;
    }
}
