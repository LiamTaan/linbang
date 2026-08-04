package cn.iocoder.yudao.module.linbang.service.ordersplitrule;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_SPLIT_PLAN_GENERATE_FAILED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderSplitRuleServiceImplTest {

    private final OrderSplitRuleServiceImpl service = new OrderSplitRuleServiceImpl();

    @Test
    void matchRule_rejectsExcessiveGeneratedUnitCountBeforeAllocatingMemory() {
        OrderSplitPreviewContext context = OrderSplitPreviewContext.builder()
                .orderAmount(new BigDecimal("20000.01"))
                .quantity(BigDecimal.ONE)
                .workerCount(1)
                .autoSplitEnabled(Boolean.TRUE)
                .build();

        ServiceException exception = assertThrows(ServiceException.class, () -> service.matchRule(context));

        assertEquals(ORDER_SPLIT_PLAN_GENERATE_FAILED.getCode(), exception.getCode());
    }

}
