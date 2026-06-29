package cn.iocoder.yudao.module.linbang.controller.app.member.security;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberLoginLogPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberLoginLogRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppMemberSecurityUpdatePasswordReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.security.vo.AppUserSensitiveCustomWordStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberSecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 安全中心")
@RestController
@RequestMapping("/member/security")
@Validated
public class AppMemberSecurityController {

    @Resource
    private AppMemberSecurityService appMemberSecurityService;

    @PutMapping("/password/update")
    @Operation(summary = "修改当前会员密码")
    public CommonResult<Boolean> updatePassword(@Valid @RequestBody AppMemberSecurityUpdatePasswordReqVO reqVO) {
        appMemberSecurityService.updatePassword(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @GetMapping("/login-log/page")
    @Operation(summary = "分页获取当前会员登录记录")
    public CommonResult<PageResult<AppMemberLoginLogRespVO>> getLoginLogPage(@Valid AppMemberLoginLogPageReqVO reqVO) {
        return success(appMemberSecurityService.getLoginLogPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/sensitive-custom-word/page")
    @Operation(summary = "分页获取当前会员的自定义脱敏词")
    public CommonResult<PageResult<AppUserSensitiveCustomWordRespVO>> getSensitiveCustomWordPage(
            @Valid AppUserSensitiveCustomWordPageReqVO reqVO) {
        return success(appMemberSecurityService.getSensitiveCustomWordPage(getLoginUserId(), reqVO));
    }

    @PostMapping("/sensitive-custom-word/create")
    @Operation(summary = "新增当前会员的自定义脱敏词")
    public CommonResult<Long> createSensitiveCustomWord(@Valid @RequestBody AppUserSensitiveCustomWordCreateReqVO reqVO) {
        return success(appMemberSecurityService.createSensitiveCustomWord(getLoginUserId(), reqVO));
    }

    @DeleteMapping("/sensitive-custom-word/delete")
    @Operation(summary = "删除当前会员的自定义脱敏词")
    @Parameter(name = "id", required = true, description = "编号")
    public CommonResult<Boolean> deleteSensitiveCustomWord(@RequestParam("id") Long id) {
        appMemberSecurityService.deleteSensitiveCustomWord(getLoginUserId(), id);
        return success(Boolean.TRUE);
    }

    @PutMapping("/sensitive-custom-word/status")
    @Operation(summary = "启停当前会员的自定义脱敏词")
    public CommonResult<Boolean> updateSensitiveCustomWordStatus(
            @Valid @RequestBody AppUserSensitiveCustomWordStatusUpdateReqVO reqVO) {
        appMemberSecurityService.updateSensitiveCustomWordStatus(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }
}
