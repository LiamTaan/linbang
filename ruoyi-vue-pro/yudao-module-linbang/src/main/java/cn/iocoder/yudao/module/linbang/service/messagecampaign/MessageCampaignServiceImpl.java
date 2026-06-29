package cn.iocoder.yudao.module.linbang.service.messagecampaign;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.messagecampaign.vo.MessageCampaignSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.message.vo.AppMessageCampaignPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagecampaign.MessageCampaignDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagecampaign.MessageCampaignMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class MessageCampaignServiceImpl implements MessageCampaignService {

    @Resource
    private MessageCampaignMapper messageCampaignMapper;
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
        campaign.setAuditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED);
        campaign.setExecuteStatus(resolveInitialExecuteStatus(reqVO.getScheduleTime()));
        campaign.setAuditUserId(SecurityFrameworkUtils.getLoginUserId());
        campaign.setAuditTime(LocalDateTime.now());
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
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(id)
                .auditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .auditRemark(auditRemark)
                .auditUserId(SecurityFrameworkUtils.getLoginUserId())
                .auditTime(LocalDateTime.now())
                .build());
    }

    @Override
    public void reject(Long id, String rejectReason) {
        MessageCampaignDO campaign = validateExists(id);
        if (!MessageCenterConstants.CAMPAIGN_AUDIT_PENDING.equals(campaign.getAuditStatus())) {
            throw exception(MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID);
        }
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(id)
                .auditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_REJECTED)
                .rejectReason(rejectReason)
                .auditUserId(SecurityFrameworkUtils.getLoginUserId())
                .auditTime(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeNow(Long id) {
        MessageCampaignDO campaign = validateExists(id);
        if (!MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED.equals(campaign.getAuditStatus())) {
            throw exception(MESSAGE_CAMPAIGN_AUDIT_STATUS_INVALID);
        }
        if (MessageCenterConstants.EXECUTE_STATUS_CANCELLED.equals(campaign.getExecuteStatus())) {
            throw exception(MESSAGE_CAMPAIGN_EXECUTE_STATUS_INVALID);
        }
        if (!isWithinDeliveryWindow(campaign.getDeliveryTimeWindows(), LocalDateTime.now())) {
            throw exception(MESSAGE_CAMPAIGN_DELIVERY_TIME_INVALID);
        }
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(id)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_PROCESSING)
                .executeTime(LocalDateTime.now())
                .build());
        List<Long> targetUserIds = resolveTargetUserIds(campaign);
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(id)
                .plannedAudienceCount(targetUserIds.size())
                .build());
        if (CollUtil.isEmpty(targetUserIds)) {
            messageCampaignMapper.updateById(MessageCampaignDO.builder()
                    .id(id)
                    .executeStatus(MessageCenterConstants.EXECUTE_STATUS_SUCCESS)
                    .executeTime(LocalDateTime.now())
                    .build());
            return;
        }
        List<cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget> targets = targetUserIds.stream()
                .map(userId -> new cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget(userId, campaign.getBizId()))
                .collect(Collectors.toList());
        messagePushDispatchService.dispatchBatch(campaign.getSceneCode(), campaign.getCampaignName(), campaign.getTargetMode(),
                StrUtil.blankToDefault(campaign.getBizType(), MessageCenterConstants.BIZ_TYPE_MARKETING), campaign.getBizId(),
                "消息投放活动执行", targets);
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(id)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_SUCCESS)
                .executeTime(LocalDateTime.now())
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    public int executeScheduledCampaigns() {
        List<MessageCampaignDO> campaigns = messageCampaignMapper.selectList(new LambdaQueryWrapperX<MessageCampaignDO>()
                .eq(MessageCampaignDO::getAuditStatus, MessageCenterConstants.CAMPAIGN_AUDIT_APPROVED)
                .eq(MessageCampaignDO::getExecuteStatus, MessageCenterConstants.EXECUTE_STATUS_PENDING)
                .isNotNull(MessageCampaignDO::getScheduleTime)
                .le(MessageCampaignDO::getScheduleTime, LocalDateTime.now())
                .orderByAsc(MessageCampaignDO::getScheduleTime)
                .orderByAsc(MessageCampaignDO::getId));
        int executedCount = 0;
        for (MessageCampaignDO campaign : campaigns) {
            if (!isWithinDeliveryWindow(campaign.getDeliveryTimeWindows(), LocalDateTime.now())) {
                continue;
            }
            executeNow(campaign.getId());
            executedCount++;
        }
        return executedCount;
    }

    @Override
    public void cancel(Long id, String reason) {
        MessageCampaignDO campaign = validateExists(id);
        if (MessageCenterConstants.EXECUTE_STATUS_SUCCESS.equals(campaign.getExecuteStatus())
                || MessageCenterConstants.EXECUTE_STATUS_PARTIAL_FAILED.equals(campaign.getExecuteStatus())) {
            throw exception(MESSAGE_CAMPAIGN_CANCEL_STATUS_INVALID);
        }
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(id)
                .auditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_CANCELLED)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_CANCELLED)
                .cancelReason(reason)
                .build());
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
        messageCampaignMapper.updateById(MessageCampaignDO.builder()
                .id(id)
                .auditStatus(MessageCenterConstants.CAMPAIGN_AUDIT_CANCELLED)
                .executeStatus(MessageCenterConstants.EXECUTE_STATUS_CANCELLED)
                .cancelReason("用户撤回定向推送申请")
                .build());
    }

    private MessageCampaignDO validateExists(Long id) {
        MessageCampaignDO campaign = messageCampaignMapper.selectById(id);
        if (campaign == null) {
            throw exception(MESSAGE_CAMPAIGN_NOT_EXISTS);
        }
        return campaign;
    }

    private String resolveInitialExecuteStatus(LocalDateTime scheduleTime) {
        if (scheduleTime != null && scheduleTime.isAfter(LocalDateTime.now())) {
            return MessageCenterConstants.EXECUTE_STATUS_PENDING;
        }
        return MessageCenterConstants.EXECUTE_STATUS_PROCESSING;
    }

    private List<Long> resolveTargetUserIds(MessageCampaignDO campaign) {
        List<MemberUserDO> users = memberUserMapper.selectList();
        if (CollUtil.isEmpty(users)) {
            return Collections.emptyList();
        }
        Set<Long> result = users.stream().map(MemberUserDO::getId).collect(Collectors.toCollection(LinkedHashSet::new));
        if (MessageCenterConstants.TARGET_MODE_FULL_PLATFORM.equals(campaign.getTargetMode())) {
            return new ArrayList<>(result);
        }

        if (StrUtil.isNotBlank(campaign.getTargetRoleCodes())) {
            Set<String> roleCodes = splitToSet(campaign.getTargetRoleCodes());
            result.removeIf(userId -> {
                MemberUserDO user = users.stream().filter(item -> item.getId().equals(userId)).findFirst().orElse(null);
                return user == null || StrUtil.isBlank(user.getCurrentRoleCode()) || !roleCodes.contains(user.getCurrentRoleCode());
            });
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

    private Map<Long, String> buildUserRegionMap(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<MerchantEntryDO> entries = merchantEntryMapper.selectList(new LambdaQueryWrapperX<MerchantEntryDO>()
                .in(MerchantEntryDO::getUserId, userIds)
                .orderByDesc(MerchantEntryDO::getId));
        return entries.stream()
                .filter(entry -> StrUtil.isNotBlank(entry.getRegionCode()))
                .collect(Collectors.toMap(MerchantEntryDO::getUserId, MerchantEntryDO::getRegionCode, (oldVal, newVal) -> oldVal));
    }

    private Map<Long, Set<Long>> buildUserCategoryMap(Set<Long> userIds) {
        if (CollUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<MerchantInfoDO> merchants = merchantInfoMapper.selectList(new LambdaQueryWrapperX<MerchantInfoDO>()
                .in(MerchantInfoDO::getUserId, userIds));
        if (CollUtil.isEmpty(merchants)) {
            return Collections.emptyMap();
        }
        Map<Long, MerchantInfoDO> merchantByUserId = convertMap(merchants, MerchantInfoDO::getUserId);
        Set<Long> merchantIds = convertSet(merchants, MerchantInfoDO::getId);
        List<MerchantCategoryRelDO> rels = merchantCategoryRelMapper.selectList(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                .in(MerchantCategoryRelDO::getMerchantId, merchantIds));
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
