package cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo.AppMemberRoleContextRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.rolecontext.vo.AppMemberRoleSwitchReqVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberRoleContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 角色上下文")
@RestController
@RequestMapping("/member/role-context")
@Validated
public class AppMemberRoleContextController {

    @Resource
    private AppMemberRoleContextService appMemberRoleContextService;

    @GetMapping("/get")
    @Operation(summary = "获取当前角色上下文")
    public CommonResult<AppMemberRoleContextRespVO> getRoleContext() {
        return success(appMemberRoleContextService.getRoleContext(getLoginUserId()));
    }

    @PostMapping("/switch")
    @Operation(summary = "切换当前生效角色")
    public CommonResult<Boolean> switchRole(@Valid @RequestBody AppMemberRoleSwitchReqVO reqVO) {
        appMemberRoleContextService.switchRole(getLoginUserId(), reqVO.getTargetRoleCode());
        return success(Boolean.TRUE);
    }
}
