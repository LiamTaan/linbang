package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberWechatMiniProgramLoginReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.system.api.logger.LoginLogApi;
import cn.iocoder.yudao.module.system.api.social.SocialClientApi;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialWxPhoneNumberInfoRespDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppMemberAuthServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AppMemberAuthServiceImpl authService;
    @Mock
    private SocialClientApi socialClientApi;
    @Mock
    private SocialUserApi socialUserApi;
    @Mock
    private MemberUserService memberUserService;
    @Mock
    private OAuth2TokenCommonApi oauth2TokenCommonApi;
    @Mock
    private LoginLogApi loginLogApi;
    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void testWechatMiniProgramLogin_success() {
        String mobile = "13800138000";
        String phoneCode = "wechat-phone-code";
        String loginCode = "wechat-login-code";
        SocialWxPhoneNumberInfoRespDTO phoneInfo = new SocialWxPhoneNumberInfoRespDTO();
        phoneInfo.setPurePhoneNumber(mobile);
        MemberUserDO user = MemberUserDO.builder().id(1L).mobile(mobile).status("ENABLE").build();
        OAuth2AccessTokenRespDTO token = new OAuth2AccessTokenRespDTO();
        token.setAccessToken("access-token");
        token.setRefreshToken("refresh-token");
        token.setExpiresTime(LocalDateTime.now().plusHours(1));

        when(socialClientApi.getWxMaPhoneNumberInfo(eq(1), eq(phoneCode))).thenReturn(phoneInfo);
        when(memberUserService.createMemberUserIfAbsent(eq(mobile), eq("WECHAT_MINI_PROGRAM"))).thenReturn(user);
        when(oauth2TokenCommonApi.createAccessToken(any())).thenReturn(token);
        when(socialUserApi.bindSocialUser(any(SocialUserBindReqDTO.class))).thenReturn("openid-001");

        AppMemberWechatMiniProgramLoginReqVO reqVO = new AppMemberWechatMiniProgramLoginReqVO();
        reqVO.setPhoneCode(phoneCode);
        reqVO.setLoginCode(loginCode);
        AppMemberLoginRespVO result = authService.wechatMiniProgramLogin(reqVO);

        assertEquals(user.getId(), result.getUserId());
        assertEquals(token.getAccessToken(), result.getAccessToken());
        assertEquals(token.getRefreshToken(), result.getRefreshToken());
        assertEquals(SocialTypeEnum.WECHAT_MINI_PROGRAM.getType(), result.getSocialType());
        assertEquals("openid-001", result.getSocialOpenid());
        verify(memberUserService).updateMemberUserLogin(eq(user.getId()), any());
    }

    @Test
    public void testWechatMiniProgramLogin_disabledUser() {
        String mobile = "13800138000";
        String phoneCode = "wechat-phone-code";
        String loginCode = "wechat-login-code";
        SocialWxPhoneNumberInfoRespDTO phoneInfo = new SocialWxPhoneNumberInfoRespDTO();
        phoneInfo.setPurePhoneNumber(mobile);
        MemberUserDO user = MemberUserDO.builder().id(1L).mobile(mobile).status("DISABLE").build();
        AppMemberWechatMiniProgramLoginReqVO reqVO = new AppMemberWechatMiniProgramLoginReqVO();
        reqVO.setPhoneCode(phoneCode);
        reqVO.setLoginCode(loginCode);

        when(socialClientApi.getWxMaPhoneNumberInfo(eq(1), eq(phoneCode))).thenReturn(phoneInfo);
        when(memberUserService.createMemberUserIfAbsent(eq(mobile), eq("WECHAT_MINI_PROGRAM"))).thenReturn(user);

        assertThrows(ServiceException.class, () -> authService.wechatMiniProgramLogin(reqVO));
    }

    @Test
    public void getSocialAuthorizeUrl_acceptsTrustedProviderUrl() {
        Integer type = SocialTypeEnum.WECHAT_OPEN.getType();
        String redirectUri = "linbang://oauth-callback?type=" + type;
        String authorizeUrl = "https://open.weixin.qq.com/connect/qrconnect?state=state-1#wechat_redirect";
        when(socialClientApi.getAuthorizeUrl(eq(type), any(), eq(redirectUri))).thenReturn(authorizeUrl);

        assertEquals(authorizeUrl, authService.getSocialAuthorizeUrl(type, redirectUri));
    }

    @Test
    public void getSocialAuthorizeUrl_rejectsUntrustedProviderUrl() {
        Integer type = SocialTypeEnum.WECHAT_OPEN.getType();
        String redirectUri = "linbang://oauth-callback?type=" + type;
        when(socialClientApi.getAuthorizeUrl(eq(type), any(), eq(redirectUri)))
                .thenReturn("https://example.com/oauth?state=state-1");

        assertThrows(ServiceException.class, () -> authService.getSocialAuthorizeUrl(type, redirectUri));
    }

    @Test
    public void getSocialAuthorizeUrl_rejectsInvalidCallback() {
        assertThrows(ServiceException.class, () -> authService.getSocialAuthorizeUrl(
                SocialTypeEnum.WECHAT_OPEN.getType(), "https://example.com/callback"));
        verify(socialClientApi, never()).getAuthorizeUrl(any(), any(), any());
    }

    @Test
    public void getRegisterReminder_rejectsMissingAnonymousIdentity() {
        assertThrows(ServiceException.class, () -> authService.getRegisterReminder(null, null, null));
        assertThrows(ServiceException.class, () -> authService.getRegisterReminder(32, null, "device-1"));
        assertThrows(ServiceException.class, () -> authService.getRegisterReminder(null, "openid-1", null));
    }

    @Test
    public void checkAnonymousRateLimit_usesAtomicScriptAndRejectsOverflow() {
        doReturn(21L).when(stringRedisTemplate)
                .execute(any(RedisScript.class), anyList(), any());

        ServiceException ex = assertThrows(ServiceException.class, () -> ReflectionTestUtils.invokeMethod(
                authService, "checkAnonymousRateLimit", "account-login", "user", 20, 300L));

        assertEquals(429, ex.getCode());
        verify(stringRedisTemplate).execute(any(RedisScript.class), anyList(), any());
        verify(stringRedisTemplate, never()).expire(any(), anyLong(), any());
    }

    @Test
    public void checkAnonymousRateLimit_rejectsWhenRedisIsUnavailable() {
        doThrow(new IllegalStateException("redis unavailable")).when(stringRedisTemplate)
                .execute(any(RedisScript.class), anyList(), any());

        ServiceException ex = assertThrows(ServiceException.class, () -> ReflectionTestUtils.invokeMethod(
                authService, "checkAnonymousRateLimit", "account-login", "user", 20, 300L));

        assertEquals(503, ex.getCode());
    }

    @Test
    public void deleteRegistrationLicenseOwner_waitsForTransactionCommit() {
        TransactionSynchronizationManager.initSynchronization();
        TransactionSynchronizationManager.setActualTransactionActive(true);
        try {
            ReflectionTestUtils.invokeMethod(authService, "deleteRegistrationLicenseOwnerAfterCommit", 99L);
            verify(stringRedisTemplate, never()).delete("linbang:register-license:99");

            for (TransactionSynchronization synchronization
                    : TransactionSynchronizationManager.getSynchronizations()) {
                synchronization.afterCommit();
            }
            verify(stringRedisTemplate).delete("linbang:register-license:99");
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
            TransactionSynchronizationManager.setActualTransactionActive(false);
        }
    }

}
