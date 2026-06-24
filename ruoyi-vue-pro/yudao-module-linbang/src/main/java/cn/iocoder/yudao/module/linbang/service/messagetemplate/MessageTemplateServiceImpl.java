package cn.iocoder.yudao.module.linbang.service.messagetemplate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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
        MessageTemplateDO template = BeanUtils.toBean(reqVO, MessageTemplateDO.class);
        messageTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updateMessageTemplate(MessageTemplateSaveReqVO reqVO) {
        validateMessageTemplateExists(reqVO.getId());
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

        List<MessageRecordDO> allRecords = messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getTemplateId, id)
                .orderByDesc(MessageRecordDO::getId));
        List<MessageRecordDO> recentRecords = allRecords.size() > 10 ? allRecords.subList(0, 10) : allRecords;

        int successCount = 0;
        int failedCount = 0;
        int pendingCount = 0;
        Map<String, Long> channelCountMap = new LinkedHashMap<>();
        for (MessageRecordDO record : allRecords) {
            if ("SUCCESS".equalsIgnoreCase(record.getSendStatus())) {
                successCount++;
            } else if ("FAILED".equalsIgnoreCase(record.getSendStatus())) {
                failedCount++;
            } else {
                pendingCount++;
            }
            channelCountMap.merge(record.getChannelType(), 1L, Long::sum);
        }

        MessageTemplateDetailRespVO respVO = BeanUtils.toBean(template, MessageTemplateDetailRespVO.class);
        java.util.Set<Long> receiverUserIds = convertSet(recentRecords, MessageRecordDO::getReceiverUserId,
                item -> item.getReceiverUserId() != null);
        Map<Long, MemberUserDO> userMap = receiverUserIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(receiverUserIds), MemberUserDO::getId);
        respVO.setSendCount(allRecords.size());
        respVO.setSuccessCount(successCount);
        respVO.setFailedCount(failedCount);
        respVO.setPendingCount(pendingCount);
        respVO.setChannelStats(channelCountMap.isEmpty()
                ? Collections.emptyList()
                : MessageTemplateDetailAssembler.buildChannelStats(channelCountMap));
        respVO.setRecentRecords(allRecords.isEmpty()
                ? Collections.emptyList()
                : MessageTemplateDetailAssembler.buildRecords(recentRecords, userMap));
        return respVO;
    }

    private void validateMessageTemplateExists(Long id) {
        if (id == null || messageTemplateMapper.selectById(id) == null) {
            throw exception(MESSAGE_TEMPLATE_NOT_EXISTS);
        }
    }
}
