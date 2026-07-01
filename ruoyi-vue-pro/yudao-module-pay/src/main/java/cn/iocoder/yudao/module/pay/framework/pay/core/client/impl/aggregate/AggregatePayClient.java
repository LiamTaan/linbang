package cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.aggregate;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.http.HttpUtils;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.pay.enums.PayChannelEnum;
import cn.iocoder.yudao.module.pay.enums.order.PayOrderStatusEnum;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import cn.iocoder.yudao.module.pay.enums.transfer.PayTransferStatusEnum;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.order.PayOrderRespDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.order.PayOrderUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.refund.PayRefundRespDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.refund.PayRefundUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.transfer.PayTransferRespDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.transfer.PayTransferUnifiedReqDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.AbstractPayClient;
import cn.iocoder.yudao.module.pay.framework.pay.core.enums.PayOrderDisplayModeEnum;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * 聚合支付客户端。
 *
 * <p>支付下单、查单仍复用既有中间层；退款与提现按照银盛开放平台官方接口直连。</p>
 */
public class AggregatePayClient extends AbstractPayClient<AggregatePayClientConfig> {

    private static final String CREATE_ORDER_PATH = "/manage/pay/add-order";
    private static final String QUERY_ORDER_PATH = "/manage/pay/info/query-order";

    private static final String REFUND_METHOD = "ysepay.online.trade.refund";
    private static final String REFUND_QUERY_METHOD = "ysepay.online.trade.refund.query";
    private static final String TRANSFER_METHOD = "ysepay.df.single.normal.accept";
    private static final String TRANSFER_QUERY_METHOD = "ysepay.df.single.query";

