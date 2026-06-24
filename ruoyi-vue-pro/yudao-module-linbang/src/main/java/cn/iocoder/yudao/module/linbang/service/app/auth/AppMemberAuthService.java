package cn.iocoder.yudao.module.linbang.service.app.auth;

import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberLoginRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSendSmsCodeReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialBindMobileReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.auth.vo.AppMemberSocialLoginReqVO;

import javax.validation.Valid;

public interface AppMemberAuthService {

    AppMemberLoginRespVO login(@Valid AppMemberLoginReqVO reqVO);

    String getSocialAuthorizeUrl(Integer type, String redirectUri);

    AppMemberLoginRespVO socialLogin(@Valid AppMemberSocialLoginReqVO reqVO);

    AppMemberLoginRespVO socialBindMobile(@Valid AppMemberSocialBindMobileReqVO reqVO);

    void sendSmsCode(@Valid AppMemberSendSmsCodeReqVO reqVO);

    void logout(String token);

    AppMemberLoginRespVO refreshToken(String refreshToken);
}
