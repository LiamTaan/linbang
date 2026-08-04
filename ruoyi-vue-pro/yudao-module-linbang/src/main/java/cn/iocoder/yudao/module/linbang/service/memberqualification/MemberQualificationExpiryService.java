package cn.iocoder.yudao.module.linbang.service.memberqualification;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.service.merchantinfo.MerchantAccessStateService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchTarget;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_QUALIFICATION_EXPIRED;

@Service
public class MemberQualificationExpiryService {

    private static final int SCAN_BATCH_SIZE = 500;

    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private ConfigService configService;
    @Resource
    private MerchantAccessStateService merchantAccessStateService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void scanAndHandleExpiry() {
        for (Integer daysBefore : loadReminderDays()) {
            handleReminder(daysBefore);
        }
        handleExpiredQualifications();
    }

    public void validateMerchantCanAccept(Long userId) {
        if (!canMerchantAccept(userId)) {
            throw exception(MEMBER_QUALIFICATION_EXPIRED);
        }
    }

    public boolean canMerchantAccept(Long userId) {
        long totalCount = memberUserQualificationMapper.selectCount(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, userId));
        if (totalCount == 0) {
            return true;
        }
        long validApprovedCount = memberUserQualificationMapper.selectCount(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, userId)
                .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                .and(wrapper -> wrapper.isNull(MemberUserQualificationDO::getValidEndDate)
                        .or()
                        .ge(MemberUserQualificationDO::getValidEndDate, LocalDate.now())));
        return validApprovedCount > 0;
    }

    private void handleReminder(int daysBefore) {
        LocalDate targetDate = LocalDate.now().plusDays(daysBefore);
        long lastId = 0L;
        while (true) {
            List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectList(
                    new LambdaQueryWrapperX<MemberUserQualificationDO>()
                            .gt(MemberUserQualificationDO::getId, lastId)
                            .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                            .eq(MemberUserQualificationDO::getValidEndDate, targetDate)
                            .orderByAsc(MemberUserQualificationDO::getId)
                            .last("LIMIT " + SCAN_BATCH_SIZE));
            if (qualifications.isEmpty()) {
                return;
            }
            lastId = qualifications.get(qualifications.size() - 1).getId();
            String bizType = "QUALIFICATION_EXPIRE_REMINDER_D" + daysBefore;
            List<MessagePushDispatchTarget> targets = new ArrayList<>();
            for (MemberUserQualificationDO qualification : qualifications) {
                targets.add(new MessagePushDispatchTarget(qualification.getUserId(), qualification.getId(),
                        "qualification-expiry-reminder:" + daysBefore + ":" + qualification.getId()));
            }
            messagePushDispatchService.dispatchBatch("lb_qualification_expire_reminder",
                    "资质到期提醒 D-" + daysBefore, "QUALIFICATION_EXPIRY", bizType, null,
                    "系统自动生成的资质到期提醒", targets);
        }
    }

    private void handleExpiredQualifications() {
        LocalDate today = LocalDate.now();
        while (true) {
            List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectList(
                    new LambdaQueryWrapperX<MemberUserQualificationDO>()
                            .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                            .lt(MemberUserQualificationDO::getValidEndDate, today)
                            .orderByAsc(MemberUserQualificationDO::getId)
                            .last("LIMIT " + SCAN_BATCH_SIZE));
            if (qualifications.isEmpty()) {
                return;
            }
            List<MessagePushDispatchTarget> targets = new ArrayList<>();
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            for (MemberUserQualificationDO qualification : qualifications) {
                int updated = memberUserQualificationMapper.update(null,
                        new LambdaUpdateWrapper<MemberUserQualificationDO>()
                                .eq(MemberUserQualificationDO::getId, qualification.getId())
                                .eq(MemberUserQualificationDO::getAuditStatus, "APPROVED")
                                .lt(MemberUserQualificationDO::getValidEndDate, today)
                                .set(MemberUserQualificationDO::getAuditStatus, "REJECTED")
                                .set(MemberUserQualificationDO::getAuditRemark, "系统自动驳回：资质已过期")
                                .set(MemberUserQualificationDO::getRejectReason, "资质已过期，请更新资料后重新提交审核")
                                .set(MemberUserQualificationDO::getAuditTime, now)
                                .set(MemberUserQualificationDO::getPriorityEnabled, Boolean.FALSE));
                if (updated == 0) {
                    continue;
                }
                merchantAccessStateService.refreshMerchantAcceptStatus(qualification.getUserId());
                targets.add(new MessagePushDispatchTarget(qualification.getUserId(), qualification.getId(),
                        "qualification-expire-disable:" + qualification.getId()));
            }
            messagePushDispatchService.dispatchBatch("lb_qualification_expire_disable",
                    "资质到期限制接单", "QUALIFICATION_EXPIRY", "QUALIFICATION_EXPIRE_DISABLE", null,
                    "系统自动执行到期限制接单", targets);
        }
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
