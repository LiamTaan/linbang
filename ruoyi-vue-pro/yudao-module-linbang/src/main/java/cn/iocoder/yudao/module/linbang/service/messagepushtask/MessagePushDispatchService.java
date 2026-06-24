package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import java.util.List;

public interface MessagePushDispatchService {

    void dispatchSingle(String templateCode, String fallbackTaskName, String bizType, Long bizId,
                        Long receiverUserId, String creatorRemark);

    void dispatchBatch(String templateCode, String fallbackTaskName, String targetScope, String bizType,
                       Long taskBizId, String creatorRemark, List<MessagePushDispatchTarget> targets);
}
