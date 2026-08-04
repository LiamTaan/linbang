package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class RegistrationLicenseCleanupService {

    private static final String REGISTRATION_LICENSE_PATH_PREFIX = "linbang/registration-license/";
    private static final long ORPHAN_GRACE_MINUTES = 60L;
    private static final int CLEANUP_BATCH_SIZE = 100;
    private static final int MAX_BATCHES = 10;

    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private FileService fileService;

    @TenantIgnore
    public int cleanExpiredOrphans() {
        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(ORPHAN_GRACE_MINUTES);
        long afterId = 0L;
        int cleaned = 0;
        for (int batch = 0; batch < MAX_BATCHES; batch++) {
            List<Long> fileIds = memberUserQualificationMapper.selectUnreferencedFileIds(
                    REGISTRATION_LICENSE_PATH_PREFIX, expiredBefore, afterId, CLEANUP_BATCH_SIZE);
            if (fileIds.isEmpty()) {
                break;
            }
            for (Long fileId : fileIds) {
                afterId = fileId;
                try {
                    fileService.deleteFile(fileId);
                    cleaned++;
                } catch (Exception ex) {
                    log.warn("[cleanExpiredOrphans][fileId({}) cleanup failed]", fileId, ex);
                }
            }
            if (fileIds.size() < CLEANUP_BATCH_SIZE) {
                break;
            }
        }
        return cleaned;
    }

}
