package cn.iocoder.yudao.module.linbang.service.merchantpricereport;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantpricereport.vo.MerchantPriceReportAuditReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantpricereport.MerchantPriceReportDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantpricereport.MerchantPriceReportMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MerchantPriceReportServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private MerchantPriceReportServiceImpl service;

    @Mock
    private MerchantPriceReportMapper merchantPriceReportMapper;
    @Mock
    private MerchantInfoMapper merchantInfoMapper;
    @Mock
    private MessagePushDispatchService messagePushDispatchService;

    @Test
    void auditMerchantPriceReport_usesStableNotificationDedupeKey() {
        MerchantPriceReportAuditReqVO reqVO = new MerchantPriceReportAuditReqVO();
        reqVO.setId(1L);
        reqVO.setAuditStatus("APPROVED");
        when(merchantPriceReportMapper.selectByIdForUpdate(1L)).thenReturn(MerchantPriceReportDO.builder()
                .id(1L).merchantId(3L).auditStatus("PENDING").build());
        when(merchantInfoMapper.selectById(3L)).thenReturn(MerchantInfoDO.builder().id(3L).userId(2L).build());

        service.auditMerchantPriceReport(reqVO);

        verify(messagePushDispatchService).dispatchSingleIdempotent(eq("lb_price_report_audited"), any(),
                eq("PRICE_REPORT"), eq(1L), eq(2L), any(), eq("lb_price_report_audited:1"));
    }
}
