package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagetemplate.MessageTemplateDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagetemplate.MessageTemplateMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagepushtask.MessagePushTaskMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_PUSH_TASK_NOT_EXISTS;

@Service
@Validated
public class MessagePushTaskServiceImpl implements MessagePushTaskService {

    @Resource
    private MessagePushTaskMapper messagePushTaskMapper;
    @Resource
    private MessageTemplateMapper messageTemplateMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;

    @Override
    public PageResult<MessagePushTaskRespVO> getPushTaskPage(MessagePushTaskPageReqVO reqVO) {
        PageResult<MessagePushTaskDO> pageResult = messagePushTaskMapper.selectPage(reqVO);
        List<MessagePushTaskRespVO> list = BeanUtils.toBean(pageResult.getList(), MessagePushTaskRespVO.class);
        fillTemplateInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public MessagePushTaskDetailRespVO getPushTaskDetail(Long id) {
        MessagePushTaskDO task = messagePushTaskMapper.selectById(id);
        if (task == null) {
            throw exception(MESSAGE_PUSH_TASK_NOT_EXISTS);
        }
        MessagePushTaskDetailRespVO respVO = BeanUtils.toBean(task, MessagePushTaskDetailRespVO.class);
        MessageTemplateDO template = task.getTemplateId() == null ? null : messageTemplateMapper.selectById(task.getTemplateId());
        if (template != null) {
            respVO.setTemplateName(template.getTemplateName());
        }
        List<MessageRecordDO> records = messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getPushTaskId, id)
                .orderByDesc(MessageRecordDO::getId)
                .last("LIMIT 20"));
        if (CollUtil.isEmpty(records)) {
            respVO.setRecentRecords(Collections.emptyList());
            return respVO;
        }
        java.util.Set<Long> receiverUserIds = convertSet(records, MessageRecordDO::getReceiverUserId, item -> item.getReceiverUserId() != null);
        Map<Long, MemberUserDO> userMap = receiverUserIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(receiverUserIds), MemberUserDO::getId);
        List<MessagePushTaskDetailRespVO.RecordSummary> summaries = records.stream().map(record -> {
            MessagePushTaskDetailRespVO.RecordSummary summary = new MessagePushTaskDetailRespVO.RecordSummary();
            summary.setId(record.getId());
            summary.setReceiverUserId(record.getReceiverUserId());
            MemberUserDO user = userMap.get(record.getReceiverUserId());
            if (user != null) {
                summary.setReceiverUserNo(user.getUserNo());
                summary.setReceiverUserNickname(user.getNickname());
                summary.setReceiverUserMobile(user.getMobile());
            }
            summary.setSendStatus(record.getSendStatus());
            summary.setFailReason(record.getFailReason());
            summary.setSendTime(record.getSendTime());
            return summary;
        }).collect(Collectors.toList());
        respVO.setRecentRecords(summaries);
        return respVO;
    }

    @Override
    public Long createTask(MessagePushTaskDO taskDO) {
        if (taskDO.getSuccessCount() == null) {
            taskDO.setSuccessCount(0);
        }
        if (taskDO.getFailCount() == null) {
            taskDO.setFailCount(0);
        }
        if (taskDO.getPlannedAudienceCount() == null) {
            taskDO.setPlannedAudienceCount(0);
        }
        if (taskDO.getReachedCount() == null) {
            taskDO.setReachedCount(0);
        }
        if (taskDO.getClickedCount() == null) {
            taskDO.setClickedCount(0);
        }
        if (taskDO.getReadCount() == null) {
            taskDO.setReadCount(0);
        }
        if (taskDO.getVoicePlayedCount() == null) {
            taskDO.setVoicePlayedCount(0);
        }
        if (taskDO.getStatus() == null) {
            taskDO.setStatus("PENDING");
        }
        if (taskDO.getExecuteStatus() == null) {
            taskDO.setExecuteStatus(taskDO.getStatus());
        }
        if (taskDO.getPlannedSendTime() == null) {
            taskDO.setPlannedSendTime(LocalDateTime.now());
        }
        messagePushTaskMapper.insert(taskDO);
        return taskDO.getId();
    }

    @Override
    public void updateTaskResult(Long id, String status, int successCount, int failCount) {
        messagePushTaskMapper.updateById(MessagePushTaskDO.builder()
                .id(id)
                .status(status)
                .executeStatus(status)
                .successCount(successCount)
                .failCount(failCount)
                .executeTime(LocalDateTime.now())
                .build());
    }

    @Override
    public void markRetrying(Long id) {
        messagePushTaskMapper.updateById(MessagePushTaskDO.builder()
                .id(id)
                .status("PROCESSING")
                .executeStatus("PROCESSING")
                .executeTime(LocalDateTime.now())
                .build());
    }

    private void fillTemplateInfo(List<MessagePushTaskRespVO> list) {
        java.util.Set<Long> templateIds = convertSet(list, MessagePushTaskRespVO::getTemplateId, item -> item.getTemplateId() != null);
        Map<Long, MessageTemplateDO> templateMap = templateIds.isEmpty() ? Collections.emptyMap()
                : convertMap(messageTemplateMapper.selectBatchIds(templateIds), MessageTemplateDO::getId);
        list.forEach(item -> {
            MessageTemplateDO template = templateMap.get(item.getTemplateId());
            if (template != null) {
                item.setTemplateName(template.getTemplateName());
            }
        });
    }
}
