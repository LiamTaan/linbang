package cn.iocoder.yudao.module.linbang.controller.app.merchant.entry;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantEntryCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantEntryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantOnboardingProgressRespVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantEntryService;
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

@Tag(name = "用户 App - 服务商入驻")
@RestController
@RequestMapping("/merchant/entry")
@Validated
public class AppMerchantEntryController {

    @Resource
    private AppMerchantEntryService appMerchantEntryService;

    @PostMapping("/create")
    @Operation(summary = "提交服务商入驻")
    public CommonResult<Long> createEntry(@Valid @RequestBody AppMerchantEntryCreateReqVO reqVO) {
        return success(appMerchantEntryService.createEntry(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取当前用户最新入驻申请")
    public CommonResult<AppMerchantEntryRespVO> getCurrentEntry() {
        return success(appMerchantEntryService.getCurrentEntry(getLoginUserId()));
    }

    @GetMapping("/onboarding/progress/get")
    @Operation(summary = "获取入驻进度")
    public CommonResult<AppMerchantOnboardingProgressRespVO> getOnboardingProgress() {
        return success(appMerchantEntryService.getOnboardingProgress(getLoginUserId()));
    }

}
