package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 邻里 H5 支付提交 Response VO")
@Data
public class AppLinbangH5PaySubmitRespVO {

    @Schema(description = "支付订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long payOrderId;

    @Schema(description = "支付状态，10=待支付，20=支付成功，30=已退款，40=已关闭", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "展示模式，聚合支付 H5 固定返回 url", requiredMode = Schema.RequiredMode.REQUIRED, example = "url")
    private String displayMode;

    @Schema(description = "聚合支付 H5 收银台跳转地址；App WebView 或外部浏览器直接打开该地址即可发起支付", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayContent;

}
