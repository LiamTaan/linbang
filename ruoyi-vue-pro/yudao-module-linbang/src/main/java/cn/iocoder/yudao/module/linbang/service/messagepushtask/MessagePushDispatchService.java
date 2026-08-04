package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;

import java.util.List;

public interface MessagePushDispatchService {

    void dispatchSingle(String templateCode, String fallbackTaskName, String bizType, Long bizId,
                        Long receiverUserId, String creatorRemark);

    void dispatchSingleIdempotent(String templateCode, String fallbackTaskName, String bizType, Long bizId,
                                  Long receiverUserId, String creatorRemark, String dedupeKey);

    void dispatchBatch(String templateCode, String fallbackTaskName, String targetScope, String bizType,
                       Long taskBizId, String creatorRemark, List<MessagePushDispatchTarget> targets);

    void dispatchCampaign(MessageCampaignDO campaign, String creatorRemark,
                          List<MessagePushDispatchTarget> targets);

    void retryTask(Long pushTaskId);
}
