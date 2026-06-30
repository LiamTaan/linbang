package cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.aggregate;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.pay.enums.PayChannelEnum;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.order.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.refund.PayRefundRespDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.refund.PayRefundUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.transfer.PayTransferRespDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.transfer.PayTransferUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.AbstractPayClient;
import cn.iocoder.yudao.module.pay.framework.pay.core.enums.PayOrderDisplayModeEnum;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 聚合支付客户端。
 *
 * <p>当前第三方文档只提供统一下单、订单查询、支付成功回调，因此退款、转账先明确不支持。</p>
 */
public class AggregatePayClient extends AbstractPayClient<AggregatePayClientConfig> {

    private static final String CREATE_ORDER_PATH = "/manage/pay/add-order";
    private static final String QUERY_ORDER_PATH = "/manage/pay/info/query-order";
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    private static final DateTimeFormatter NOTIFY_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AggregatePayClient(Long channelId, AggregatePayClientConfig config) {
        super(channelId, PayChannelEnum.AGGREGATE.getCode(), config);
    }

    @Override
    protected void doInit() {
    }

    @Override
    protected PayOrderRespDTO doUnifiedOrder(PayOrderUnifiedReqDTO reqDTO) {
        Map<String, Object> request = new HashMap<>(8);
        request.put("orderId", reqDTO.getOutTradeNo());
        request.put("shh", config.getMerchantNo());
        request.put("money", fenToYuan(reqDTO.getPrice()));
        request.put("merchantName", config.getMerchantName());
        request.put("notifyUrl", reqDTO.getNotifyUrl());

        String responseText = postJson(CREATE_ORDER_PATH, request);
        JsonNode response = JsonUtils.parseTree(responseText);
        if (!isSuccessResponse(response)) {
            return PayOrderRespDTO.closedOf(getText(response, "code"), getText(response, "msg"),
                    reqDTO.getOutTradeNo(), responseText);
        }
        String payUrl = getText(response, "data");
        if (StrUtil.isBlank(payUrl)) {
            return PayOrderRespDTO.closedOf("EMPTY_PAY_URL", "聚合支付未返回支付跳转地址",
                    reqDTO.getOutTradeNo(), responseText);
        }
        return PayOrderRespDTO.waitingOf(PayOrderDisplayModeEnum.URL.getMode(), payUrl,
                reqDTO.getOutTradeNo(), responseText);
    }

    @Override
    protected PayOrderRespDTO doParseOrderNotify(Map<String, String> params, String body, Map<String, String> headers) {
        JsonNode notify = StrUtil.isNotBlank(body) && JsonUtils.isJsonObject(body)
                ? JsonUtils.parseTree(body) : JsonUtils.parseTree(JsonUtils.toJsonString(params));
        String outTradeNo = firstNotBlank(getText(notify, "out_trade_no"), getText(notify, "orderId"));
        if (StrUtil.isBlank(outTradeNo)) {
            return PayOrderRespDTO.of(PayOrderStatusEnum.WAITING.getStatus(), getText(notify, "trade_no"),
                    StrUtil.blankToDefault(getText(notify, "openid"), getText(notify, "buyer_user_id")),
                    parseDateTime(getText(notify, "notify_time")), null, StrUtil.isNotBlank(body) ? body : params);
        }
        // 聚合支付文档要求：收到支付回调后主动查单，并以查单结果作为支付成功判断依据。
        return doGetOrder(outTradeNo);
    }

