package cn.iocoder.yudao.module.infra.job.file;

import cn.iocoder.yudao.module.infra.service.file.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class FilePendingUploadCleanupJob {

    @Resource
    private FileService fileService;

    @Scheduled(cron = "0 */10 * * * ?")
    public void cleanExpiredPendingUploads() {
        int cleaned = fileService.cleanExpiredPendingUploads();
        if (cleaned > 0) {
            log.info("[cleanExpiredPendingUploads][cleaned({}) expired upload reservations]", cleaned);
        }
    }

}
