package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo.AppMemberRoleContextRespVO;

import javax.validation.Valid;

public interface AppMemberRoleContextService {

    AppMemberRoleContextRespVO getRoleContext(Long authUserId);

    void switchRole(Long authUserId, @Valid String targetRoleCode);
}
