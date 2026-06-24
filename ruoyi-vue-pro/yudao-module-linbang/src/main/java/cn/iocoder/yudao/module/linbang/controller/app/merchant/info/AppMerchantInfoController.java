package cn.iocoder.yudao.module.linbang.controller.app.merchant.info;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileUpdateReqVO;
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

@Tag(name = "用户 App - 服务商信息")
@RestController
@RequestMapping("/merchant/info")
@Validated
public class AppMerchantInfoController {

    @Resource
    private AppMerchantInfoService appMerchantInfoService;

    @GetMapping("/profile")
    @Operation(summary = "获取当前服务商资料总览")
    public CommonResult<AppMerchantProfileRespVO> getProfile() {
        return success(appMerchantInfoService.getProfile(getLoginUserId()));
    }

    @PutMapping("/update-profile")
    @Operation(summary = "更新当前服务商资料")
    public CommonResult<Boolean> updateProfile(@Valid @RequestBody AppMerchantProfileUpdateReqVO reqVO) {
        appMerchantInfoService.updateProfile(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @GetMapping("/accept-status/get")
    @Operation(summary = "获取当前服务商接单状态")
    public CommonResult<AppMerchantAcceptStatusRespVO> getAcceptStatus() {
        return success(appMerchantInfoService.getAcceptStatus(getLoginUserId()));
    }

    @PutMapping("/accept-status/update")
    @Operation(summary = "更新当前服务商接单状态")
    public CommonResult<Boolean> updateAcceptStatus(@Valid @RequestBody AppMerchantAcceptStatusUpdateReqVO reqVO) {
        appMerchantInfoService.updateAcceptStatus(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

}
