package cn.iocoder.yudao.module.linbang.service.certexemption;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.admin.certexemption.vo.CertExemptionAuditReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.certexemption.CertExemptionApplyDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.certexemption.CertExemptionApplyMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CertExemptionServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private CertExemptionServiceImpl service;

    @Mock
    private CertExemptionApplyMapper certExemptionApplyMapper;
    @Mock
    private MessagePushDispatchService messagePushDispatchService;

    @Test
    void audit_usesStableNotificationDedupeKey() {
        CertExemptionAuditReqVO reqVO = new CertExemptionAuditReqVO();
        reqVO.setId(1L);
        reqVO.setAuditStatus("APPROVED");
        when(certExemptionApplyMapper.selectByIdForUpdate(1L)).thenReturn(CertExemptionApplyDO.builder()
                .id(1L).userId(2L).auditStatus("PENDING").build());

        service.audit(reqVO);

        verify(messagePushDispatchService).dispatchSingleIdempotent(eq("lb_cert_exemption_audited"), any(),
                eq("CERT_EXEMPTION"), eq(1L), eq(2L), any(), eq("lb_cert_exemption_audited:1"));
    }
}
