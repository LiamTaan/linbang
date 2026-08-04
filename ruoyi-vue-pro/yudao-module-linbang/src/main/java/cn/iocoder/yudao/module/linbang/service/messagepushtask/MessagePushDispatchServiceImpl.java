package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appmessagesetting.AppMessageSettingDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagescene.MessageSceneDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appmessagesetting.AppMessageSettingMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagecampaign.MessageCampaignMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagescene.MessageSceneMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import cn.iocoder.yudao.module.linbang.service.messagefeedback.MessageFeedbackStatService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget;
import cn.iocoder.yudao.module.linbang.service.messagescene.MessageSceneService;
import cn.iocoder.yudao.module.system.api.sms.SmsSendApi;
import cn.iocoder.yudao.module.system.api.sms.dto.send.SmsSendSingleToUserReqDTO;
import cn.iocoder.yudao.module.system.api.social.SocialClientApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialWxaSubscribeMessageSendReqDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_PUSH_TASK_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_PUSH_TASK_RETRY_INVALID;

@Service
@Validated
@Slf4j
public class MessagePushDispatchServiceImpl implements MessagePushDispatchService {

    private static final String DEFAULT_TARGET_SCOPE = "SINGLE_USER";
    private static final int DISPATCH_BATCH_SIZE = 500;
    private static final int RETRY_BATCH_SIZE = 500;

    @Value("${linbang.message.public-base-url:${linbang.public-base-url:http://127.0.0.1:48080}}")
    private String publicBaseUrl;

    @Resource
    private MessageTemplateMapper messageTemplateMapper;
    @Resource
    private MessageSceneMapper messageSceneMapper;
    @Resource
    private MessageSceneService messageSceneService;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessagePushTaskService messagePushTaskService;
    @Resource
    private cn.iocoder.yudao.module.linbang.dal.mysql.messagepushtask.MessagePushTaskMapper messagePushTaskMapper;
    @Resource
    private MessageCampaignMapper messageCampaignMapper;
    @Resource
    private AppMessageSettingMapper appMessageSettingMapper;
    @Resource
    private MessageFeedbackStatService messageFeedbackStatService;
    @Resource
    private SmsSendApi smsSendApi;
    @Resource
    private SocialClientApi socialClientApi;

    @Override
    public void dispatchSingle(String templateCode, String fallbackTaskName, String bizType, Long bizId,
                               Long receiverUserId, String creatorRemark) {
        dispatchSingleIdempotent(templateCode, fallbackTaskName, bizType, bizId, receiverUserId, creatorRemark, null);
    }

    @Override
    public void dispatchSingleIdempotent(String templateCode, String fallbackTaskName, String bizType, Long bizId,
                                         Long receiverUserId, String creatorRemark, String dedupeKey) {
        if (receiverUserId == null) {
            return;
        }
        // 兼容升级前未带渠道后缀的历史幂等记录，避免部署后重复外发。
        if (StrUtil.isNotBlank(dedupeKey) && messageRecordMapper.selectByDedupeKey(dedupeKey) != null) {
            return;
        }
        dispatchBatch(templateCode, fallbackTaskName, DEFAULT_TARGET_SCOPE, bizType, bizId, creatorRemark,
                Collections.singletonList(new MessagePushDispatchTarget(receiverUserId, bizId, dedupeKey)));
    }

    @Override
    public void dispatchBatch(String templateCode, String fallbackTaskName, String targetScope, String bizType,
                              Long taskBizId, String creatorRemark, List<MessagePushDispatchTarget> targets) {
        if (CollUtil.isEmpty(targets)) {
            return;
        }
        List<MessagePushDispatchTarget> targetSnapshot = new ArrayList<>(targets);
        if (deferUntilAfterCommit(() -> dispatchBatchNow(templateCode, fallbackTaskName, targetScope, bizType,
                taskBizId, creatorRemark, targetSnapshot))) {
            return;
        }
        dispatchBatchNow(templateCode, fallbackTaskName, targetScope, bizType,
                taskBizId, creatorRemark, targetSnapshot);
    }

