package cn.iocoder.yudao.module.linbang.service.app.order;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.AppDeliveryProofDeleteReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.AppDeliveryProofUploadReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunitproof.OrderUnitProofDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunitproof.OrderUnitProofMapper;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantOperatorContext;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantOperatorContextService;
import cn.iocoder.yudao.module.linbang.service.memberqualification.MemberQualificationExpiryService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppOrderDeliveryProofConcurrencyTest extends BaseMockitoUnitTest {

    private AppOrderServiceImpl service;

    @Mock
    private MemberUserService memberUserService;
    @Mock
    private AppMerchantOperatorContextService merchantOperatorContextService;
    @Mock
    private MemberQualificationExpiryService memberQualificationExpiryService;
    @Mock
    private OrderUnitMapper orderUnitMapper;
    @Mock
    private OrderUnitProofMapper orderUnitProofMapper;

    @BeforeEach
    void setUp() {
        service = new AppOrderServiceImpl();
        ReflectionTestUtils.setField(service, "memberUserService", memberUserService);
        ReflectionTestUtils.setField(service, "merchantOperatorContextService", merchantOperatorContextService);
        ReflectionTestUtils.setField(service, "memberQualificationExpiryService", memberQualificationExpiryService);
        ReflectionTestUtils.setField(service, "orderUnitMapper", orderUnitMapper);
        ReflectionTestUtils.setField(service, "orderUnitProofMapper", orderUnitProofMapper);
        MemberUserDO user = MemberUserDO.builder().id(1L).build();
        MerchantInfoDO merchant = MerchantInfoDO.builder()
                .id(2L).userId(1L).status("ENABLE").build();
        AppMerchantOperatorContext context = AppMerchantOperatorContext.builder()
                .loginUser(user).merchant(merchant).mainAccount(Boolean.TRUE).build();
        when(memberUserService.getOrCreateMemberUser(100L)).thenReturn(user);
        when(merchantOperatorContextService.getRequiredOrderAcceptContext(100L)).thenReturn(context);
    }

    @Test
    void uploadDeliveryProof_locksUnitBeforeStatusValidation() {
        when(orderUnitMapper.selectByIdForUpdate(10L)).thenReturn(OrderUnitDO.builder()
                .id(10L).merchantId(2L).status("FINISHED").build());
        AppDeliveryProofUploadReqVO reqVO = new AppDeliveryProofUploadReqVO();
        reqVO.setUnitId(10L);
        reqVO.setFileIds(Collections.singletonList(1L));

        assertThrows(ServiceException.class, () -> service.uploadDeliveryProof(100L, reqVO));

        verify(orderUnitMapper).selectByIdForUpdate(10L);
    }

    @Test
    void deleteDeliveryProof_reloadsProofWithLockAfterUnitLock() {
        OrderUnitProofDO proof = OrderUnitProofDO.builder().id(20L).unitId(10L).merchantId(2L).build();
        when(orderUnitProofMapper.selectById(20L)).thenReturn(proof);
        when(orderUnitMapper.selectByIdForUpdate(10L)).thenReturn(OrderUnitDO.builder()
                .id(10L).merchantId(2L).status("FINISHED").build());
        when(orderUnitProofMapper.selectByIdForUpdate(20L)).thenReturn(proof);
        AppDeliveryProofDeleteReqVO reqVO = new AppDeliveryProofDeleteReqVO();
        reqVO.setProofId(20L);

        assertThrows(ServiceException.class, () -> service.deleteDeliveryProof(100L, reqVO));

        verify(orderUnitMapper).selectByIdForUpdate(10L);
        verify(orderUnitProofMapper).selectByIdForUpdate(20L);
    }
}
