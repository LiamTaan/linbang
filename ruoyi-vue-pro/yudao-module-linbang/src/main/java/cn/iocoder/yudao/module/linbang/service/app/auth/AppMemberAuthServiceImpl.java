package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.DigestUtil;
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
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberWechatMiniProgramLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberRegistrationLicenseUploadReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberRegistrationLicenseUploadRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppAgreementRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.registerreminder.RegisterReminderRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.registerreminder.RegisterReminderRecordMapper;
import cn.iocoder.yudao.module.linbang.service.platformconfig.PlatformConfigService;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.infra.framework.file.core.utils.FileTypeUtils;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.system.api.logger.LoginLogApi;
import cn.iocoder.yudao.module.system.api.logger.dto.LoginLogCreateReqDTO;
import cn.iocoder.yudao.module.system.api.sms.SmsCodeApi;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import cn.iocoder.yudao.module.system.api.social.SocialClientApi;
import cn.iocoder.yudao.module.system.api.social.SocialUserApi;
import cn.iocoder.yudao.module.system.api.social.dto.SocialWxPhoneNumberInfoRespDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserRespDTO;
import cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum;
import cn.iocoder.yudao.module.system.enums.logger.LoginResultEnum;
import cn.iocoder.yudao.module.system.enums.oauth2.OAuth2ClientConstants;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import cn.iocoder.yudao.module.system.enums.social.SocialTypeEnum;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.dao.DuplicateKeyException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import cn.hutool.core.util.StrUtil;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.getClientIP;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_ACCOUNT_TYPE_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_BUSINESS_LICENSE_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_BUSINESS_LICENSE_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_DISABLED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_MOBILE_DUPLICATED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_PASSWORD_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_REGISTER_AGREEMENT_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_USERNAME_DUPLICATED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_SOCIAL_REDIRECT_URI_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.ORDER_ACCESS_DENIED;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.SOCIAL_CLIENT_WEIXIN_MINI_APP_PHONE_CODE_ERROR;
import static cn.iocoder.yudao.module.system.enums.logger.LoginLogTypeEnum.LOGIN_USERNAME;

@Service
@Validated
@Slf4j
public class AppMemberAuthServiceImpl implements AppMemberAuthService {

