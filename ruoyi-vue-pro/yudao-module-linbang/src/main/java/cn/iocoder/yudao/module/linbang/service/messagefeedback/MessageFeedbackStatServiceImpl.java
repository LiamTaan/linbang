package cn.iocoder.yudao.module.linbang.service.messagefeedback;

import cn.hutool.crypto.digest.DigestUtil;
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.function.Consumer;

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
        MessagePushTaskDO task = messagePushTaskMapper.selectById(pushTaskId);
        if (task == null) {
            return;
        }
        Metrics metrics = calculateMetrics(query -> query.eq(MessageRecordDO::getPushTaskId, pushTaskId));
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
        MessageCampaignDO campaign = messageCampaignMapper.selectById(campaignId);
        if (campaign == null) {
            return;
        }
        Metrics metrics = calculateMetrics(query -> query.eq(MessageRecordDO::getCampaignId, campaignId));
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
        String statKey = buildStatKey(statDate, record);
        MessageFeedbackStatDO stat = messageFeedbackStatMapper.selectByStatKey(statKey);
        Metrics metrics = calculateMetrics(query -> {
            query.eq(MessageRecordDO::getSceneCode, record.getSceneCode())
                    .eq(MessageRecordDO::getMessageCategory, record.getMessageCategory())
                    .eq(MessageRecordDO::getChannelType, record.getChannelType())
                    .between(MessageRecordDO::getCreateTime, statDate.atStartOfDay(), statDate.plusDays(1).atStartOfDay());
            applyRecordDimension(query, record);
        });
        MessageFeedbackStatDO saveDO = MessageFeedbackStatDO.builder()
                .id(stat == null ? null : stat.getId())
                .statDate(statDate)
                .statKey(statKey)
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
            try {
                messageFeedbackStatMapper.insert(saveDO);
            } catch (DuplicateKeyException ex) {
                MessageFeedbackStatDO concurrent = messageFeedbackStatMapper.selectByStatKeyForUpdate(statKey);
                if (concurrent == null) {
                    throw ex;
                }
                saveDO.setId(concurrent.getId());
                messageFeedbackStatMapper.updateById(saveDO);
            }
        } else {
            messageFeedbackStatMapper.updateById(saveDO);
        }
    }

    private void applyRecordDimension(LambdaQueryWrapperX<MessageRecordDO> query, MessageRecordDO record) {
        if (record.getTemplateId() == null) {
            query.isNull(MessageRecordDO::getTemplateId);
        } else {
            query.eq(MessageRecordDO::getTemplateId, record.getTemplateId());
        }
        if (record.getCampaignId() == null) {
            query.isNull(MessageRecordDO::getCampaignId);
        } else {
            query.eq(MessageRecordDO::getCampaignId, record.getCampaignId());
        }
        if (record.getPushTaskId() == null) {
            query.isNull(MessageRecordDO::getPushTaskId);
        } else {
            query.eq(MessageRecordDO::getPushTaskId, record.getPushTaskId());
        }
    }

    private String buildStatKey(LocalDate statDate, MessageRecordDO record) {
        String rawKey = String.join("|",
                statDate.toString(),
                valueOrEmpty(record.getSceneCode()),
                valueOrEmpty(record.getMessageCategory()),
                valueOrEmpty(record.getTemplateId()),
                valueOrEmpty(record.getCampaignId()),
                valueOrEmpty(record.getPushTaskId()),
                valueOrEmpty(record.getChannelType()));
        return DigestUtil.sha256Hex(rawKey);
    }

    private String valueOrEmpty(Object value) {
        return value == null ? "" : String.valueOf(value);
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

    private Metrics calculateMetrics(Consumer<LambdaQueryWrapperX<MessageRecordDO>> filter) {
        Metrics metrics = new Metrics();
        metrics.plannedAudienceCount = count(filter, query -> { });
        metrics.successCount = count(filter, query -> query.eq(MessageRecordDO::getSendStatus, "SUCCESS"));
        metrics.failCount = count(filter, query -> query.eq(MessageRecordDO::getSendStatus, "FAILED"));
        metrics.reachedCount = count(filter, query -> query.and(wrapper -> wrapper
                .and(app -> app.eq(MessageRecordDO::getChannelType, MessageCenterConstants.CHANNEL_APP_POPUP)
                        .isNotNull(MessageRecordDO::getExposedTime))
                .or(other -> other.ne(MessageRecordDO::getChannelType, MessageCenterConstants.CHANNEL_APP_POPUP)
                        .eq(MessageRecordDO::getSendStatus, "SUCCESS"))));
        metrics.clickedCount = count(filter, query -> query.isNotNull(MessageRecordDO::getClickTime));
        metrics.readCount = count(filter, query -> query.and(wrapper -> wrapper
                .eq(MessageRecordDO::getReadStatus, MessageCenterConstants.READ_STATUS_READ)
                .or()
                .isNotNull(MessageRecordDO::getReadTime)));
        metrics.voicePlayedCount = count(filter, query -> query.isNotNull(MessageRecordDO::getVoicePlayedTime));
        return metrics;
    }

    private int count(Consumer<LambdaQueryWrapperX<MessageRecordDO>> filter,
                      Consumer<LambdaQueryWrapperX<MessageRecordDO>> metricCondition) {
        LambdaQueryWrapperX<MessageRecordDO> query = new LambdaQueryWrapperX<>();
        filter.accept(query);
        metricCondition.accept(query);
        return (int) Math.min(messageRecordMapper.selectCount(query), Integer.MAX_VALUE);
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
