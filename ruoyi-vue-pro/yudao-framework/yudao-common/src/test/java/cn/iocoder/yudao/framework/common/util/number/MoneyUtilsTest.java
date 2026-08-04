package cn.iocoder.yudao.framework.common.util.number;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyUtilsTest {

    @Test
    void yuanToFen_roundsHalfUpAndAcceptsMaximum() {
        assertEquals(1235, MoneyUtils.yuanToFen(new BigDecimal("12.345")));
        assertEquals(Integer.MAX_VALUE, MoneyUtils.yuanToFen(MoneyUtils.MAX_YUAN_AMOUNT));
    }

    @Test
    void yuanToFen_rejectsOverflow() {
        assertThrows(ArithmeticException.class,
                () -> MoneyUtils.yuanToFen(new BigDecimal("21474836.48")));
    }

    @Test
    void calculator_rejectsOverflow() {
        assertThrows(ArithmeticException.class,
                () -> MoneyUtils.calculator(Integer.MAX_VALUE, 2, null));
    }

}
