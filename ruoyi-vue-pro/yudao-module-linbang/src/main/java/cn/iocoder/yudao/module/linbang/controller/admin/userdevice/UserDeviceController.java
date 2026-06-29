package cn.iocoder.yudao.module.linbang.controller.admin.userdevice;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo.UserDevicePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.userdevice.vo.UserDeviceRespVO;
import cn.iocoder.yudao.module.linbang.service.userdevice.UserDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 用户设备监控")
@RestController
@RequestMapping("/risk/user-device")
@Validated
public class UserDeviceController {

    @Resource
    private UserDeviceService userDeviceService;

    @GetMapping("/page")
    @Operation(summary = "获取用户设备监控分页")
    @PreAuthorize("@ss.hasPermission('linbang:risk:user-device:query')")
    public CommonResult<PageResult<UserDeviceRespVO>> getPage(@Valid UserDevicePageReqVO reqVO) {
        return success(userDeviceService.getPage(reqVO));
    }
}
