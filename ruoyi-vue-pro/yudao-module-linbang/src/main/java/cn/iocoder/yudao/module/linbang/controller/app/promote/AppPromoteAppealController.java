package cn.iocoder.yudao.module.linbang.controller.app.promote;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.promote.vo.AppPromoteAppealRespVO;
import cn.iocoder.yudao.module.linbang.service.promoteappeal.PromoteAppealService;
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

@Tag(name = "用户 App - 推广申诉")
@RestController
@RequestMapping("/promote/appeal")
@Validated
public class AppPromoteAppealController {

    @Resource
    private PromoteAppealService promoteAppealService;

    @PostMapping("/create")
    @Operation(summary = "提交推广申诉")
    public CommonResult<Long> create(@Valid @RequestBody AppPromoteAppealCreateReqVO reqVO) {
        return success(promoteAppealService.createAppAppeal(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询推广申诉")
    public CommonResult<PageResult<AppPromoteAppealRespVO>> getPage(@Valid AppPromoteAppealPageReqVO reqVO) {
        return success(promoteAppealService.getAppPage(getLoginUserId(), reqVO));
    }
}
