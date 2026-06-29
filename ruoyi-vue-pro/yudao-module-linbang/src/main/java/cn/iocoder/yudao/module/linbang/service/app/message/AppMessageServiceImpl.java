package cn.iocoder.yudao.module.linbang.service.app.message;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageFeedbackReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSendReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordDetailRespVO;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSettingRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageSettingUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appmessagesetting.AppMessageSettingDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appmessagesetting.AppMessageSettingMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import cn.iocoder.yudao.module.linbang.service.messagecampaign.MessageCampaignService;
import cn.iocoder.yudao.module.linbang.service.messagefeedback.MessageFeedbackStatService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveContentDetectService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveDetectResult;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CONTENT_SENSITIVE_BLOCKED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_CAMPAIGN_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_RECORD_NOT_EXISTS;

@Service
@Validated
public class AppMessageServiceImpl implements AppMessageService {

    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessageTemplateMapper messageTemplateMapper;
    @Resource
    private SensitiveContentDetectService sensitiveContentDetectService;
    @Resource
    private AppMessageSettingMapper appMessageSettingMapper;
    @Resource
    private MessageCampaignService messageCampaignService;
    @Resource
    private MessageFeedbackStatService messageFeedbackStatService;

    @Override
    public PageResult<MessageRecordDO> getMessageRecordPage(Long userId, AppMessageRecordPageReqVO reqVO) {
        return messageRecordMapper.selectAppPage(userId, reqVO);
    }

