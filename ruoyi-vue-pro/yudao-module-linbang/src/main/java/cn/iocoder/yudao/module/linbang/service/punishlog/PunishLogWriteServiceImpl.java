package cn.iocoder.yudao.module.linbang.service.punishlog;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoterpenaltyrecord.PromoterPenaltyRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.punishlog.PunishLogDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userfrozenfundrecord.UserFrozenFundRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.userrestrictrecord.UserRestrictRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.blacklist.BlacklistMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoterpenaltyrecord.PromoterPenaltyRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.punishlog.PunishLogMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userfrozenfundrecord.UserFrozenFundRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.userrestrictrecord.UserRestrictRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class PunishLogWriteServiceImpl implements PunishLogWriteService {

    private static final String SYNC_LOCK_KEY = "linbang:punish-log:sync";
    private static final long SYNC_LOCK_TIMEOUT_MINUTES = 30L;
    private static final int SYNC_BATCH_SIZE = 200;

    private static final String SOURCE_RECORD_TYPE_RESTRICT = "USER_RESTRICT_RECORD";
    private static final String SOURCE_RECORD_TYPE_FROZEN = "USER_FROZEN_FUND_RECORD";
    private static final String SOURCE_RECORD_TYPE_PROMOTE_PENALTY = "PROMOTER_PENALTY_RECORD";
    private static final String SOURCE_RECORD_TYPE_BLACKLIST = "BLACKLIST_RECORD";

    @Resource
    private PunishLogMapper punishLogMapper;
    @Resource
    private UserRestrictRecordMapper userRestrictRecordMapper;
    @Resource
    private UserFrozenFundRecordMapper userFrozenFundRecordMapper;
    @Resource
    private PromoterPenaltyRecordMapper promoterPenaltyRecordMapper;
    @Resource
    private BlacklistMapper blacklistMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPunishLog(Long userId, String punishType, String status, String reason,
                                String sourceBizType, Long sourceBizId, String sourceRecordType, Long sourceRecordId,
                                Long operatorId, LocalDateTime operateTime, LocalDateTime startTime, LocalDateTime endTime,
                                String extJson) {
        PunishLogDO existing = sourceRecordType == null || sourceRecordId == null ? null
                : punishLogMapper.selectBySourceRecord(sourceRecordType, sourceRecordId);
        if (existing != null) {
            return existing.getId();
        }
        PunishLogDO record = PunishLogDO.builder()
                .userId(userId)
                .punishType(punishType)
                .status(status)
                .reason(reason)
                .sourceBizType(sourceBizType)
                .sourceBizId(sourceBizId)
                .sourceRecordType(sourceRecordType)
                .sourceRecordId(sourceRecordId)
                .operatorId(operatorId)
                .operateTime(operateTime)
                .startTime(startTime)
                .endTime(endTime)
                .extJson(extJson)
                .build();
        punishLogMapper.insert(record);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releasePunishLog(String sourceRecordType, Long sourceRecordId, String status,
                                 Long releaseOperatorId, LocalDateTime releaseTime, String releaseRemark) {
        if (StrUtil.isBlank(sourceRecordType) || sourceRecordId == null) {
            return;
        }
        PunishLogDO existing = punishLogMapper.selectBySourceRecord(sourceRecordType, sourceRecordId);
        if (existing == null) {
            return;
        }
        punishLogMapper.updateById(PunishLogDO.builder()
                .id(existing.getId())
                .status(status)
                .releaseOperatorId(releaseOperatorId)
                .releaseTime(releaseTime)
                .releaseRemark(releaseRemark)
                .endTime(releaseTime)
                .build());
    }

    @Override
    public void syncHistoricalLogs() {
        String lockValue = UUID.randomUUID().toString();
        Boolean locked = stringRedisTemplate.opsForValue()
                .setIfAbsent(SYNC_LOCK_KEY, lockValue, SYNC_LOCK_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (!Boolean.TRUE.equals(locked)) {
            log.info("[syncHistoricalLogs] another sync is already running, skip this round");
            return;
        }
        try {
            syncRestrictRecords();
            syncFrozenFundRecords();
            syncPromoterPenaltyRecords();
            syncBlacklistRecords();
        } finally {
            if (lockValue.equals(stringRedisTemplate.opsForValue().get(SYNC_LOCK_KEY))) {
                stringRedisTemplate.delete(SYNC_LOCK_KEY);
            }
        }
    }

    private void syncRestrictRecords() {
        Long minId = null;
        while (true) {
            List<UserRestrictRecordDO> records = userRestrictRecordMapper.selectBatchByMinId(minId, SYNC_BATCH_SIZE);
            if (records.isEmpty()) {
                return;
            }
            records.forEach(this::upsertRestrictPunishLog);
            minId = records.get(records.size() - 1).getId();
        }
    }

    private void syncFrozenFundRecords() {
        Long minId = null;
        while (true) {
            List<UserFrozenFundRecordDO> records = userFrozenFundRecordMapper.selectBatchByMinId(minId, SYNC_BATCH_SIZE);
            if (records.isEmpty()) {
                return;
            }
            records.forEach(this::upsertFrozenPunishLog);
            minId = records.get(records.size() - 1).getId();
        }
    }

    private void syncPromoterPenaltyRecords() {
        Long minId = null;
        while (true) {
            List<PromoterPenaltyRecordDO> records = promoterPenaltyRecordMapper.selectBatchByMinId(minId, SYNC_BATCH_SIZE);
            if (records.isEmpty()) {
                return;
            }
            records.forEach(this::upsertPromoterPenaltyPunishLog);
            minId = records.get(records.size() - 1).getId();
        }
    }

    private void syncBlacklistRecords() {
        Long minId = null;
        while (true) {
            List<BlacklistDO> records = blacklistMapper.selectBatchByMinId(minId, SYNC_BATCH_SIZE);
            if (records.isEmpty()) {
                return;
            }
            records.forEach(this::upsertBlacklistPunishLog);
            minId = records.get(records.size() - 1).getId();
        }
    }

    private void upsertRestrictPunishLog(UserRestrictRecordDO item) {
        upsertPunishLog(PunishLogDO.builder()
                .userId(item.getUserId())
                .punishType("RESTRICT_" + item.getRestrictType())
                .status(item.getStatus())
                .reason(item.getReason())
                .sourceBizType(item.getSourceBizType())
                .sourceBizId(item.getSourceBizId())
                .sourceRecordType(SOURCE_RECORD_TYPE_RESTRICT)
                .sourceRecordId(item.getId())
                .operateTime(item.getCreateTime())
                .startTime(item.getStartTime())
                .endTime(item.getReleasedTime() != null ? item.getReleasedTime() : item.getEndTime())
                .releaseOperatorId(item.getReleasedBy())
                .releaseTime(item.getReleasedTime())
                .releaseRemark(item.getReleaseRemark())
                .build());
    }

    private void upsertFrozenPunishLog(UserFrozenFundRecordDO item) {
        upsertPunishLog(PunishLogDO.builder()
                .userId(item.getUserId())
                .punishType("FREEZE_FUNDS")
                .status(item.getStatus())
                .reason(item.getReason())
                .sourceBizType(item.getSourceBizType())
                .sourceBizId(item.getSourceBizId())
                .sourceRecordType(SOURCE_RECORD_TYPE_FROZEN)
                .sourceRecordId(item.getId())
                .operateTime(item.getCreateTime())
                .startTime(item.getCreateTime())
                .endTime(item.getReleasedTime())
                .releaseOperatorId(item.getReleasedBy())
                .releaseTime(item.getReleasedTime())
                .releaseRemark(item.getReleaseRemark())
                .build());
    }

    private void upsertPromoterPenaltyPunishLog(PromoterPenaltyRecordDO item) {
        upsertPunishLog(PunishLogDO.builder()
                .userId(item.getUserId())
                .punishType(item.getPenaltyAction())
                .status(item.getStatus())
                .reason(item.getReason())
                .sourceBizType("PROMOTE_CONTENT")
                .sourceBizId(item.getContentId())
                .sourceRecordType(SOURCE_RECORD_TYPE_PROMOTE_PENALTY)
                .sourceRecordId(item.getId())
                .operateTime(item.getCreateTime())
                .startTime(item.getCreateTime())
                .build());
    }

    private void upsertBlacklistPunishLog(BlacklistDO item) {
        upsertPunishLog(PunishLogDO.builder()
                .userId(item.getUserId())
                .punishType("BLACKLIST_" + item.getBlackType())
                .status(item.getStatus())
                .reason(item.getReason())
                .sourceBizType("USER")
                .sourceBizId(item.getUserId())
                .sourceRecordType(SOURCE_RECORD_TYPE_BLACKLIST)
                .sourceRecordId(item.getId())
                .operateTime(item.getCreateTime())
                .startTime(item.getStartTime())
                .endTime(item.getEndTime())
                .build());
    }

    private void upsertPunishLog(PunishLogDO target) {
        PunishLogDO existing = punishLogMapper.selectBySourceRecord(target.getSourceRecordType(), target.getSourceRecordId());
        if (existing == null) {
            punishLogMapper.insert(target);
            return;
        }
        target.setId(existing.getId());
        punishLogMapper.updateById(target);
    }

}
