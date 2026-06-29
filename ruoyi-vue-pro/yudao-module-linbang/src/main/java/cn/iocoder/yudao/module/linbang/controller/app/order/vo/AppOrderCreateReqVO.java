package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import cn.iocoder.yudao.module.linbang.constants.OpenApiSchemaConstants;
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

    @Schema(description = OpenApiSchemaConstants.PRICING_MODE, requiredMode = Schema.RequiredMode.REQUIRED, example = "FIXED_PRICE")
    @NotBlank(message = "计价方式不能为空")
    private String pricingMode;

    @Schema(description = "预算金额，单位元；当未传 priceItems 时，系统默认按该金额作为订单应付金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "199.00")
    @NotNull(message = "预算金额不能为空")
    @DecimalMin(value = "0.00", message = "预算金额不能小于 0")
    private BigDecimal budgetAmount;

    @Schema(description = "数量。按件/次/小时等业务口径传值，具体口径由类目和计价方式决定", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0.01", message = "数量必须大于 0")
    private BigDecimal quantity;

    @Schema(description = "服务人数。用于多人服务拆单规则匹配，默认可传 1", example = "2")
    private Integer workerCount;

    @Schema(description = "服务时长/工期说明，仅用于展示，例如 1小时、半天、今天内、3天内上门", example = "1小时")
    private String serviceDurationDesc;

    @Schema(description = "具体需求描述，同时作为订单标题来源。首页发布需求场景无需再单独传 title", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "具体需求描述不能为空")
    private String requireDesc;

    @Schema(description = "省", requiredMode = Schema.RequiredMode.REQUIRED, example = "广东省")
    @NotBlank(message = "省不能为空")
    private String province;

    @Schema(description = "市", requiredMode = Schema.RequiredMode.REQUIRED, example = "深圳市")
    @NotBlank(message = "市不能为空")
    private String city;

    @Schema(description = "区", requiredMode = Schema.RequiredMode.REQUIRED, example = "南山区")
    @NotBlank(message = "区不能为空")
    private String district;

    @Schema(description = "街道", example = "粤海街道")
    private String street;

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "科技园南区 XX 大厦 1201")
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    @Schema(description = "经度，前端已拿到坐标时可直接传；不传时后端会根据地址尝试解析", example = "113.941513")
    private BigDecimal longitude;

    @Schema(description = "纬度，前端已拿到坐标时可直接传；不传时后端会根据地址尝试解析", example = "22.540503")
    private BigDecimal latitude;

    @Schema(description = "区域编码，高德 adcode，便于提升地址解析准确度", example = "440305")
    private String adcode;

    @Schema(description = "是否需要发票。当前仅作为订单标记字段保存", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否需要发票不能为空")
    private Boolean needInvoice;

    @Schema(description = "是否需要拆单。当前版本暂不启用自动拆单，传 true/false 都只会生成 1 个订单单元", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
    @NotNull(message = "是否需要拆单不能为空")
    private Boolean needSplit;

    @Schema(description = "是否确认协议。必须传 true 才允许创建订单", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "请确认服务协议")
    private Boolean agreementConfirmed;

    @Schema(description = "协议版本。后端会按该版本做快照保存", requiredMode = Schema.RequiredMode.REQUIRED, example = "v2026.06")
    @NotBlank(message = "协议版本不能为空")
    private String agreementVersion;

    @Schema(description = "预览快照令牌。必须由预览接口返回", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "预览快照不能为空")
    private String previewToken;

    @Schema(description = "是否确认防逃单提醒。发单前必须传 true", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    @NotNull(message = "请确认防逃单提醒")
    private Boolean antiEscapeConfirmed;

    @Schema(description = "附件文件 ID 列表。需先调用 /app-api/infra/file/presigned-url 直传文件，再调用 /app-api/infra/file/create 获取 fileId，最后把 fileId 传到这里", example = "[101,102]")
    private List<Long> attachmentFileIds;

    @Schema(description = "价格明细。传了以后，系统会按各项 itemAmount 求和作为订单应付金额；不传则默认使用 budgetAmount")
    @Valid
    private List<OrderPriceItemReqVO> priceItems;

    @Schema(description = "用户 App - 订单价格项")
    @Data
    public static class OrderPriceItemReqVO {

        @Schema(description = "明细类型，按价格项类型字典展示，例如 LABOR 人工费、MATERIAL 材料费", requiredMode = Schema.RequiredMode.REQUIRED, example = "LABOR")
        @NotBlank(message = "明细类型不能为空")
        private String itemType;

        @Schema(description = "明细名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "人工费")
        @NotBlank(message = "明细名称不能为空")
        private String itemName;

        @Schema(description = "明细金额，单位元", requiredMode = Schema.RequiredMode.REQUIRED, example = "99.00")
        @NotNull(message = "明细金额不能为空")
        @DecimalMin(value = "0.00", message = "明细金额不能小于 0")
        private BigDecimal itemAmount;

        @Schema(description = "排序号；不传时后端按列表顺序自动补 1、2、3 ...", example = "1")
        private Integer sortNo;
    }
}
