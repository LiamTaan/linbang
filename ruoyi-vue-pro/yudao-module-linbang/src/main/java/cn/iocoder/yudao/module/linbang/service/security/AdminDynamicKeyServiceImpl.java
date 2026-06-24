package cn.iocoder.yudao.module.linbang.service.security;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.security.vo.AdminDynamicKeyVerifyRespVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ADMIN_DYNAMIC_KEY_INVALID;

@Service
public class AdminDynamicKeyServiceImpl implements AdminDynamicKeyService {

    private static final int TOKEN_TTL_MINUTES = 5;

    private final Map<String, TokenRecord> tokenStore = new ConcurrentHashMap<>();

    @Resource
    private ConfigService configService;

    @Override
    public AdminDynamicKeyVerifyRespVO verify(String password) {
        ConfigDO config = configService.getConfigByKey(PlatformConfigKeyConstants.ADMIN_DYNAMIC_KEY_PASSWORD);
        String expectedPassword = config == null ? null : config.getValue();
        if (StrUtil.isBlank(expectedPassword) || !StrUtil.equals(expectedPassword, password)) {
            throw exception(ADMIN_DYNAMIC_KEY_INVALID);
        }
        Long adminUserId = SecurityFrameworkUtils.getLoginUserId();
        String token = IdUtil.fastSimpleUUID();
        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(TOKEN_TTL_MINUTES);
        tokenStore.put(token, new TokenRecord(adminUserId, expireTime));
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
        TokenRecord record = tokenStore.get(token);
        if (record == null || record.getExpireTime().isBefore(LocalDateTime.now())) {
            tokenStore.remove(token);
            return false;
        }
        return record.getUserId().equals(SecurityFrameworkUtils.getLoginUserId());
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
}
