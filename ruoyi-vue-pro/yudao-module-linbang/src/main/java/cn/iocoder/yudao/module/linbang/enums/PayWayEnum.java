package cn.iocoder.yudao.module.linbang.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * App 支付方式枚举。
 *
 * <p>业务侧统一使用该枚举选择聚合支付入口，不直接暴露银盛原始支付方式编码。</p>
 */
@Getter
@AllArgsConstructor
public enum PayWayEnum implements ArrayValuable<String> {

    WECHAT_H5("WECHAT_H5", "微信支付"),
    ALIPAY_H5("ALIPAY_H5", "支付宝支付"),
    UNIONPAY_WAP("UNIONPAY_WAP", "银行卡/云闪付");

    public static final String[] ARRAYS = Arrays.stream(values()).map(PayWayEnum::getCode).toArray(String[]::new);

    private final String code;
    private final String name;

    @Override
    public String[] array() {
        return ARRAYS;
    }

    public static PayWayEnum getByCode(String code) {
        return ArrayUtil.firstMatch(item -> item.getCode().equals(code), values());
    }
}
