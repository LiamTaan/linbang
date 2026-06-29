package cn.iocoder.yudao.module.linbang.service.app.pay;

import java.time.LocalDateTime;

public interface AutoFlowRefundService {

    Long createAutoRefund(Long orderId, Long unitId, LocalDateTime flowTime);

    void handleRefundCallback(Long payRefundId, String merchantRefundId, Integer status, String channelErrorMsg);
}
