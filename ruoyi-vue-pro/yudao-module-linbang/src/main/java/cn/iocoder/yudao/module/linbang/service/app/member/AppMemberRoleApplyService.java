package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyRespVO;

import javax.validation.Valid;

public interface AppMemberRoleApplyService {

    Long createRoleApply(Long authUserId, @Valid AppMemberRoleApplyCreateReqVO reqVO);

    PageResult<AppMemberRoleApplyRespVO> getRoleApplyPage(Long authUserId, @Valid AppMemberRoleApplyPageReqVO reqVO);

    AppMemberRoleApplyRespVO getRoleApply(Long authUserId, Long id);
}
