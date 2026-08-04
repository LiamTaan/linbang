package cn.iocoder.yudao.framework.common.util.number;

import cn.hutool.core.math.Money;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 金额工具类
 *
 * @author 芋道源码
 */
public class MoneyUtils {

    /**
     * 金额的小数位数
     */
    private static final int PRICE_SCALE = 2;

    /**
     * Integer 分金额能够表示的最大元金额
     */
    public static final String MAX_YUAN_AMOUNT_STR = "21474836.47";

    public static final BigDecimal MAX_YUAN_AMOUNT = new BigDecimal(MAX_YUAN_AMOUNT_STR);

    /**
     * 百分比对应的 BigDecimal 对象
     */
    public static final BigDecimal PERCENT_100 = BigDecimal.valueOf(100);

    /**
     * 计算百分比金额，四舍五入
     *
     * @param price 金额
     * @param rate  百分比，例如说 56.77% 则传入 56.77
     * @return 百分比金额
     */
    public static Integer calculateRatePrice(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.HALF_UP).intValueExact();
    }

    /**
     * 计算百分比金额，向下传入
     *
     * @param price 金额
     * @param rate  百分比，例如说 56.77% 则传入 56.77
     * @return 百分比金额
     */
    public static Integer calculateRatePriceFloor(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.FLOOR).intValueExact();
    }

    /**
     * 计算百分比金额
     *
     * @param price   金额（单位分）
     * @param count   数量
     * @param percent 折扣（单位分），列如 60.2%，则传入 6020
     * @return 商品总价
     */
    public static Integer calculator(Integer price, Integer count, Integer percent) {
        price = Math.multiplyExact(price, count);
        if (percent == null) {
            return price;
        }
        return MoneyUtils.calculateRatePriceFloor(price, (double) (percent / 100));
    }

    /**
     * 计算百分比金额
     *
     * @param price        金额
     * @param rate         百分比，例如说 56.77% 则传入 56.77
     * @param scale        保留小数位数
     * @param roundingMode 舍入模式
     */
    public static BigDecimal calculateRatePrice(Number price, Number rate, int scale, RoundingMode roundingMode) {
        return NumberUtil.toBigDecimal(price).multiply(NumberUtil.toBigDecimal(rate)) // 乘以
                .divide(BigDecimal.valueOf(100), scale, roundingMode); // 除以 100
    }

    /**
     * 分转元
     *
     * @param fen 分
     * @return 元
     */
    public static BigDecimal fenToYuan(int fen) {
        return new Money(0, fen).getAmount();
    }

    /**
     * 元转分，按人民币最小单位四舍五入，并在超出 Integer 分金额范围时失败。
     *
     * @param yuan 元
     * @return 分
     * @throws ArithmeticException 金额超出 Integer 分金额范围
     */
    public static Integer yuanToFen(BigDecimal yuan) {
        Objects.requireNonNull(yuan, "yuan must not be null");
        return yuan.multiply(PERCENT_100).setScale(0, RoundingMode.HALF_UP).intValueExact();
    }

    /**
     * 分转元（字符串）
     *
     * 例如说 fen 为 1 时，则结果为 0.01
     *
     * @param fen 分
     * @return 元
     */
    public static String fenToYuanStr(int fen) {
        return new Money(0, fen).toString();
    }

    /**
     * 金额相乘，默认进行四舍五入
     *
     * 位数：{@link #PRICE_SCALE}
     *
     * @param price 金额
     * @param count 数量
     * @return 金额相乘结果
     */
    public static BigDecimal priceMultiply(BigDecimal price, BigDecimal count) {
        if (price == null || count == null) {
            return null;
        }
        return price.multiply(count).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 金额相乘（百分比），默认进行四舍五入
     *
     * 位数：{@link #PRICE_SCALE}
     *
     * @param price  金额
     * @param percent 百分比
     * @return 金额相乘结果
     */
    public static BigDecimal priceMultiplyPercent(BigDecimal price, BigDecimal percent) {
        if (price == null || percent == null) {
            return null;
        }
        return price.multiply(percent).divide(PERCENT_100, PRICE_SCALE, RoundingMode.HALF_UP);
    }

}
