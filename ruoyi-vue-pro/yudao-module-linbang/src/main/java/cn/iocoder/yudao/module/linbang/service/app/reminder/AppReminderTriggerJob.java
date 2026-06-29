package cn.iocoder.yudao.module.linbang.service.app.reminder;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AppReminderTriggerJob {

    @Resource
    private AppReminderService appReminderService;

    @Scheduled(cron = "0 */10 * * * ?")
    public void triggerDueReminders() {
        appReminderService.triggerDueReminders();
    }
}