    private static final String REGISTER_SOURCE_APP_SMS = "APP_SMS";
    private static final String REGISTER_SOURCE_APP_ACCOUNT = "APP_ACCOUNT";
    private static final String REGISTER_SOURCE_APP_SOCIAL = "APP_SOCIAL";
    private static final String REGISTER_SOURCE_WECHAT_MINI_PROGRAM = "WECHAT_MINI_PROGRAM";
    private static final String REGISTRATION_LICENSE_OWNER_KEY_PREFIX = "linbang:register-license:";
    private static final long REGISTRATION_LICENSE_TTL_MINUTES = 15L;
    private static final String ANONYMOUS_RATE_KEY_PREFIX = "linbang:auth-rate:";
    private static final DefaultRedisScript<Long> ANONYMOUS_RATE_LIMIT_SCRIPT = createRateLimitScript();

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
    @Resource
    private FileService fileService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public AppMemberLoginRespVO login(AppMemberLoginReqVO reqVO) {
        checkAnonymousRateLimit("sms-login", reqVO.getMobile(), 30, 300);
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
        checkAnonymousRateLimit("account-login", reqVO.getAccount(), 20, 300);
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
        checkAnonymousRateLimit("account-register", reqVO.getMobile(), 10, 600);
        if (!Boolean.TRUE.equals(reqVO.getAgreementConfirmed())) {
            throw exception(MEMBER_USER_REGISTER_AGREEMENT_REQUIRED);
        }
        if (!"PERSONAL".equals(reqVO.getAccountType()) && !"ENTERPRISE".equals(reqVO.getAccountType())) {
            throw exception(MEMBER_USER_ACCOUNT_TYPE_INVALID);
        }
        if ("ENTERPRISE".equals(reqVO.getAccountType()) && reqVO.getBusinessLicenseFileId() == null) {
            throw exception(MEMBER_USER_BUSINESS_LICENSE_REQUIRED);
        }
        if ("ENTERPRISE".equals(reqVO.getAccountType())) {
            FileDO licenseFile;
            try {
                licenseFile = fileService.getFile(reqVO.getBusinessLicenseFileId());
            } catch (ServiceException ex) {
                throw exception(MEMBER_USER_BUSINESS_LICENSE_INVALID);
            }
            String ownerMobile = stringRedisTemplate.opsForValue()
                    .get(buildRegistrationLicenseOwnerKey(reqVO.getBusinessLicenseFileId()));
            if (licenseFile == null || !Objects.equals(ownerMobile, reqVO.getMobile())
                    || !StrUtil.startWithIgnoreCase(licenseFile.getType(), "image/")) {
                throw exception(MEMBER_USER_BUSINESS_LICENSE_INVALID);
            }
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
            deleteRegistrationLicenseOwnerAfterCommit(reqVO.getBusinessLicenseFileId());
        }
        createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.SUCCESS, LOGIN_USERNAME);
        memberUserService.updateMemberUserLogin(user.getId(), getClientIP());
        return buildLoginResp(user, null, null, null, null);
    }

    @Override
    public AppMemberRegistrationLicenseUploadRespVO uploadRegistrationLicense(
            AppMemberRegistrationLicenseUploadReqVO reqVO) throws Exception {
        checkAnonymousRateLimit("license-upload", reqVO.getMobile(), 10, 600);
        SmsCodeValidateReqDTO validateReqDTO = new SmsCodeValidateReqDTO();
        validateReqDTO.setMobile(reqVO.getMobile());
        validateReqDTO.setScene(SmsSceneEnum.MEMBER_LOGIN.getScene());
        validateReqDTO.setCode(reqVO.getSmsCode());
        smsCodeApi.validateSmsCode(validateReqDTO);

        if (reqVO.getFile().isEmpty() || reqVO.getFile().getSize() > FileService.MAX_FILE_SIZE_BYTES) {
            throw exception(MEMBER_USER_BUSINESS_LICENSE_INVALID);
        }
        byte[] content = IoUtil.readBytes(reqVO.getFile().getInputStream());
        String detectedType = FileTypeUtils.getMineType(content, reqVO.getFile().getOriginalFilename());
        if (!StrUtil.startWithIgnoreCase(detectedType, "image/")) {
            throw exception(MEMBER_USER_BUSINESS_LICENSE_INVALID);
        }
        FileDO file = fileService.createFileInfo(content, reqVO.getFile().getOriginalFilename(),
                "linbang/registration-license", detectedType);
        try {
            stringRedisTemplate.opsForValue().set(buildRegistrationLicenseOwnerKey(file.getId()), reqVO.getMobile(),
                    REGISTRATION_LICENSE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (RuntimeException ex) {
            cleanupUploadedRegistrationLicense(file.getId(), ex);
            throw ex;
        }
        AppMemberRegistrationLicenseUploadRespVO respVO = new AppMemberRegistrationLicenseUploadRespVO();
        respVO.setFileId(file.getId());
        respVO.setUrl(file.getUrl());
        return respVO;
    }

    private String buildRegistrationLicenseOwnerKey(Long fileId) {
        return REGISTRATION_LICENSE_OWNER_KEY_PREFIX + fileId;
    }

    private void deleteRegistrationLicenseOwnerAfterCommit(Long fileId) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()
                || !TransactionSynchronizationManager.isSynchronizationActive()) {
            deleteRegistrationLicenseOwner(fileId);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                deleteRegistrationLicenseOwner(fileId);
            }
        });
    }

    private void deleteRegistrationLicenseOwner(Long fileId) {
        try {
            stringRedisTemplate.delete(buildRegistrationLicenseOwnerKey(fileId));
        } catch (RuntimeException ex) {
            log.warn("[deleteRegistrationLicenseOwner][fileId({}) Redis cleanup failed]", fileId, ex);
        }
    }

    private void cleanupUploadedRegistrationLicense(Long fileId, RuntimeException original) {
        try {
            fileService.deleteFile(fileId);
        } catch (Exception cleanupEx) {
            original.addSuppressed(cleanupEx);
            log.error("[cleanupUploadedRegistrationLicense][fileId({}) cleanup failed]", fileId, cleanupEx);
        }
    }

    @Override
    public String getSocialAuthorizeUrl(Integer type, String redirectUri) {
        validateSocialRedirectUri(type, redirectUri);
        String authorizeUrl = socialClientApi.getAuthorizeUrl(type, UserTypeEnum.MEMBER.getValue(), redirectUri);
        validateSocialAuthorizeUrl(type, authorizeUrl);
        return authorizeUrl;
    }

    private void validateSocialRedirectUri(Integer type, String redirectUri) {
        if ((!Objects.equals(type, SocialTypeEnum.WECHAT_OPEN.getType())
                && !Objects.equals(type, SocialTypeEnum.ALIPAY_MINI_PROGRAM.getType()))
                || StrUtil.isBlank(redirectUri)) {
            throw exception(MEMBER_SOCIAL_REDIRECT_URI_INVALID);
        }
        try {
            URI uri = URI.create(redirectUri);
            if (!"linbang".equalsIgnoreCase(uri.getScheme())
                    || !"oauth-callback".equalsIgnoreCase(uri.getHost())
                    || StrUtil.isNotBlank(uri.getUserInfo())
                    || StrUtil.isNotBlank(uri.getFragment())
                    || !("type=" + type).equals(uri.getRawQuery())) {
                throw exception(MEMBER_SOCIAL_REDIRECT_URI_INVALID);
            }
        } catch (IllegalArgumentException ex) {
            throw exception(MEMBER_SOCIAL_REDIRECT_URI_INVALID);
        }
    }

    private void validateSocialAuthorizeUrl(Integer type, String authorizeUrl) {
        try {
            URI uri = URI.create(authorizeUrl);
            String host = uri.getHost();
            boolean allowedHost = Objects.equals(type, SocialTypeEnum.WECHAT_OPEN.getType())
                    ? "open.weixin.qq.com".equalsIgnoreCase(host)
                    : Objects.equals(type, SocialTypeEnum.ALIPAY_MINI_PROGRAM.getType())
                    && ("openauth.alipay.com".equalsIgnoreCase(host)
                    || "openauth.alipaydev.com".equalsIgnoreCase(host));
            if (!"https".equalsIgnoreCase(uri.getScheme()) || !allowedHost
                    || StrUtil.isNotBlank(uri.getUserInfo()) || (uri.getPort() != -1 && uri.getPort() != 443)) {
                throw exception(MEMBER_SOCIAL_REDIRECT_URI_INVALID);
            }
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw exception(MEMBER_SOCIAL_REDIRECT_URI_INVALID);
        }
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
    @Transactional(rollbackFor = Exception.class)
    public AppMemberLoginRespVO wechatMiniProgramLogin(AppMemberWechatMiniProgramLoginReqVO reqVO) {
        SocialWxPhoneNumberInfoRespDTO phoneInfo = socialClientApi.getWxMaPhoneNumberInfo(
                UserTypeEnum.MEMBER.getValue(), reqVO.getPhoneCode());
        String mobile = phoneInfo != null ? phoneInfo.getPurePhoneNumber() : null;
        if (cn.hutool.core.util.StrUtil.isBlank(mobile)) {
            throw exception(SOCIAL_CLIENT_WEIXIN_MINI_APP_PHONE_CODE_ERROR);
        }

        MemberUserDO user = memberUserService.createMemberUserIfAbsent(mobile, REGISTER_SOURCE_WECHAT_MINI_PROGRAM);
        if ("DISABLE".equals(user.getStatus())) {
            createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.USER_DISABLED, LoginLogTypeEnum.LOGIN_MOBILE);
            throw exception(MEMBER_USER_DISABLED);
        }
        String openid = socialUserApi.bindSocialUser(new SocialUserBindReqDTO(user.getId(),
                UserTypeEnum.MEMBER.getValue(), SocialTypeEnum.WECHAT_MINI_PROGRAM.getType(),
                reqVO.getLoginCode(), ""));
        createLoginLog(user.getId(), user.getMobile(), LoginResultEnum.SUCCESS, LoginLogTypeEnum.LOGIN_MOBILE);
        memberUserService.updateMemberUserLogin(user.getId(), getClientIP());
        return buildLoginResp(user, SocialTypeEnum.WECHAT_MINI_PROGRAM.getType(), openid, null, null);
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
    @Transactional(rollbackFor = Exception.class)
    public AppRegisterReminderRespVO getRegisterReminder(Integer socialType, String socialOpenid, String deviceId) {
        String reminderKey = buildReminderKey(socialType, socialOpenid, deviceId);
        checkAnonymousRateLimit("register-reminder", reminderKey, 30, 60);
        RegisterReminderRecordDO record = registerReminderRecordMapper.selectByReminderKey(reminderKey);
        if (record == null) {
            record = RegisterReminderRecordDO.builder()
                    .reminderKey(reminderKey)
                    .reminderScene(socialType != null ? "SOCIAL_UNREGISTERED" : "DEVICE_UNREGISTERED")
                    .deviceId(hashNullable(deviceId))
                    .socialType(socialType)
                    .socialOpenid(hashNullable(socialOpenid))
                    .triggerCount(0)
                    .cooldownMinutes(60)
                    .status("PENDING")
                    .build();
            try {
                registerReminderRecordMapper.insert(record);
            } catch (DuplicateKeyException ex) {
                record = registerReminderRecordMapper.selectByReminderKeyForUpdate(reminderKey);
                if (record == null) {
                    throw ex;
                }
            }
        }
        if (registerReminderRecordMapper.incrementTrigger(reminderKey, LocalDateTime.now()) != 1) {
            throw new ServiceException(500, "注册提醒记录更新失败，请稍后重试");
        }
        record = registerReminderRecordMapper.selectByReminderKeyForUpdate(reminderKey);
        if (record == null) {
            throw new ServiceException(500, "注册提醒记录创建失败，请稍后重试");
        }
        AppRegisterReminderRespVO respVO = new AppRegisterReminderRespVO();
        respVO.setRemindRequired(Boolean.TRUE);
        respVO.setReminderKey(reminderKey);
        respVO.setReminderScene(record.getReminderScene());
        respVO.setTitle("完成注册后才可继续");
        respVO.setContent("当前操作需要完成正式注册，请先绑定唯一手机号并确认注册协议。");
        respVO.setCooldownMinutes(record.getCooldownMinutes());
        respVO.setTriggerCount(record == null || record.getTriggerCount() == null ? 0 : record.getTriggerCount());
        respVO.setLastTriggerTime(record == null ? null : record.getLastTriggerTime());
        return respVO;
    }

    @Override
    public void ackRegisterReminder(AppRegisterReminderAckReqVO reqVO) {
        checkAnonymousRateLimit("register-reminder-ack", reqVO.getReminderKey(), 60, 60);
        registerReminderRecordMapper.acknowledge(reqVO.getReminderKey(), LocalDateTime.now());
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
        if (socialType != null) {
            if (StrUtil.isBlank(socialOpenid)) {
                throw new ServiceException(400, "传入社交平台类型时，第三方用户标识不能为空");
            }
            return "SOCIAL_" + socialType + "_" + DigestUtil.sha256Hex(socialOpenid);
        }
        if (StrUtil.isNotBlank(socialOpenid)) {
            throw new ServiceException(400, "传入第三方用户标识时，社交平台类型不能为空");
        }
        if (StrUtil.isBlank(deviceId)) {
            throw new ServiceException(400, "设备标识不能为空");
        }
        return "DEVICE_" + DigestUtil.sha256Hex(deviceId);
    }

    private String hashNullable(String value) {
        return StrUtil.isBlank(value) ? null : DigestUtil.sha256Hex(value);
    }

    private void checkAnonymousRateLimit(String scene, String subject, int maxAttempts, long windowSeconds) {
        String fingerprint = DigestUtil.sha256Hex(getClientIP() + "|" + StrUtil.blankToDefault(subject, "UNKNOWN"));
        String key = ANONYMOUS_RATE_KEY_PREFIX + scene + ":" + fingerprint;
        try {
            Long attempts = stringRedisTemplate.execute(ANONYMOUS_RATE_LIMIT_SCRIPT,
                    Collections.singletonList(key), String.valueOf(windowSeconds));
            if (attempts == null) {
                throw new IllegalStateException("Rate limit script returned no result");
            }
            if (attempts > maxAttempts) {
                throw new ServiceException(429, "请求过于频繁，请稍后再试");
            }
        } catch (ServiceException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("[checkAnonymousRateLimit][scene({}) rate limit storage unavailable]", scene, ex);
            throw new ServiceException(503, "服务繁忙，请稍后再试");
        }
    }

    private static DefaultRedisScript<Long> createRateLimitScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText("local current = redis.call('INCR', KEYS[1]); "
                + "if current == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; return current;");
        return script;
    }
}