    private static final String REFUND_RESPONSE_KEY = "ysepay_online_trade_refund_response";
    private static final String REFUND_QUERY_RESPONSE_KEY = "ysepay_online_trade_refund_query_response";
    private static final String TRANSFER_RESPONSE_KEY = "ysepay_df_single_normal_accept_response";
    private static final String TRANSFER_QUERY_RESPONSE_KEY = "ysepay_df_single_query_respose";

    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    private static final String TRADE_ACCEPT_SUCCESS = "TRADE_ACCEPT_SUCCESS";
    private static final String TRADE_FAILURE = "TRADE_FAILURE";
    private static final String DISHONOUR_SUCCESS = "DISHONOUR_SUCCESS";
    private static final String REFUND_NOTIFY_TYPE = "refund.status.sync";
    private static final String TRANSFER_NOTIFY_TYPE = "ysepay.df.single.notify";
    private static final String SIGN_TYPE_SM = "SM";

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        return doGetOrder(outTradeNo);
    }

    @Override
    protected PayOrderRespDTO doGetOrder(String outTradeNo) {
        Map<String, Object> request = new HashMap<>(4);
        request.put("orderId", outTradeNo);
        request.put("shh", config.getMerchantNo());
        return parseOrderQueryResponse(outTradeNo, postJson(QUERY_ORDER_PATH, request));
    }

    @Override
    protected PayRefundRespDTO doUnifiedRefund(PayRefundUnifiedReqDTO reqDTO) throws Throwable {
        ensureOfficialGatewayConfig();
        Map<String, Object> bizContent = new LinkedHashMap<>();
        bizContent.put("out_trade_no", reqDTO.getOutTradeNo());
        bizContent.put("shopdate", resolveShopDate(reqDTO.getOutTradeNo()));
        bizContent.put("refund_amount", fenToYuan(reqDTO.getRefundPrice()).toPlainString());
        bizContent.put("refund_reason", reqDTO.getReason());
        bizContent.put("out_request_no", reqDTO.getOutRefundNo());
        return parseRefundUnifiedResponse(reqDTO.getOutRefundNo(),
                postYsepayForm(config.getOpenApiGatewayUrl(), REFUND_METHOD, reqDTO.getNotifyUrl(), bizContent),
                null);
    }

    @Override
    protected PayRefundRespDTO doParseRefundNotify(Map<String, String> params, String body, Map<String, String> headers)
            throws Throwable {
        Map<String, String> notify = mergeNotifyParams(params, body);
        verifyNotifySign(notify);
        String notifyType = notify.get("notify_type");
        if (StrUtil.isNotBlank(notifyType) && !REFUND_NOTIFY_TYPE.equals(notifyType)) {
            throw new IllegalArgumentException("未知的退款通知类型：" + notifyType);
        }
        String outRefundNo = firstNotBlank(notify.get("out_request_no"), notify.get("out_trade_no"));
        String channelRefundNo = firstNotBlank(notify.get("refundsn"), notify.get("trade_no"));
        String refundState = firstNotBlank(notify.get("refund_state"), notify.get("funds_state"));
        Object rawData = StrUtil.isNotBlank(body) ? body : notify;
        if (StrUtil.isBlank(outRefundNo)) {
            throw new IllegalArgumentException("退款通知缺少商户退款单号");
        }
        if (StrUtil.isNotBlank(refundState)) {
            return buildRefundRespFromState(outRefundNo, channelRefundNo, refundState,
                    parseDateTime(notify.get("notify_time")), null, rawData);
        }
        return doGetRefund(firstNotBlank(notify.get("origin_out_trade_no"), notify.get("trade_no")), outRefundNo);
    }

    @Override
    protected PayRefundRespDTO doGetRefund(String outTradeNo, String outRefundNo) throws Throwable {
        ensureOfficialGatewayConfig();
        Map<String, Object> bizContent = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(outTradeNo)) {
            bizContent.put("out_trade_no", outTradeNo);
            bizContent.put("shopdate", resolveShopDate(outTradeNo));
        }
        bizContent.put("out_request_no", outRefundNo);
        bizContent.put("refund_query_type", 1);
        String responseText = postYsepayForm(config.getOpenApiGatewayUrl(), REFUND_QUERY_METHOD, null, bizContent);
        return parseRefundQueryResponse(outRefundNo, responseText);
    }

    @Override
    protected PayTransferRespDTO doUnifiedTransfer(PayTransferUnifiedReqDTO reqDTO) throws Throwable {
        ensureOfficialGatewayConfig();
        Map<String, Object> bizContent = new LinkedHashMap<>();
        bizContent.put("out_trade_no", reqDTO.getOutTransferNo());
        bizContent.put("shopdate", resolveShopDate(reqDTO.getOutTransferNo()));
        bizContent.put("trans_amount", fenToYuan(reqDTO.getPrice()).toPlainString());
        bizContent.put("subject", StrUtil.maxLength(reqDTO.getSubject(), 500));
        bizContent.put("bank_name", resolveTransferExtra(reqDTO, "bank_name", null));
        bizContent.put("bank_code", resolveTransferExtra(reqDTO, "bank_code", null));
        bizContent.put("bank_province", resolveTransferExtra(reqDTO, "bank_province", null));
        bizContent.put("bank_city", resolveTransferExtra(reqDTO, "bank_city",
                resolveTransferExtra(reqDTO, "bank_name", "未知城市")));
        bizContent.put("bank_account_no", reqDTO.getUserAccount());
        bizContent.put("bank_account_name", reqDTO.getUserName());
        bizContent.put("bank_account_type", resolveTransferExtra(reqDTO, "bank_account_type", "personal"));
        bizContent.put("bank_card_type", resolveTransferExtra(reqDTO, "bank_card_type", "debit"));
        bizContent.put("bank_telephone_no", resolveTransferExtra(reqDTO, "bank_telephone_no", null));
        String responseText = postYsepayForm(config.getTransferGatewayUrl(), TRANSFER_METHOD, reqDTO.getNotifyUrl(), bizContent);
        return parseTransferUnifiedResponse(reqDTO.getOutTransferNo(), responseText);
    }

    @Override
    protected PayTransferRespDTO doParseTransferNotify(Map<String, String> params, String body, Map<String, String> headers)
            throws Throwable {
        Map<String, String> notify = mergeNotifyParams(params, body);
        verifyNotifySign(notify);
        String notifyType = notify.get("notify_type");
        if (StrUtil.isNotBlank(notifyType) && !TRANSFER_NOTIFY_TYPE.equals(notifyType)) {
            throw new IllegalArgumentException("未知的转账通知类型：" + notifyType);
        }
        return buildTransferRespFromStatus(
                notify.get("out_trade_no"),
                notify.get("trade_no"),
                notify.get("trade_status"),
                parseDateTime(notify.get("notify_time")),
                notify.get("trade_status_description"),
                StrUtil.isNotBlank(body) ? body : notify);
    }

    @Override
    protected PayTransferRespDTO doGetTransfer(String outTradeNo) throws Throwable {
        ensureOfficialGatewayConfig();
        Map<String, Object> bizContent = new LinkedHashMap<>();
        bizContent.put("out_trade_no", outTradeNo);
        bizContent.put("shopdate", resolveShopDate(outTradeNo));
        String responseText = postYsepayForm(config.getTransferGatewayUrl(), TRANSFER_QUERY_METHOD, null, bizContent);
        return parseTransferQueryResponse(outTradeNo, responseText);
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

    private PayRefundRespDTO parseRefundUnifiedResponse(String outRefundNo, String responseText, String fallbackChannelRefundNo) {
        JsonNode business = parseSignedBusinessResponse(responseText, REFUND_RESPONSE_KEY);
        String code = getText(business, "code");
        String msg = getText(business, "msg");
        String channelRefundNo = firstNotBlank(getText(business, "refundsn"), fallbackChannelRefundNo, getText(business, "trade_no"));
        if ("10000".equals(code)) {
            return PayRefundRespDTO.waitingOf(channelRefundNo, outRefundNo, responseText);
        }
        if (isYsepayUnknownState(code)) {
            return PayRefundRespDTO.waitingOf(channelRefundNo, outRefundNo, responseText);
        }
        return PayRefundRespDTO.failureOf(code, msg, outRefundNo, responseText);
    }

    private PayRefundRespDTO parseRefundQueryResponse(String outRefundNo, String responseText) {
        JsonNode business = parseSignedBusinessResponse(responseText, REFUND_QUERY_RESPONSE_KEY);
        String code = getText(business, "code");
        String msg = getText(business, "msg");
        if (StrUtil.isNotBlank(code) && !"10000".equals(code) && !isYsepayUnknownState(code)) {
            return PayRefundRespDTO.failureOf(code, msg, outRefundNo, responseText);
        }
        String refundState = firstNotBlank(getText(business, "refund_state"), getText(business, "funds_state"));
        String channelRefundNo = firstNotBlank(getText(business, "refundsn"), getText(business, "trade_no"));
        return buildRefundRespFromState(outRefundNo, channelRefundNo, refundState,
                parseDateTime(firstNotBlank(getText(business, "notify_time"), getText(business, "account_date"))),
                msg, responseText);
    }

    private PayRefundRespDTO buildRefundRespFromState(String outRefundNo, String channelRefundNo, String refundState,
                                                      LocalDateTime successTime, String channelErrorMsg, Object rawData) {
        if (StrUtil.isBlank(refundState)) {
            return PayRefundRespDTO.waitingOf(channelRefundNo, outRefundNo, rawData);
        }
        String normalized = refundState.toLowerCase(Locale.ROOT);
        if ("success".equals(normalized)) {
            return PayRefundRespDTO.successOf(channelRefundNo, successTime, outRefundNo, rawData);
        }
        if ("in_process".equals(normalized)) {
            return PayRefundRespDTO.waitingOf(channelRefundNo, outRefundNo, rawData);
        }
        if ("fail_to_manual_deal".equals(normalized) || "fail_due_manual_close".equals(normalized)
                || "fail".equals(normalized)) {
            return PayRefundRespDTO.failureOf(refundState, channelErrorMsg, outRefundNo, rawData);
        }
        return PayRefundRespDTO.waitingOf(channelRefundNo, outRefundNo, rawData);
    }

    private PayTransferRespDTO parseTransferUnifiedResponse(String outTransferNo, String responseText) {
        JsonNode business = parseSignedBusinessResponse(responseText, TRANSFER_RESPONSE_KEY);
        String code = getText(business, "code");
        String msg = getText(business, "msg");
        String tradeStatus = getText(business, "trade_status");
        String channelTransferNo = getText(business, "trade_no");
        if ("10000".equals(code) || isYsepayUnknownState(code)) {
            return buildTransferRespFromStatus(outTransferNo, channelTransferNo,
                    StrUtil.blankToDefault(tradeStatus, TRADE_ACCEPT_SUCCESS), null, msg, responseText);
        }
        return PayTransferRespDTO.closedOf(code, msg, outTransferNo, responseText);
    }

    private PayTransferRespDTO parseTransferQueryResponse(String outTransferNo, String responseText) {
        JsonNode business = parseSignedBusinessResponse(responseText, TRANSFER_QUERY_RESPONSE_KEY);
        String code = getText(business, "code");
        String msg = getText(business, "msg");
        if (StrUtil.isNotBlank(code) && !"10000".equals(code) && !isYsepayUnknownState(code)) {
            return PayTransferRespDTO.closedOf(code, msg, outTransferNo, responseText);
        }
        return buildTransferRespFromStatus(outTransferNo, getText(business, "trade_no"),
                getText(business, "trade_status"),
                parseDateTime(firstNotBlank(getText(business, "notify_time"), getText(business, "account_date"))),
                firstNotBlank(getText(business, "trade_status_description"), msg), responseText);
    }

    private PayTransferRespDTO buildTransferRespFromStatus(String outTransferNo, String channelTransferNo, String tradeStatus,
                                                           LocalDateTime successTime, String errorMsg, Object rawData) {
        if (TRADE_SUCCESS.equals(tradeStatus)) {
            return PayTransferRespDTO.successOf(channelTransferNo, successTime, outTransferNo, rawData);
        }
        if (TRADE_ACCEPT_SUCCESS.equals(tradeStatus) || StrUtil.isBlank(tradeStatus)) {
            return PayTransferRespDTO.processingOf(channelTransferNo, outTransferNo, rawData);
        }
        if (TRADE_FAILURE.equals(tradeStatus) || DISHONOUR_SUCCESS.equals(tradeStatus)) {
            return PayTransferRespDTO.closedOf(tradeStatus, errorMsg, outTransferNo, rawData);
        }
        return PayTransferRespDTO.processingOf(channelTransferNo, outTransferNo, rawData);
    }

    private JsonNode parseSignedBusinessResponse(String responseText, String businessKey) {
        JsonNode response = JsonUtils.parseTree(responseText);
        verifyResponseSign(response);
        JsonNode business = response.path(businessKey);
        if (business.isTextual() && JsonUtils.isJsonObject(business.asText())) {
            return JsonUtils.parseTree(business.asText());
        }
        if (!business.isMissingNode() && !business.isNull()) {
            return business;
        }
        return response;
    }

    private String postYsepayForm(String gatewayUrl, String method, String notifyUrl, Map<String, Object> bizContent)
            throws Throwable {
        ensureOfficialGatewayConfig();
        Charset charset = resolveCharset();
        Map<String, String> request = new TreeMap<>();
        request.put("partner_id", config.getResolvedPartnerId());
        request.put("method", method);
        request.put("timestamp", DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        request.put("sign_type", config.getSignType());
        request.put("charset", charset.name().toLowerCase(Locale.ROOT));
        request.put("version", config.getVersion());
        if (StrUtil.isNotBlank(notifyUrl)) {
            request.put("notify_url", notifyUrl);
        }
        request.put("biz_content", JsonUtils.toJsonString(bizContent));
        request.put("sign", sign(request, charset));
        return HttpUtils.post(gatewayUrl,
                MapUtil.of("Content-Type", "application/x-www-form-urlencoded; charset=" + charset.name()),
                buildFormBody(request, charset));
    }

    private void ensureOfficialGatewayConfig() {
        if (StrUtil.isBlank(config.getPrivateKeyFilePath())
                || StrUtil.isBlank(config.getPrivateKeyPassword())
                || StrUtil.isBlank(config.getYsepayPublicKeyFilePath())) {
            throw new IllegalStateException("聚合支付银盛证书配置未补齐，请配置 privateKeyFilePath/privateKeyPassword/ysepayPublicKeyFilePath");
        }
    }

    private String sign(Map<String, String> params, Charset charset) throws Exception {
        Signature signature = Signature.getInstance(resolveSignatureAlgorithm());
        signature.initSign(loadPrivateKey());
        signature.update(buildSignContent(params).getBytes(charset));
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private void verifyResponseSign(JsonNode response) {
        if (response == null || response.isMissingNode() || response.isNull()) {
            return;
        }
        String sign = getText(response, "sign");
        if (StrUtil.isBlank(sign)) {
            return;
        }
        Map<String, String> signedFields = new TreeMap<>();
        response.fields().forEachRemaining(entry -> {
            if (!"sign".equals(entry.getKey()) && !entry.getValue().isNull()) {
                signedFields.put(entry.getKey(), entry.getValue().isTextual()
                        ? entry.getValue().asText() : JsonUtils.toJsonString(entry.getValue()));
            }
        });
        verifySign(signedFields, sign, resolveCharset());
    }

    private void verifyNotifySign(Map<String, String> params) {
        String sign = params.get("sign");
        if (StrUtil.isBlank(sign)) {
            return;
        }
        Map<String, String> signedFields = new TreeMap<>(params);
        signedFields.remove("sign");
        verifySign(signedFields, sign, resolveCharset());
    }

    private void verifySign(Map<String, String> params, String sign, Charset charset) {
        try {
            Signature signature = Signature.getInstance(resolveSignatureAlgorithm());
            signature.initVerify(loadYsepayPublicCertificate());
            signature.update(buildSignContent(params).getBytes(charset));
            boolean success = signature.verify(Base64.getDecoder().decode(sign));
            if (!success) {
                throw new IllegalArgumentException("银盛回执验签失败");
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("银盛回执验签失败", ex);
        }
    }

    private PrivateKey loadPrivateKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (InputStream inputStream = new FileInputStream(config.getPrivateKeyFilePath())) {
            keyStore.load(inputStream, config.getPrivateKeyPassword().toCharArray());
        }
        String alias = keyStore.aliases().nextElement();
        return (PrivateKey) keyStore.getKey(alias, config.getPrivateKeyPassword().toCharArray());
    }

    private X509Certificate loadYsepayPublicCertificate() throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        try (InputStream inputStream = resolveCertificateInputStream(config.getYsepayPublicKeyFilePath())) {
            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            return (X509Certificate) certificate;
        }
    }

    private InputStream resolveCertificateInputStream(String filePathOrBase64) throws Exception {
        if (StrUtil.startWith(filePathOrBase64, "-----BEGIN")) {
            return new ByteArrayInputStream(filePathOrBase64.getBytes(StandardCharsets.UTF_8));
        }
        if (StrUtil.contains(filePathOrBase64, File.separator) || StrUtil.contains(filePathOrBase64, ":/")) {
            return new FileInputStream(filePathOrBase64);
        }
        return new ByteArrayInputStream(Base64.getDecoder().decode(filePathOrBase64));
    }

    private String buildSignContent(Map<String, String> params) {
        List<String> parts = new ArrayList<>();
        params.entrySet().stream()
                .filter(entry -> StrUtil.isNotBlank(entry.getValue()))
                .forEach(entry -> parts.add(entry.getKey() + "=" + entry.getValue()));
        return String.join("&", parts);
    }

    private String buildFormBody(Map<String, String> params, Charset charset) {
        List<String> parts = new ArrayList<>();
        params.forEach((key, value) -> {
            if (value != null) {
                parts.add(urlEncode(key, charset) + "=" + urlEncode(value, charset));
            }
        });
        return String.join("&", parts);
    }

    private String urlEncode(String value, Charset charset) {
        return URLEncoder.encode(value, charset).replace("+", "%20");
    }

    private Map<String, String> mergeNotifyParams(Map<String, String> params, String body) {
        Map<String, String> result = new HashMap<>();
        if (params != null) {
            result.putAll(params);
        }
        if (StrUtil.isNotBlank(body)) {
            if (JsonUtils.isJsonObject(body)) {
                JsonNode jsonNode = JsonUtils.parseTree(body);
                jsonNode.fields().forEachRemaining(entry -> result.putIfAbsent(entry.getKey(),
                        entry.getValue().isTextual() ? entry.getValue().asText() : JsonUtils.toJsonString(entry.getValue())));
            } else {
                String[] pairs = body.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2) {
                        result.putIfAbsent(kv[0], kv[1]);
                    }
                }
            }
        }
        return result;
    }

    private Charset resolveCharset() {
        if (StrUtil.isBlank(config.getCharset())) {
            return StandardCharsets.UTF_8;
        }
        return Charset.forName(config.getCharset());
    }

    private String resolveSignatureAlgorithm() {
        return SIGN_TYPE_SM.equalsIgnoreCase(config.getSignType()) ? "SM3withSM2" : "SHA1withRSA";
    }

    private String resolveShopDate(String outTradeNo) {
        if (StrUtil.isNotBlank(outTradeNo) && outTradeNo.length() >= 8) {
            String prefix = outTradeNo.substring(0, 8);
            if (prefix.chars().allMatch(Character::isDigit)) {
                return prefix;
            }
        }
        return DATE_FORMATTER.format(LocalDate.now());
    }

    private String resolveTransferExtra(PayTransferUnifiedReqDTO reqDTO, String key, String defaultValue) {
        if (reqDTO.getChannelExtras() == null) {
            return defaultValue;
        }
        return StrUtil.blankToDefault(reqDTO.getChannelExtras().get(key), defaultValue);
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

    private boolean isYsepayUnknownState(String code) {
        return StrUtil.equalsAnyIgnoreCase(code, "ACQ.SYSTEM_ERROR", "ACQ.QUERY_NO_RECORD", "ACQ.BUSINESS_TIMEOUT_ERROR");
    }

    private String getText(JsonNode node, String fieldName) {
        return JsonUtils.getText(node, fieldName);
    }

    private LocalDateTime parseDateTime(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        try {
            if (text.length() == 19) {
                return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
            }
            if (text.length() == 10 && text.contains("-")) {
                return LocalDate.parse(text, DATE_ONLY_FORMATTER).atStartOfDay();
            }
            if (text.length() == 8 && text.chars().allMatch(Character::isDigit)) {
                return LocalDate.parse(text, DATE_FORMATTER).atStartOfDay();
            }
        } catch (DateTimeParseException ignored) {
        }
        return null;
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
