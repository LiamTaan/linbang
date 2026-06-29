package cn.iocoder.yudao.module.linbang.service.finance;

import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletaccount.WalletAccountDO;
import cn.iocoder.yudao.module.pay.api.transfer.dto.PayTransferRespDTO;

import java.math.BigDecimal;

public interface LinbangFinanceService {

    void handleOrderPaid(OrderInfoDO order, Long payOrderId);

    void handleUnitFinished(OrderInfoDO order, OrderUnitDO unit);

    void handleRefundSuccess(OrderInfoDO order, OrderUnitDO unit, BigDecimal refundAmount);

    void handleWithdrawTransferSuccess(WalletAccountDO walletAccount, Long withdrawId, PayTransferRespDTO transfer);

    void handleWithdrawTransferFailed(WalletAccountDO walletAccount, Long withdrawId, PayTransferRespDTO transfer);
}
