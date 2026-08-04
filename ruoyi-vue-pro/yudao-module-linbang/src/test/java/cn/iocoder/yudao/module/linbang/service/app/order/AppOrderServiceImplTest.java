package cn.iocoder.yudao.module.linbang.service.app.order;

import cn.iocoder.yudao.module.linbang.controller.app.order.vo.AppOrderAcceptPageItemRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.AppOrderAcceptPageReqVO;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AppOrderServiceImplTest {

    private final AppOrderServiceImpl service = new AppOrderServiceImpl();

    @Test
    void buildAcceptOrderComparator_usesUnitIdAsStableTieBreaker() {
        AppOrderAcceptPageReqVO reqVO = new AppOrderAcceptPageReqVO();
        Comparator<AppOrderAcceptPageItemRespVO> comparator = ReflectionTestUtils.invokeMethod(
                service, "buildAcceptOrderComparator", reqVO);
        LocalDateTime createTime = LocalDateTime.of(2026, 8, 3, 10, 0);
        AppOrderAcceptPageItemRespVO lowerUnit = buildItem(100L, 1L, createTime);
        AppOrderAcceptPageItemRespVO higherUnit = buildItem(100L, 2L, createTime);

        assertTrue(comparator.compare(higherUnit, lowerUnit) < 0);

        reqVO.setPublishTimeSort("OLDEST");
        comparator = ReflectionTestUtils.invokeMethod(service, "buildAcceptOrderComparator", reqVO);
        assertTrue(comparator.compare(lowerUnit, higherUnit) < 0);
    }

    private AppOrderAcceptPageItemRespVO buildItem(Long orderId, Long unitId, LocalDateTime createTime) {
        AppOrderAcceptPageItemRespVO item = new AppOrderAcceptPageItemRespVO();
        item.setOrderId(orderId);
        item.setUnitId(unitId);
        item.setCreateTime(createTime);
        return item;
    }
}
