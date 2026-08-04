package cn.iocoder.yudao.module.linbang.service.messagepushtask;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
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
    private static final int USER_BATCH_SIZE = 500;
    private static final long MAX_MANUAL_AUDIENCE = 10_000L;

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
    public Long manualSend(MessagePushTaskManualSendReqVO reqVO) {
        validateReq(reqVO);
        String bizType = StrUtil.blankToDefault(reqVO.getBizType(), DEFAULT_BIZ_TYPE);
        String targetScope = resolveTargetScope(reqVO);
        String title = sanitizeContent(reqVO.getTitle(), bizType + "_TITLE");
        String content = sanitizeContent(reqVO.getContent(), bizType);
        MemberUserDO receiverUser = resolveSingleReceiver(reqVO, targetScope);
        long audienceCount = TARGET_SCOPE_ALL_USERS.equals(targetScope)
                ? memberUserMapper.selectCount(MemberUserDO::getStatus, "ENABLE") : 1L;
        if (audienceCount > MAX_MANUAL_AUDIENCE) {
            throw new ServiceException(400, "手动同步群发最多支持 10000 人，请改用投放活动分批执行");
        }
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
                .plannedAudienceCount((int) Math.min(audienceCount, Integer.MAX_VALUE))
                .creatorRemark(StrUtil.maxLength(content, 255))
                .build());
        int successCount = 0;
        MessageRecordDO lastRecord = null;
        try {
            if (TARGET_SCOPE_ALL_USERS.equals(targetScope)) {
                Long lastUserId = null;
                while (true) {
                    List<MemberUserDO> userBatch = selectNextUserBatch(lastUserId);
                    if (userBatch.isEmpty()) {
                        break;
                    }
                    for (MemberUserDO user : userBatch) {
                        lastRecord = insertMessageRecord(pushTaskId, user.getId(), bizType, title, content, reqVO);
                        successCount++;
                    }
                    lastUserId = userBatch.get(userBatch.size() - 1).getId();
                }
            } else {
                lastRecord = insertMessageRecord(pushTaskId, receiverUser.getId(), bizType, title, content, reqVO);
                successCount++;
            }
        } catch (RuntimeException ex) {
            messagePushTaskService.updateTaskResult(pushTaskId,
                    resolveTaskStatus(successCount, 1), successCount, 1);
            throw ex;
        }
        messagePushTaskService.updateTaskResult(pushTaskId, MessageCenterConstants.EXECUTE_STATUS_SUCCESS, successCount, 0);
        if (lastRecord != null) {
            messageFeedbackStatService.refreshByRecord(lastRecord);
        }
        return pushTaskId;
    }

    private List<MemberUserDO> selectNextUserBatch(Long lastUserId) {
        LambdaQueryWrapperX<MemberUserDO> query = new LambdaQueryWrapperX<>();
        query.select(MemberUserDO::getId);
        query.eq(MemberUserDO::getStatus, "ENABLE");
        if (lastUserId != null) {
            query.gt(MemberUserDO::getId, lastUserId);
        }
        query.orderByAsc(MemberUserDO::getId).last("LIMIT " + USER_BATCH_SIZE);
        return memberUserMapper.selectList(query);
    }

    private MessageRecordDO insertMessageRecord(Long pushTaskId, Long receiverUserId, String bizType,
                                                String title, String content,
                                                MessagePushTaskManualSendReqVO reqVO) {
        MessageRecordDO record = MessageRecordDO.builder()
                .pushTaskId(pushTaskId)
                .receiverUserId(receiverUserId)
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
        return record;
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
        String scope = StrUtil.blankToDefault(reqVO.getReceiverScope(), DEFAULT_TARGET_SCOPE).trim().toUpperCase();
        if (!DEFAULT_TARGET_SCOPE.equals(scope) && !TARGET_SCOPE_ALL_USERS.equals(scope)) {
            throw new ServiceException(400, "接收范围仅支持 SINGLE_USER 或 ALL_USERS");
        }
        return scope;
    }

    private MemberUserDO resolveSingleReceiver(MessagePushTaskManualSendReqVO reqVO, String targetScope) {
        if (TARGET_SCOPE_ALL_USERS.equals(targetScope)) {
            return null;
        }
        if (reqVO.getReceiverUserId() == null) {
            throw new ServiceException(400, "请选择接收用户");
        }
        MemberUserDO receiverUser = memberUserMapper.selectById(reqVO.getReceiverUserId());
        if (receiverUser == null) {
            throw exception(MEMBER_USER_NOT_EXISTS);
        }
        return receiverUser;
    }

    private String resolveTaskStatus(int successCount, int failCount) {
        if (successCount > 0 && failCount > 0) {
            return MessageCenterConstants.EXECUTE_STATUS_PARTIAL_FAILED;
        }
        return failCount > 0 ? MessageCenterConstants.EXECUTE_STATUS_FAILED
                : MessageCenterConstants.EXECUTE_STATUS_SUCCESS;
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
