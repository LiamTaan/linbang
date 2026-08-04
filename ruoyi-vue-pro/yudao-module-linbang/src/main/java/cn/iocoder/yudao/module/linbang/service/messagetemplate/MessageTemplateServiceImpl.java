package cn.iocoder.yudao.module.linbang.service.messagetemplate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplatePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagetemplate.vo.MessageTemplateSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_TEMPLATE_NOT_EXISTS;

@Service
@Validated
public class MessageTemplateServiceImpl implements MessageTemplateService {

    @Resource
    private MessageTemplateMapper messageTemplateMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<MessageTemplateDO> getMessageTemplatePage(MessageTemplatePageReqVO reqVO) {
        return messageTemplateMapper.selectPage(reqVO);
    }

    @Override
    public Long createMessageTemplate(MessageTemplateSaveReqVO reqVO) {
        validateTemplateBusinessRules(reqVO);
        MessageTemplateDO template = BeanUtils.toBean(reqVO, MessageTemplateDO.class);
        messageTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updateMessageTemplate(MessageTemplateSaveReqVO reqVO) {
        validateMessageTemplateExists(reqVO.getId());
        validateTemplateBusinessRules(reqVO);
        messageTemplateMapper.updateById(BeanUtils.toBean(reqVO, MessageTemplateDO.class));
    }

    @Override
    public MessageTemplateDO getMessageTemplate(Long id) {
        return messageTemplateMapper.selectById(id);
    }

    @Override
    public MessageTemplateDetailRespVO getMessageTemplateDetail(Long id) {
        MessageTemplateDO template = messageTemplateMapper.selectById(id);
        if (template == null) {
            throw exception(MESSAGE_TEMPLATE_NOT_EXISTS);
        }

        List<MessageRecordDO> recentRecords = messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getTemplateId, id)
                .orderByDesc(MessageRecordDO::getId)
                .last("LIMIT 10"));

        int sendCount = countRecords(id, null, null);
        int successCount = countRecords(id, "SUCCESS", null);
        int failedCount = countRecords(id, "FAILED", null);
        int pendingCount = Math.max(0, sendCount - successCount - failedCount);
        Map<String, Long> channelCountMap = new LinkedHashMap<>();
        for (String channelType : java.util.Arrays.asList(
                MessageCenterConstants.CHANNEL_APP_POPUP,
                MessageCenterConstants.CHANNEL_WECHAT_MP_TEMPLATE,
                MessageCenterConstants.CHANNEL_SMS,
                MessageCenterConstants.CHANNEL_APP_VOICE)) {
            int channelCount = countRecords(id, null, channelType);
            if (channelCount > 0) {
                channelCountMap.put(channelType, (long) channelCount);
            }
        }

        MessageTemplateDetailRespVO respVO = BeanUtils.toBean(template, MessageTemplateDetailRespVO.class);
        java.util.Set<Long> receiverUserIds = convertSet(recentRecords, MessageRecordDO::getReceiverUserId,
                item -> item.getReceiverUserId() != null);
        Map<Long, MemberUserDO> userMap = receiverUserIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(receiverUserIds), MemberUserDO::getId);
        respVO.setSendCount(sendCount);
        respVO.setSuccessCount(successCount);
        respVO.setFailedCount(failedCount);
        respVO.setPendingCount(pendingCount);
        respVO.setChannelStats(channelCountMap.isEmpty()
                ? Collections.emptyList()
                : MessageTemplateDetailAssembler.buildChannelStats(channelCountMap));
        respVO.setRecentRecords(recentRecords.isEmpty()
                ? Collections.emptyList()
                : MessageTemplateDetailAssembler.buildRecords(recentRecords, userMap));
        return respVO;
    }

    private int countRecords(Long templateId, String sendStatus, String channelType) {
        long count = messageRecordMapper.selectCount(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getTemplateId, templateId)
                .eqIfPresent(MessageRecordDO::getSendStatus, sendStatus)
                .eqIfPresent(MessageRecordDO::getChannelType, channelType));
        return (int) Math.min(count, Integer.MAX_VALUE);
    }

    private void validateMessageTemplateExists(Long id) {
        if (id == null || messageTemplateMapper.selectById(id) == null) {
            throw exception(MESSAGE_TEMPLATE_NOT_EXISTS);
        }
    }

    private void validateTemplateBusinessRules(MessageTemplateSaveReqVO reqVO) {
        if (reqVO.getSceneCode() == null || !MessageCenterConstants.FINANCE_SCENES.contains(reqVO.getSceneCode())) {
            return;
        }
        if (MessageCenterConstants.CHANNEL_SMS.equals(reqVO.getChannelType())
                && "DISABLE".equalsIgnoreCase(reqVO.getStatus())) {
            throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_FINANCE_SMS_REQUIRED);
        }
        if (!MessageCenterConstants.CHANNEL_SMS.equals(reqVO.getChannelType())) {
            List<MessageTemplateDO> siblings = messageTemplateMapper.selectList(new LambdaQueryWrapperX<MessageTemplateDO>()
                    .eq(MessageTemplateDO::getSceneCode, reqVO.getSceneCode())
                    .eq(MessageTemplateDO::getStatus, "ENABLE"));
            boolean hasSms = siblings.stream().anyMatch(item ->
                    !java.util.Objects.equals(item.getId(), reqVO.getId())
                            && MessageCenterConstants.CHANNEL_SMS.equals(item.getChannelType()));
            if (!hasSms) {
                throw exception(cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_FINANCE_SMS_REQUIRED);
            }
        }
    }
}
