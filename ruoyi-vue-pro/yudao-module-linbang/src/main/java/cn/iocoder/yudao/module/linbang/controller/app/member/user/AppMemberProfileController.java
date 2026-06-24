package cn.iocoder.yudao.module.linbang.controller.app.member.user;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.user.vo.AppMemberProfileUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 邻里个人资料")
@RestController
@RequestMapping("/member/user")
@Validated
public class AppMemberProfileController {

    @Resource
    private AppMemberProfileService appMemberProfileService;

    @GetMapping("/profile")
    @Operation(summary = "获取个人资料")
    public CommonResult<AppMemberProfileRespVO> getProfile() {
        return success(appMemberProfileService.getProfile(getLoginUserId()));
    }

    @PutMapping("/update-profile")
    @Operation(summary = "更新个人资料")
    public CommonResult<Boolean> updateProfile(@Valid @RequestBody AppMemberProfileUpdateReqVO reqVO) {
        appMemberProfileService.updateProfile(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }
}
