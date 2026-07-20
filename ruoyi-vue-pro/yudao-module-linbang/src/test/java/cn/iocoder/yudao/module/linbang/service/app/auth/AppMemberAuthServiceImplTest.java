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

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

}
