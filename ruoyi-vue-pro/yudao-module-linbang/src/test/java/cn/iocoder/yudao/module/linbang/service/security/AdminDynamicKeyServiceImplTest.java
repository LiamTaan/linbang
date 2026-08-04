package cn.iocoder.yudao.module.linbang.service.security;

import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.security.core.LoginUser;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.infra.controller.admin.config.vo.ConfigSaveReqVO;
import cn.iocoder.yudao.module.infra.dal.dataobject.config.ConfigDO;
import cn.iocoder.yudao.module.infra.service.config.ConfigService;
import cn.iocoder.yudao.module.linbang.constants.PlatformConfigKeyConstants;
import cn.iocoder.yudao.module.linbang.controller.admin.security.vo.AdminDynamicKeyVerifyRespVO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminDynamicKeyServiceImplTest extends BaseMockitoUnitTest {

    private static final Long ADMIN_USER_ID = 100L;
    private static final String PASSWORD = "test-dynamic-key";
    private static final Instant NOW = Instant.parse("2026-08-03T10:00:00Z");
    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Shanghai");

    @InjectMocks
    private AdminDynamicKeyServiceImpl service;
    @Mock
    private ConfigService configService;
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @BeforeEach
    void setUp() {
        setLoginUser(ADMIN_USER_ID);
        setClock(NOW);
        String encodedPassword = passwordEncoder.encode(PASSWORD);
        when(configService.getConfigByKey(PlatformConfigKeyConstants.ADMIN_DYNAMIC_KEY_PASSWORD))
                .thenReturn(buildConfig(encodedPassword));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void tokenShouldBeSingleUseAndBoundToCurrentAdmin() {
        AdminDynamicKeyVerifyRespVO response = service.verify(PASSWORD);

        setLoginUser(ADMIN_USER_ID + 1);
        assertFalse(service.validateCurrentAdminToken(response.getVerifyToken()));
        setLoginUser(ADMIN_USER_ID);
        assertTrue(service.validateCurrentAdminToken(response.getVerifyToken()));
        assertFalse(service.validateCurrentAdminToken(response.getVerifyToken()));
    }

    @Test
    void issuingNewTokenShouldRevokePreviousToken() {
        String firstToken = service.verify(PASSWORD).getVerifyToken();
        String secondToken = service.verify(PASSWORD).getVerifyToken();

        assertFalse(service.validateCurrentAdminToken(firstToken));
        assertTrue(service.validateCurrentAdminToken(secondToken));
    }

    @Test
    void expiredTokenShouldBeRejectedAndRemoved() {
        String token = service.verify(PASSWORD).getVerifyToken();
        setClock(NOW.plus(6, ChronoUnit.MINUTES));

        assertFalse(service.validateCurrentAdminToken(token));
    }

    @Test
    void tooManyFailuresShouldTemporarilyLockVerification() {
        for (int i = 0; i < 5; i++) {
            assertThrows(ServiceException.class, () -> service.verify("wrong-password"));
        }

        assertThrows(ServiceException.class, () -> service.verify(PASSWORD));
        setClock(NOW.plus(6, ChronoUnit.MINUTES));
        assertTrue(service.validateCurrentAdminToken(service.verify(PASSWORD).getVerifyToken()));
    }

    @Test
    void successfulLegacyPasswordShouldBeMigratedToBcrypt() {
        when(configService.getConfigByKey(PlatformConfigKeyConstants.ADMIN_DYNAMIC_KEY_PASSWORD))
                .thenReturn(buildConfig(PASSWORD));

        service.verify(PASSWORD);

        ArgumentCaptor<ConfigSaveReqVO> captor = ArgumentCaptor.forClass(ConfigSaveReqVO.class);
        verify(configService).updateConfig(captor.capture());
        assertTrue(passwordEncoder.matches(PASSWORD, captor.getValue().getValue()));
    }

    private void setClock(Instant instant) {
        ReflectionTestUtils.setField(service, "clock", Clock.fixed(instant, ZONE_ID));
    }

    private void setLoginUser(Long userId) {
        LoginUser loginUser = new LoginUser();
        loginUser.setId(userId);
        loginUser.setUserType(UserTypeEnum.ADMIN.getValue());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, Collections.emptyList()));
    }

    private ConfigDO buildConfig(String value) {
        ConfigDO config = new ConfigDO();
        config.setId(1L);
        config.setCategory("linbang");
        config.setName("dynamic key");
        config.setConfigKey(PlatformConfigKeyConstants.ADMIN_DYNAMIC_KEY_PASSWORD);
        config.setValue(value);
        config.setVisible(false);
        config.setRemark("sensitive operation verification");
        return config;
    }
}
