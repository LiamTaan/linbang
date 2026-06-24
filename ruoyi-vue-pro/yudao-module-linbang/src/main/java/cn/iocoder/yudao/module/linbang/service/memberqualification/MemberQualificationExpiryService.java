package cn.iocoder.yudao.module.linbang.service.memberqualification;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.messagerecord.MessageRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.messagerecord.MessageRecordMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_QUALIFICATION_EXPIRED;

@Service
public class MemberQualificationExpiryService {

    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MessageRecordMapper messageRecordMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private ConfigService configService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void scanAndHandleExpiry() {
        for (Integer daysBefore : loadReminderDays()) {
            handleReminder(daysBefore);
        }
        handleExpiredQualifications();
    }

    public void validateMerchantCanAccept(Long userId) {
        MemberUserQualificationDO latestApproved = memberUserQualificationMapper.selectOne(
                new LambdaQueryWrapperX<MemberUserQualificationDO>()
                        .eq(MemberUserQualificationDO::getUserId, userId)
                        .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                        .isNotNull(MemberUserQualificationDO::getValidEndDate)
                        .orderByDesc(MemberUserQualificationDO::getValidEndDate, MemberUserQualificationDO::getId)
                        .last("LIMIT 1"));
        if (latestApproved != null && !latestApproved.getValidEndDate().isAfter(LocalDate.now())) {
            throw exception(MEMBER_QUALIFICATION_EXPIRED);
        }
    }

    private void handleReminder(int daysBefore) {
        LocalDate targetDate = LocalDate.now().plusDays(daysBefore);
        List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectList(
                new LambdaQueryWrapperX<MemberUserQualificationDO>()
                        .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                        .eq(MemberUserQualificationDO::getValidEndDate, targetDate)
                        .orderByAsc(MemberUserQualificationDO::getId));
        if (qualifications.isEmpty()) {
            return;
        }
        String bizType = "QUALIFICATION_EXPIRE_REMINDER_D" + daysBefore;
        List<MessagePushDispatchTarget> targets = new ArrayList<>();
        for (MemberUserQualificationDO qualification : qualifications) {
            if (existsMessage(qualification.getUserId(), bizType, qualification.getId())) {
                continue;
            }
            targets.add(new MessagePushDispatchTarget(qualification.getUserId(), qualification.getId()));
        }
        messagePushDispatchService.dispatchBatch("lb_qualification_expire_reminder",
                "资质到期提醒 D-" + daysBefore, "QUALIFICATION_EXPIRY", bizType, null,
                "系统自动生成的资质到期提醒", targets);
    }

    private void handleExpiredQualifications() {
        LocalDate today = LocalDate.now();
        List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectList(
                new LambdaQueryWrapperX<MemberUserQualificationDO>()
                        .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                        .le(MemberUserQualificationDO::getValidEndDate, today)
                        .orderByAsc(MemberUserQualificationDO::getId));
        if (qualifications.isEmpty()) {
            return;
        }
        List<MessagePushDispatchTarget> targets = new ArrayList<>();
        for (MemberUserQualificationDO qualification : qualifications) {
            MerchantInfoDO merchant = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                    .eq(MerchantInfoDO::getUserId, qualification.getUserId())
                    .last("LIMIT 1"));
            if (merchant != null && !Objects.equals(merchant.getAcceptStatus(), "DISABLE")) {
                merchantInfoMapper.updateById(MerchantInfoDO.builder()
                        .id(merchant.getId())
                        .acceptStatus("DISABLE")
                        .build());
            }
            if (!existsMessage(qualification.getUserId(), "QUALIFICATION_EXPIRE_DISABLE", qualification.getId())) {
                targets.add(new MessagePushDispatchTarget(qualification.getUserId(), qualification.getId()));
            }
        }
        messagePushDispatchService.dispatchBatch("lb_qualification_expire_disable",
                "资质到期限制接单", "QUALIFICATION_EXPIRY", "QUALIFICATION_EXPIRE_DISABLE", null,
                "系统自动执行到期限制接单", targets);
    }

    private boolean existsMessage(Long userId, String bizType, Long bizId) {
        return messageRecordMapper.selectCount(new LambdaQueryWrapperX<MessageRecordDO>()
                .eq(MessageRecordDO::getReceiverUserId, userId)
                .eq(MessageRecordDO::getBizType, bizType)
                .eq(MessageRecordDO::getBizId, bizId)) > 0;
    }

    private List<Integer> loadReminderDays() {
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
}
