package cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantSubAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 商户子账号")
@RestController
@RequestMapping("/merchant/sub-account")
@Validated
public class AppMerchantSubAccountController {

    @Resource
    private AppMerchantSubAccountService appMerchantSubAccountService;

    @PostMapping("/create")
    @Operation(summary = "创建子账号")
    public CommonResult<Long> create(@Valid @RequestBody AppMerchantSubAccountCreateReqVO reqVO) {
        return success(appMerchantSubAccountService.create(getLoginUserId(), reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改子账号")
    public CommonResult<Boolean> update(@Valid @RequestBody AppMerchantSubAccountUpdateReqVO reqVO) {
        appMerchantSubAccountService.update(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @GetMapping("/page")
    @Operation(summary = "分页获取子账号")
    public CommonResult<PageResult<AppMerchantSubAccountRespVO>> page() {
        return success(appMerchantSubAccountService.page(getLoginUserId()));
    }

    @PutMapping("/status/update")
    @Operation(summary = "更新子账号状态")
    public CommonResult<Boolean> updateStatus(@Valid @RequestBody AppMerchantSubAccountStatusUpdateReqVO reqVO) {
        appMerchantSubAccountService.updateStatus(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @PutMapping("/service-point/update")
    @Operation(summary = "更新子账号关联服务点")
    public CommonResult<Boolean> updateServicePoints(@Valid @RequestBody AppMerchantSubAccountServicePointUpdateReqVO reqVO) {
        appMerchantSubAccountService.updateServicePoints(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }
}