    private PayOrderRespDTO parseOrderQueryResponse(String outTradeNo, String responseText) {
        JsonNode response = JsonUtils.parseTree(responseText);
        if (!isSuccessResponse(response)) {
            return PayOrderRespDTO.closedOf(getText(response, "code"), getText(response, "msg"),
                    outTradeNo, responseText);
        }
        JsonNode business = parseBusinessResponse(response);
        String tradeStatus = getText(business, "trade_status");
        String tradeNo = getText(business, "trade_no");
        String channelUserId = StrUtil.blankToDefault(getText(business, "openid"), getText(business, "buyer_user_id"));
        LocalDateTime successTime = parseDateTime(firstNotBlank(getText(business, "pay_time"),
                getText(business, "gmt_payment"), getText(business, "notify_time")));
        if (TRADE_SUCCESS.equals(tradeStatus)) {
            return PayOrderRespDTO.successOf(tradeNo, channelUserId, successTime,
                    firstNotBlank(getText(business, "out_trade_no"), getText(business, "orderId"), outTradeNo),
                    responseText);
        }
        return PayOrderRespDTO.of(PayOrderStatusEnum.WAITING.getStatus(), tradeNo, channelUserId,
                null, outTradeNo, responseText);
    }

    @Override
    protected PayOrderRespDTO doGetOrder(String outTradeNo) {
        Map<String, Object> request = new HashMap<>(4);
        request.put("orderId", outTradeNo);
        request.put("shh", config.getMerchantNo());

        return parseOrderQueryResponse(outTradeNo, postJson(QUERY_ORDER_PATH, request));
    }

    @Override
    protected PayRefundRespDTO doUnifiedRefund(PayRefundUnifiedReqDTO reqDTO) {
        throw new UnsupportedOperationException("聚合支付文档暂未提供退款接口");
    }

    @Override
    protected PayRefundRespDTO doParseRefundNotify(Map<String, String> params, String body, Map<String, String> headers) {
        throw new UnsupportedOperationException("聚合支付文档暂未提供退款回调");
    }

    @Override
    protected PayRefundRespDTO doGetRefund(String outTradeNo, String outRefundNo) {
        throw new UnsupportedOperationException("聚合支付文档暂未提供退款查询接口");
    }

    @Override
    protected PayTransferRespDTO doUnifiedTransfer(PayTransferUnifiedReqDTO reqDTO) {
        throw new UnsupportedOperationException("聚合支付文档暂未提供转账打款接口");
    }

    @Override
    protected PayTransferRespDTO doParseTransferNotify(Map<String, String> params, String body, Map<String, String> headers) {
        throw new UnsupportedOperationException("聚合支付文档暂未提供转账回调");
    }

    @Override
    protected PayTransferRespDTO doGetTransfer(String outTradeNo) {
        throw new UnsupportedOperationException("聚合支付文档暂未提供转账查询接口");
    }

    private String postJson(String path, Map<String, Object> request) {
        if (StrUtil.isBlank(config.getBaseUrl())) {
            throw new IllegalStateException("聚合支付 baseUrl 未配置");
        }
        return HttpUtils.post(buildUrl(path), MapUtil.of("Content-Type", "application/json; charset=utf-8"),
                JsonUtils.toJsonString(request));
    }

    private String buildUrl(String path) {
        return StrUtil.removeSuffix(config.getBaseUrl(), "/") + path;
    }

    private BigDecimal fenToYuan(Integer price) {
        return BigDecimal.valueOf(price).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    private boolean isSuccessResponse(JsonNode response) {
        return response != null && response.path("code").asInt() == 200;
    }

    private JsonNode parseBusinessResponse(JsonNode response) {
        JsonNode data = response.path("data");
        if (data.hasNonNull("trade_status")) {
            return data;
        }
        JsonNode business = data.path("ysepay_online_trade_order_query_response");
        if (business.isTextual() && JsonUtils.isJsonObject(business.asText())) {
            return JsonUtils.parseTree(business.asText());
        }
        if (!business.isMissingNode() && !business.isNull()) {
            return business;
        }
        return data.isMissingNode() || data.isNull() ? response : data;
    }

    private String getText(JsonNode node, String fieldName) {
        return JsonUtils.getText(node, fieldName);
    }

    private LocalDateTime parseDateTime(String text) {
        if (StrUtil.isBlank(text)) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(text, NOTIFY_TIME_FORMATTER);
        } catch (Exception ignored) {
            return LocalDateTime.now();
        }
    }

    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

}
