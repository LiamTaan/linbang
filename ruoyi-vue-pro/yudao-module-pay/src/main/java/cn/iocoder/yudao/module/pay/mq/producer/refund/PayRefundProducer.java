package cn.iocoder.yudao.module.pay.mq.producer.refund;

import cn.iocoder.yudao.module.pay.mq.message.refund.PayRefundAuditResultMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 退款消息 Producer
 */
@Component
public class PayRefundProducer {

    @Resource
    private ApplicationContext applicationContext;

    public void sendRefundAuditResultMessage(PayRefundAuditResultMessage message) {
        applicationContext.publishEvent(message);
    }
}
