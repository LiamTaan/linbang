package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.module.linbang.constants.LinbangRiskConstants;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.messagepushtask.vo.MessagePushTaskManualSendReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagepushtask.MessagePushTaskDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.service.messagefeedback.MessageFeedbackStatService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveContentDetectService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveDetectResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_SEND_CONTENT_BLOCKED;

@Service
@Validated
public class ManualMessagePushServiceImpl implements ManualMessagePushService {

    private static final String DEFAULT_BIZ_TYPE = "ADMIN_MANUAL_NOTICE";
    private static final String DEFAULT_TARGET_SCOPE = "SINGLE_USER";
    private static final String TARGET_SCOPE_ALL_USERS = "ALL_USERS";

    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MessagePushTaskService messagePushTaskService;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessageFeedbackStatService messageFeedbackStatService;
    @Resource
    private SensitiveContentDetectService sensitiveContentDetectService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long manualSend(MessagePushTaskManualSendReqVO reqVO) {
        validateReq(reqVO);
        String bizType = StrUtil.blankToDefault(reqVO.getBizType(), DEFAULT_BIZ_TYPE);
        String targetScope = resolveTargetScope(reqVO);
        String title = sanitizeContent(reqVO.getTitle(), bizType + "_TITLE");
        String content = sanitizeContent(reqVO.getContent(), bizType);
        List<MemberUserDO> receiverUsers = resolveReceiverUsers(reqVO, targetScope);
        Long pushTaskId = messagePushTaskService.createTask(MessagePushTaskDO.builder()
                .taskName(StrUtil.maxLength(title, 128))
                .sceneCode(MessageCenterConstants.SCENE_SYSTEM_NOTICE)
                .messageCategory(MessageCenterConstants.CATEGORY_SYSTEM)
                .targetScope(targetScope)
                .channelType(MessageCenterConstants.CHANNEL_APP_POPUP)
                .bizType(bizType)
                .plannedSendTime(LocalDateTime.now())
                .status(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .plannedAudienceCount(receiverUsers.size())
                .creatorRemark(StrUtil.maxLength(content, 255))
                .build());
        int successCount = 0;
        for (MemberUserDO receiverUser : receiverUsers) {
            MessageRecordDO record = MessageRecordDO.builder()
                    .pushTaskId(pushTaskId)
                    .receiverUserId(receiverUser.getId())
                    .sceneCode(MessageCenterConstants.SCENE_SYSTEM_NOTICE)
                    .messageCategory(MessageCenterConstants.CATEGORY_SYSTEM)
                    .channelType(MessageCenterConstants.CHANNEL_APP_POPUP)
                    .bizType(bizType)
                    .sendStatus("SUCCESS")
                    .sendTime(LocalDateTime.now())
                    .title(title)
                    .contentSnapshot(content)
                    .routeType(reqVO.getRouteType())
                    .routeValue(reqVO.getRouteValue())
                    .readStatus(MessageCenterConstants.READ_STATUS_UNREAD)
                    .build();
            messageRecordMapper.insert(record);
            messageFeedbackStatService.refreshByRecord(record);
            successCount++;
        }
        messagePushTaskService.updateTaskResult(pushTaskId, MessageCenterConstants.EXECUTE_STATUS_SUCCESS, successCount, 0);
        return pushTaskId;
    }

    private void validateReq(MessagePushTaskManualSendReqVO reqVO) {
        if (StrUtil.isBlank(reqVO.getTitle())) {
            throw new ServiceException(400, "消息标题不能为空");
        }
        if (StrUtil.isBlank(reqVO.getContent())) {
            throw new ServiceException(400, "消息内容不能为空");
        }
    }

    private String resolveTargetScope(MessagePushTaskManualSendReqVO reqVO) {
        return TARGET_SCOPE_ALL_USERS.equalsIgnoreCase(reqVO.getReceiverScope()) ? TARGET_SCOPE_ALL_USERS : DEFAULT_TARGET_SCOPE;
    }

    private List<MemberUserDO> resolveReceiverUsers(MessagePushTaskManualSendReqVO reqVO, String targetScope) {
        if (TARGET_SCOPE_ALL_USERS.equals(targetScope)) {
            return memberUserMapper.selectList();
        }
        if (reqVO.getReceiverUserId() == null) {
            throw new ServiceException(400, "请选择接收用户");
        }
        MemberUserDO receiverUser = memberUserMapper.selectById(reqVO.getReceiverUserId());
        if (receiverUser == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        return java.util.Collections.singletonList(receiverUser);
    }

    private String sanitizeContent(String content, String bizType) {
        SensitiveDetectResult detectResult = sensitiveContentDetectService.detect(
                LinbangRiskConstants.SCENE_MESSAGE, getLoginUserId(), bizType, null, content);
        if (detectResult.isHit() && LinbangRiskConstants.SENSITIVE_STRATEGY_BLOCK.equals(detectResult.getStrategy())) {
            throw exception(MESSAGE_SEND_CONTENT_BLOCKED);
        }
        return StrUtil.blankToDefault(detectResult.getProcessedContent(), content);
    }
}
