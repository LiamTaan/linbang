package cn.iocoder.yudao.module.linbang.controller.admin.security;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.admin.security.vo.AdminDynamicKeyVerifyReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.security.vo.AdminDynamicKeyVerifyRespVO;
import cn.iocoder.yudao.module.linbang.service.security.AdminDynamicKeyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 动态密钥")
@RestController
@RequestMapping("/security/dynamic-key")
@Validated
public class AdminDynamicKeyController {

    @Resource
    private AdminDynamicKeyService adminDynamicKeyService;

    @PostMapping("/verify")
    @Operation(summary = "校验管理员动态密钥")
    @PreAuthorize("isAuthenticated()")
    public CommonResult<AdminDynamicKeyVerifyRespVO> verify(@Valid @RequestBody AdminDynamicKeyVerifyReqVO reqVO) {
        return success(adminDynamicKeyService.verify(reqVO.getPassword()));
    }
}
