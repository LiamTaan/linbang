package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskManualSendReqVO;

public interface ManualMessagePushService {

    Long manualSend(MessagePushTaskManualSendReqVO reqVO);
}
