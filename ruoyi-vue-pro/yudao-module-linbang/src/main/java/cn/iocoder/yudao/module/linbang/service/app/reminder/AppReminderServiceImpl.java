package cn.iocoder.yudao.module.linbang.service.app.reminder;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.linbang.constants.MessageCenterConstants;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderHistoryPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderHistoryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.reminder.vo.AppReminderUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userreminder.UserReminderDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userreminder.UserReminderMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_REMINDER_EVENT_TIME_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_REMINDER_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.USER_REMINDER_TYPE_INVALID;

@Service
@Validated
public class AppReminderServiceImpl implements AppReminderService {

    private static final String REMINDER_TYPE_BIRTHDAY = "BIRTHDAY";
    private static final String REMINDER_TYPE_CUSTOM_EVENT = "CUSTOM_EVENT";
    private static final String REMINDER_TYPE_QUALIFICATION_EXPIRY = "QUALIFICATION_EXPIRY";
    private static final String REMINDER_STATUS_ACTIVE = "ACTIVE";
    private static final String REMINDER_STATUS_TRIGGERED = "TRIGGERED";
    private static final String REMINDER_STATUS_DISABLED = "DISABLED";
    private static final String REMINDER_STATUS_EXPIRED = "EXPIRED";
    private static final String REPEAT_TYPE_NONE = "NONE";
    private static final String REPEAT_TYPE_YEARLY = "YEARLY";
    private static final LocalTime DEFAULT_REMIND_TIME = LocalTime.of(9, 0);
    private static final DateTimeFormatter DEDUPE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private UserReminderMapper userReminderMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private ConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReminder(Long authUserId, @Valid AppReminderCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        String reminderType = validateCreatableReminderType(reqVO.getReminderType());
        String repeatType = normalizeRepeatType(reminderType, reqVO.getRepeatType());
        LocalDateTime nextRemindTime = resolveNextRemindTime(reqVO.getEventTime(), reminderType, repeatType, LocalDateTime.now());
        UserReminderDO reminder = UserReminderDO.builder()
                .userId(loginUser.getId())
                .reminderType(reminderType)
                .title(reqVO.getTitle())
                .content(reqVO.getContent())
                .eventTime(reqVO.getEventTime())
                .nextRemindTime(nextRemindTime)
                .repeatType(repeatType)
                .status(REMINDER_STATUS_ACTIVE)
                .routeType(reqVO.getRouteType())
                .routeValue(reqVO.getRouteValue())
                .build();
        userReminderMapper.insert(reminder);
        return reminder.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateReminder(Long authUserId, @Valid AppReminderUpdateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        UserReminderDO reminder = getOwnedReminder(loginUser.getId(), reqVO.getId());
        String reminderType = validateCreatableReminderType(reqVO.getReminderType());
        String repeatType = normalizeRepeatType(reminderType, reqVO.getRepeatType());
        LocalDateTime nextRemindTime = resolveNextRemindTime(reqVO.getEventTime(), reminderType, repeatType, LocalDateTime.now());
        userReminderMapper.updateById(UserReminderDO.builder()
                .id(reminder.getId())
                .reminderType(reminderType)
                .title(reqVO.getTitle())
                .content(reqVO.getContent())
                .eventTime(reqVO.getEventTime())
                .nextRemindTime(nextRemindTime)
                .repeatType(repeatType)
                .status(REMINDER_STATUS_ACTIVE)
                .routeType(reqVO.getRouteType())
                .routeValue(reqVO.getRouteValue())
                .lastTriggerTime(null)
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReminder(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        UserReminderDO reminder = getOwnedReminder(loginUser.getId(), id);
        userReminderMapper.deleteById(reminder.getId());
    }

    @Override
    public AppReminderRespVO getReminder(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        return convertReminder(getOwnedReminder(loginUser.getId(), id));
    }

    @Override
    public PageResult<AppReminderRespVO> getReminderPage(Long authUserId, AppReminderPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        List<AppReminderRespVO> list = new ArrayList<>();
        list.addAll(userReminderMapper.selectListByUserId(loginUser.getId()).stream()
                .map(this::convertReminder)
                .collect(Collectors.toList()));
        list.addAll(buildQualificationReminderList(loginUser.getId()));
        List<AppReminderRespVO> filtered = list.stream()
                .filter(item -> reqVO.getReminderType() == null || Objects.equals(item.getReminderType(), reqVO.getReminderType()))
                .filter(item -> reqVO.getStatus() == null || Objects.equals(item.getStatus(), reqVO.getStatus()))
                .sorted(Comparator.comparing(AppReminderRespVO::getNextRemindTime,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(AppReminderRespVO::getEventTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        return manualPage(filtered, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    public PageResult<AppReminderHistoryRespVO> getReminderHistoryPage(Long authUserId, AppReminderHistoryPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        List<UserReminderDO> reminders = userReminderMapper.selectListByUserId(loginUser.getId());
        List<AppReminderHistoryRespVO> list = messageRecordMapper.selectList(new LambdaQueryWrapperX<MessageRecordDO>()
                        .eq(MessageRecordDO::getReceiverUserId, loginUser.getId())
                        .in(MessageRecordDO::getBizType, Arrays.asList(
                                MessageCenterConstants.BIZ_TYPE_USER_REMINDER,
                                MessageCenterConstants.BIZ_TYPE_QUALIFICATION_EXPIRY))
                        .orderByDesc(MessageRecordDO::getId))
                .stream()
                .map(record -> convertHistory(record, reminders))
                .filter(Objects::nonNull)
                .filter(item -> reqVO.getReminderType() == null || Objects.equals(item.getReminderType(), reqVO.getReminderType()))
                .filter(item -> reqVO.getReadStatus() == null || Objects.equals(item.getReadStatus(), reqVO.getReadStatus()))
                .collect(Collectors.toList());
        return manualHistoryPage(list, reqVO.getPageNo(), reqVO.getPageSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void triggerDueReminders() {
        LocalDateTime now = LocalDateTime.now();
        for (UserReminderDO reminder : userReminderMapper.selectDueList(now)) {
            String dedupeKey = buildDedupeKey(reminder.getId(), reminder.getNextRemindTime());
            if (messageRecordMapper.selectByDedupeKey(dedupeKey) == null) {
                messageRecordMapper.insert(MessageRecordDO.builder()
                        .receiverUserId(reminder.getUserId())
                        .sceneCode(MessageCenterConstants.SCENE_REMINDER_TRIGGER)
                        .messageCategory(MessageCenterConstants.CATEGORY_SYSTEM)
                        .channelType(MessageCenterConstants.CHANNEL_APP_POPUP)
                        .bizType(MessageCenterConstants.BIZ_TYPE_USER_REMINDER)
                        .bizId(reminder.getId())
                        .dedupeKey(dedupeKey)
                        .sendStatus("SUCCESS")
                        .sendTime(now)
                        .title(reminder.getTitle())
                        .contentSnapshot(reminder.getContent())
                        .routeType(reminder.getRouteType())
                        .routeValue(reminder.getRouteValue())
                        .readStatus(MessageCenterConstants.READ_STATUS_UNREAD)
                        .build());
            }
            userReminderMapper.updateById(UserReminderDO.builder()
                    .id(reminder.getId())
                    .lastTriggerTime(now)
                    .nextRemindTime(resolveTriggeredNextRemindTime(reminder))
                    .status(resolveTriggeredStatus(reminder))
                    .build());
        }
    }

    private AppReminderHistoryRespVO convertHistory(MessageRecordDO record, List<UserReminderDO> reminders) {
        AppReminderHistoryRespVO respVO = new AppReminderHistoryRespVO();
        respVO.setMessageRecordId(record.getId());
        respVO.setTitle(record.getTitle());
        respVO.setContentSnapshot(record.getContentSnapshot());
        respVO.setReadStatus(record.getReadStatus());
        respVO.setSendTime(record.getSendTime());
        respVO.setBizId(record.getBizId());
        if (Objects.equals(record.getBizType(), MessageCenterConstants.BIZ_TYPE_USER_REMINDER)) {
            UserReminderDO reminder = reminders.stream()
                    .filter(item -> Objects.equals(item.getId(), record.getBizId()))
                    .findFirst().orElse(null);
            respVO.setReminderId(record.getBizId());
            respVO.setReminderType(reminder == null ? REMINDER_TYPE_CUSTOM_EVENT : reminder.getReminderType());
            return respVO;
        }
        if (Objects.equals(record.getBizType(), MessageCenterConstants.BIZ_TYPE_QUALIFICATION_EXPIRY)) {
            respVO.setReminderType(REMINDER_TYPE_QUALIFICATION_EXPIRY);
            return respVO;
        }
        return null;
    }

    private List<AppReminderRespVO> buildQualificationReminderList(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        List<Integer> remindDays = loadQualificationReminderDays();
        return memberUserQualificationMapper.selectListByUserId(userId).stream()
                .filter(item -> "APPROVED".equals(item.getAuditStatus()))
                .filter(item -> item.getValidEndDate() != null)
                .map(item -> convertQualificationReminder(item, remindDays, now))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private AppReminderRespVO convertQualificationReminder(MemberUserQualificationDO qualification,
                                                           List<Integer> remindDays,
                                                           LocalDateTime now) {
        LocalDate endDate = qualification.getValidEndDate();
        AppReminderRespVO respVO = new AppReminderRespVO();
        respVO.setReminderType(REMINDER_TYPE_QUALIFICATION_EXPIRY);
        respVO.setTitle(qualification.getQualificationName() + " 到期提醒");
        respVO.setContent("您的证件/资质将于 " + endDate + " 到期，请尽快更新，避免影响接单。");
        respVO.setEventTime(endDate.atTime(DEFAULT_REMIND_TIME));
        respVO.setNextRemindTime(resolveQualificationNextRemindTime(endDate, remindDays, now));
        respVO.setStatus(resolveQualificationReminderStatus(endDate, respVO.getNextRemindTime(), now));
        respVO.setRepeatType(REPEAT_TYPE_NONE);
        respVO.setSystemGenerated(Boolean.TRUE);
        respVO.setEditable(Boolean.FALSE);
        respVO.setBizId(qualification.getId());
        respVO.setRouteType("APP_PAGE");
        respVO.setRouteValue("/pages/qualification/index");
        return respVO;
    }

    private AppReminderRespVO convertReminder(UserReminderDO reminder) {
        AppReminderRespVO respVO = new AppReminderRespVO();
        respVO.setId(reminder.getId());
        respVO.setReminderType(reminder.getReminderType());
        respVO.setTitle(reminder.getTitle());
        respVO.setContent(reminder.getContent());
        respVO.setEventTime(reminder.getEventTime());
        respVO.setNextRemindTime(reminder.getNextRemindTime());
        respVO.setLastTriggerTime(reminder.getLastTriggerTime());
        respVO.setRepeatType(reminder.getRepeatType());
        respVO.setStatus(reminder.getStatus());
        respVO.setSystemGenerated(Boolean.FALSE);
        respVO.setEditable(Boolean.TRUE);
        respVO.setRouteType(reminder.getRouteType());
        respVO.setRouteValue(reminder.getRouteValue());
        return respVO;
    }

    private UserReminderDO getOwnedReminder(Long userId, Long id) {
        UserReminderDO reminder = userReminderMapper.selectByIdAndUserId(id, userId);
        if (reminder == null) {
            throw exception(USER_REMINDER_NOT_EXISTS);
        }
        return reminder;
    }

    private String validateCreatableReminderType(String reminderType) {
        if (REMINDER_TYPE_BIRTHDAY.equals(reminderType) || REMINDER_TYPE_CUSTOM_EVENT.equals(reminderType)) {
            return reminderType;
        }
        throw exception(USER_REMINDER_TYPE_INVALID);
    }

    private String normalizeRepeatType(String reminderType, String repeatType) {
        if (REMINDER_TYPE_BIRTHDAY.equals(reminderType)) {
            return REPEAT_TYPE_YEARLY;
        }
        return repeatType == null || repeatType.trim().isEmpty() ? REPEAT_TYPE_NONE : repeatType;
    }

    private LocalDateTime resolveNextRemindTime(LocalDateTime eventTime, String reminderType,
                                                String repeatType, LocalDateTime now) {
        if (eventTime == null) {
            throw exception(USER_REMINDER_EVENT_TIME_INVALID);
        }
        if (REMINDER_TYPE_BIRTHDAY.equals(reminderType) || REPEAT_TYPE_YEARLY.equals(repeatType)) {
            LocalDateTime candidate = eventTime.withYear(now.getYear());
            if (!candidate.isAfter(now)) {
                candidate = candidate.plusYears(1);
            }
            return candidate;
        }
        if (!eventTime.isAfter(now)) {
            throw exception(USER_REMINDER_EVENT_TIME_INVALID);
        }
        return eventTime;
    }

    private LocalDateTime resolveTriggeredNextRemindTime(UserReminderDO reminder) {
        if (REPEAT_TYPE_YEARLY.equals(reminder.getRepeatType()) && reminder.getNextRemindTime() != null) {
            return reminder.getNextRemindTime().plusYears(1);
        }
        return null;
    }

    private String resolveTriggeredStatus(UserReminderDO reminder) {
        return REPEAT_TYPE_YEARLY.equals(reminder.getRepeatType()) ? REMINDER_STATUS_ACTIVE : REMINDER_STATUS_TRIGGERED;
    }

    private LocalDateTime resolveQualificationNextRemindTime(LocalDate endDate, List<Integer> remindDays, LocalDateTime now) {
        for (Integer day : remindDays) {
            LocalDateTime candidate = endDate.minusDays(day).atTime(DEFAULT_REMIND_TIME);
            if (candidate.isAfter(now)) {
                return candidate;
            }
        }
        return null;
    }

    private String resolveQualificationReminderStatus(LocalDate endDate, LocalDateTime nextRemindTime, LocalDateTime now) {
        if (!endDate.isAfter(now.toLocalDate())) {
            return REMINDER_STATUS_EXPIRED;
        }
        if (nextRemindTime != null) {
            return REMINDER_STATUS_ACTIVE;
        }
        return REMINDER_STATUS_DISABLED;
    }

    private List<Integer> loadQualificationReminderDays() {
        ConfigDO config = configService.getConfigByKey(PlatformConfigKeyConstants.QUALIFICATION_EXPIRE_REMIND_DAYS);
        if (config == null || config.getValue() == null || config.getValue().trim().isEmpty()) {
            return Arrays.asList(7, 1);
        }
        Set<Integer> values = new LinkedHashSet<>();
        for (String item : config.getValue().split(",")) {
            String trimmed = item == null ? "" : item.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                int value = Integer.parseInt(trimmed);
                if (value > 0) {
                    values.add(value);
                }
            } catch (NumberFormatException ignored) {
                // ignore invalid config fragments and keep parsing the remaining items
            }
        }
        if (values.isEmpty()) {
            return Arrays.asList(7, 1);
        }
        List<Integer> days = new ArrayList<>(values);
        days.sort(Comparator.reverseOrder());
        return days;
    }

    private String buildDedupeKey(Long reminderId, LocalDateTime remindTime) {
        return "USER_REMINDER:" + reminderId + ":" + remindTime.format(DEDUPE_TIME_FORMAT);
    }

    private PageResult<AppReminderRespVO> manualPage(List<AppReminderRespVO> list, Integer pageNo, Integer pageSize) {
        if (list.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = (safePageNo - 1) * safePageSize;
        if (fromIndex >= list.size()) {
            return new PageResult<>(new ArrayList<>(), (long) list.size());
        }
        int toIndex = Math.min(fromIndex + safePageSize, list.size());
        return new PageResult<>(list.subList(fromIndex, toIndex), (long) list.size());
    }

    private PageResult<AppReminderHistoryRespVO> manualHistoryPage(List<AppReminderHistoryRespVO> list,
                                                                   Integer pageNo, Integer pageSize) {
        if (list.isEmpty()) {
            return new PageResult<>(new ArrayList<>(), 0L);
        }
        int safePageNo = pageNo == null || pageNo < 1 ? 1 : pageNo;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int fromIndex = (safePageNo - 1) * safePageSize;
        if (fromIndex >= list.size()) {
            return new PageResult<>(new ArrayList<>(), (long) list.size());
        }
        int toIndex = Math.min(fromIndex + safePageSize, list.size());
        return new PageResult<>(list.subList(fromIndex, toIndex), (long) list.size());
    }
}
