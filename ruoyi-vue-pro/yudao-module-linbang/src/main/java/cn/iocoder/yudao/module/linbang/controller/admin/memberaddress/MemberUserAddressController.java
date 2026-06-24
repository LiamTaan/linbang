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

import cn.iocoder.yudao.framework.common.pojo.PageParam;
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

    @PostMapping("/create")
    @Operation(summary = "创建用户地址表")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-address:create')")
    public CommonResult<Long> createMemberUserAddress(@Valid @RequestBody MemberUserAddressSaveReqVO createReqVO) {
        return success(memberUserAddressService.createMemberUserAddress(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户地址表")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-address:update')")
    public CommonResult<Boolean> updateMemberUserAddress(@Valid @RequestBody MemberUserAddressSaveReqVO updateReqVO) {
        memberUserAddressService.updateMemberUserAddress(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户地址表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:member-user-address:delete')")
    public CommonResult<Boolean> deleteMemberUserAddress(@RequestParam("id") Long id) {
        memberUserAddressService.deleteMemberUserAddress(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除用户地址表")
                @PreAuthorize("@ss.hasPermission('linbang:member-user-address:delete')")
    public CommonResult<Boolean> deleteMemberUserAddressList(@RequestParam("ids") List<Long> ids) {
        memberUserAddressService.deleteMemberUserAddressListByIds(ids);
        return success(true);
    }

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
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MemberUserAddressRespVO> list = memberUserAddressService.getMemberUserAddressPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "用户地址表.xls", "数据", MemberUserAddressRespVO.class, list);
    }

}
