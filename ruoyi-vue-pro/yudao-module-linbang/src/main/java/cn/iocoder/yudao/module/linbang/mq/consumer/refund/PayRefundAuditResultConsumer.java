package cn.iocoder.yudao.module.linbang.mq.consumer.refund;

import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.pay.mq.message.refund.PayRefundAuditResultMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 退款审核结果消费者
 */
@Component
@Slf4j
public class PayRefundAuditResultConsumer {

    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @EventListener
    @Async
    public void onMessage(PayRefundAuditResultMessage message) {
        log.info("[onMessage][退款审核结果消息({})]", message);
        messagePushDispatchService.dispatchSingle("lb_refund_audited", "退款审核结果通知", "REFUND",
                message.getRefundId(), message.getUserId(), "管理员审核退款后自动通知申请人");
    }
}
