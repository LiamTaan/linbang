package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 修改订单 Request VO")
@Data
public class AppOrderUpdateReqVO {

    @Schema(description = "订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @Schema(description = "服务时长/工期说明，例如 1小时、半天、今天内、3天内上门", example = "明天下午上门")
    @Size(max = 64, message = "服务时长说明不能超过 64 个字符")
    private String serviceDurationDesc;

    @Schema(description = "具体需求描述", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "具体需求描述不能为空")
    @Size(max = 5000, message = "具体需求描述不能超过 5000 个字符")
    private String requireDesc;

    @Schema(description = "省", requiredMode = Schema.RequiredMode.REQUIRED, example = "广东省")
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

    @Schema(description = "详细地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "科技园南区 XX 大厦 1201")
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

    @Schema(description = "区域编码，高德 adcode", example = "440305")
    @Size(max = 12, message = "区域编码不能超过 12 个字符")
    private String adcode;

    @Schema(description = "是否需要发票，仅允许待付款和待接单状态修改", example = "false")
    @NotNull(message = "是否需要发票不能为空")
    private Boolean needInvoice;

    @Schema(description = "附件文件 ID 列表，会覆盖原有附件", example = "[101,102]")
    @Size(max = 10, message = "订单附件不能超过 10 个")
    private List<Long> attachmentFileIds;
}
