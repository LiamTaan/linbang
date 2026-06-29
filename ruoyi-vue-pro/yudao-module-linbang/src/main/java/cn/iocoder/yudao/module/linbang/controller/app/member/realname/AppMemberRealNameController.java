package cn.iocoder.yudao.module.linbang.controller.app.member.realname;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameProgressRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.realname.vo.AppMemberRealNameStartVerifyRespVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberRealNameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 实名认证")
@RestController
@RequestMapping("/member/real-name")
@Validated
public class AppMemberRealNameController {

    @Resource
    private AppMemberRealNameService appMemberRealNameService;

    @PostMapping("/start-verify")
    @Operation(summary = "发起实名认证核验")
    public CommonResult<AppMemberRealNameStartVerifyRespVO> startVerify() {
        return success(appMemberRealNameService.startVerify(getLoginUserId()));
    }

    @PostMapping("/create-or-update")
    @Operation(summary = "提交或更新实名认证")
    public CommonResult<Long> createRealName(@Valid @RequestBody AppMemberRealNameCreateReqVO reqVO) {
        return success(appMemberRealNameService.createOrUpdateRealName(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取实名认证信息")
    public CommonResult<AppMemberRealNameRespVO> getRealName() {
        return success(appMemberRealNameService.getRealName(getLoginUserId()));
    }

    @GetMapping("/progress/get")
    @Operation(summary = "获取实名认证进度")
    public CommonResult<AppMemberRealNameProgressRespVO> getProgress() {
        return success(appMemberRealNameService.getProgress(getLoginUserId()));
    }
}
