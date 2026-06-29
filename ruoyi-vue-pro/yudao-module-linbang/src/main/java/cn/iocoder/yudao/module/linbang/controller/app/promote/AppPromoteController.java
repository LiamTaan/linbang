package cn.iocoder.yudao.module.linbang.controller.app.promote;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageItemRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppCommissionPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppInviteCodeRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteInviteCodeBindReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteCenterRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTemplatePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTemplateRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteTeamStatsRespVO;
import cn.iocoder.yudao.module.linbang.service.app.promote.AppPromoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/team-stats/get")
    @Operation(summary = "获取两级分销统计")
    public CommonResult<AppPromoteTeamStatsRespVO> getTeamStats() {
        return success(appPromoteService.getTeamStats(getLoginUserId()));
    }

    @GetMapping("/template/page")
    @Operation(summary = "获取推广模板分页")
    public CommonResult<PageResult<AppPromoteTemplateRespVO>> getTemplatePage(@Valid AppPromoteTemplatePageReqVO reqVO) {
        return success(appPromoteService.getTemplatePage(getLoginUserId(), reqVO));
    }

    @GetMapping("/template/get")
    @Operation(summary = "获取推广模板详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppPromoteTemplateRespVO> getTemplate(Long id) {
        return success(appPromoteService.getTemplate(getLoginUserId(), id));
    }

    @PostMapping("/invite-code/bind")
    @Operation(summary = "绑定邀请码")
    public CommonResult<Boolean> bindInviteCode(@Valid @RequestBody AppPromoteInviteCodeBindReqVO reqVO) {
        appPromoteService.bindInviteCode(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }
}
