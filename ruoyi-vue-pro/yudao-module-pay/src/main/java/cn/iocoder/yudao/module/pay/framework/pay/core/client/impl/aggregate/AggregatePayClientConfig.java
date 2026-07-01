package cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.aggregate;

import cn.iocoder.yudao.framework.common.util.validation.ValidationUtils;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.PayClientConfig;
import lombok.Data;

import javax.validation.Validator;
import javax.validation.constraints.NotBlank;

/**
 * 聚合支付渠道配置。
 *
 * <p>邻里互助业务只暴露一个聚合支付通道，不再按微信、支付宝分别配置商户号。</p>
 */
@Data
public class AggregatePayClientConfig implements PayClientConfig {

    /**
     * 聚合支付接口基础地址，例如：https://pay.example.com
     */
    @NotBlank(message = "聚合支付接口基础地址不能为空")
    private String baseUrl;

    /**
     * 第三方文档提供的正式收款商户号。
     */
    @NotBlank(message = "收款商户号不能为空")
    private String merchantNo = "826584873720104";

    /**
     * 第三方文档提供的正式商户名称。
     */
    @NotBlank(message = "商户名称不能为空")
    private String merchantName = "深圳市旺佳盈科技有限公司";

    /**
     * 银盛开放平台服务商商户号。
     *
     * <p>若为空，则默认复用 {@link #merchantNo}。</p>
     */
    private String partnerId;

    /**
     * 银盛线上交易网关地址，退款与退款查询使用。
     */
    @NotBlank(message = "退款网关地址不能为空")
    private String openApiGatewayUrl = "https://openapi.ysepay.com/gateway.do";

    /**
     * 银盛代付网关地址，提现打款与查询使用。
     */
    @NotBlank(message = "提现网关地址不能为空")
    private String transferGatewayUrl = "https://df.ysepay.com/gateway.do";

    /**
     * 商户私钥证书 pfx 文件路径。
     */
    @NotBlank(message = "商户私钥证书路径不能为空")
    private String privateKeyFilePath;

    /**
     * 商户私钥证书密码。
     */
    @NotBlank(message = "商户私钥证书密码不能为空")
    private String privateKeyPassword;

    /**
     * 银盛公钥证书 cer 文件路径。
     */
    @NotBlank(message = "银盛公钥证书路径不能为空")
    private String ysepayPublicKeyFilePath;

    /**
     * 签名算法，当前支持 RSA / SM。
     */
    @NotBlank(message = "签名算法不能为空")
    private String signType = "RSA";

    /**
     * 请求字符集。
     */
    @NotBlank(message = "字符集不能为空")
    private String charset = "utf-8";

    /**
     * 银盛接口版本。
     */
    @NotBlank(message = "接口版本不能为空")
    private String version = "3.0";

    public String getResolvedPartnerId() {
        return partnerId != null && !partnerId.isBlank() ? partnerId : merchantNo;
    }

    @Override
    public void validate(Validator validator) {
        ValidationUtils.validate(validator, this);
    }

}
