package cn.iocoder.yudao.module.linbang.service.promoter;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterrelation.PromoterRelationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.commissionorder.CommissionOrderMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.dividerule.DivideRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterrelation.PromoterRelationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoteroperationlog.PromoterOperationLogMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PromoterServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private PromoterServiceImpl promoterService;
    @Mock private PromoterMapper promoterMapper;
    @Mock private MemberUserMapper memberUserMapper;
    @Mock private CommissionOrderMapper commissionOrderMapper;
    @Mock private PromoterRelationMapper promoterRelationMapper;
    @Mock private OrderInfoMapper orderInfoMapper;
    @Mock private OrderUnitMapper orderUnitMapper;
    @Mock private DivideRuleMapper divideRuleMapper;
    @Mock private PromoterOperationLogMapper promoterOperationLogMapper;

    @Test
    void getOrCreatePromoter_returnsConcurrentInsertAfterDuplicateKey() {
        PromoterDO concurrent = PromoterDO.builder().id(6L).userId(40L).inviteCode("LBEXIST1").build();
        when(promoterMapper.selectByUserId(40L)).thenReturn(null);
        when(memberUserMapper.selectById(40L)).thenReturn(MemberUserDO.builder().id(40L).build());
        when(promoterMapper.insert(any(PromoterDO.class)))
                .thenThrow(new DuplicateKeyException("duplicate promoter"));
        when(promoterMapper.selectByUserIdForUpdate(40L)).thenReturn(concurrent);

        assertSame(concurrent, promoterService.getOrCreatePromoter(40L));
    }

    @Test
    void bindInviteCode_rejectsSelfBinding() {
        AppPromoteInviteCodeBindReqVO reqVO = request("LBABC123");
        when(memberUserMapper.selectById(10L)).thenReturn(MemberUserDO.builder().id(10L).build());
        when(promoterMapper.selectByInviteCode("LBABC123"))
                .thenReturn(PromoterDO.builder().id(1L).userId(10L).status("ENABLE").build());

        assertThrows(ServiceException.class, () -> promoterService.bindInviteCode(10L, reqVO));
        verify(promoterRelationMapper, never()).insert(any(PromoterRelationDO.class));
    }

    @Test
    void bindInviteCode_rejectsChangingPromoter() {
        AppPromoteInviteCodeBindReqVO reqVO = request("LBABC123");
        when(memberUserMapper.selectById(10L)).thenReturn(MemberUserDO.builder().id(10L).build());
        when(promoterMapper.selectByInviteCode("LBABC123"))
                .thenReturn(PromoterDO.builder().id(1L).userId(20L).status("ENABLE").build());
        when(promoterRelationMapper.selectByUserId(10L))
                .thenReturn(PromoterRelationDO.builder().id(9L).promoterId(2L).userId(10L).build());

        assertThrows(ServiceException.class, () -> promoterService.bindInviteCode(10L, reqVO));
        verify(promoterRelationMapper, never()).insert(any(PromoterRelationDO.class));
    }

    @Test
    void bindInviteCode_persistsAuditAndRecalculatesLevel() {
        AppPromoteInviteCodeBindReqVO reqVO = request("lbabc123");
        reqVO.setSourceChannel("QRCODE");
        reqVO.setSourcePage("pages/index/index");
        PromoterDO promoter = PromoterDO.builder().id(1L).userId(20L).status("ENABLE")
                .bindUserCount(9).convertCount(0).levelCode("L1").build();
        when(memberUserMapper.selectById(10L)).thenReturn(MemberUserDO.builder().id(10L).build());
        when(promoterMapper.selectByInviteCode("LBABC123")).thenReturn(promoter);
        when(promoterMapper.selectById(1L)).thenReturn(promoter);
        when(promoterRelationMapper.selectCount(any())).thenReturn(10L, 0L);

        promoterService.bindInviteCode(10L, reqVO);

        ArgumentCaptor<PromoterRelationDO> captor = ArgumentCaptor.forClass(PromoterRelationDO.class);
        verify(promoterRelationMapper).insert(captor.capture());
        assertEquals("LBABC123", captor.getValue().getInviteCode());
        assertEquals("QRCODE", captor.getValue().getSourceChannel());
        assertEquals("L2", promoter.getLevelCode());
    }

    @Test
    void handleOrderFinished_convertsWithoutCommissionRule() {
        OrderInfoDO order = OrderInfoDO.builder().id(100L).userId(10L).categoryId(3L)
                .orderAmount(new BigDecimal("100.00")).build();
        OrderUnitDO unit = OrderUnitDO.builder().id(200L).orderId(100L).build();
        PromoterRelationDO relation = PromoterRelationDO.builder().id(5L).promoterId(1L).userId(10L)
                .convertStatus("BOUND").build();
        PromoterDO promoter = PromoterDO.builder().id(1L).bindUserCount(1).convertCount(0).levelCode("L1").build();
        when(promoterRelationMapper.selectByUserId(10L)).thenReturn(relation);
        when(promoterMapper.selectById(1L)).thenReturn(promoter);
        when(promoterRelationMapper.selectCount(any())).thenReturn(1L, 1L);

        promoterService.handleOrderFinished(order, unit);

        ArgumentCaptor<PromoterRelationDO> captor = ArgumentCaptor.forClass(PromoterRelationDO.class);
        verify(promoterRelationMapper).updateById(captor.capture());
        assertEquals("CONVERTED", captor.getValue().getConvertStatus());
        verify(commissionOrderMapper, never()).insert(any(cn.iocoder.yudao.module.linbang.dal.dataobject.commissionorder.CommissionOrderDO.class));
    }

    private AppPromoteInviteCodeBindReqVO request(String inviteCode) {
        AppPromoteInviteCodeBindReqVO reqVO = new AppPromoteInviteCodeBindReqVO();
        reqVO.setInviteCode(inviteCode);
        return reqVO;
    }
}