    @Override
    public AppMessageRecordDetailRespVO getMessageRecord(Long userId, Long id) {
        MessageRecordDO record = messageRecordMapper.selectOne(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getId, id)
                .eq(MessageRecordDO::getReceiverUserId, userId));
        if (record == null) {
            throw exception(MESSAGE_RECORD_NOT_EXISTS);
        }
        MessageTemplateDO template = record.getTemplateId() == null ? null : messageTemplateMapper.selectById(record.getTemplateId());
        AppMessageRecordDetailRespVO respVO = BeanUtils.toBean(record, AppMessageRecordDetailRespVO.class);
        if (template != null) {
            respVO.setTemplate(BeanUtils.toBean(template, AppMessageRecordDetailRespVO.TemplateRespVO.class));
        }
        return respVO;
    }

    @Override
    public Long sendMessage(Long userId, AppMessageSendReqVO reqVO) {
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_MESSAGE, userId, "APP_MESSAGE", null, reqVO.getContent());
        if (detectResult.isHit() && LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(detectResult.getStrategy())) {
            throw exception(CONTENT_SENSITIVE_BLOCKED);
        }
        MessageRecordDO record = MessageRecordDO.builder()
                .receiverUserId(reqVO.getReceiverUserId())
                .sceneCode("CHAT")
                .messageCategory(MessageCenterConstants.CATEGORY_SYSTEM)
                .channelType(MessageCenterConstants.CHANNEL_APP_POPUP)
                .bizType("APP_MESSAGE")
                .sendStatus("SUCCESS")
                .sendTime(LocalDateTime.now())
                .title(reqVO.getTitle())
                .contentSnapshot(detectResult.getProcessedContent())
                .readStatus(MessageCenterConstants.READ_STATUS_UNREAD)
                .build();
        messageRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return messageRecordMapper.selectCount(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getReceiverUserId, userId)
                .eq(MessageRecordDO::getReadStatus, MessageCenterConstants.READ_STATUS_UNREAD));
    }

    @Override
    public void markRead(Long userId, Long id) {
        MessageRecordDO record = getOwnedRecord(userId, id);
        if (MessageCenterConstants.READ_STATUS_READ.equals(record.getReadStatus())) {
            return;
        }
        messageRecordMapper.updateById(MessageRecordDO.builder()
                .id(id)
                .readStatus(MessageCenterConstants.READ_STATUS_READ)
                .readTime(LocalDateTime.now())
                .build());
        messageFeedbackStatService.refreshByRecord(getOwnedRecord(userId, id));
    }

    @Override
    public void markAllRead(Long userId, String messageCategory) {
        for (MessageRecordDO record : messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getReceiverUserId, userId)
                .eqIfPresent(MessageRecordDO::getMessageCategory, messageCategory)
                .eq(MessageRecordDO::getReadStatus, MessageCenterConstants.READ_STATUS_UNREAD))) {
            markRead(userId, record.getId());
        }
    }

    @Override
    public void submitExposeFeedback(Long userId, AppMessageFeedbackReqVO reqVO) {
        MessageRecordDO record = getOwnedRecord(userId, reqVO.getRecordId());
        if (record.getExposedTime() != null) {
            return;
        }
        messageRecordMapper.updateById(MessageRecordDO.builder()
                .id(record.getId())
                .exposedTime(LocalDateTime.now())
                .build());
        messageFeedbackStatService.refreshByRecord(getOwnedRecord(userId, record.getId()));
    }

    @Override
    public void submitClickFeedback(Long userId, AppMessageFeedbackReqVO reqVO) {
        MessageRecordDO record = getOwnedRecord(userId, reqVO.getRecordId());
        messageRecordMapper.updateById(MessageRecordDO.builder()
                .id(record.getId())
                .clickTime(LocalDateTime.now())
                .build());
        messageFeedbackStatService.refreshByRecord(getOwnedRecord(userId, record.getId()));
    }

    @Override
    public void submitVoicePlayedFeedback(Long userId, AppMessageFeedbackReqVO reqVO) {
        MessageRecordDO record = getOwnedRecord(userId, reqVO.getRecordId());
        messageRecordMapper.updateById(MessageRecordDO.builder()
                .id(record.getId())
                .voicePlayedTime(LocalDateTime.now())
                .build());
        messageFeedbackStatService.refreshByRecord(getOwnedRecord(userId, record.getId()));
    }

    @Override
    public void recordExternalClick(Long recordId) {
        if (recordId == null) {
            return;
        }
        MessageRecordDO record = messageRecordMapper.selectById(recordId);
        if (record == null) {
            return;
        }
        if (record.getClickTime() == null) {
            messageRecordMapper.updateById(MessageRecordDO.builder()
                    .id(record.getId())
                    .clickTime(LocalDateTime.now())
                    .build());
            record = messageRecordMapper.selectById(recordId);
        }
        messageFeedbackStatService.refreshByRecord(record);
    }

    @Override
    public String resolveRedirectTarget(Long recordId, String targetUrl) {
        MessageRecordDO record = recordId == null ? null : messageRecordMapper.selectById(recordId);
        String candidate = targetUrl;
        if (record != null && candidate == null) {
            candidate = record.getRouteValue();
        }
        if (candidate == null) {
            return "/";
        }
        candidate = candidate.trim();
        if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
            return candidate;
        }
        if (candidate.startsWith("/")) {
            return candidate;
        }
        if (Objects.equals(candidate, "")) {
            return "/";
        }
        return "/" + candidate;
    }

    @Override
    public AppMessageSettingRespVO getMessageSetting(Long userId) {
        return BeanUtils.toBean(getOrCreateSetting(userId), AppMessageSettingRespVO.class);
    }

    @Override
    public void updateMessageSetting(Long userId, AppMessageSettingUpdateReqVO reqVO) {
        AppMessageSettingDO setting = getOrCreateSetting(userId);
        appMessageSettingMapper.updateById(AppMessageSettingDO.builder()
                .id(setting.getId())
                .voiceReadEnabled(reqVO.getVoiceReadEnabled())
                .popupEnabled(reqVO.getPopupEnabled())
                .marketingEnabled(reqVO.getMarketingEnabled())
                .build());
    }

    @Override
    public Long createDirectedCampaign(Long userId, AppMessageCampaignCreateReqVO reqVO) {
        return messageCampaignService.createUserDirected(userId, reqVO);
    }

    @Override
    public PageResult<MessageCampaignRespVO> getDirectedCampaignPage(Long userId, AppMessageCampaignPageReqVO reqVO) {
        return messageCampaignService.getAppPage(userId, reqVO);
    }

    @Override
    public MessageCampaignRespVO getDirectedCampaign(Long userId, Long id) {
        MessageCampaignDO campaign = messageCampaignService.getDO(id);
        if (campaign == null || !userId.equals(campaign.getApplicantUserId())) {
            throw exception(MESSAGE_CAMPAIGN_NOT_EXISTS);
        }
        return messageCampaignService.get(id);
    }

    @Override
    public void withdrawDirectedCampaign(Long userId, Long id) {
        messageCampaignService.withdrawByUser(userId, id);
    }

    private MessageRecordDO getOwnedRecord(Long userId, Long id) {
        MessageRecordDO record = messageRecordMapper.selectOne(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getId, id)
                .eq(MessageRecordDO::getReceiverUserId, userId));
        if (record == null) {
            throw exception(MESSAGE_RECORD_NOT_EXISTS);
        }
        return record;
    }

    private AppMessageSettingDO getOrCreateSetting(Long userId) {
        AppMessageSettingDO setting = appMessageSettingMapper.selectOne(AppMessageSettingDO::getUserId, userId);
        if (setting != null) {
            return setting;
        }
        setting = AppMessageSettingDO.builder()
                .userId(userId)
                .voiceReadEnabled(Boolean.TRUE)
                .popupEnabled(Boolean.TRUE)
                .marketingEnabled(Boolean.TRUE)
                .build();
        appMessageSettingMapper.insert(setting);
        return setting;
    }
}
