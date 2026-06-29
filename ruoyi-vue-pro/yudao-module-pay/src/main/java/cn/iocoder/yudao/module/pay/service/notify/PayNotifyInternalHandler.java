package cn.iocoder.yudao.module.pay.service.notify;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.pay.dal.dataobject.notify.PayNotifyTaskDO;

public interface PayNotifyInternalHandler {

    boolean supports(PayNotifyTaskDO task);

    CommonResult<?> handle(PayNotifyTaskDO task, Object request);

}
