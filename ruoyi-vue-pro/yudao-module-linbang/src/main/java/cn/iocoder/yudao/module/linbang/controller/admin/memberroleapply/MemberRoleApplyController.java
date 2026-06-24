package cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyRespVO;
import cn.iocoder.yudao.module.linbang.service.memberroleapply.MemberRoleApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 身份申请")
@RestController
@RequestMapping("/member/role-apply")
@Validated
public class MemberRoleApplyController {

    @Resource
    private MemberRoleApplyService memberRoleApplyService;

    @GetMapping("/page")
    @Operation(summary = "获得身份申请分页")
    @PreAuthorize("@ss.hasPermission('linbang:member:role-apply:query')")
    public CommonResult<PageResult<MemberRoleApplyRespVO>> getRoleApplyPage(@Valid MemberRoleApplyPageReqVO reqVO) {
        return success(memberRoleApplyService.getRoleApplyPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得身份申请详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:member:role-apply:query')")
    public CommonResult<MemberRoleApplyDetailRespVO> getRoleApply(@RequestParam("id") Long id) {
        return success(memberRoleApplyService.getRoleApplyDetail(id));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核身份申请")
    @PreAuthorize("@ss.hasPermission('linbang:member:role-apply:audit')")
    public CommonResult<Boolean> auditRoleApply(@Valid @RequestBody MemberRoleApplyAuditReqVO reqVO) {
        memberRoleApplyService.auditRoleApply(reqVO);
        return success(true);
    }
}
