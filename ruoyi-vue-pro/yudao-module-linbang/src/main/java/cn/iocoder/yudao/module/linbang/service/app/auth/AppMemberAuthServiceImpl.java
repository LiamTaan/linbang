package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSendSmsCodeReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialBindMobileReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialLoginReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.system.api.logger.LoginLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.iocoder.yudao.module.system.api.social.SocialClientApi;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_DISABLED;

@Service
@Validated
public class AppMemberAuthServiceImpl implements AppMemberAuthService {

    private static final String REGISTER_SOURCE_APP_SMS = "APP_SMS";

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private SmsCodeApi smsCodeApi;
    @Resource
    private LoginLogApi loginLogApi;
    @Resource
    private OAuth2TokenCommonApi oauth2TokenCommonApi;
    @Resource
    private SocialUserApi socialUserApi;
    @Resource
    private SocialClientApi socialClientApi;

    @Override
    public AppMemberLoginRespVO login(AppMemberLoginReqVO reqVO) {
        try {
            smsCodeApi.useSmsCode(buildUseReq(reqVO));
        } catch (ServiceException ex) {
            createLoginLog(null, reqVO.getMobile(), LoginResultEnum.BAD_CREDENTIALS);
            throw ex;
        }

        MemberUserDO user = memberUserService.createMemberUserIfAbsent(reqVO.getMobile(), REGISTER_SOURCE_APP_SMS);
        if ("DISABLE".equals(user.getStatus())) {
            createLoginLog(user.getId(), reqVO.getMobile(), LoginResultEnum.USER_DISABLED);
            throw exception(MEMBER_USER_DISABLED);
        }

        createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.SUCCESS);
        memberUserService.updateMemberUserLogin(user.getId(), getClientIP());
        return buildLoginResp(user, null, null, null, null);
    }

    @Override
    public String getSocialAuthorizeUrl(Integer type, String redirectUri) {
        return socialClientApi.getAuthorizeUrl(type, UserTypeEnum.MEMBER.getValue(), redirectUri);
    }

    @Override
    public AppMemberLoginRespVO socialLogin(AppMemberSocialLoginReqVO reqVO) {
        SocialUserRespDTO socialUser = socialUserApi.getSocialUserByCode(UserTypeEnum.MEMBER.getValue(),
                reqVO.getType(), reqVO.getCode(), reqVO.getState());
        if (socialUser == null) {
            return AppMemberLoginRespVO.builder()
                    .bindRequired(Boolean.TRUE)
                    .socialType(reqVO.getType())
                    .build();
        }
        if (socialUser.getUserId() == null) {
            return AppMemberLoginRespVO.builder()
                    .bindRequired(Boolean.TRUE)
                    .socialType(reqVO.getType())
                    .socialOpenid(socialUser.getOpenid())
                    .socialNickname(socialUser.getNickname())
                    .socialAvatar(socialUser.getAvatar())
                    .build();
        }

        MemberUserDO user = memberUserService.getMemberUser(socialUser.getUserId());
        if (user == null) {
            return AppMemberLoginRespVO.builder()
                    .bindRequired(Boolean.TRUE)
                    .socialType(reqVO.getType())
                    .socialOpenid(socialUser.getOpenid())
                    .socialNickname(socialUser.getNickname())
                    .socialAvatar(socialUser.getAvatar())
                    .build();
        }
        if ("DISABLE".equals(user.getStatus())) {
            createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.USER_DISABLED, LoginLogTypeEnum.LOGIN_SOCIAL);
            throw exception(MEMBER_USER_DISABLED);
        }
        createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.SUCCESS, LoginLogTypeEnum.LOGIN_SOCIAL);
        memberUserService.updateMemberUserLogin(user.getId(), getClientIP());
        return buildLoginResp(user, reqVO.getType(), socialUser.getOpenid(), socialUser.getNickname(), socialUser.getAvatar());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMemberLoginRespVO socialBindMobile(AppMemberSocialBindMobileReqVO reqVO) {
        smsCodeApi.useSmsCode(buildUseReq(reqVO.getMobile(), reqVO.getCodeSms()));
        MemberUserDO user = memberUserService.createMemberUserIfAbsent(reqVO.getMobile(), "APP_SOCIAL");
        if ("DISABLE".equals(user.getStatus())) {
            createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.USER_DISABLED, LoginLogTypeEnum.LOGIN_SOCIAL);
            throw exception(MEMBER_USER_DISABLED);
        }
        String openid = socialUserApi.bindSocialUser(new SocialUserBindReqDTO(user.getId(),
                UserTypeEnum.MEMBER.getValue(), reqVO.getType(), reqVO.getCode(), reqVO.getState()));
        createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.SUCCESS, LoginLogTypeEnum.LOGIN_SOCIAL);
        memberUserService.updateMemberUserLogin(user.getId(), getClientIP());
        return buildLoginResp(user, reqVO.getType(), openid, null, null);
    }

    @Override
    public void sendSmsCode(AppMemberSendSmsCodeReqVO reqVO) {
        SmsCodeSendReqDTO sendReqDTO = new SmsCodeSendReqDTO();
        sendReqDTO.setMobile(reqVO.getMobile());
        sendReqDTO.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        sendReqDTO.setCreateIp(getClientIP());
        smsCodeApi.sendSmsCode(sendReqDTO);
    }

    @Override
    public void logout(String token) {
        OAuth2AccessTokenRespDTO accessToken = oauth2TokenCommonApi.removeAccessToken(token);
        if (accessToken == null) {
            return;
        }
        MemberUserDO user = memberUserService.getMemberUser(accessToken.getUserId());
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(LoginLogTypeEnum.LOGOUT_SELF.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(accessToken.getUserId());
        reqDTO.setUserType(UserTypeEnum.MEMBER.getValue());
        reqDTO.setUsername(user != null ? user.getMobile() : null);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(LoginResultEnum.SUCCESS.getResult());
        loginLogApi.createLoginLog(reqDTO);
    }

    @Override
    public AppMemberLoginRespVO refreshToken(String refreshToken) {
        OAuth2AccessTokenRespDTO token = oauth2TokenCommonApi.refreshAccessToken(refreshToken,
                OAuth2ClientConstants.CLIENT_ID_DEFAULT);
        return AppMemberLoginRespVO.builder()
                .userId(token.getUserId())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresTime(token.getExpiresTime())
                .bindRequired(Boolean.FALSE)
                .build();
    }

    private SmsCodeUseReqDTO buildUseReq(AppMemberLoginReqVO reqVO) {
        return buildUseReq(reqVO.getMobile(), reqVO.getCode());
    }

    private SmsCodeUseReqDTO buildUseReq(String mobile, String code) {
        SmsCodeUseReqDTO useReqDTO = new SmsCodeUseReqDTO();
        useReqDTO.setMobile(mobile);
        useReqDTO.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        useReqDTO.setCode(code);
        useReqDTO.setUsedIp(getClientIP());
        return useReqDTO;
    }

    private void createLoginLog(Long userId, String mobile, LoginResultEnum loginResult) {
        createLoginLog(userId, mobile, loginResult, LoginLogTypeEnum.LOGIN_SMS);
    }

    private void createLoginLog(Long userId, String mobile, LoginResultEnum loginResult, LoginLogTypeEnum loginType) {
        LoginLogCreateReqDTO reqDTO = new LoginLogCreateReqDTO();
        reqDTO.setLogType(loginType.getType());
        reqDTO.setTraceId(TracerUtils.getTraceId());
        reqDTO.setUserId(userId);
        reqDTO.setUserType(UserTypeEnum.MEMBER.getValue());
        reqDTO.setUsername(mobile);
        reqDTO.setUserAgent(ServletUtils.getUserAgent());
        reqDTO.setUserIp(getClientIP());
        reqDTO.setResult(loginResult.getResult());
        loginLogApi.createLoginLog(reqDTO);
    }

    private AppMemberLoginRespVO buildLoginResp(MemberUserDO user, Integer socialType,
                                                String socialOpenid, String socialNickname, String socialAvatar) {
        OAuth2AccessTokenRespDTO token = oauth2TokenCommonApi.createAccessToken(new OAuth2AccessTokenCreateReqDTO()
                .setUserId(user.getId())
                .setUserType(UserTypeEnum.MEMBER.getValue())
                .setClientId(OAuth2ClientConstants.CLIENT_ID_DEFAULT));
        return AppMemberLoginRespVO.builder()
                .userId(user.getId())
                .accessToken(token.getAccessToken())
                .refreshToken(token.getRefreshToken())
                .expiresTime(token.getExpiresTime())
                .bindRequired(Boolean.FALSE)
                .socialType(socialType)
                .socialOpenid(socialOpenid)
                .socialNickname(socialNickname)
                .socialAvatar(socialAvatar)
                .build();
    }
}
