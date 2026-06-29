package cn.iocoder.yudao.module.linbang.controller.app.merchant.dispatchsetting;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.dispatchsetting.vo.AppMerchantDispatchSettingRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.dispatchsetting.vo.AppMerchantDispatchSettingUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 服务商派单设置")
@RestController
@RequestMapping("/merchant/dispatch-setting")
@Validated
public class AppMerchantDispatchSettingController {

    @Resource
    private AppMerchantInfoService appMerchantInfoService;

    @GetMapping("/get")
    @Operation(summary = "获取当前服务商派单设置")
    public CommonResult<AppMerchantDispatchSettingRespVO> getDispatchSetting() {
        return success(appMerchantInfoService.getDispatchSetting(getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新当前服务商派单设置")
    public CommonResult<Boolean> updateDispatchSetting(@Valid @RequestBody AppMerchantDispatchSettingUpdateReqVO reqVO) {
        appMerchantInfoService.updateDispatchSetting(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }
}
