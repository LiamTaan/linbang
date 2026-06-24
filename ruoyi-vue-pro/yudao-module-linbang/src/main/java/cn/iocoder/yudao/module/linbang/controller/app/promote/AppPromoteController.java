package cn.iocoder.yudao.module.linbang.controller.app.promote;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageItemRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppInviteCodeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteCenterRespVO;
import cn.iocoder.yudao.module.linbang.service.app.promote.AppPromoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 推广中心")
@RestController
@RequestMapping("/promote")
@Validated
public class AppPromoteController {

    @Resource
    private AppPromoteService appPromoteService;

    @GetMapping("/center/get")
    @Operation(summary = "获取推广中心")
    public CommonResult<AppPromoteCenterRespVO> getPromoteCenter() {
        return success(appPromoteService.getPromoteCenter(getLoginUserId()));
    }

    @GetMapping("/commission/page")
    @Operation(summary = "获取佣金分页")
    public CommonResult<PageResult<AppCommissionPageItemRespVO>> getCommissionPage(@Valid AppCommissionPageReqVO reqVO) {
        return success(BeanUtils.toBean(appPromoteService.getCommissionPage(getLoginUserId(), reqVO), AppCommissionPageItemRespVO.class));
    }

    @GetMapping("/invite-code/get")
    @Operation(summary = "获取邀请码")
    public CommonResult<AppInviteCodeRespVO> getInviteCode() {
        return success(appPromoteService.getInviteCode(getLoginUserId()));
    }
}
