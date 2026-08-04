package cn.iocoder.yudao.module.linbang.service.concurrency;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.AppealAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.ComplaintProcessReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo.OrderAbnormalFinalAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo.OrderUnitUnlockReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderoperatelog.OrderOperateLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderabnormal.OrderAbnormalMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderoperatelog.OrderOperateLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.service.appeal.AppealServiceImpl;
import cn.iocoder.yudao.module.linbang.service.complaint.ComplaintServiceImpl;
import cn.iocoder.yudao.module.linbang.service.orderabnormal.OrderAbnormalServiceImpl;
import cn.iocoder.yudao.module.linbang.service.orderunit.OrderUnitServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StateTransitionIdempotencyTest extends BaseMockitoUnitTest {

    @Mock
    private ComplaintMapper complaintMapper;
    @Mock
    private AppealMapper appealMapper;
    @Mock
    private OrderAbnormalMapper orderAbnormalMapper;
    @Mock
    private OrderUnitMapper orderUnitMapper;
    @Mock
    private OrderOperateLogMapper orderOperateLogMapper;

    @Test
    void processComplaint_rejectsTerminalComplaint() {
        ComplaintServiceImpl service = new ComplaintServiceImpl();
        ReflectionTestUtils.setField(service, "complaintMapper", complaintMapper);
        when(complaintMapper.selectByIdForUpdate(1L))
                .thenReturn(ComplaintDO.builder().id(1L).status("FINISHED").build());
        ComplaintProcessReqVO reqVO = new ComplaintProcessReqVO();
        reqVO.setId(1L);
        reqVO.setStatus("FINISHED");

        assertThrows(ServiceException.class, () -> service.processComplaint(reqVO));

        verify(complaintMapper, never()).updateById(any(ComplaintDO.class));
    }

    @Test
    void auditAppeal_rejectsPreviouslyAuditedAppeal() {
        AppealServiceImpl service = new AppealServiceImpl();
        ReflectionTestUtils.setField(service, "appealMapper", appealMapper);
        when(appealMapper.selectByIdForUpdate(1L)).thenReturn(AppealDO.builder()
                .id(1L).status("PROCESSING").auditStatus("APPROVED").build());
        AppealAuditReqVO reqVO = new AppealAuditReqVO();
        reqVO.setId(1L);
        reqVO.setAuditStatus("APPROVED");

        assertThrows(ServiceException.class, () -> service.auditAppeal(reqVO));

        verify(appealMapper, never()).updateById(any(AppealDO.class));
    }

    @Test
    void finalAuditOrderAbnormal_rejectsPreviouslyAuditedRecord() {
        OrderAbnormalServiceImpl service = new OrderAbnormalServiceImpl();
        ReflectionTestUtils.setField(service, "orderAbnormalMapper", orderAbnormalMapper);
        ReflectionTestUtils.setField(service, "orderOperateLogMapper", orderOperateLogMapper);
        when(orderAbnormalMapper.selectByIdForUpdate(1L)).thenReturn(OrderAbnormalDO.builder()
                .id(1L).finalAuditStatus("APPROVED").handleStatus("FINISHED").build());
        OrderAbnormalFinalAuditReqVO reqVO = new OrderAbnormalFinalAuditReqVO();
        reqVO.setId(1L);
        reqVO.setFinalAuditStatus("APPROVED");
        reqVO.setFinalAuditRemark("done");

        assertThrows(ServiceException.class, () -> service.finalAuditOrderAbnormal(reqVO));

        verify(orderAbnormalMapper, never()).updateById(any(OrderAbnormalDO.class));
        verify(orderOperateLogMapper, never()).insert(any(OrderOperateLogDO.class));
    }

    @Test
    void unlockOrderUnit_returnsWithoutDuplicateLogWhenAlreadyUnlocked() {
        OrderUnitServiceImpl service = new OrderUnitServiceImpl();
        ReflectionTestUtils.setField(service, "orderUnitMapper", orderUnitMapper);
        ReflectionTestUtils.setField(service, "orderOperateLogMapper", orderOperateLogMapper);
        when(orderUnitMapper.selectByIdForUpdate(1L)).thenReturn(OrderUnitDO.builder()
                .id(1L).isLocked(Boolean.FALSE).build());
        OrderUnitUnlockReqVO reqVO = new OrderUnitUnlockReqVO();
        reqVO.setUnitId(1L);

        service.unlockOrderUnit(reqVO);

        verify(orderUnitMapper, never()).updateById(any(OrderUnitDO.class));
        verify(orderOperateLogMapper, never()).insert(any(OrderOperateLogDO.class));
    }
}
