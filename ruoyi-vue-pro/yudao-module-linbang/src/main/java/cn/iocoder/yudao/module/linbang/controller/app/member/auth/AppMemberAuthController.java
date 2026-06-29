package cn.iocoder.yudao.module.linbang.controller.app.member.auth;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.OAuth2TokenCommonApi;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2SceneTicketCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2SceneTicketRespDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.config.SecurityProperties;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberAccountLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberAccountRegisterReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberRefreshTokenReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppRegisterReminderAckReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppRegisterReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSendSmsCodeReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSceneTicketCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSceneTicketRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialBindMobileReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppAgreementRespVO;
import cn.iocoder.yudao.module.linbang.service.app.auth.AppMemberAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 邻里认证")
@RestController
@RequestMapping("/member/auth")
@Validated
public class AppMemberAuthController {

    @Resource
    private AppMemberAuthService appMemberAuthService;
    @Resource
    private SecurityProperties securityProperties;
    @Resource
    private OAuth2TokenCommonApi oauth2TokenApi;

    @PostMapping("/login")
    @Operation(summary = "手机号验证码登录", description = "登录页和注册页共用本接口。入参为手机号和短信验证码；若手机号未注册，则校验验证码成功后自动完成注册并返回正式登录态。")
    @PermitAll
    public CommonResult<AppMemberLoginRespVO> login(@Valid @RequestBody AppMemberLoginReqVO reqVO) {
        return success(appMemberAuthService.login(reqVO));
    }

    @PostMapping("/account-login")
    @Operation(summary = "账号密码登录")
    @PermitAll
    public CommonResult<AppMemberLoginRespVO> accountLogin(@Valid @RequestBody AppMemberAccountLoginReqVO reqVO) {
        return success(appMemberAuthService.accountLogin(reqVO));
    }

    @PostMapping("/account-register")
    @Operation(summary = "账号注册")
    @PermitAll
    public CommonResult<AppMemberLoginRespVO> accountRegister(@Valid @RequestBody AppMemberAccountRegisterReqVO reqVO) {
        return success(appMemberAuthService.accountRegister(reqVO));
    }

    @GetMapping("/register-agreement/get")
    @Operation(summary = "获取注册协议")
    @PermitAll
    public CommonResult<AppAgreementRespVO> getRegisterAgreement() {
        return success(appMemberAuthService.getRegisterAgreement());
    }

    @GetMapping("/register-reminder/get")
    @Operation(summary = "获取未注册提醒")
    @PermitAll
    public CommonResult<AppRegisterReminderRespVO> getRegisterReminder(@RequestParam(value = "socialType", required = false) Integer socialType,
                                                                       @RequestParam(value = "socialOpenid", required = false) String socialOpenid,
                                                                       @RequestParam(value = "deviceId", required = false) String deviceId) {
        return success(appMemberAuthService.getRegisterReminder(socialType, socialOpenid, deviceId));
    }

    @PostMapping("/register-reminder/ack")
    @Operation(summary = "确认未注册提醒")
    @PermitAll
    public CommonResult<Boolean> ackRegisterReminder(@Valid @RequestBody AppRegisterReminderAckReqVO reqVO) {
        appMemberAuthService.ackRegisterReminder(reqVO);
        return success(Boolean.TRUE);
    }

    @GetMapping("/social-auth-redirect")
    @Operation(summary = "获取原生 App 第三方授权跳转地址",
            description = "当前仅支持原生 App 第三方授权：type=32 表示微信开放平台授权，type=40 表示支付宝授权。redirectUri 为 App 端接收授权回调的地址。")
    @Parameters({
            @Parameter(name = "type", description = "社交平台类型，仅支持 32=微信开放平台授权、40=支付宝授权", required = true),
            @Parameter(name = "redirectUri", description = "原生 App 授权回调地址", required = true)
    })
    @PermitAll
    public CommonResult<String> socialAuthRedirect(@RequestParam("type") Integer type,
                                                   @RequestParam("redirectUri") String redirectUri) {
        return success(appMemberAuthService.getSocialAuthorizeUrl(type, redirectUri));
    }

    @PostMapping("/social-login")
    @Operation(summary = "原生 App 第三方授权登录",
            description = "已绑定手机号的第三方账号将直接返回正式登录态；未绑定手机号时返回 bindRequired=true，此时 userId、accessToken、refreshToken、expiresTime 允许为空，前端需继续调用绑定手机号接口。")
    @PermitAll
    public CommonResult<AppMemberLoginRespVO> socialLogin(@Valid @RequestBody AppMemberSocialLoginReqVO reqVO) {
        return success(appMemberAuthService.socialLogin(reqVO));
    }

    @PostMapping("/social-bind-mobile")
    @Operation(summary = "第三方登录绑定手机号并完成注册/登录",
            description = "校验短信验证码后，将第三方账号绑定到手机号对应的正式账号。若手机号未注册则自动创建账号；若手机号已注册则绑定到既有账号；绑定成功后返回正式登录态。")
    @PermitAll
    public CommonResult<AppMemberLoginRespVO> socialBindMobile(@Valid @RequestBody AppMemberSocialBindMobileReqVO reqVO) {
        return success(appMemberAuthService.socialBindMobile(reqVO));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    @PermitAll
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            appMemberAuthService.logout(token);
        }
        return success(Boolean.TRUE);
    }

    @PostMapping("/send-sms-code")
    @Operation(summary = "发送短信登录/注册验证码",
            description = "短信登录和短信注册共用本接口。当前注册页复用手机号验证码登录接口，因此验证码用途统一为登录/注册。")
    @PermitAll
    public CommonResult<Boolean> sendSmsCode(@Valid @RequestBody AppMemberSendSmsCodeReqVO reqVO) {
        appMemberAuthService.sendSmsCode(reqVO);
        return success(Boolean.TRUE);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新令牌", description = "使用 refreshToken 换取新的正式登录态。仅在已获取正式登录态后可调用。")
    @PermitAll
    public CommonResult<AppMemberLoginRespVO> refreshToken(@RequestBody(required = false) AppMemberRefreshTokenReqVO body,
                                                           @RequestParam(value = "refreshToken", required = false) String refreshToken) {
        return success(appMemberAuthService.refreshToken(StrUtil.blankToDefault(
                body != null ? body.getRefreshToken() : null, refreshToken)));
    }

    @PostMapping("/scene-ticket")
    @Operation(summary = "创建 App 场景票据")
    public CommonResult<AppMemberSceneTicketRespVO> createSceneTicket(@RequestBody @Valid AppMemberSceneTicketCreateReqVO reqVO) {
        OAuth2SceneTicketCreateReqDTO createReqDTO = new OAuth2SceneTicketCreateReqDTO();
        createReqDTO.setScene(reqVO.getScene());
        createReqDTO.setUserId(SecurityFrameworkUtils.getLoginUserId());
        createReqDTO.setUserType(SecurityFrameworkUtils.getLoginUser() != null ? SecurityFrameworkUtils.getLoginUser().getUserType() : null);
        createReqDTO.setTenantId(SecurityFrameworkUtils.getLoginUser() != null ? SecurityFrameworkUtils.getLoginUser().getTenantId() : null);
        OAuth2SceneTicketRespDTO ticket = oauth2TokenApi.createSceneTicket(createReqDTO);
        return success(BeanUtils.toBean(ticket, AppMemberSceneTicketRespVO.class));
    }
}
