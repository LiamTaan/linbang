package cn.iocoder.yudao.module.linbang.service.messagecampaign;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagecampaign.MessageCampaignMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_CAMPAIGN_CANCEL_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_CAMPAIGN_DELIVERY_TIME_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_CAMPAIGN_EXECUTE_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MESSAGE_CAMPAIGN_NOT_EXISTS;

@Service
@Validated
@Slf4j
public class MessageCampaignServiceImpl implements MessageCampaignService {

    private static final int SCHEDULED_CAMPAIGN_BATCH_SIZE = 100;
    private static final int FILTER_QUERY_BATCH_SIZE = 500;
    private static final int TARGET_USER_QUERY_BATCH_SIZE = 500;
    private static final long STALE_PROCESSING_HOURS = 2L;

    @Resource
    private MessageCampaignMapper messageCampaignMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public PageResult<MessageCampaignRespVO> getPage(MessageCampaignPageReqVO reqVO) {
        PageResult<MessageCampaignDO> pageResult = messageCampaignMapper.selectPage(reqVO);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), MessageCampaignRespVO.class), pageResult.getTotal());
    }

    @Override
    public MessageCampaignRespVO get(Long id) {
        return BeanUtils.toBean(validateExists(id), MessageCampaignRespVO.class);
    }

    @Override
    public MessageCampaignDO getDO(Long id) {
        return messageCampaignMapper.selectById(id);
    }

    @Override
    public Long create(MessageCampaignSaveReqVO reqVO) {
        MessageCampaignDO campaign = BeanUtils.toBean(reqVO, MessageCampaignDO.class);
        campaign.setAuditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_PENDING);
        campaign.setExecuteStatus(resolveInitialExecuteStatus(reqVO.getScheduleTime()));
        messageCampaignMapper.insert(campaign);
        return campaign.getId();
    }

    @Override
    public Long createUserDirected(Long userId, AppMessageCampaignCreateReqVO reqVO) {
        MessageCampaignDO campaign = MessageCampaignDO.builder()
                .campaignName(reqVO.getCampaignName())
                .sourceType(MessageCenterConstants.CAMPAIGN_SOURCE_USER_DIRECTED)
                .auditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_PENDING)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_PENDING)
                .targetMode(MessageCenterConstants.TARGET_MODE_CUSTOM_FILTER)
                .targetRegionCodes(reqVO.getTargetRegionCodes())
                .targetCategoryIds(reqVO.getTargetCategoryIds())
                .targetRoleCodes(reqVO.getTargetRoleCodes())
                .deliveryTimeWindows(reqVO.getDeliveryTimeWindows())
                .sceneCode(MessageCenterConstants.SCENE_MARKETING_BROADCAST)
                .messageCategory(MessageCenterConstants.CATEGORY_MARKETING)
                .bizType(MessageCenterConstants.BIZ_TYPE_MARKETING)
                .applicantUserId(userId)
                .contentSnapshot(reqVO.getContentSnapshot())
                .build();
        messageCampaignMapper.insert(campaign);
        return campaign.getId();
    }

    @Override
    public PageResult<MessageCampaignRespVO> getAppPage(Long userId, AppMessageCampaignPageReqVO reqVO) {
        PageResult<MessageCampaignDO> pageResult = messageCampaignMapper.selectAppPage(userId, reqVO);
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), MessageCampaignRespVO.class), pageResult.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approve(Long id, String auditRemark) {
        MessageCampaignDO campaign = validateExists(id);
        if (!MessageCenterConstants.CAMPAIGN_AUDIT_PENDING.equals(campaign.getAuditStatus())) {
            throw exception(MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID);
        }
        int updated = messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .set(MessageCampaignDO::getAuditRemark, auditRemark)
                .set(MessageCampaignDO::getAuditUserId, SecurityFrameworkUtils.getLoginUserId())
                .set(MessageCampaignDO::getAuditTime, LocalDateTime.now())
                .eq(MessageCampaignDO::getId, id)
                .eq(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_PENDING));
        if (updated != 1) {
            throw exception(MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID);
        }
    }

    @Override
    public void reject(Long id, String rejectReason) {
        MessageCampaignDO campaign = validateExists(id);
        if (!MessageCenterConstants.CAMPAIGN_AUDIT_PENDING.equals(campaign.getAuditStatus())) {
            throw exception(MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID);
        }
        int updated = messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_REJECTED)
                .set(MessageCampaignDO::getRejectReason, rejectReason)
                .set(MessageCampaignDO::getAuditUserId, SecurityFrameworkUtils.getLoginUserId())
                .set(MessageCampaignDO::getAuditTime, LocalDateTime.now())
                .eq(MessageCampaignDO::getId, id)
                .eq(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_PENDING));
        if (updated != 1) {
            throw exception(MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID);
        }
    }

    @Override
    public void executeNow(Long id) {
        MessageCampaignDO campaign = transactionTemplate.execute(status -> claimCampaign(id));
        if (campaign == null) {
            throw exception(MESSAGE_CAMPAIGN_EXECUTE_STATUS_INVALID);
        }
        try {
            int plannedAudienceCount = dispatchTargetUserBatches(campaign);
            updateCampaignProgress(campaign.getId(), plannedAudienceCount);
            finishCampaignFromDeliveryRecords(campaign.getId());
        } catch (RuntimeException ex) {
            markCampaignFailed(campaign.getId());
            throw ex;
        }
    }

    private MessageCampaignDO claimCampaign(Long id) {
        MessageCampaignDO campaign = validateExists(id);
        if (!MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED.equals(campaign.getAuditStatus())) {
            throw exception(MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID);
        }
        if (!isWithinDeliveryWindow(campaign.getDeliveryTimeWindows(), LocalDateTime.now())) {
            throw exception(MESSAGE_CAMPAIGN_DELIVERY_TIME_INVALID);
        }
        int claimed = messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .set(MessageCampaignDO::getExecuteTime, LocalDateTime.now())
                .eq(MessageCampaignDO::getId, id)
                .eq(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .and(wrapper -> wrapper
                        .in(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PENDING,
                                MessageCenterConstants.EXECUTE_STATUS_FAILED)
                        .or(legacy -> legacy
                                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                                .isNull(MessageCampaignDO::getExecuteTime))));
        if (claimed != 1) {
            throw exception(MESSAGE_CAMPAIGN_EXECUTE_STATUS_INVALID);
        }
        campaign.setExecuteStatus(MessageCenterConstants.EXECUTE_STATUS_PROCESSING);
        campaign.setExecuteTime(LocalDateTime.now());
        return campaign;
    }

    @Override
    public int executeScheduledCampaigns() {
        recoverStaleProcessingCampaigns();
        List<MessageCampaignDO> campaigns = messageCampaignMapper.selectList(new LambdaQueryWrapperX<MessageCampaignDO>()
                .eq(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PENDING)
                .isNotNull(MessageCampaignDO::getScheduleTime)
                .le(MessageCampaignDO::getScheduleTime, LocalDateTime.now())
                .orderByAsc(MessageCampaignDO::getScheduleTime)
                .orderByAsc(MessageCampaignDO::getId)
                .last("LIMIT " + SCHEDULED_CAMPAIGN_BATCH_SIZE));
        int executedCount = 0;
        for (MessageCampaignDO campaign : campaigns) {
            if (!isWithinDeliveryWindow(campaign.getDeliveryTimeWindows(), LocalDateTime.now())) {
                continue;
            }
            try {
                executeNow(campaign.getId());
                executedCount++;
            } catch (RuntimeException ex) {
                log.warn("[executeScheduledCampaigns][campaignId={}] execution failed: {}",
                        campaign.getId(), ex.getClass().getSimpleName());
            }
        }
        return executedCount;
    }

    @Override
    public void cancel(Long id, String reason) {
        MessageCampaignDO campaign = validateExists(id);
        if (!(MessageCenterConstants.EXECUTE_STATUS_PENDING.equals(campaign.getExecuteStatus())
                || MessageCenterConstants.EXECUTE_STATUS_FAILED.equals(campaign.getExecuteStatus()))) {
            throw exception(MESSAGE_CAMPAIGN_CANCEL_STATUS_INVALID);
        }
        int updated = messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_CANCELLED)
                .set(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_CANCELLED)
                .set(MessageCampaignDO::getCancelReason, reason)
                .eq(MessageCampaignDO::getId, id)
                .in(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PENDING,
                        MessageCenterConstants.EXECUTE_STATUS_FAILED));
        if (updated != 1) {
            throw exception(MESSAGE_CAMPAIGN_CANCEL_STATUS_INVALID);
        }
    }

    @Override
    public void withdrawByUser(Long userId, Long id) {
        MessageCampaignDO campaign = validateExists(id);
        if (!userId.equals(campaign.getApplicantUserId())) {
            throw exception(MESSAGE_CAMPAIGN_NOT_EXISTS);
        }
        if (!MessageCenterConstants.CAMPAIGN_AUDIT_PENDING.equals(campaign.getAuditStatus())) {
            throw exception(MESSAGE_CAMPAIGN_CANCEL_STATUS_INVALID);
        }
        int updated = messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_CANCELLED)
                .set(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_CANCELLED)
                .set(MessageCampaignDO::getCancelReason, "用户撤回定向推送申请")
                .eq(MessageCampaignDO::getId, id)
                .eq(MessageCampaignDO::getApplicantUserId, userId)
                .eq(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_PENDING)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PENDING));
        if (updated != 1) {
            throw exception(MESSAGE_CAMPAIGN_CANCEL_STATUS_INVALID);
        }
    }

    private MessageCampaignDO validateExists(Long id) {
        MessageCampaignDO campaign = messageCampaignMapper.selectById(id);
        if (campaign == null) {
            throw exception(MESSAGE_CAMPAIGN_NOT_EXISTS);
        }
        return campaign;
    }

    private String resolveInitialExecuteStatus(LocalDateTime scheduleTime) {
        return MessageCenterConstants.EXECUTE_STATUS_PENDING;
    }

    private int dispatchTargetUserBatches(MessageCampaignDO campaign) {
        long lastUserId = 0L;
        int plannedAudienceCount = 0;
        while (true) {
            List<MemberUserDO> users = selectTargetUserBatch(campaign, lastUserId);
            if (CollUtil.isEmpty(users)) {
                break;
            }
            lastUserId = users.get(users.size() - 1).getId();
            List<Long> targetUserIds = filterTargetUserIds(campaign, users);
            if (CollUtil.isNotEmpty(targetUserIds)) {
                List<cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget> targets =
                        new ArrayList<>(targetUserIds.size());
                for (Long userId : targetUserIds) {
                    String dedupeKey = "campaign:" + campaign.getId() + ":user:" + userId;
                    targets.add(new cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget(
                            userId, campaign.getBizId(), dedupeKey));
                }
                messagePushDispatchService.dispatchCampaign(campaign, "消息投放活动执行", targets);
                plannedAudienceCount += targetUserIds.size();
                updateCampaignProgress(campaign.getId(), plannedAudienceCount);
            }
            if (users.size() < TARGET_USER_QUERY_BATCH_SIZE) {
                break;
            }
        }
        return plannedAudienceCount;
    }

    private List<MemberUserDO> selectTargetUserBatch(MessageCampaignDO campaign, long lastUserId) {
        LambdaQueryWrapperX<MemberUserDO> userQuery = new LambdaQueryWrapperX<>();
        userQuery.select(MemberUserDO::getId);
        userQuery.eq(MemberUserDO::getStatus, "ENABLE")
                .gt(MemberUserDO::getId, lastUserId)
                .orderByAsc(MemberUserDO::getId)
                .last("LIMIT " + TARGET_USER_QUERY_BATCH_SIZE);
        if (StrUtil.isNotBlank(campaign.getTargetRoleCodes())) {
            userQuery.in(MemberUserDO::getCurrentRoleCode, splitToSet(campaign.getTargetRoleCodes()));
        }
        return memberUserMapper.selectList(userQuery);
    }

    private List<Long> filterTargetUserIds(MessageCampaignDO campaign, List<MemberUserDO> users) {
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        Set<Long> result = users.stream().map(MemberUserDO::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        if (MessageCenterConstants.TARGET_MODE_FULL_PLATFORM.equals(campaign.getTargetMode())) {
            return new ArrayList<>(result);
        }

        if (StrUtil.isNotBlank(campaign.getTargetRegionCodes())) {
            Set<String> regionCodes = splitToSet(campaign.getTargetRegionCodes());
            Map<Long, String> userRegionMap = buildUserRegionMap(result);
            result.removeIf(userId -> !regionCodes.contains(userRegionMap.get(userId)));
        }

        if (StrUtil.isNotBlank(campaign.getTargetCategoryIds())) {
            Set<Long> categoryIds = splitToLongSet(campaign.getTargetCategoryIds());
            Map<Long, Set<Long>> userCategoryMap = buildUserCategoryMap(result);
            result.removeIf(userId -> CollUtil.intersectionDistinct(userCategoryMap.getOrDefault(userId, Collections.emptySet()), categoryIds).isEmpty());
        }
        return new ArrayList<>(result);
    }

    private void updateCampaignProgress(Long campaignId, int plannedAudienceCount) {
        messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getPlannedAudienceCount, plannedAudienceCount)
                .eq(MessageCampaignDO::getId, campaignId)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PROCESSING));
    }

    private void finishCampaignFromDeliveryRecords(Long campaignId) {
        long successCount = messageRecordMapper.selectCount(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getCampaignId, campaignId)
                .eq(MessageRecordDO::getSendStatus, "SUCCESS"));
        long failedOrPendingCount = messageRecordMapper.selectCount(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getCampaignId, campaignId)
                .in(MessageRecordDO::getSendStatus, "FAILED", "PENDING"));
        String executeStatus = MessageCenterConstants.EXECUTE_STATUS_SUCCESS;
        if (failedOrPendingCount > 0) {
            executeStatus = successCount > 0 ? MessageCenterConstants.EXECUTE_STATUS_PARTIAL_FAILED
                    : MessageCenterConstants.EXECUTE_STATUS_FAILED;
        }
        messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getExecuteStatus, executeStatus)
                .set(MessageCampaignDO::getExecuteTime, LocalDateTime.now())
                .eq(MessageCampaignDO::getId, campaignId)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PROCESSING));
    }

    private void markCampaignFailed(Long campaignId) {
        messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_FAILED)
                .eq(MessageCampaignDO::getId, campaignId)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PROCESSING));
    }

    private void recoverStaleProcessingCampaigns() {
        messageCampaignMapper.update(null, new LambdaUpdateWrapper<MessageCampaignDO>()
                .set(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_FAILED)
                .eq(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .le(MessageCampaignDO::getUpdateTime, LocalDateTime.now().minusHours(STALE_PROCESSING_HOURS)));
    }

    private Map<Long, String> buildUserRegionMap(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        Map<Long, String> result = new java.util.LinkedHashMap<>();
        List<Long> idList = new ArrayList<>(userIds);
        for (int fromIndex = 0; fromIndex < idList.size(); fromIndex += FILTER_QUERY_BATCH_SIZE) {
            List<Long> idBatch = idList.subList(fromIndex, Math.min(fromIndex + FILTER_QUERY_BATCH_SIZE, idList.size()));
            List<MerchantEntryDO> entries = merchantEntryMapper.selectList(new LambdaQueryWrapperX<MerchantEntryDO>()
                    .in(MerchantEntryDO::getUserId, idBatch)
                    .orderByDesc(MerchantEntryDO::getId));
            for (MerchantEntryDO entry : entries) {
                if (StrUtil.isNotBlank(entry.getRegionCode())) {
                    result.putIfAbsent(entry.getUserId(), entry.getRegionCode());
                }
            }
        }
        return result;
    }

    private Map<Long, Set<Long>> buildUserCategoryMap(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<MerchantInfoDO> merchants = new ArrayList<>();
        List<Long> userIdList = new ArrayList<>(userIds);
        for (int fromIndex = 0; fromIndex < userIdList.size(); fromIndex += FILTER_QUERY_BATCH_SIZE) {
            List<Long> idBatch = userIdList.subList(fromIndex,
                    Math.min(fromIndex + FILTER_QUERY_BATCH_SIZE, userIdList.size()));
            merchants.addAll(merchantInfoMapper.selectList(new LambdaQueryWrapperX<MerchantInfoDO>()
                    .in(MerchantInfoDO::getUserId, idBatch)));
        }
        if (CollUtil.isEmpty(merchants)) {
            return Collections.emptyMap();
        }
        Map<Long, MerchantInfoDO> merchantByUserId = convertMap(merchants, MerchantInfoDO::getUserId);
        Set<Long> merchantIds = convertSet(merchants, MerchantInfoDO::getId);
        List<MerchantCategoryRelDO> rels = new ArrayList<>();
        List<Long> merchantIdList = new ArrayList<>(merchantIds);
        for (int fromIndex = 0; fromIndex < merchantIdList.size(); fromIndex += FILTER_QUERY_BATCH_SIZE) {
            List<Long> idBatch = merchantIdList.subList(fromIndex,
                    Math.min(fromIndex + FILTER_QUERY_BATCH_SIZE, merchantIdList.size()));
            rels.addAll(merchantCategoryRelMapper.selectList(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                    .in(MerchantCategoryRelDO::getMerchantId, idBatch)));
        }
        Map<Long, Set<Long>> merchantCategoryMap = rels.stream().collect(Collectors.groupingBy(MerchantCategoryRelDO::getMerchantId,
                Collectors.mapping(MerchantCategoryRelDO::getCategoryId, Collectors.toCollection(LinkedHashSet::new))));
        return userIds.stream().collect(Collectors.toMap(userId -> userId,
                userId -> {
                    MerchantInfoDO merchant = merchantByUserId.get(userId);
                    return merchant == null ? Collections.emptySet() : merchantCategoryMap.getOrDefault(merchant.getId(), Collections.emptySet());
                }));
    }

    private Set<String> splitToSet(String raw) {
        return StrUtil.splitTrim(raw, ',').stream()
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<Long> splitToLongSet(String raw) {
        return StrUtil.splitTrim(raw, ',').stream()
                .filter(StrUtil::isNotBlank)
                .map(Long::valueOf)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean isWithinDeliveryWindow(String deliveryTimeWindows, LocalDateTime now) {
        if (StrUtil.isBlank(deliveryTimeWindows)) {
            return true;
        }
        LocalTime currentTime = now.toLocalTime();
        List<String> windows = StrUtil.splitTrim(deliveryTimeWindows, ',');
        for (String window : windows) {
            if (StrUtil.isBlank(window)) {
                continue;
            }
            List<String> pair = StrUtil.splitTrim(window, '-');
            if (pair.size() != 2) {
                continue;
            }
            LocalTime start = parseTime(pair.get(0));
            LocalTime end = parseTime(pair.get(1));
            if (start == null || end == null) {
                continue;
            }
            if (!currentTime.isBefore(start) && !currentTime.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    private LocalTime parseTime(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        String normalized = text.trim();
        if (normalized.length() == 5) {
            return LocalTime.parse(normalized);
        }
        if (normalized.length() == 8) {
            return LocalTime.parse(normalized);
        }
        return null;
    }
}
