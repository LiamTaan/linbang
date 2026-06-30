package cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.aggregate;

import cn.iocoder.yudao.framework.common.util.validation.ValidationUtils;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.PayClientConfig;
import lombok.Data;

import javax.validation.Validator;

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
    private String baseUrl;

    /**
     * 第三方文档提供的正式收款商户号。
     */
    private String merchantNo = "826584873720104";

    /**
     * 第三方文档提供的正式商户名称。
     */
    private String merchantName = "深圳市旺佳盈科技有限公司";

    @Override
    public void validate(Validator validator) {
        ValidationUtils.validate(validator, this);
    }

}
