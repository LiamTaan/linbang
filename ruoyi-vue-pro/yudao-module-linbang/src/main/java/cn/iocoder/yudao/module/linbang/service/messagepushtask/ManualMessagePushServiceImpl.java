package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.hutool.core.util.StrUtil;
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

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_SEND_CONTENT_BLOCKED;

@Service
@Validated
public class ManualMessagePushServiceImpl implements ManualMessagePushService {

    private static final String DEFAULT_BIZ_TYPE = "ADMIN_MANUAL_NOTICE";
    private static final String DEFAULT_TARGET_SCOPE = "SINGLE_USER";

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
        MemberUserDO receiverUser = memberUserMapper.selectById(reqVO.getReceiverUserId());
        if (receiverUser == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        String bizType = StrUtil.blankToDefault(reqVO.getBizType(), DEFAULT_BIZ_TYPE);
        String title = sanitizeContent(reqVO.getTitle(), bizType + "_TITLE");
        String content = sanitizeContent(reqVO.getContent(), bizType);
        Long pushTaskId = messagePushTaskService.createTask(MessagePushTaskDO.builder()
                .taskName(StrUtil.maxLength(title, 128))
                .sceneCode(MessageCenterConstants.SCENE_SYSTEM_NOTICE)
                .messageCategory(MessageCenterConstants.CATEGORY_SYSTEM)
                .targetScope(DEFAULT_TARGET_SCOPE)
                .channelType(MessageCenterConstants.CHANNEL_APP_POPUP)
                .bizType(bizType)
                .plannedSendTime(LocalDateTime.now())
                .status(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .plannedAudienceCount(1)
                .creatorRemark(StrUtil.maxLength(content, 255))
                .build());
        MessageRecordDO record = MessageRecordDO.builder()
                .pushTaskId(pushTaskId)
                .receiverUserId(reqVO.getReceiverUserId())
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
        messagePushTaskService.updateTaskResult(pushTaskId, MessageCenterConstants.EXECUTE_STATUS_SUCCESS, 1, 0);
        messageFeedbackStatService.refreshByRecord(record);
        return pushTaskId;
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
