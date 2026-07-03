package cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.merchant.AppMerchantReferencePriceService;
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
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 服务商参考价格")
@RestController
@RequestMapping("/merchant/reference-price")
@Validated
public class AppMerchantReferencePriceController {

    @Resource
    private AppMerchantReferencePriceService appMerchantReferencePriceService;

    @PostMapping("/create")
    @Operation(summary = "新增参考价格")
    public CommonResult<Long> create(@Valid @RequestBody AppMerchantReferencePriceCreateReqVO reqVO) {
        return success(appMerchantReferencePriceService.create(getLoginUserId(), reqVO));
    }

    @GetMapping("/list")
    @Operation(summary = "获取参考价格列表")
    public CommonResult<List<AppMerchantReferencePriceRespVO>> getList() {
        return success(appMerchantReferencePriceService.getList(getLoginUserId()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新参考价格")
    public CommonResult<Boolean> update(@Valid @RequestBody AppMerchantReferencePriceUpdateReqVO reqVO) {
        appMerchantReferencePriceService.update(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @PutMapping("/status/update")
    @Operation(summary = "更新参考价格状态")
    public CommonResult<Boolean> updateStatus(@Valid @RequestBody AppMerchantReferencePriceStatusUpdateReqVO reqVO) {
        appMerchantReferencePriceService.updateStatus(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除参考价格")
    @Parameter(name = "id", description = "参考价格 ID", required = true)
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        appMerchantReferencePriceService.delete(getLoginUserId(), id);
        return success(Boolean.TRUE);
    }
}

