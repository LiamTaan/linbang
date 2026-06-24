package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileUpdateReqVO;

import javax.validation.Valid;

public interface AppMemberProfileService {

    AppMemberProfileRespVO getProfile(Long authUserId);

    void updateProfile(Long authUserId, @Valid AppMemberProfileUpdateReqVO reqVO);
}
