package cn.iocoder.yudao.module.linbang.controller.app.merchant.prioritypool;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.prioritypool.vo.AppPriorityPoolCurrentRespVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantPriorityFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 服务商优先池")
@RestController
@RequestMapping("/merchant/priority-pool")
@Validated
public class AppPriorityPoolController {

    @Resource
    private AppMerchantPriorityFacadeService appMerchantPriorityFacadeService;

    @GetMapping("/current")
    @Operation(summary = "获取当前服务商优先池状态")
    public CommonResult<AppPriorityPoolCurrentRespVO> getCurrent() {
        return success(appMerchantPriorityFacadeService.getCurrentPriorityPool(getLoginUserId()));
    }
}
