package cn.iocoder.yudao.module.linbang.service.payrefund;

import cn.iocoder.yudao.module.linbang.controller.admin.payrefund.vo.PayRefundBizContextRespVO;

public interface PayRefundBizContextService {

    /**
     * 获得退款业务上下文
     *
     * @param payRefundId 支付退款单 ID
     * @return 退款业务上下文
     */
    PayRefundBizContextRespVO getPayRefundBizContext(Long payRefundId);

}
