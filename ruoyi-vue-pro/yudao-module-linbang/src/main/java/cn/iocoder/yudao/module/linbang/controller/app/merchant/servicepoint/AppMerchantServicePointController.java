package cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantServicePointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 服务点")
@RestController
@RequestMapping("/merchant/service-point")
@Validated
public class AppMerchantServicePointController {

    @Resource
    private AppMerchantServicePointService appMerchantServicePointService;

    @PostMapping("/create")
    @Operation(summary = "新增服务点")
    public CommonResult<Long> createServicePoint(@Valid @RequestBody AppMerchantServicePointCreateReqVO reqVO) {
        return success(appMerchantServicePointService.createServicePoint(getLoginUserId(), reqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获取服务点分页")
    public CommonResult<PageResult<AppMerchantServicePointRespVO>> getServicePointPage(@Valid AppMerchantServicePointPageReqVO reqVO) {
        return success(appMerchantServicePointService.getServicePointPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取服务点详情")
    public CommonResult<AppMerchantServicePointRespVO> getServicePoint(@RequestParam("id") Long id) {
        return success(appMerchantServicePointService.getServicePoint(getLoginUserId(), id));
    }

    @PutMapping("/update")
    @Operation(summary = "更新服务点")
    public CommonResult<Boolean> updateServicePoint(@Valid @RequestBody AppMerchantServicePointUpdateReqVO reqVO) {
        appMerchantServicePointService.updateServicePoint(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @PutMapping("/status/update")
    @Operation(summary = "更新服务点状态")
    public CommonResult<Boolean> updateServicePointStatus(@Valid @RequestBody AppMerchantServicePointStatusUpdateReqVO reqVO) {
        appMerchantServicePointService.updateServicePointStatus(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除服务点")
    @Parameter(name = "id", description = "服务点 ID", required = true)
    public CommonResult<Boolean> deleteServicePoint(@RequestParam("id") Long id) {
        appMerchantServicePointService.deleteServicePoint(getLoginUserId(), id);
        return success(Boolean.TRUE);
    }

}
