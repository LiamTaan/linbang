package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.service.walletwithdraw.WalletWithdrawService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayTransferNotifyReqDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.notify.PayNotifyTaskDO;
import cn.iocoder.yudao.module.pay.enums.notify.PayNotifyTypeEnum;
import cn.iocoder.yudao.module.pay.service.notify.PayNotifyInternalHandler;
import cn.iocoder.yudao.module.pay.service.notify.PayNotifyRouteHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Component
public class LinbangPayNotifyInternalHandler implements PayNotifyInternalHandler {

    @Resource
    private AppLinbangPayOrderService appLinbangPayOrderService;
    @Resource
    private AppPayRefundService appPayRefundService;
    @Resource
    private WalletWithdrawService walletWithdrawService;

    @Override
    public boolean supports(PayNotifyTaskDO task) {
        return matchesOrder(task) || matchesRefund(task) || matchesTransfer(task);
    }

    @Override
    public CommonResult<?> handle(PayNotifyTaskDO task, Object request) {
        if (matchesOrder(task)) {
            appLinbangPayOrderService.updatePaid((PayOrderNotifyReqDTO) request);
            return success(true);
        }
        if (matchesRefund(task)) {
            appPayRefundService.updateRefunded((PayRefundNotifyReqDTO) request);
            return success(true);
        }
        if (matchesTransfer(task)) {
            walletWithdrawService.updateWalletWithdrawTransferred((PayTransferNotifyReqDTO) request);
            return success(true);
        }
        return null;
    }

    private boolean matchesOrder(PayNotifyTaskDO task) {
        return Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())
                && matchesAny(task.getNotifyUrl(),
                "/app-api/linbang/pay/order/update-paid",
                "/admin-api/pay/notify/order");
    }

    private boolean matchesRefund(PayNotifyTaskDO task) {
        return Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())
                && matchesAny(task.getNotifyUrl(),
                "/app-api/pay/refund/update-refunded",
                "/admin-api/pay/notify/refund");
    }

    private boolean matchesTransfer(PayNotifyTaskDO task) {
        return Objects.equals(task.getType(), PayNotifyTypeEnum.TRANSFER.getType())
                && matchesAny(task.getNotifyUrl(),
                "/admin-api/wallet/withdraw/update-transferred",
                "/admin-api/pay/notify/transfer");
    }

    private boolean matchesAny(String notifyUrl, String... suffixes) {
        for (String suffix : suffixes) {
            if (PayNotifyRouteHelper.matches(notifyUrl, suffix)) {
                return true;
            }
        }
        return false;
    }

}
