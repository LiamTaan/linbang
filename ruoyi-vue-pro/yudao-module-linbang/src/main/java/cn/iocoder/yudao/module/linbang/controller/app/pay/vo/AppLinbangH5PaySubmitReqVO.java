package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 邻里 H5 支付提交 Request VO")
@Data
public class AppLinbangH5PaySubmitReqVO {

    @Schema(description = "订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @Schema(description = "支付完成后的 App 回跳地址；聚合支付 H5 收银台完成后按渠道配置回跳", example = "https://app.linbang.com/pay/result")
    @URL(message = "回跳地址的格式必须是 URL")
    private String returnUrl;

}
