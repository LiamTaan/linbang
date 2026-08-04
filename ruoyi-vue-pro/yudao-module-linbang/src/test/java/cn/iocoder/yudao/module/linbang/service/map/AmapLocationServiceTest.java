package cn.iocoder.yudao.module.linbang.service.map;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MAP_LOCATION_INVALID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AmapLocationServiceTest {

    private final AmapLocationService service = new AmapLocationService();

    @Test
    void resolveAddress_rejectsPartialOrOutOfRangeCoordinatesBeforeRemoteRequest() {
        ServiceException partial = assertThrows(ServiceException.class,
                () -> service.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                        .longitude(new BigDecimal("113.941513"))
                        .build()));
        ServiceException outOfRange = assertThrows(ServiceException.class,
                () -> service.resolveAddress(AmapLocationService.ResolveAddressRequest.builder()
                        .longitude(new BigDecimal("181"))
                        .latitude(new BigDecimal("22"))
                        .build()));

        assertEquals(MAP_LOCATION_INVALID.getCode(), partial.getCode());
        assertEquals(MAP_LOCATION_INVALID.getCode(), outOfRange.getCode());
    }

}
