package cn.iocoder.yudao.module.linbang.controller.app.pay.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 微信小程序支付提交 Response VO")
@Data
public class AppLinbangWechatMiniProgramPaySubmitRespVO {

    @Schema(description = "支付订单 ID，关联 pay_order.id", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "1024")
    private Long payOrderId;

    @Schema(description = "支付渠道编码。正式环境为 wx_lite；开启开发模拟支付时为 mock",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "wx_lite")
    private String channelCode;

    @Schema(description = "支付状态：10 待支付、20 支付成功、30 已退款、40 已关闭",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer status;

    @Schema(description = "展示模式。微信小程序支付为 app，开发模拟支付为 mock",
            requiredMode = Schema.RequiredMode.REQUIRED, example = "app")
    private String displayMode;

    @Schema(description = "微信小程序调起支付参数。正式 wx_lite 支付时返回；开发模拟支付时为空")
    private WechatPaymentParams paymentParams;

    @Schema(description = "微信小程序 requestPayment 参数")
    @Data
    public static class WechatPaymentParams {

        @Schema(description = "时间戳，单位秒", requiredMode = Schema.RequiredMode.REQUIRED, example = "1710000000")
        private String timeStamp;

        @Schema(description = "随机字符串", requiredMode = Schema.RequiredMode.REQUIRED, example = "5K8264ILTKCH16CQ2502SI8ZNMTM67VS")
        private String nonceStr;

        @Schema(description = "统一下单接口返回的预支付标识，格式通常为 prepay_id=xxx",
                requiredMode = Schema.RequiredMode.REQUIRED, example = "prepay_id=wx201410272009395522657a690389285100")
        private String packageValue;

        @Schema(description = "签名类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "RSA")
        private String signType;

        @Schema(description = "支付签名", requiredMode = Schema.RequiredMode.REQUIRED)
        private String paySign;
    }

}
