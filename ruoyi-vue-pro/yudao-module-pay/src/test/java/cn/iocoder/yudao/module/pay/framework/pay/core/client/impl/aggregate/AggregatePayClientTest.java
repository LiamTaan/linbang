package cn.iocoder.yudao.module.pay.framework.pay.core.client.impl.aggregate;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.pay.enums.refund.PayRefundStatusEnum;
import cn.iocoder.yudao.module.pay.enums.transfer.PayTransferStatusEnum;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.refund.PayRefundRespDTO;
import cn.iocoder.yudao.module.pay.framework.pay.core.client.dto.transfer.PayTransferRespDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AggregatePayClientTest extends BaseMockitoUnitTest {

    private AggregatePayClient client;

    @BeforeEach
    void setUp() {
        AggregatePayClientConfig config = new AggregatePayClientConfig();
        config.setMerchantNo("826584873720104");
        client = new AggregatePayClient(1L, config);
    }

    @Test
    void parseRefundQueryResponse_success() {
        PayRefundRespDTO resp = ReflectionTestUtils.invokeMethod(client, "parseRefundQueryResponse",
                "RF202607010001",
                "{\"ysepay_online_trade_refund_query_response\":{\"code\":\"10000\",\"msg\":\"SUCCESS\",\"refund_state\":\"success\",\"refundsn\":\"YSR123\",\"notify_time\":\"2026-07-01 10:11:12\"}}");

        assertThat(resp.getStatus()).isEqualTo(PayRefundStatusEnum.SUCCESS.getStatus());
        assertThat(resp.getOutRefundNo()).isEqualTo("RF202607010001");
        assertThat(resp.getChannelRefundNo()).isEqualTo("YSR123");
        assertThat(resp.getSuccessTime()).isNotNull();
    }

    @Test
    void parseRefundQueryResponse_processing() {
        PayRefundRespDTO resp = ReflectionTestUtils.invokeMethod(client, "parseRefundQueryResponse",
                "RF202607010002",
                "{\"ysepay_online_trade_refund_query_response\":{\"code\":\"10000\",\"msg\":\"PROCESSING\",\"refund_state\":\"in_process\",\"refundsn\":\"YSR124\"}}");

        assertThat(resp.getStatus()).isEqualTo(PayRefundStatusEnum.WAITING.getStatus());
        assertThat(resp.getOutRefundNo()).isEqualTo("RF202607010002");
        assertThat(resp.getChannelRefundNo()).isEqualTo("YSR124");
    }

    @Test
    void parseRefundQueryResponse_failure() {
        PayRefundRespDTO resp = ReflectionTestUtils.invokeMethod(client, "parseRefundQueryResponse",
                "RF202607010003",
                "{\"ysepay_online_trade_refund_query_response\":{\"code\":\"10000\",\"msg\":\"FAILURE\",\"refund_state\":\"fail_to_manual_deal\",\"refundsn\":\"YSR125\"}}");

        assertThat(resp.getStatus()).isEqualTo(PayRefundStatusEnum.FAILURE.getStatus());
        assertThat(resp.getOutRefundNo()).isEqualTo("RF202607010003");
        assertThat(resp.getChannelErrorCode()).isEqualTo("fail_to_manual_deal");
        assertThat(resp.getChannelErrorMsg()).isEqualTo("FAILURE");
    }

    @Test
    void parseTransferQueryResponse_processing() {
        PayTransferRespDTO resp = ReflectionTestUtils.invokeMethod(client, "parseTransferQueryResponse",
                "TR202607010001",
                "{\"ysepay_df_single_query_respose\":{\"code\":\"10000\",\"msg\":\"ACCEPTED\",\"trade_no\":\"YST1001\",\"trade_status\":\"TRADE_ACCEPT_SUCCESS\"}}");

        assertThat(resp.getStatus()).isEqualTo(PayTransferStatusEnum.PROCESSING.getStatus());
        assertThat(resp.getOutTransferNo()).isEqualTo("TR202607010001");
        assertThat(resp.getChannelTransferNo()).isEqualTo("YST1001");
    }

    @Test
    void parseTransferQueryResponse_success() {
        PayTransferRespDTO resp = ReflectionTestUtils.invokeMethod(client, "parseTransferQueryResponse",
                "TR202607010002",
                "{\"ysepay_df_single_query_respose\":{\"code\":\"10000\",\"msg\":\"SUCCESS\",\"trade_no\":\"YST1002\",\"trade_status\":\"TRADE_SUCCESS\",\"notify_time\":\"2026-07-01 12:13:14\"}}");

        assertThat(resp.getStatus()).isEqualTo(PayTransferStatusEnum.SUCCESS.getStatus());
        assertThat(resp.getOutTransferNo()).isEqualTo("TR202607010002");
        assertThat(resp.getChannelTransferNo()).isEqualTo("YST1002");
        assertThat(resp.getSuccessTime()).isNotNull();
    }

    @Test
    void parseTransferQueryResponse_closed() {
        PayTransferRespDTO resp = ReflectionTestUtils.invokeMethod(client, "parseTransferQueryResponse",
                "TR202607010003",
                "{\"ysepay_df_single_query_respose\":{\"code\":\"10000\",\"msg\":\"FAILURE\",\"trade_no\":\"YST1003\",\"trade_status\":\"DISHONOUR_SUCCESS\",\"trade_status_description\":\"退票成功\"}}");

        assertThat(resp.getStatus()).isEqualTo(PayTransferStatusEnum.CLOSED.getStatus());
        assertThat(resp.getOutTransferNo()).isEqualTo("TR202607010003");
        assertThat(resp.getChannelErrorCode()).isEqualTo("DISHONOUR_SUCCESS");
        assertThat(resp.getChannelErrorMsg()).isEqualTo("退票成功");
    }

    @Test
    void parseRefundNotify_prefersOutRequestNo() throws Throwable {
        PayRefundRespDTO resp = ReflectionTestUtils.invokeMethod(client, "doParseRefundNotify",
                null,
                "notify_type=refund.status.sync&out_trade_no=ORDER202607010001&out_request_no=RF202607010004&refund_state=success&refundsn=YSR126",
                null);

        assertThat(resp.getStatus()).isEqualTo(PayRefundStatusEnum.SUCCESS.getStatus());
        assertThat(resp.getOutRefundNo()).isEqualTo("RF202607010004");
        assertThat(resp.getChannelRefundNo()).isEqualTo("YSR126");
    }

    @Test
    void parseDateTime_blank_returnsNull() {
        LocalDateTime parsed = ReflectionTestUtils.invokeMethod(client, "parseDateTime", "");
        assertThat(parsed).isNull();
    }
}
