package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
public class MessagePushDispatchServiceImpl implements MessagePushDispatchService {

    private static final String DEFAULT_CHANNEL_TYPE = "INTERNAL_MESSAGE";
    private static final String DEFAULT_TARGET_SCOPE = "SINGLE_USER";

    @Resource
    private MessageTemplateMapper messageTemplateMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessagePushTaskService messagePushTaskService;

    @Override
    public void dispatchSingle(String templateCode, String fallbackTaskName, String bizType, Long bizId,
                               Long receiverUserId, String creatorRemark) {
        if (receiverUserId == null) {
            return;
        }
        dispatchBatch(templateCode, fallbackTaskName, DEFAULT_TARGET_SCOPE, bizType, bizId, creatorRemark,
                java.util.Collections.singletonList(new MessagePushDispatchTarget(receiverUserId, bizId)));
    }

    @Override
    public void dispatchBatch(String templateCode, String fallbackTaskName, String targetScope, String bizType,
                              Long taskBizId, String creatorRemark, List<MessagePushDispatchTarget> targets) {
        if (CollUtil.isEmpty(targets)) {
            return;
        }
        MessageTemplateDO template = resolveTemplate(templateCode);
        String channelType = template != null && StrUtil.isNotBlank(template.getChannelType())
                ? template.getChannelType() : DEFAULT_CHANNEL_TYPE;
        Long pushTaskId = messagePushTaskService.createTask(MessagePushTaskDO.builder()
                .taskName(template != null ? template.getTemplateName() : fallbackTaskName)
                .targetScope(StrUtil.blankToDefault(targetScope, DEFAULT_TARGET_SCOPE))
                .channelType(channelType)
                .templateId(template != null ? template.getId() : null)
                .bizType(bizType)
                .bizId(taskBizId)
                .plannedSendTime(LocalDateTime.now())
                .status("PROCESSING")
                .creatorRemark(creatorRemark)
                .build());
        int successCount = 0;
        int failCount = 0;
        for (MessagePushDispatchTarget target : targets) {
            if (target == null || target.getReceiverUserId() == null) {
                failCount++;
                continue;
            }
            messageRecordMapper.insert(MessageRecordDO.builder()
                    .templateId(template != null ? template.getId() : null)
                    .pushTaskId(pushTaskId)
                    .receiverUserId(target.getReceiverUserId())
                    .channelType(channelType)
                    .bizType(bizType)
                    .bizId(target.getBizId())
                    .sendStatus("SUCCESS")
                    .sendTime(LocalDateTime.now())
                    .build());
            successCount++;
        }
        messagePushTaskService.updateTaskResult(pushTaskId, resolveTaskStatus(successCount, failCount), successCount, failCount);
    }

    private MessageTemplateDO resolveTemplate(String templateCode) {
        if (StrUtil.isBlank(templateCode)) {
            return null;
        }
        return messageTemplateMapper.selectOne(new LambdaQueryWrapperX<MessageTemplateDO>()
                .eq(MessageTemplateDO::getTemplateCode, templateCode)
                .eq(MessageTemplateDO::getStatus, "ENABLE")
                .last("LIMIT 1"));
    }

    private String resolveTaskStatus(int successCount, int failCount) {
        if (successCount > 0 && failCount > 0) {
            return "PARTIAL_FAILED";
        }
        if (successCount > 0) {
            return "SUCCESS";
        }
        return "FAILED";
    }
}
