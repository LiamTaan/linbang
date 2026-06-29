package cn.iocoder.yudao.module.linbang.controller.app.benefit;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.benefit.vo.AppBenefitOverviewRespVO;
import cn.iocoder.yudao.module.linbang.service.app.benefit.AppBenefitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 我的权益")
@RestController
@RequestMapping("/benefit")
@Validated
public class AppBenefitController {

    @Resource
    private AppBenefitService appBenefitService;

    @GetMapping("/overview/get")
    @Operation(summary = "获取我的权益总览")
    public CommonResult<AppBenefitOverviewRespVO> getBenefitOverview() {
        return success(appBenefitService.getBenefitOverview(getLoginUserId()));
    }
}
