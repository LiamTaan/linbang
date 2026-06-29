package cn.iocoder.yudao.module.linbang.service.messagefeedback;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo.MessageFeedbackStatPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagefeedback.vo.MessageFeedbackStatRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagefeedbackstat.MessageFeedbackStatDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagecampaign.MessageCampaignMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagefeedbackstat.MessageFeedbackStatMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagepushtask.MessagePushTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_FEEDBACK_STAT_NOT_EXISTS;

@Service
@Validated
public class MessageFeedbackStatServiceImpl implements MessageFeedbackStatService {

    @Resource
    private MessageFeedbackStatMapper messageFeedbackStatMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessagePushTaskMapper messagePushTaskMapper;
    @Resource
    private MessageCampaignMapper messageCampaignMapper;

    @Override
    public PageResult<MessageFeedbackStatRespVO> getPage(MessageFeedbackStatPageReqVO reqVO) {
        PageResult<MessageFeedbackStatDO> pageResult = messageFeedbackStatMapper.selectPage(reqVO);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), MessageFeedbackStatRespVO.class), pageResult.getTotal());
    }

    @Override
    public MessageFeedbackStatRespVO get(Long id) {
        MessageFeedbackStatDO stat = messageFeedbackStatMapper.selectById(id);
        if (stat == null) {
            throw exception(MESSAGE_FEEDBACK_STAT_NOT_EXISTS);
        }
        return BeanUtils.toBean(stat, MessageFeedbackStatRespVO.class);
    }

    @Override
    public void refreshByRecord(MessageRecordDO record) {
        if (record == null) {
            return;
        }
        if (record.getPushTaskId() != null) {
            refreshByTaskId(record.getPushTaskId());
        }
        if (record.getCampaignId() != null) {
            refreshByCampaignId(record.getCampaignId());
        }
        refreshDailyStat(record);
    }

    @Override
    public void refreshByTaskId(Long pushTaskId) {
        if (pushTaskId == null) {
            return;
        }
        List<MessageRecordDO> records = messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getPushTaskId, pushTaskId));
        MessagePushTaskDO task = messagePushTaskMapper.selectById(pushTaskId);
        if (task == null) {
            return;
        }
        Metrics metrics = calculateMetrics(records);
        messagePushTaskMapper.updateById(MessagePushTaskDO.builder()
                .id(pushTaskId)
                .plannedAudienceCount(metrics.plannedAudienceCount)
                .reachedCount(metrics.reachedCount)
                .clickedCount(metrics.clickedCount)
                .readCount(metrics.readCount)
                .voicePlayedCount(metrics.voicePlayedCount)
                .successCount(metrics.successCount)
                .failCount(metrics.failCount)
                .status(resolveTaskStatus(metrics))
                .executeStatus(resolveTaskStatus(metrics))
                .build());
    }

    @Override
    public void refreshByCampaignId(Long campaignId) {
        if (campaignId == null) {
            return;
        }
        List<MessageRecordDO> records = messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getCampaignId, campaignId));
        MessageCampaignDO campaign = messageCampaignMapper.selectById(campaignId);
        if (campaign == null) {
            return;
        }
        Metrics metrics = calculateMetrics(records);
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(campaignId)
                .plannedAudienceCount(metrics.plannedAudienceCount)
                .reachedCount(metrics.reachedCount)
                .clickedCount(metrics.clickedCount)
                .readCount(metrics.readCount)
                .voicePlayedCount(metrics.voicePlayedCount)
                .executeStatus(resolveTaskStatus(metrics))
                .build());
    }

    private void refreshDailyStat(MessageRecordDO record) {
        LocalDate statDate = resolveStatDate(record);
        MessageFeedbackStatDO stat = messageFeedbackStatMapper.selectOne(new LambdaQueryWrapperX<MessageFeedbackStatDO>()
                .eq(MessageFeedbackStatDO::getStatDate, statDate)
                .eq(MessageFeedbackStatDO::getSceneCode, record.getSceneCode())
                .eq(MessageFeedbackStatDO::getMessageCategory, record.getMessageCategory())
                .eqIfPresent(MessageFeedbackStatDO::getTemplateId, record.getTemplateId())
                .eqIfPresent(MessageFeedbackStatDO::getCampaignId, record.getCampaignId())
                .eqIfPresent(MessageFeedbackStatDO::getPushTaskId, record.getPushTaskId())
                .eq(MessageFeedbackStatDO::getChannelType, record.getChannelType())
                .last("LIMIT 1"));
        List<MessageRecordDO> records = messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getSceneCode, record.getSceneCode())
                .eq(MessageRecordDO::getMessageCategory, record.getMessageCategory())
                .eqIfPresent(MessageRecordDO::getTemplateId, record.getTemplateId())
                .eqIfPresent(MessageRecordDO::getCampaignId, record.getCampaignId())
                .eqIfPresent(MessageRecordDO::getPushTaskId, record.getPushTaskId())
                .eq(MessageRecordDO::getChannelType, record.getChannelType())
                .between(MessageRecordDO::getCreateTime, statDate.atStartOfDay(), statDate.plusDays(1).atStartOfDay()));
        Metrics metrics = calculateMetrics(records);
        MessageFeedbackStatDO saveDO = MessageFeedbackStatDO.builder()
                .id(stat == null ? null : stat.getId())
                .statDate(statDate)
                .sceneCode(record.getSceneCode())
                .messageCategory(record.getMessageCategory())
                .templateId(record.getTemplateId())
                .campaignId(record.getCampaignId())
                .pushTaskId(record.getPushTaskId())
                .channelType(record.getChannelType())
                .plannedAudienceCount(metrics.plannedAudienceCount)
                .reachedCount(metrics.reachedCount)
                .clickedCount(metrics.clickedCount)
                .readCount(metrics.readCount)
                .voicePlayedCount(metrics.voicePlayedCount)
                .reachRate(calculateRate(metrics.reachedCount, metrics.plannedAudienceCount))
                .clickRate(calculateRate(metrics.clickedCount, metrics.reachedCount))
                .readRate(calculateRate(metrics.readCount, metrics.reachedCount))
                .build();
        if (stat == null) {
            messageFeedbackStatMapper.insert(saveDO);
        } else {
            messageFeedbackStatMapper.updateById(saveDO);
        }
    }

    private LocalDate resolveStatDate(MessageRecordDO record) {
        if (record.getSendTime() != null) {
            return record.getSendTime().toLocalDate();
        }
        if (record.getCreateTime() != null) {
            return record.getCreateTime().toLocalDate();
        }
        return LocalDate.now();
    }

    private Metrics calculateMetrics(List<MessageRecordDO> records) {
        Metrics metrics = new Metrics();
        metrics.plannedAudienceCount = records.size();
        for (MessageRecordDO record : records) {
            if ("SUCCESS".equalsIgnoreCase(record.getSendStatus())) {
                metrics.successCount++;
            } else if ("FAILED".equalsIgnoreCase(record.getSendStatus())) {
                metrics.failCount++;
            }
            if (isReached(record)) {
                metrics.reachedCount++;
            }
            if (record.getClickTime() != null) {
                metrics.clickedCount++;
            }
            if (MessageCenterConstants.READ_STATUS_READ.equals(record.getReadStatus()) || record.getReadTime() != null) {
                metrics.readCount++;
            }
            if (record.getVoicePlayedTime() != null) {
                metrics.voicePlayedCount++;
            }
        }
        return metrics;
    }

    private boolean isReached(MessageRecordDO record) {
        if (MessageCenterConstants.CHANNEL_APP_POPUP.equals(record.getChannelType())) {
            return record.getExposedTime() != null;
        }
        if (MessageCenterConstants.CHANNEL_APP_VOICE.equals(record.getChannelType())) {
            return "SUCCESS".equalsIgnoreCase(record.getSendStatus());
        }
        return "SUCCESS".equalsIgnoreCase(record.getSendStatus());
    }

    private String resolveTaskStatus(Metrics metrics) {
        if (metrics.successCount > 0 && metrics.failCount > 0) {
            return MessageCenterConstants.EXECUTE_STATUS_PARTIAL_FAILED;
        }
        if (metrics.successCount > 0) {
            return MessageCenterConstants.EXECUTE_STATUS_SUCCESS;
        }
        if (metrics.failCount > 0) {
            return MessageCenterConstants.EXECUTE_STATUS_FAILED;
        }
        return MessageCenterConstants.EXECUTE_STATUS_PENDING;
    }

    private BigDecimal calculateRate(Integer numerator, Integer denominator) {
        if (numerator == null || denominator == null || denominator <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
    }

    private static class Metrics {
        private int plannedAudienceCount;
        private int reachedCount;
        private int clickedCount;
        private int readCount;
        private int voicePlayedCount;
        private int successCount;
        private int failCount;
    }
}
