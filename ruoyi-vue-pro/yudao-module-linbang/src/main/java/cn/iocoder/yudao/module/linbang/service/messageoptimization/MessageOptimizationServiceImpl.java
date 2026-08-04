package cn.iocoder.yudao.module.linbang.service.messageoptimization;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messageoptimization.vo.MessageOptimizationSaveReqVO;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagefeedbackstat.MessageFeedbackStatDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messageoptimization.MessageOptimizationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagefeedbackstat.MessageFeedbackStatMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messageoptimization.MessageOptimizationMapper;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_OPTIMIZATION_NOT_EXISTS;

@Service
@Validated
public class MessageOptimizationServiceImpl implements MessageOptimizationService {

    private static final int STAT_BATCH_SIZE = 500;

    @Resource
    private MessageOptimizationMapper messageOptimizationMapper;
    @Resource
    private MessageFeedbackStatMapper messageFeedbackStatMapper;

    @Override
    public PageResult<MessageOptimizationRespVO> getPage(MessageOptimizationPageReqVO reqVO) {
        PageResult<MessageOptimizationDO> pageResult = messageOptimizationMapper.selectPage(reqVO);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), MessageOptimizationRespVO.class), pageResult.getTotal());
    }

    @Override
    public MessageOptimizationRespVO get(Long id) {
        MessageOptimizationDO optimization = messageOptimizationMapper.selectById(id);
        if (optimization == null) {
            throw exception(MESSAGE_OPTIMIZATION_NOT_EXISTS);
        }
        return BeanUtils.toBean(optimization, MessageOptimizationRespVO.class);
    }

    @Override
    public void save(MessageOptimizationSaveReqVO reqVO) {
        if (messageOptimizationMapper.selectById(reqVO.getId()) == null) {
            throw exception(MESSAGE_OPTIMIZATION_NOT_EXISTS);
        }
        messageOptimizationMapper.updateById(BeanUtils.toBean(reqVO, MessageOptimizationDO.class));
    }

    @Override
    public int refreshCandidates() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(29);
        Map<String, CandidateAggregate> aggregateMap = new LinkedHashMap<>();
        Long lastStatId = null;
        while (true) {
            LambdaQueryWrapperX<MessageFeedbackStatDO> query = new LambdaQueryWrapperX<>();
            query.between(MessageFeedbackStatDO::getStatDate, startDate, endDate);
            if (lastStatId != null) {
                query.gt(MessageFeedbackStatDO::getId, lastStatId);
            }
            query.orderByAsc(MessageFeedbackStatDO::getId).last("LIMIT " + STAT_BATCH_SIZE);
            List<MessageFeedbackStatDO> stats = messageFeedbackStatMapper.selectList(query);
            if (CollUtil.isEmpty(stats)) {
                break;
            }
            for (MessageFeedbackStatDO stat : stats) {
                if (stat.getTemplateId() == null && stat.getCampaignId() == null) {
                    continue;
                }
                String referenceKey = buildReferenceKey(stat);
                aggregateMap.computeIfAbsent(referenceKey, key -> new CandidateAggregate(stat)).add(stat);
            }
            lastStatId = stats.get(stats.size() - 1).getId();
        }
        Map<String, MessageOptimizationDO> existingMap = new LinkedHashMap<>();
        messageOptimizationMapper.selectList(new LambdaQueryWrapperX<MessageOptimizationDO>()
                        .eq(MessageOptimizationDO::getStatStartDate, startDate)
                        .eq(MessageOptimizationDO::getStatEndDate, endDate))
                .forEach(item -> existingMap.put(item.getOptimizationKey(), item));
        int refreshed = 0;
        for (CandidateAggregate aggregate : aggregateMap.values()) {
            if (!needOptimization(aggregate.getReachRate(), aggregate.getClickRate())) {
                continue;
            }
            refreshCandidate(aggregate, startDate, endDate, existingMap);
            refreshed++;
        }
        return refreshed;
    }

    private void refreshCandidate(CandidateAggregate aggregate, LocalDate startDate, LocalDate endDate,
                                  Map<String, MessageOptimizationDO> existingMap) {
        MessageFeedbackStatDO stat = aggregate.sample;
        String optimizationKey = buildOptimizationKey(stat, startDate, endDate);
        MessageOptimizationDO existing = existingMap.get(optimizationKey);
        MessageOptimizationDO saveDO = MessageOptimizationDO.builder()
                .id(existing == null ? null : existing.getId())
                .optimizationKey(optimizationKey)
                .refType(stat.getCampaignId() != null ? MessageCenterConstants.REF_TYPE_CAMPAIGN : MessageCenterConstants.REF_TYPE_TEMPLATE)
                .templateId(stat.getTemplateId())
                .campaignId(stat.getCampaignId())
                .sceneCode(stat.getSceneCode())
                .messageCategory(stat.getMessageCategory())
                .channelType(stat.getChannelType())
                .statStartDate(startDate)
                .statEndDate(endDate)
                .reachRate(aggregate.getReachRate())
                .clickRate(aggregate.getClickRate())
                .optimizationNote(existing == null ? "系统自动识别为低效消息，请补充优化备注" : existing.getOptimizationNote())
                .nextAction(existing == null ? "复核模板标题、内容与渠道配置" : existing.getNextAction())
                .owner(existing == null ? null : existing.getOwner())
                .deadline(existing == null ? null : existing.getDeadline())
                .build();
        if (existing == null) {
            try {
                messageOptimizationMapper.insert(saveDO);
                existingMap.put(optimizationKey, saveDO);
            } catch (DuplicateKeyException ex) {
                MessageOptimizationDO concurrent = messageOptimizationMapper
                        .selectByOptimizationKeyForUpdate(optimizationKey);
                if (concurrent == null) {
                    throw ex;
                }
                saveDO.setId(concurrent.getId());
                messageOptimizationMapper.updateById(saveDO);
                existingMap.put(optimizationKey, saveDO);
            }
        } else {
            messageOptimizationMapper.updateById(saveDO);
        }
    }

    private boolean needOptimization(BigDecimal reachRate, BigDecimal clickRate) {
        return reachRate.compareTo(new BigDecimal("0.60")) < 0 || clickRate.compareTo(new BigDecimal("0.05")) < 0;
    }

    private String buildReferenceKey(MessageFeedbackStatDO stat) {
        return String.join("|",
                stat.getCampaignId() != null ? MessageCenterConstants.REF_TYPE_CAMPAIGN : MessageCenterConstants.REF_TYPE_TEMPLATE,
                valueOrEmpty(stat.getTemplateId()), valueOrEmpty(stat.getCampaignId()),
                valueOrEmpty(stat.getSceneCode()), valueOrEmpty(stat.getChannelType()));
    }

    private String buildOptimizationKey(MessageFeedbackStatDO stat, LocalDate startDate, LocalDate endDate) {
        return DigestUtil.sha256Hex(buildReferenceKey(stat) + "|" + startDate + "|" + endDate);
    }

    private String valueOrEmpty(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private static class CandidateAggregate {
        private final MessageFeedbackStatDO sample;
        private long plannedCount;
        private long reachedCount;
        private long clickedCount;

        private CandidateAggregate(MessageFeedbackStatDO sample) {
            this.sample = sample;
        }

        private void add(MessageFeedbackStatDO stat) {
            plannedCount += defaultCount(stat.getPlannedAudienceCount());
            reachedCount += defaultCount(stat.getReachedCount());
            clickedCount += defaultCount(stat.getClickedCount());
        }

        private BigDecimal getReachRate() {
            return calculateRate(reachedCount, plannedCount);
        }

        private BigDecimal getClickRate() {
            return calculateRate(clickedCount, reachedCount);
        }

        private static long defaultCount(Integer count) {
            return count == null ? 0L : Math.max(count, 0);
        }

        private static BigDecimal calculateRate(long numerator, long denominator) {
            if (denominator <= 0) {
                return BigDecimal.ZERO;
            }
            return BigDecimal.valueOf(numerator).divide(BigDecimal.valueOf(denominator), 4, RoundingMode.HALF_UP);
        }
    }
}
