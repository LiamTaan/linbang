package cn.iocoder.yudao.module.linbang.service.punishlog;

import java.time.LocalDateTime;

public interface PunishLogWriteService {

    Long createPunishLog(Long userId, String punishType, String status, String reason,
                         String sourceBizType, Long sourceBizId, String sourceRecordType, Long sourceRecordId,
                         Long operatorId, LocalDateTime operateTime, LocalDateTime startTime, LocalDateTime endTime,
                         String extJson);

    void releasePunishLog(String sourceRecordType, Long sourceRecordId, String status,
                          Long releaseOperatorId, LocalDateTime releaseTime, String releaseRemark);

    void syncHistoricalLogs();
}
