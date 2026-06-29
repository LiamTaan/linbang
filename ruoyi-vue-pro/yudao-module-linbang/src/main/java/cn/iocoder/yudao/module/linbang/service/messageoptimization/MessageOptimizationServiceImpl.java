package cn.iocoder.yudao.module.linbang.service.messageoptimization;

import cn.hutool.core.collection.CollUtil;
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
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_OPTIMIZATION_NOT_EXISTS;

@Service
@Validated
public class MessageOptimizationServiceImpl implements MessageOptimizationService {

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
        List<MessageFeedbackStatDO> stats = messageFeedbackStatMapper.selectList();
        if (CollUtil.isEmpty(stats)) {
            return 0;
        }
        int refreshed = 0;
        for (MessageFeedbackStatDO stat : stats) {
            if (stat.getStatDate() == null || stat.getStatDate().isBefore(startDate) || stat.getStatDate().isAfter(endDate)) {
                continue;
            }
            if (!needOptimization(stat)) {
                continue;
            }
            MessageOptimizationDO existing = messageOptimizationMapper.selectOne(new LambdaQueryWrapperX<MessageOptimizationDO>()
                    .eq(MessageOptimizationDO::getRefType, stat.getCampaignId() != null ? MessageCenterConstants.REF_TYPE_CAMPAIGN : MessageCenterConstants.REF_TYPE_TEMPLATE)
                    .eq(MessageOptimizationDO::getSceneCode, stat.getSceneCode())
                    .eq(MessageOptimizationDO::getChannelType, stat.getChannelType())
                    .eqIfPresent(MessageOptimizationDO::getTemplateId, stat.getTemplateId())
                    .eqIfPresent(MessageOptimizationDO::getCampaignId, stat.getCampaignId())
                    .eq(MessageOptimizationDO::getStatStartDate, startDate)
                    .eq(MessageOptimizationDO::getStatEndDate, endDate)
                    .last("LIMIT 1"));
            MessageOptimizationDO saveDO = MessageOptimizationDO.builder()
                    .id(existing == null ? null : existing.getId())
                    .refType(stat.getCampaignId() != null ? MessageCenterConstants.REF_TYPE_CAMPAIGN : MessageCenterConstants.REF_TYPE_TEMPLATE)
                    .templateId(stat.getTemplateId())
                    .campaignId(stat.getCampaignId())
                    .sceneCode(stat.getSceneCode())
                    .messageCategory(stat.getMessageCategory())
                    .channelType(stat.getChannelType())
                    .statStartDate(startDate)
                    .statEndDate(endDate)
                    .reachRate(stat.getReachRate())
                    .clickRate(stat.getClickRate())
                    .optimizationNote(existing == null ? "系统自动识别为低效消息，请补充优化备注" : existing.getOptimizationNote())
                    .nextAction(existing == null ? "复核模板标题、内容与渠道配置" : existing.getNextAction())
                    .owner(existing == null ? null : existing.getOwner())
                    .deadline(existing == null ? null : existing.getDeadline())
                    .build();
            if (existing == null) {
                messageOptimizationMapper.insert(saveDO);
            } else {
                messageOptimizationMapper.updateById(saveDO);
            }
            refreshed++;
        }
        return refreshed;
    }

    private boolean needOptimization(MessageFeedbackStatDO stat) {
        BigDecimal reachRate = stat.getReachRate() == null ? BigDecimal.ZERO : stat.getReachRate();
        BigDecimal clickRate = stat.getClickRate() == null ? BigDecimal.ZERO : stat.getClickRate();
        return reachRate.compareTo(new BigDecimal("0.60")) < 0 || clickRate.compareTo(new BigDecimal("0.05")) < 0;
    }
}
