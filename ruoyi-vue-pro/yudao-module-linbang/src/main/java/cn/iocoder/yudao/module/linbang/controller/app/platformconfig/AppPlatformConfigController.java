package cn.iocoder.yudao.module.linbang.controller.app.platformconfig;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppAgreementRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.platformconfig.vo.AppPlatformSettingsRespVO;
import cn.iocoder.yudao.module.linbang.service.platformconfig.PlatformConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 平台配置")
@RestController
@RequestMapping("/platform-config")
@Validated
public class AppPlatformConfigController {

    @Resource
    private PlatformConfigService platformConfigService;

    @GetMapping("/app-settings")
    @Operation(summary = "获取 App 设置")
    public CommonResult<AppPlatformSettingsRespVO> getAppSettings() {
        return success(platformConfigService.getAppSettings());
    }

    @GetMapping("/agreement")
    @Operation(summary = "获取协议文案")
    public CommonResult<AppAgreementRespVO> getAgreement() {
        return success(platformConfigService.getAgreement());
    }
}
