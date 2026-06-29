package cn.iocoder.yudao.module.linbang.controller.app.merchant.category;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo.AppMerchantServiceCategoryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo.AppMerchantSelectedCategoryUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantServiceCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 服务类目")
@RestController
@RequestMapping("/merchant/service-category")
@Validated
public class AppMerchantServiceCategoryController {

    @Resource
    private AppMerchantServiceCategoryService appMerchantServiceCategoryService;

    @GetMapping("/list")
    @Operation(summary = "获取服务类目列表")
    public CommonResult<List<AppMerchantServiceCategoryRespVO>> getCategoryList(
            @RequestParam(value = "keyword", required = false) String keyword) {
        return success(appMerchantServiceCategoryService.getCategoryList(keyword));
    }

    @PutMapping("/selected/update")
    @Operation(summary = "更新当前服务商已选类目")
    public CommonResult<Boolean> updateSelectedCategories(@Valid @RequestBody AppMerchantSelectedCategoryUpdateReqVO reqVO) {
        appMerchantServiceCategoryService.updateSelectedCategories(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

}