    private void dispatchBatchNow(String templateCode, String fallbackTaskName, String targetScope, String bizType,
                                  Long taskBizId, String creatorRemark, List<MessagePushDispatchTarget> targets) {
        String sceneCode = resolveSceneCode(templateCode);
        MessageSceneDO scene = resolveScene(sceneCode, bizType);
        List<MessageTemplateDO> templates = resolveTemplates(templateCode, scene);
        if (CollUtil.isEmpty(templates)) {
            templates = buildFallbackTemplates(templateCode, fallbackTaskName, scene);
        }
        templates = enforceMandatorySms(scene, templates, fallbackTaskName);
        MessageCampaignDO campaign = createSystemCampaign(scene, fallbackTaskName, bizType, taskBizId, creatorRemark);
        int[] counts = dispatchResolvedCampaign(campaign, scene, templates,
                StrUtil.blankToDefault(targetScope, DEFAULT_TARGET_SCOPE),
                bizType, taskBizId, creatorRemark, targets);
        finishSystemCampaign(campaign.getId(), counts[0], counts[1]);
    }

    @Override
    public void dispatchCampaign(MessageCampaignDO campaign, String creatorRemark,
                                 List<MessagePushDispatchTarget> targets) {
        if (campaign == null || campaign.getId() == null || CollUtil.isEmpty(targets)) {
            return;
        }
        List<MessagePushDispatchTarget> targetSnapshot = new ArrayList<>(targets);
        if (deferUntilAfterCommit(() -> dispatchCampaignNow(campaign, creatorRemark, targetSnapshot))) {
            return;
        }
        dispatchCampaignNow(campaign, creatorRemark, targetSnapshot);
    }

    private void dispatchCampaignNow(MessageCampaignDO campaign, String creatorRemark,
                                     List<MessagePushDispatchTarget> targets) {
        String sceneCode = resolveSceneCode(campaign.getSceneCode());
        String bizType = StrUtil.blankToDefault(campaign.getBizType(), MessageCenterConstants.BIZ_TYPE_MARKETING);
        MessageSceneDO scene = resolveScene(sceneCode, bizType);
        List<MessageTemplateDO> templates = resolveTemplates(campaign.getSceneCode(), scene);
        if (CollUtil.isEmpty(templates)) {
            templates = buildFallbackTemplates(campaign.getSceneCode(), campaign.getCampaignName(), scene);
        }
        templates = enforceMandatorySms(scene, templates, campaign.getCampaignName());
        dispatchResolvedCampaign(campaign, scene, templates,
                StrUtil.blankToDefault(campaign.getTargetMode(), DEFAULT_TARGET_SCOPE), bizType,
                campaign.getBizId(), creatorRemark, targets);
    }

    private int[] dispatchResolvedCampaign(MessageCampaignDO campaign, MessageSceneDO scene,
                                           List<MessageTemplateDO> templates, String targetScope,
                                           String bizType, Long taskBizId, String creatorRemark,
                                           List<MessagePushDispatchTarget> targets) {
        int successCount = 0;
        int failCount = 0;
        for (MessageTemplateDO template : templates) {
            for (int fromIndex = 0; fromIndex < targets.size(); fromIndex += DISPATCH_BATCH_SIZE) {
                int toIndex = Math.min(fromIndex + DISPATCH_BATCH_SIZE, targets.size());
                List<MessagePushDispatchTarget> targetBatch = targets.subList(fromIndex, toIndex);
                List<MessagePushDispatchTarget> channelTargets = filterTargetsByPreference(
                        scene, template.getChannelType(), targetBatch);
                channelTargets = filterAlreadyDispatchedTargets(template, channelTargets);
                if (CollUtil.isEmpty(channelTargets)) {
                    continue;
                }
                int[] batchCounts = dispatchByChannel(campaign, scene, template, targetScope,
                        bizType, taskBizId, creatorRemark, channelTargets);
                successCount += batchCounts[0];
                failCount += batchCounts[1];
            }
        }
        return new int[]{successCount, failCount};
    }

