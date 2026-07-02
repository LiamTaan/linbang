package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 邻里 H5 支付提交 Response VO")
@Data
public class AppLinbangH5PaySubmitRespVO {

    @Schema(description = "支付订单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long payOrderId;

    @Schema(description = "本次提交的前端支付方式：WECHAT_H5 微信支付、ALIPAY_H5 支付宝支付、UNIONPAY_WAP 银行卡/云闪付",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "WECHAT_H5")
    private String payWay;

    @Schema(description = "支付方式名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "微信支付")
    private String payWayName;

    @Schema(description = "支付状态，10=待支付，20=支付成功，30=已退款，40=已关闭", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "展示模式，聚合支付 H5 固定返回 url", requiredMode = Schema.RequiredMode.REQUIRED, example = "url")
    private String displayMode;

    @Schema(description = "聚合支付 H5 收银台跳转地址；App WebView 或外部浏览器直接打开该地址即可发起支付", requiredMode = Schema.RequiredMode.REQUIRED)
    private String displayContent;

}
