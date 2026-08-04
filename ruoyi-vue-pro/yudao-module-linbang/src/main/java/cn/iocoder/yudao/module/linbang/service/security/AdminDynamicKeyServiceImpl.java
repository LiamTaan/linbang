package cn.iocoder.yudao.module.linbang.service.security;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.security.vo.AdminDynamicKeyVerifyRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ADMIN_DYNAMIC_KEY_INVALID;

@Service
@Slf4j
public class AdminDynamicKeyServiceImpl implements AdminDynamicKeyService {

    private static final int TOKEN_TTL_MINUTES = 5;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int FAILURE_WINDOW_MINUTES = 5;

    /** Token values are hashed before storage, so a heap dump cannot directly replay them. */
    private final Map<String, TokenRecord> tokenStore = new ConcurrentHashMap<>();
    private final Map<Long, String> currentTokenByUser = new ConcurrentHashMap<>();
    private final Map<Long, FailureRecord> failureStore = new ConcurrentHashMap<>();

    private Clock clock = Clock.systemDefaultZone();

    @Resource
    private ConfigService configService;
    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminDynamicKeyVerifyRespVO verify(String password) {
        Long adminUserId = SecurityFrameworkUtils.getLoginUserId();
        LocalDateTime now = LocalDateTime.now(clock);
        cleanupExpiredEntries(now);
        if (adminUserId == null || isLocked(adminUserId, now)) {
            throw exception(ADMIN_DYNAMIC_KEY_INVALID);
        }
        ConfigDO config = configService.getConfigByKey(PlatformConfigKeyConstants.ADMIN_DYNAMIC_KEY_PASSWORD);
        String expectedPassword = config == null ? null : config.getValue();
        if (!matchesPassword(password, expectedPassword)) {
            recordFailure(adminUserId, now);
            throw exception(ADMIN_DYNAMIC_KEY_INVALID);
        }
        failureStore.remove(adminUserId);
        migrateLegacyPassword(config, password, expectedPassword);

        String token = IdUtil.fastSimpleUUID();
        String tokenHash = hashToHex(token);
        LocalDateTime expireTime = now.plusMinutes(TOKEN_TTL_MINUTES);
        String previousTokenHash = currentTokenByUser.put(adminUserId, tokenHash);
        if (previousTokenHash != null) {
            tokenStore.remove(previousTokenHash);
        }
        tokenStore.put(tokenHash, new TokenRecord(adminUserId, expireTime));
        AdminDynamicKeyVerifyRespVO respVO = new AdminDynamicKeyVerifyRespVO();
        respVO.setVerifyToken(token);
        respVO.setExpireTime(expireTime);
        return respVO;
    }

    @Override
    public boolean validateCurrentAdminToken(String token) {
        if (StrUtil.isBlank(token)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now(clock);
        cleanupExpiredEntries(now);
        String tokenHash = hashToHex(token);
        TokenRecord record = tokenStore.get(tokenHash);
        if (record == null) {
            return false;
        }
        if (!record.getExpireTime().isAfter(now)) {
            removeToken(tokenHash, record);
            return false;
        }
        if (!record.getUserId().equals(SecurityFrameworkUtils.getLoginUserId())) {
            return false;
        }
        if (!tokenHash.equals(currentTokenByUser.get(record.getUserId()))) {
            tokenStore.remove(tokenHash, record);
            return false;
        }
        boolean removed = tokenStore.remove(tokenHash, record);
        if (removed) {
            currentTokenByUser.remove(record.getUserId(), tokenHash);
        }
        return removed;
    }

    private boolean matchesPassword(String password, String expectedPassword) {
        if (isBcryptHash(expectedPassword)) {
            return password != null && passwordEncoder.matches(password, expectedPassword);
        }
        byte[] expectedHash = hash(StrUtil.blankToDefault(expectedPassword, "invalid-dynamic-key"));
        byte[] suppliedHash = hash(password == null ? "" : password);
        return StrUtil.isNotBlank(expectedPassword) && password != null
                && MessageDigest.isEqual(expectedHash, suppliedHash);
    }

    private boolean isBcryptHash(String value) {
        return value != null && value.length() == 60
                && (value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$"));
    }

    private void migrateLegacyPassword(ConfigDO config, String password, String expectedPassword) {
        if (config == null || isBcryptHash(expectedPassword)) {
            return;
        }
        ConfigSaveReqVO updateReqVO = new ConfigSaveReqVO();
        updateReqVO.setId(config.getId());
        updateReqVO.setCategory(config.getCategory());
        updateReqVO.setName(config.getName());
        updateReqVO.setKey(config.getConfigKey());
        updateReqVO.setValue(passwordEncoder.encode(password));
        updateReqVO.setVisible(config.getVisible());
        updateReqVO.setRemark(config.getRemark());
        configService.updateConfig(updateReqVO);
        log.info("Migrated admin dynamic key configuration to BCrypt");
    }

    private boolean isLocked(Long adminUserId, LocalDateTime now) {
        FailureRecord record = failureStore.get(adminUserId);
        if (record == null) {
            return false;
        }
        if (!record.getExpireTime().isAfter(now)) {
            failureStore.remove(adminUserId, record);
            return false;
        }
        return record.getAttempts() >= MAX_FAILED_ATTEMPTS;
    }

    private void recordFailure(Long adminUserId, LocalDateTime now) {
        failureStore.compute(adminUserId, (key, existing) -> {
            if (existing == null || !existing.getExpireTime().isAfter(now)) {
                return new FailureRecord(1, now.plusMinutes(FAILURE_WINDOW_MINUTES));
            }
            return new FailureRecord(existing.getAttempts() + 1, existing.getExpireTime());
        });
    }

    private void cleanupExpiredEntries(LocalDateTime now) {
        tokenStore.forEach((tokenHash, record) -> {
            if (!record.getExpireTime().isAfter(now)) {
                removeToken(tokenHash, record);
            }
        });
        failureStore.forEach((userId, record) -> {
            if (!record.getExpireTime().isAfter(now)) {
                failureStore.remove(userId, record);
            }
        });
    }

    private void removeToken(String tokenHash, TokenRecord record) {
        if (tokenStore.remove(tokenHash, record)) {
            currentTokenByUser.remove(record.getUserId(), tokenHash);
        }
    }

    private static String hashToHex(String value) {
        byte[] bytes = hash(value);
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte item : bytes) {
            builder.append(Character.forDigit((item >>> 4) & 0x0f, 16));
            builder.append(Character.forDigit(item & 0x0f, 16));
        }
        return builder.toString();
    }

    private static byte[] hash(String value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }

    private static class TokenRecord {

        private final Long userId;

        private final LocalDateTime expireTime;

        private TokenRecord(Long userId, LocalDateTime expireTime) {
            this.userId = userId;
            this.expireTime = expireTime;
        }

        public Long getUserId() {
            return userId;
        }

        public LocalDateTime getExpireTime() {
            return expireTime;
        }
    }

    private static class FailureRecord {

        private final int attempts;

        private final LocalDateTime expireTime;

        private FailureRecord(int attempts, LocalDateTime expireTime) {
            this.attempts = attempts;
            this.expireTime = expireTime;
        }

        public int getAttempts() {
            return attempts;
        }

        public LocalDateTime getExpireTime() {
            return expireTime;
        }
    }
}
