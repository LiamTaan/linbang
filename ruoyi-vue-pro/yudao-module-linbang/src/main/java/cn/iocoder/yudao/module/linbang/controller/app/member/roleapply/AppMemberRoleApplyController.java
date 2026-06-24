package cn.iocoder.yudao.module.linbang.controller.app.member.roleapply;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyRespVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberRoleApplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 身份申请")
@RestController
@RequestMapping("/member/role-apply")
@Validated
public class AppMemberRoleApplyController {

    @Resource
    private AppMemberRoleApplyService appMemberRoleApplyService;

    @PostMapping("/create")
    @Operation(summary = "提交身份申请")
    public CommonResult<Long> createRoleApply(@Valid @RequestBody AppMemberRoleApplyCreateReqVO reqVO) {
        return success(appMemberRoleApplyService.createRoleApply(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获取身份申请分页")
    public CommonResult<PageResult<AppMemberRoleApplyRespVO>> getRoleApplyPage(@Valid AppMemberRoleApplyPageReqVO reqVO) {
        return success(appMemberRoleApplyService.getRoleApplyPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取身份申请详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppMemberRoleApplyRespVO> getRoleApply(@RequestParam("id") Long id) {
        return success(appMemberRoleApplyService.getRoleApply(getLoginUserId(), id));
    }
}
