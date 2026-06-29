package cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.showcasereward.vo.AppShowcaseRewardRespVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantPriorityFacadeService;
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

@Tag(name = "用户 App - 服务商晒单悬赏")
@RestController
@RequestMapping("/merchant/showcase/reward")
@Validated
public class AppShowcaseRewardController {

    @Resource
    private AppMerchantPriorityFacadeService appMerchantPriorityFacadeService;

    @PostMapping("/create")
    @Operation(summary = "创建晒单悬赏申请")
    public CommonResult<Long> create(@Valid @RequestBody AppShowcaseRewardCreateReqVO reqVO) {
        return success(appMerchantPriorityFacadeService.createShowcaseReward(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取当前服务商晒单悬赏申请")
    public CommonResult<PageResult<AppShowcaseRewardRespVO>> page() {
        return success(appMerchantPriorityFacadeService.getShowcaseRewardPage(getLoginUserId()));
    }
}
