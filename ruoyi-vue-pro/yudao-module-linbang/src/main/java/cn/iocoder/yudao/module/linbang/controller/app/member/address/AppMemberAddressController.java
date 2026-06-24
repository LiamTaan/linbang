package cn.iocoder.yudao.module.linbang.controller.app.member.address;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.address.vo.AppMemberAddressUpdateReqVO;
import cn.iocoder.yudao.module.linbang.service.app.member.AppMemberAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 地址管理")
@RestController
@RequestMapping("/member/address")
@Validated
public class AppMemberAddressController {

    @Resource
    private AppMemberAddressService appMemberAddressService;

    @GetMapping("/page")
    @Operation(summary = "分页获取地址列表")
    public CommonResult<PageResult<AppMemberAddressRespVO>> getAddressPage() {
        return success(appMemberAddressService.getAddressPage(getLoginUserId()));
    }

    @GetMapping("/get")
    @Operation(summary = "获取地址详情")
    public CommonResult<AppMemberAddressRespVO> getAddress(@RequestParam("id") Long id) {
        return success(appMemberAddressService.getAddress(getLoginUserId(), id));
    }

    @PostMapping("/create")
    @Operation(summary = "新增地址")
    public CommonResult<Long> createAddress(@Valid @RequestBody AppMemberAddressCreateReqVO reqVO) {
        return success(appMemberAddressService.createAddress(getLoginUserId(), reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新地址")
    public CommonResult<Boolean> updateAddress(@Valid @RequestBody AppMemberAddressUpdateReqVO reqVO) {
        appMemberAddressService.updateAddress(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除地址")
    @Parameter(name = "id", description = "地址 ID", required = true)
    public CommonResult<Boolean> deleteAddress(@RequestParam("id") Long id) {
        appMemberAddressService.deleteAddress(getLoginUserId(), id);
        return success(Boolean.TRUE);
    }
}
