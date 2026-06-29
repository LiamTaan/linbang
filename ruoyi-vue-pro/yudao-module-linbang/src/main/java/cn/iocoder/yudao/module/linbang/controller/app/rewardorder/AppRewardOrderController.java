package cn.iocoder.yudao.module.linbang.controller.app.rewardorder;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipationPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderParticipationRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.rewardorder.vo.AppRewardOrderRespVO;
import cn.iocoder.yudao.module.linbang.service.app.rewardorder.AppRewardOrderService;
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

@Tag(name = "用户 App - 我的悬赏")
@RestController
@RequestMapping("/reward-order")
@Validated
public class AppRewardOrderController {

    @Resource
    private AppRewardOrderService appRewardOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建我发布的悬赏")
    public CommonResult<Long> createRewardOrder(@Valid @RequestBody AppRewardOrderCreateReqVO reqVO) {
        return success(appRewardOrderService.createRewardOrder(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取我发布的悬赏")
    public CommonResult<PageResult<AppRewardOrderRespVO>> getRewardOrderPage(@Valid AppRewardOrderPageReqVO reqVO) {
        return success(appRewardOrderService.getRewardOrderPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取悬赏详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppRewardOrderRespVO> getRewardOrder(@RequestParam("id") Long id) {
        return success(appRewardOrderService.getRewardOrder(getLoginUserId(), id));
    }

    @PostMapping("/participate")
    @Operation(summary = "参与悬赏")
    public CommonResult<Long> participateRewardOrder(@Valid @RequestBody AppRewardOrderParticipateReqVO reqVO) {
        return success(appRewardOrderService.participateRewardOrder(getLoginUserId(), reqVO));
    }

    @GetMapping("/participated/page")
    @Operation(summary = "分页获取我参与的悬赏")
    public CommonResult<PageResult<AppRewardOrderParticipationRespVO>> getParticipatedRewardOrderPage(
            @Valid AppRewardOrderParticipationPageReqVO reqVO) {
        return success(appRewardOrderService.getParticipatedRewardOrderPage(getLoginUserId(), reqVO));
    }
}
