package cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.merchantsubaccount.MerchantSubAccountAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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

@Tag(name = "管理后台 - 商户子账号")
@RestController
@RequestMapping("/merchant/sub-account")
@Validated
public class MerchantSubAccountController {

    @Resource
    private MerchantSubAccountAdminService merchantSubAccountAdminService;

    @PostMapping("/create")
    @Operation(summary = "创建商户子账号")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:sub-account:create')")
    public CommonResult<Long> create(@Valid @RequestBody MerchantSubAccountSaveReqVO reqVO) {
        return success(merchantSubAccountAdminService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改商户子账号")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:sub-account:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MerchantSubAccountSaveReqVO reqVO) {
        merchantSubAccountAdminService.update(reqVO);
        return success(Boolean.TRUE);
    }

    @GetMapping("/page")
    @Operation(summary = "获得商户子账号分页")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:sub-account:query')")
    public CommonResult<PageResult<MerchantSubAccountRespVO>> getPage(@Valid MerchantSubAccountPageReqVO reqVO) {
        return success(merchantSubAccountAdminService.getPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得商户子账号详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:merchant:sub-account:query')")
    public CommonResult<MerchantSubAccountRespVO> get(@RequestParam("id") Long id) {
        return success(merchantSubAccountAdminService.get(id));
    }

    @PutMapping("/status/update")
    @Operation(summary = "更新商户子账号状态")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:sub-account:update')")
    public CommonResult<Boolean> updateStatus(@Valid @RequestBody MerchantSubAccountStatusUpdateReqVO reqVO) {
        merchantSubAccountAdminService.updateStatus(reqVO);
        return success(Boolean.TRUE);
    }

    @PutMapping("/service-point/update")
    @Operation(summary = "更新商户子账号关联服务点")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:sub-account:update')")
    public CommonResult<Boolean> updateServicePoints(@Valid @RequestBody MerchantSubAccountServicePointUpdateReqVO reqVO) {
        merchantSubAccountAdminService.updateServicePoints(reqVO);
        return success(Boolean.TRUE);
    }
}
