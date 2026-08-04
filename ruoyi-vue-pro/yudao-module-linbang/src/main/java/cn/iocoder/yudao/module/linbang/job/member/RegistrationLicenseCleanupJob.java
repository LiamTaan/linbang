package cn.iocoder.yudao.module.linbang.job.member;

import cn.iocoder.yudao.module.linbang.service.app.auth.RegistrationLicenseCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class RegistrationLicenseCleanupJob {

    @Resource
    private RegistrationLicenseCleanupService cleanupService;

    @Scheduled(cron = "0 */30 * * * ?")
    public void cleanExpiredOrphans() {
        int cleaned = cleanupService.cleanExpiredOrphans();
        if (cleaned > 0) {
            log.info("[cleanExpiredOrphans][cleaned({}) registration license files]", cleaned);
        }
    }

}
