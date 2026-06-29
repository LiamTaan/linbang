package cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo.UserSensitiveCustomWordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.usersensitivecustomword.vo.UserSensitiveCustomWordRespVO;
import cn.iocoder.yudao.module.linbang.service.usersensitivecustomword.UserSensitiveCustomWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 用户自定义脱敏词")
@RestController
@RequestMapping("/risk/user-sensitive-custom-word")
@Validated
public class UserSensitiveCustomWordController {

    @Resource
    private UserSensitiveCustomWordService userSensitiveCustomWordService;

    @GetMapping("/page")
    @Operation(summary = "获取用户自定义脱敏词分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:user-sensitive-custom-word:query')")
    public CommonResult<PageResult<UserSensitiveCustomWordRespVO>> getPage(@Valid UserSensitiveCustomWordPageReqVO reqVO) {
        return success(userSensitiveCustomWordService.getPage(reqVO));
    }
}
