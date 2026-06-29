package cn.iocoder.yudao.module.linbang.controller.app.order.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "用户 App - 修改订单 Request VO")
@Data
public class AppOrderUpdateReqVO {

    @Schema(description = "订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @Schema(description = "服务时长/工期说明，例如 1小时、半天、今天内、3天内上门", example = "明天下午上门")
    private String serviceDurationDesc;

    @Schema(description = "具体需求描述", requiredMode = Schema.RequiredMode.REQUIRED)
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

    @Schema(description = "经度", example = "113.941513")
    private BigDecimal longitude;

    @Schema(description = "纬度", example = "22.540503")
    private BigDecimal latitude;

    @Schema(description = "区域编码，高德 adcode", example = "440305")
    private String adcode;

    @Schema(description = "是否需要发票，仅允许待付款和待接单状态修改", example = "false")
    @NotNull(message = "是否需要发票不能为空")
    private Boolean needInvoice;

    @Schema(description = "附件文件 ID 列表，会覆盖原有附件", example = "[101,102]")
    private List<Long> attachmentFileIds;
}
