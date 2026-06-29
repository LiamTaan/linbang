package cn.iocoder.yudao.module.pay.service.notify;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;
import cn.iocoder.yudao.module.pay.dal.dataobject.notify.PayNotifyTaskDO;
import cn.iocoder.yudao.module.pay.enums.notify.PayNotifyTypeEnum;
import cn.iocoder.yudao.module.pay.service.wallet.PayWalletRechargeService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Component
public class PayWalletRechargeNotifyInternalHandler implements PayNotifyInternalHandler {

    @Resource
    private PayWalletRechargeService payWalletRechargeService;

    @Override
    public boolean supports(PayNotifyTaskDO task) {
        return matchesOrder(task) || matchesRefund(task);
    }

    @Override
    public CommonResult<?> handle(PayNotifyTaskDO task, Object request) {
        if (matchesOrder(task)) {
            PayOrderNotifyReqDTO dto = (PayOrderNotifyReqDTO) request;
            payWalletRechargeService.updateWalletRechargerPaid(Long.valueOf(dto.getMerchantOrderId()), dto.getPayOrderId());
            return success(true);
        }
        if (matchesRefund(task)) {
            PayRefundNotifyReqDTO dto = (PayRefundNotifyReqDTO) request;
            payWalletRechargeService.updateWalletRechargeRefunded(
                    Long.valueOf(dto.getMerchantOrderId()), dto.getMerchantRefundId(), dto.getPayRefundId());
            return success(true);
        }
        return null;
    }

    private boolean matchesOrder(PayNotifyTaskDO task) {
        return Objects.equals(task.getType(), PayNotifyTypeEnum.ORDER.getType())
                && PayNotifyRouteHelper.matches(task.getNotifyUrl(), "/pay/wallet-recharge/update-paid");
    }

    private boolean matchesRefund(PayNotifyTaskDO task) {
        return Objects.equals(task.getType(), PayNotifyTypeEnum.REFUND.getType())
                && PayNotifyRouteHelper.matches(task.getNotifyUrl(), "/pay/wallet-recharge/update-refunded");
    }

}
