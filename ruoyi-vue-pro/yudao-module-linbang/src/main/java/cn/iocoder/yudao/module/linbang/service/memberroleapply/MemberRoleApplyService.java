package cn.iocoder.yudao.module.linbang.service.memberroleapply;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyRespVO;

import javax.validation.Valid;

public interface MemberRoleApplyService {

    PageResult<MemberRoleApplyRespVO> getRoleApplyPage(MemberRoleApplyPageReqVO reqVO);

    MemberRoleApplyDetailRespVO getRoleApplyDetail(Long id);

    void auditRoleApply(@Valid MemberRoleApplyAuditReqVO reqVO);
}
