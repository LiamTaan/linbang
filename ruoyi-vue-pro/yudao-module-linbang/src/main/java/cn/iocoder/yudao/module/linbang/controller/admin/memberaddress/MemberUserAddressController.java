package cn.iocoder.yudao.module.linbang.controller.admin.memberaddress;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import static cn.iocoder.yudao.module.linbang.constants.LinbangExportConstants.MAX_EXPORT_ROWS;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.linbang.controller.admin.memberaddress.vo.*;
import cn.iocoder.yudao.module.linbang.service.memberaddress.MemberUserAddressService;

@Tag(name = "管理后台 - 用户地址表")
@RestController
@RequestMapping("/linbang/member-user-address")
@Validated
public class MemberUserAddressController {

    @Resource
    private MemberUserAddressService memberUserAddressService;

    @GetMapping("/get")
    @Operation(summary = "获得用户地址表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-address:query')")
    public CommonResult<MemberUserAddressRespVO> getMemberUserAddress(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(memberUserAddressService.getMemberUserAddress(id), MemberUserAddressRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户地址表分页")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-address:query')")
    public CommonResult<PageResult<MemberUserAddressRespVO>> getMemberUserAddressPage(@Valid MemberUserAddressPageReqVO pageReqVO) {
        return success(memberUserAddressService.getMemberUserAddressPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出用户地址表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-address:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMemberUserAddressExcel(@Valid MemberUserAddressPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<MemberUserAddressRespVO> list = memberUserAddressService.getMemberUserAddressPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户地址表.xls", "数据", MemberUserAddressRespVO.class, list);
    }

}
