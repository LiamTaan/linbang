package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberAccountLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberAccountRegisterReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppRegisterReminderAckReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppRegisterReminderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSendSmsCodeReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialBindMobileReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppAgreementRespVO;

import javax.validation.Valid;

public interface AppMemberAuthService {

    AppMemberLoginRespVO login(@Valid AppMemberLoginReqVO reqVO);

    AppMemberLoginRespVO accountLogin(@Valid AppMemberAccountLoginReqVO reqVO);

    AppMemberLoginRespVO accountRegister(@Valid AppMemberAccountRegisterReqVO reqVO);

    String getSocialAuthorizeUrl(Integer type, String redirectUri);

    AppMemberLoginRespVO socialLogin(@Valid AppMemberSocialLoginReqVO reqVO);

    AppMemberLoginRespVO socialBindMobile(@Valid AppMemberSocialBindMobileReqVO reqVO);

    void sendSmsCode(@Valid AppMemberSendSmsCodeReqVO reqVO);

    void logout(String token);

    AppMemberLoginRespVO refreshToken(String refreshToken);

    AppAgreementRespVO getRegisterAgreement();

    AppRegisterReminderRespVO getRegisterReminder(Integer socialType, String socialOpenid, String deviceId);

    void ackRegisterReminder(@Valid AppRegisterReminderAckReqVO reqVO);
}
