package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.framework.common.enums.UserTypeEnum;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.util.monitor.TracerUtils;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberAccountLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberAccountRegisterReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppRegisterReminderAckReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppRegisterReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSendSmsCodeReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialBindMobileReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppAgreementRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.registerreminder.RegisterReminderRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.registerreminder.RegisterReminderRecordMapper;
import cn.iocoder.yudao.module.linbang.service.platformconfig.PlatformConfigService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_ACCOUNT_TYPE_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_BUSINESS_LICENSE_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_DISABLED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_MOBILE_DUPLICATED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_PASSWORD_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_REGISTER_AGREEMENT_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_USERNAME_DUPLICATED;
import static cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum.LOGIN_USERNAME;

@Service
@Validated
public class AppMemberAuthServiceImpl implements AppMemberAuthService {

    private static final String REGISTER_SOURCE_APP_SMS = "APP_SMS";
    private static final String REGISTER_SOURCE_APP_ACCOUNT = "APP_ACCOUNT";
    private static final String REGISTER_SOURCE_APP_SOCIAL = "APP_SOCIAL";

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
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private PlatformConfigService platformConfigService;
    @Resource
    private RegisterReminderRecordMapper registerReminderRecordMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;

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
    public AppMemberLoginRespVO accountLogin(AppMemberAccountLoginReqVO reqVO) {
        MemberUserDO user = resolveAccountUser(reqVO.getAccount());
        if (user == null || user.getPassword() == null || !passwordEncoder.matches(reqVO.getPassword(), user.getPassword())) {
            createLoginLog(null, reqVO.getAccount(), LoginResultEnum.BAD_CREDENTIALS, LOGIN_USERNAME);
            throw exception(MEMBER_USER_PASSWORD_INVALID);
        }
        if ("DISABLE".equals(user.getStatus())) {
            createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.USER_DISABLED, LOGIN_USERNAME);
            throw exception(MEMBER_USER_DISABLED);
        }
        createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.SUCCESS, LOGIN_USERNAME);
        memberUserService.updateMemberUserLogin(user.getId(), getClientIP());
        return buildLoginResp(user, null, null, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AppMemberLoginRespVO accountRegister(AppMemberAccountRegisterReqVO reqVO) {
        if (!Boolean.TRUE.equals(reqVO.getAgreementConfirmed())) {
            throw exception(MEMBER_USER_REGISTER_AGREEMENT_REQUIRED);
        }
        if (!"PERSONAL".equals(reqVO.getAccountType()) && !"ENTERPRISE".equals(reqVO.getAccountType())) {
            throw exception(MEMBER_USER_ACCOUNT_TYPE_INVALID);
        }
        if ("ENTERPRISE".equals(reqVO.getAccountType()) && reqVO.getBusinessLicenseFileId() == null) {
            throw exception(MEMBER_USER_BUSINESS_LICENSE_REQUIRED);
        }
        if (memberUserService.getMemberUserByUsername(reqVO.getUsername()) != null) {
            throw exception(MEMBER_USER_USERNAME_DUPLICATED);
        }
        MemberUserDO existedByMobile = memberUserService.getMemberUserByMobile(reqVO.getMobile());
        if (existedByMobile != null && existedByMobile.getPassword() != null) {
            throw exception(MEMBER_USER_MOBILE_DUPLICATED);
        }
        smsCodeApi.useSmsCode(buildUseReq(reqVO.getMobile(), reqVO.getSmsCode()));
        MemberUserDO user = memberUserService.registerMemberUser(reqVO.getUsername(), reqVO.getMobile(),
                passwordEncoder.encode(reqVO.getPassword()), reqVO.getAccountType(), REGISTER_SOURCE_APP_ACCOUNT,
                reqVO.getAccountType(), reqVO.getAgreementVersion(), LocalDateTime.now());
        if ("ENTERPRISE".equals(reqVO.getAccountType()) && reqVO.getBusinessLicenseFileId() != null) {
            memberUserQualificationMapper.insert(MemberUserQualificationDO.builder()
                    .userId(user.getId())
                    .qualificationType("BUSINESS_LICENSE")
                    .qualificationName("营业执照")
                    .fileId(reqVO.getBusinessLicenseFileId())
                    .auditStatus("PENDING")
                    .priorityEnabled(Boolean.FALSE)
                    .build());
        }
        createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.SUCCESS, LOGIN_USERNAME);
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
                    .registerReminder(getRegisterReminder(reqVO.getType(), null, null))
                    .build();
        }
        if (socialUser.getUserId() == null) {
            return AppMemberLoginRespVO.builder()
                    .bindRequired(Boolean.TRUE)
                    .socialType(reqVO.getType())
                    .socialOpenid(socialUser.getOpenid())
                    .socialNickname(socialUser.getNickname())
                    .socialAvatar(socialUser.getAvatar())
                    .registerReminder(getRegisterReminder(reqVO.getType(), socialUser.getOpenid(), null))
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
                    .registerReminder(getRegisterReminder(reqVO.getType(), socialUser.getOpenid(), null))
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
        MemberUserDO user = memberUserService.createMemberUserIfAbsent(reqVO.getMobile(), REGISTER_SOURCE_APP_SOCIAL);
        if ("DISABLE".equals(user.getStatus())) {
            createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.USER_DISABLED, LoginLogTypeEnum.LOGIN_SOCIAL);
            throw exception(MEMBER_USER_DISABLED);
        }
        memberUserService.updateRegisterAgreement(user.getId(),
                platformConfigService.getAgreement().getRegisterAgreementVersion(), LocalDateTime.now());
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

    @Override
    public AppAgreementRespVO getRegisterAgreement() {
        return platformConfigService.getAgreement();
    }

    @Override
    public AppRegisterReminderRespVO getRegisterReminder(Integer socialType, String socialOpenid, String deviceId) {
        String reminderKey = buildReminderKey(socialType, socialOpenid, deviceId);
        RegisterReminderRecordDO record = registerReminderRecordMapper.selectByReminderKey(reminderKey);
        if (record == null) {
            record = RegisterReminderRecordDO.builder()
                    .reminderKey(reminderKey)
                    .reminderScene(socialType != null ? "SOCIAL_UNREGISTERED" : "DEVICE_UNREGISTERED")
                    .deviceId(deviceId)
                    .socialType(socialType)
                    .socialOpenid(socialOpenid)
                    .triggerCount(0)
                    .cooldownMinutes(60)
                    .status("PENDING")
                    .build();
            registerReminderRecordMapper.insert(record);
        }
        registerReminderRecordMapper.updateById(RegisterReminderRecordDO.builder()
                .id(record.getId())
                .triggerCount(record.getTriggerCount() == null ? 1 : record.getTriggerCount() + 1)
                .lastTriggerTime(LocalDateTime.now())
                .status("PENDING")
                .build());
        AppRegisterReminderRespVO respVO = new AppRegisterReminderRespVO();
        respVO.setRemindRequired(Boolean.TRUE);
        respVO.setReminderKey(reminderKey);
        respVO.setReminderScene(record.getReminderScene());
        respVO.setTitle("完成注册后才可继续");
        respVO.setContent("当前操作需要完成正式注册，请先绑定唯一手机号并确认注册协议。");
        respVO.setCooldownMinutes(record.getCooldownMinutes());
        respVO.setTriggerCount((record.getTriggerCount() == null ? 0 : record.getTriggerCount()) + 1);
        respVO.setLastTriggerTime(LocalDateTime.now());
        return respVO;
    }

    @Override
    public void ackRegisterReminder(AppRegisterReminderAckReqVO reqVO) {
        RegisterReminderRecordDO record = registerReminderRecordMapper.selectByReminderKey(reqVO.getReminderKey());
        if (record == null) {
            return;
        }
        registerReminderRecordMapper.updateById(RegisterReminderRecordDO.builder()
                .id(record.getId())
                .status("ACKED")
                .lastAckTime(LocalDateTime.now())
                .build());
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

    private MemberUserDO resolveAccountUser(String account) {
        MemberUserDO user = memberUserService.getMemberUserByUsername(account);
        return user != null ? user : memberUserService.getMemberUserByMobile(account);
    }

    private String buildReminderKey(Integer socialType, String socialOpenid, String deviceId) {
        if (socialType != null && socialOpenid != null) {
            return "SOCIAL_" + socialType + "_" + socialOpenid;
        }
        return "DEVICE_" + (deviceId == null ? "UNKNOWN" : deviceId);
    }
}