    private void finishSystemCampaign(Long campaignId, int successCount, int failCount) {
        String status = resolveTaskStatus(successCount, failCount);
        if (MessageCenterConstants.EXECUTE_STATUS_PENDING.equals(status)) {
            status = MessageCenterConstants.EXECUTE_STATUS_SUCCESS;
        }
        messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getExecuteStatus, status)
                .set(MessageCampaignDO::getExecuteTime, LocalDateTime.now())
                .eq(MessageCampaignDO::getId, campaignId)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PROCESSING));
    }

    @Override
    public void retryTask(Long pushTaskId) {
        MessagePushTaskDO task = messagePushTaskMapper.selectById(pushTaskId);
        if (task == null) {
            throw exception(MESSAGE_PUSH_TASK_NOT_EXISTS);
        }
        if (!(MessageCenterConstants.EXECUTE_STATUS_FAILED.equals(task.getExecuteStatus())
                || MessageCenterConstants.EXECUTE_STATUS_PARTIAL_FAILED.equals(task.getExecuteStatus())
                || (task.getFailCount() != null && task.getFailCount() > 0))) {
            throw exception(MESSAGE_PUSH_TASK_RETRY_INVALID);
        }
        if (messageRecordMapper.selectCount(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getPushTaskId, pushTaskId)
                .eq(MessageRecordDO::getSendStatus, "FAILED")) == 0) {
            throw exception(MESSAGE_PUSH_TASK_RETRY_INVALID);
        }
        MessageTemplateDO template = task.getTemplateId() == null ? null : messageTemplateMapper.selectById(task.getTemplateId());
        if (template == null) {
            template = MessageTemplateDO.builder()
                    .id(task.getTemplateId())
                    .templateName(task.getTaskName())
                    .channelType(task.getChannelType())
                    .titleTemplate(task.getTaskName())
                    .contentTemplate(task.getTaskName())
                    .build();
        }
        messagePushTaskService.markRetrying(pushTaskId);
        MessageRecordDO lastRecord = null;
        Long lastRecordId = null;
        while (true) {
            LambdaQueryWrapperX<MessageRecordDO> query = new LambdaQueryWrapperX<MessageRecordDO>()
                    .eq(MessageRecordDO::getPushTaskId, pushTaskId)
                    .eq(MessageRecordDO::getSendStatus, "FAILED");
            if (lastRecordId != null) {
                query.gt(MessageRecordDO::getId, lastRecordId);
            }
            query.orderByAsc(MessageRecordDO::getId).last("LIMIT " + RETRY_BATCH_SIZE);
            List<MessageRecordDO> failedRecords = messageRecordMapper.selectList(query);
            if (CollUtil.isEmpty(failedRecords)) {
                break;
            }
            for (MessageRecordDO record : failedRecords) {
                record.setSendStatus("PENDING");
                record.setFailReason(null);
                sendRecord(record, template);
                messageRecordMapper.updateById(record);
                lastRecord = record;
            }
            lastRecordId = failedRecords.get(failedRecords.size() - 1).getId();
        }
        if (lastRecord != null) {
            messageFeedbackStatService.refreshByRecord(lastRecord);
        }
    }

    private boolean deferUntilAfterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            return false;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    action.run();
                } catch (RuntimeException ex) {
                    log.error("[deferUntilAfterCommit][事务提交后的消息分发失败]", ex);
                }
            }
        });
        return true;
    }

    private int[] dispatchByChannel(MessageCampaignDO campaign, MessageSceneDO scene, MessageTemplateDO template,
                                    String targetScope, String bizType, Long taskBizId, String creatorRemark,
                                    List<MessagePushDispatchTarget> targets) {
        Long pushTaskId = messagePushTaskService.createTask(MessagePushTaskDO.builder()
                .taskName(resolveTaskName(template, scene))
                .campaignId(campaign.getId())
                .sceneCode(scene.getSceneCode())
                .messageCategory(scene.getMessageCategory())
                .targetScope(targetScope)
                .channelType(template.getChannelType())
                .templateId(template.getId())
                .bizType(bizType)
                .bizId(taskBizId)
                .plannedSendTime(LocalDateTime.now())
                .status(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .plannedAudienceCount(targets.size())
                .creatorRemark(creatorRemark)
                .build());
        int successCount = 0;
        int failCount = 0;
        MessageRecordDO lastRecord = null;
        for (MessagePushDispatchTarget target : targets) {
            MessageRecordDO record = buildRecord(campaign, pushTaskId, scene, template, bizType, target, taskBizId);
            try {
                messageRecordMapper.insert(record);
            } catch (DuplicateKeyException ignored) {
                continue;
            }
            sendRecord(record, template);
            if (messageRecordMapper.updateById(record) != 1) {
                throw new IllegalStateException("Message record result update failed: " + record.getId());
            }
            if ("SUCCESS".equalsIgnoreCase(record.getSendStatus())) {
                successCount++;
            } else {
                failCount++;
            }
            lastRecord = record;
        }
        messagePushTaskService.updateTaskResult(pushTaskId, resolveTaskStatus(successCount, failCount), successCount, failCount);
        if (lastRecord != null) {
            try {
                messageFeedbackStatService.refreshByRecord(lastRecord);
            } catch (RuntimeException ex) {
                log.warn("[dispatchByChannel][recordId({}) feedback refresh failed]", lastRecord.getId(), ex);
            }
        }
        return new int[]{successCount, failCount};
    }

    private MessageRecordDO buildRecord(MessageCampaignDO campaign, Long pushTaskId, MessageSceneDO scene,
                                        MessageTemplateDO template, String bizType, MessagePushDispatchTarget target, Long taskBizId) {
        LocalDateTime now = LocalDateTime.now();
        String content = StrUtil.blankToDefault(template.getContentTemplate(), campaign.getContentSnapshot());
        return MessageRecordDO.builder()
                .templateId(template.getId())
                .campaignId(campaign.getId())
                .pushTaskId(pushTaskId)
                .receiverUserId(target.getReceiverUserId())
                .sceneCode(scene.getSceneCode())
                .messageCategory(scene.getMessageCategory())
                .channelType(template.getChannelType())
                .bizType(bizType)
                .bizId(target.getBizId() != null ? target.getBizId() : taskBizId)
                .dedupeKey(buildChannelDedupeKey(target.getDedupeKey(), template))
                .sendStatus("PENDING")
                .sendTime(now)
                .title(StrUtil.blankToDefault(template.getTitleTemplate(), campaign.getCampaignName()))
                .contentSnapshot(content)
                .routeType(template.getRouteType())
                .routeValue(normalizeRouteValue(template.getRouteType(), template.getRouteValue()))
                .readStatus(MessageCenterConstants.READ_STATUS_UNREAD)
                .voiceText(resolveVoiceText(template, content))
                .build();
    }

    private String buildChannelDedupeKey(String logicalKey, MessageTemplateDO template) {
        if (StrUtil.isBlank(logicalKey)) {
            return null;
        }
        String candidate = logicalKey + ":" + StrUtil.blankToDefault(template.getChannelType(), "UNKNOWN")
                + ":" + (template.getId() == null ? "FALLBACK" : template.getId());
        if (candidate.length() <= 128) {
            return candidate;
        }
        return StrUtil.maxLength(logicalKey, 60) + ":" + DigestUtil.sha256Hex(candidate);
    }

    private void sendRecord(MessageRecordDO record, MessageTemplateDO template) {
        try {
            if (MessageCenterConstants.CHANNEL_APP_POPUP.equals(template.getChannelType())) {
                record.setProviderMessageId(record.getId() == null ? null : String.valueOf(record.getId()));
            } else if (MessageCenterConstants.CHANNEL_SMS.equals(template.getChannelType())) {
                SmsSendSingleToUserReqDTO reqDTO = new SmsSendSingleToUserReqDTO();
                reqDTO.setUserId(record.getReceiverUserId());
                reqDTO.setTemplateCode(resolveSmsTemplateCode(template));
                reqDTO.setTemplateParams(buildTemplateParams(record));
                Long providerId = smsSendApi.sendSingleSmsToMember(reqDTO);
                record.setProviderMessageId(providerId == null ? null : String.valueOf(providerId));
            } else if (MessageCenterConstants.CHANNEL_WECHAT_MP_TEMPLATE.equals(template.getChannelType())) {
                SocialWxaSubscribeMessageSendReqDTO reqDTO = new SocialWxaSubscribeMessageSendReqDTO();
                reqDTO.setUserId(record.getReceiverUserId());
                reqDTO.setUserType(UserTypeEnum.MEMBER.getValue());
                reqDTO.setTemplateTitle(StrUtil.blankToDefault(template.getTemplateName(), record.getTitle()));
                reqDTO.setPage(record.getRouteValue());
                reqDTO.addMessage("title", StrUtil.blankToDefault(record.getTitle(), template.getTemplateName()));
                reqDTO.addMessage("content", StrUtil.blankToDefault(record.getContentSnapshot(), ""));
                reqDTO.addMessage("clickUrl", buildClickUrl(record));
                socialClientApi.sendWxaSubscribeMessage(reqDTO);
            }
            record.setSendStatus("SUCCESS");
            record.setSendTime(LocalDateTime.now());
        } catch (Exception ex) {
            record.setSendStatus("FAILED");
            record.setFailReason("SEND_FAILED");
            record.setSendTime(LocalDateTime.now());
        }
    }

    private String resolveVoiceText(MessageTemplateDO template, String content) {
        if (StrUtil.isNotBlank(template.getVoiceTextTemplate())) {
            return template.getVoiceTextTemplate();
        }
        return content;
    }

    private String normalizeRouteValue(String routeType, String routeValue) {
        if (!StrUtil.equals("APP_PAGE", routeType) || StrUtil.isBlank(routeValue)) {
            return routeValue;
        }
        if (StrUtil.equals(routeValue, "/pages/qualification/index")) {
            return "/pages/certificate/certificate";
        }
        return routeValue;
    }

    private Map<String, Object> buildTemplateParams(MessageRecordDO record) {
        Map<String, Object> params = new HashMap<>();
        params.put("title", StrUtil.blankToDefault(record.getTitle(), ""));
        params.put("content", StrUtil.blankToDefault(record.getContentSnapshot(), ""));
        params.put("recordId", record.getId());
        params.put("bizId", record.getBizId());
        params.put("routeValue", StrUtil.blankToDefault(record.getRouteValue(), ""));
        params.put("clickUrl", buildClickUrl(record));
        return params;
    }

    private String buildClickUrl(MessageRecordDO record) {
        String clickToken = ensureExternalClickToken(record);
        return UriComponentsBuilder.fromHttpUrl(StrUtil.removeSuffix(publicBaseUrl, "/"))
                .path("/app-api/message/redirect/click")
                .queryParam("recordId", record.getId())
                .queryParam("clickToken", clickToken)
                .build()
                .toUriString();
    }

    private String ensureExternalClickToken(MessageRecordDO record) {
        if (StrUtil.isNotBlank(record.getExternalClickToken())) {
            return record.getExternalClickToken();
        }
        String token = IdUtil.fastSimpleUUID();
        int updated = messageRecordMapper.update(null, new LambdaUpdateWrapper<MessageRecordDO>()
                .eq(MessageRecordDO::getId, record.getId())
                .isNull(MessageRecordDO::getExternalClickToken)
                .set(MessageRecordDO::getExternalClickToken, token));
        if (updated > 0) {
            record.setExternalClickToken(token);
            return token;
        }
        MessageRecordDO current = messageRecordMapper.selectById(record.getId());
        return current == null ? token : StrUtil.blankToDefault(current.getExternalClickToken(), token);
    }

    private String resolveSmsTemplateCode(MessageTemplateDO template) {
        return StrUtil.blankToDefault(template.getSmsTemplateCode(), template.getTemplateCode());
    }

    private String resolveTaskName(MessageTemplateDO template, MessageSceneDO scene) {
        if (StrUtil.isNotBlank(template.getTemplateName())) {
            return template.getTemplateName();
        }
        return scene.getSceneName();
    }

    private MessageCampaignDO createSystemCampaign(MessageSceneDO scene, String fallbackTaskName, String bizType, Long bizId, String creatorRemark) {
        MessageCampaignDO campaign = MessageCampaignDO.builder()
                .campaignName(StrUtil.blankToDefault(fallbackTaskName, scene.getSceneName()))
                .sourceType(MessageCenterConstants.CAMPAIGN_SOURCE_SYSTEM_TRIGGER)
                .auditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .targetMode(MessageCenterConstants.TARGET_MODE_CUSTOM_FILTER)
                .sceneCode(scene.getSceneCode())
                .messageCategory(scene.getMessageCategory())
                .bizType(bizType)
                .bizId(bizId)
                .contentSnapshot(creatorRemark)
                .executeTime(LocalDateTime.now())
                .build();
        messageCampaignMapper.insert(campaign);
        return campaign;
    }

    private String resolveSceneCode(String templateCode) {
        if (StrUtil.isBlank(templateCode)) {
            return MessageCenterConstants.SCENE_SYSTEM_NOTICE;
        }
        return MessageCenterConstants.LEGACY_TEMPLATE_SCENE_MAPPING.getOrDefault(templateCode, templateCode);
    }

    private MessageSceneDO resolveScene(String sceneCode, String bizType) {
        MessageSceneDO scene = messageSceneService.getMessageScene(sceneCode);
        if (scene != null) {
            return scene;
        }
        MessageSceneDO fallback = messageSceneMapper.selectOne(new LambdaQueryWrapperX<MessageSceneDO>()
                .eq(MessageSceneDO::getSceneCode, MessageCenterConstants.SCENE_SYSTEM_NOTICE)
                .last("LIMIT 1"));
        if (fallback != null) {
            return fallback;
        }
        return MessageSceneDO.builder()
                .sceneCode(sceneCode)
                .sceneName(StrUtil.blankToDefault(sceneCode, "系统通知"))
                .messageCategory(resolveCategoryByBizType(bizType))
                .defaultChannels(MessageCenterConstants.CHANNEL_APP_POPUP)
                .mandatorySms(Boolean.FALSE)
                .voiceEnabled(Boolean.FALSE)
                .status("ENABLE")
                .bizType(bizType)
                .build();
    }

    private String resolveCategoryByBizType(String bizType) {
        if ("ORDER".equalsIgnoreCase(bizType)) {
            return MessageCenterConstants.CATEGORY_ORDER;
        }
        if ("WITHDRAW".equalsIgnoreCase(bizType) || "REFUND".equalsIgnoreCase(bizType) || "PAY".equalsIgnoreCase(bizType)) {
            return MessageCenterConstants.CATEGORY_FINANCE;
        }
        return MessageCenterConstants.CATEGORY_SYSTEM;
    }

    private List<MessageTemplateDO> resolveTemplates(String templateCode, MessageSceneDO scene) {
        List<MessageTemplateDO> templates = new ArrayList<>();
        if (StrUtil.isBlank(templateCode)) {
            return templates;
        }
        templates.addAll(messageTemplateMapper.selectList(new LambdaQueryWrapperX<MessageTemplateDO>()
                .eq(MessageTemplateDO::getTemplateCode, templateCode)
                .eq(MessageTemplateDO::getStatus, "ENABLE")
                .orderByAsc(MessageTemplateDO::getSort)
                .orderByAsc(MessageTemplateDO::getId)));
        return deduplicateByChannel(templates);
    }

    private List<MessageTemplateDO> buildFallbackTemplates(String templateCode, String fallbackTaskName, MessageSceneDO scene) {
        Set<String> channels = splitChannels(scene.getDefaultChannels());
        if (CollUtil.isEmpty(channels)) {
            channels.add(MessageCenterConstants.CHANNEL_APP_POPUP);
        }
        List<MessageTemplateDO> templates = new ArrayList<>();
        for (String channel : channels) {
            templates.add(MessageTemplateDO.builder()
                    .templateCode(templateCode)
                    .templateName(StrUtil.blankToDefault(fallbackTaskName, scene.getSceneName()))
                    .sceneCode(scene.getSceneCode())
                    .messageCategory(scene.getMessageCategory())
                    .channelType(channel)
                    .titleTemplate(StrUtil.blankToDefault(fallbackTaskName, scene.getSceneName()))
                    .contentTemplate(StrUtil.blankToDefault(fallbackTaskName, scene.getSceneName()))
                    .status("ENABLE")
                    .build());
        }
        return templates;
    }

    private List<MessageTemplateDO> enforceMandatorySms(MessageSceneDO scene, List<MessageTemplateDO> templates, String fallbackTaskName) {
        boolean smsRequired = Boolean.TRUE.equals(scene.getMandatorySms()) || MessageCenterConstants.FINANCE_SCENES.contains(scene.getSceneCode());
        if (!smsRequired) {
            return deduplicateByChannel(templates);
        }
        boolean hasSms = templates.stream().anyMatch(template -> MessageCenterConstants.CHANNEL_SMS.equals(template.getChannelType()));
        if (!hasSms) {
            templates = new ArrayList<>(templates);
            templates.add(MessageTemplateDO.builder()
                    .templateName(StrUtil.blankToDefault(fallbackTaskName, scene.getSceneName()))
                    .sceneCode(scene.getSceneCode())
                    .messageCategory(scene.getMessageCategory())
                    .channelType(MessageCenterConstants.CHANNEL_SMS)
                    .titleTemplate(StrUtil.blankToDefault(fallbackTaskName, scene.getSceneName()))
                    .contentTemplate(StrUtil.blankToDefault(fallbackTaskName, scene.getSceneName()))
                    .status("ENABLE")
                    .build());
        }
        return deduplicateByChannel(templates);
    }

    private List<MessagePushDispatchTarget> filterTargetsByPreference(MessageSceneDO scene, String channelType, List<MessagePushDispatchTarget> targets) {
        if (CollUtil.isEmpty(targets)) {
            return Collections.emptyList();
        }
        List<MessagePushDispatchTarget> result = new ArrayList<>();
        Map<Long, AppMessageSettingDO> settingMap = loadSettingMap(targets);
        for (MessagePushDispatchTarget target : targets) {
            AppMessageSettingDO setting = settingMap.get(target.getReceiverUserId());
            if (skipByPreference(scene, channelType, setting)) {
                continue;
            }
            result.add(target);
        }
        return result;
    }

    private List<MessagePushDispatchTarget> filterAlreadyDispatchedTargets(
            MessageTemplateDO template, List<MessagePushDispatchTarget> targets) {
        if (CollUtil.isEmpty(targets)) {
            return Collections.emptyList();
        }
        Map<String, MessagePushDispatchTarget> keyedTargets = new LinkedHashMap<>();
        List<MessagePushDispatchTarget> unkeyedTargets = new ArrayList<>();
        for (MessagePushDispatchTarget target : targets) {
            String channelDedupeKey = buildChannelDedupeKey(target.getDedupeKey(), template);
            if (StrUtil.isBlank(channelDedupeKey)) {
                unkeyedTargets.add(target);
            } else {
                keyedTargets.put(channelDedupeKey, target);
            }
        }
        if (keyedTargets.isEmpty()) {
            return targets;
        }
        List<MessageRecordDO> existingRecords = messageRecordMapper.selectList(
                new LambdaQueryWrapperX<MessageRecordDO>()
                        .select(MessageRecordDO::getDedupeKey)
                        .in(MessageRecordDO::getDedupeKey, keyedTargets.keySet()));
        for (MessageRecordDO existingRecord : existingRecords) {
            keyedTargets.remove(existingRecord.getDedupeKey());
        }
        List<MessagePushDispatchTarget> result = new ArrayList<>(unkeyedTargets.size() + keyedTargets.size());
        result.addAll(unkeyedTargets);
        result.addAll(keyedTargets.values());
        return result;
    }

    private boolean skipByPreference(MessageSceneDO scene, String channelType, AppMessageSettingDO setting) {
        if (setting == null) {
            return false;
        }
        if (MessageCenterConstants.CATEGORY_MARKETING.equals(scene.getMessageCategory()) && Boolean.FALSE.equals(setting.getMarketingEnabled())) {
            return true;
        }
        if (MessageCenterConstants.CHANNEL_APP_POPUP.equals(channelType) && Boolean.FALSE.equals(setting.getPopupEnabled())) {
            return true;
        }
        return MessageCenterConstants.CHANNEL_APP_VOICE.equals(channelType) && Boolean.FALSE.equals(setting.getVoiceReadEnabled());
    }

    private Map<Long, AppMessageSettingDO> loadSettingMap(List<MessagePushDispatchTarget> targets) {
        Set<Long> userIds = new LinkedHashSet<>();
        for (MessagePushDispatchTarget target : targets) {
            if (target != null && target.getReceiverUserId() != null) {
                userIds.add(target.getReceiverUserId());
            }
        }
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<AppMessageSettingDO> settings = appMessageSettingMapper.selectList(new LambdaQueryWrapperX<AppMessageSettingDO>()
                .in(AppMessageSettingDO::getUserId, userIds));
        Map<Long, AppMessageSettingDO> result = new LinkedHashMap<>();
        for (AppMessageSettingDO setting : settings) {
            result.put(setting.getUserId(), setting);
        }
        return result;
    }

    private List<MessageTemplateDO> deduplicateByChannel(List<MessageTemplateDO> templates) {
        if (CollUtil.isEmpty(templates)) {
            return Collections.emptyList();
        }
        Map<String, MessageTemplateDO> templateMap = new LinkedHashMap<>();
        for (MessageTemplateDO template : templates) {
            if (template == null || StrUtil.isBlank(template.getChannelType())) {
                continue;
            }
            templateMap.putIfAbsent(template.getChannelType(), template);
        }
        return new ArrayList<>(templateMap.values());
    }

    private Set<String> splitChannels(String defaultChannels) {
        if (StrUtil.isBlank(defaultChannels)) {
            return new LinkedHashSet<>();
        }
        return new LinkedHashSet<>(StrUtil.splitTrim(defaultChannels, ','));
    }

    private String resolveTaskStatus(int successCount, int failCount) {
        if (successCount > 0 && failCount > 0) {
            return MessageCenterConstants.EXECUTE_STATUS_PARTIAL_FAILED;
        }
        if (successCount > 0) {
            return MessageCenterConstants.EXECUTE_STATUS_SUCCESS;
        }
        if (failCount > 0) {
            return MessageCenterConstants.EXECUTE_STATUS_FAILED;
        }
        return MessageCenterConstants.EXECUTE_STATUS_PENDING;
    }
}
