package cn.iocoder.yudao.module.linbang.controller.admin.memberrealname;

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
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo.*;
import cn.iocoder.yudao.module.linbang.service.memberrealname.MemberUserRealNameService;

@Tag(name = "管理后台 - 实名认证表")
@RestController
@RequestMapping("/member/real-name")
@Validated
public class MemberUserRealNameController {

    @Resource
    private MemberUserRealNameService memberUserRealNameService;

    @PostMapping("/create")
    @Operation(summary = "创建实名认证表")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-real-name:create')")
    public CommonResult<Long> createMemberUserRealName(@Valid @RequestBody MemberUserRealNameSaveReqVO createReqVO) {
        return success(memberUserRealNameService.createMemberUserRealName(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新实名认证表")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-real-name:update')")
    public CommonResult<Boolean> updateMemberUserRealName(@Valid @RequestBody MemberUserRealNameSaveReqVO updateReqVO) {
        memberUserRealNameService.updateMemberUserRealName(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除实名认证表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:member-user-real-name:delete')")
    public CommonResult<Boolean> deleteMemberUserRealName(@RequestParam("id") Long id) {
        memberUserRealNameService.deleteMemberUserRealName(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除实名认证表")
                @PreAuthorize("@ss.hasPermission('linbang:member-user-real-name:delete')")
    public CommonResult<Boolean> deleteMemberUserRealNameList(@RequestParam("ids") List<Long> ids) {
        memberUserRealNameService.deleteMemberUserRealNameListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得实名认证表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-real-name:query')")
    public CommonResult<MemberUserRealNameDetailRespVO> getMemberUserRealName(@RequestParam("id") Long id) {
        return success(memberUserRealNameService.getMemberUserRealNameDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得实名认证表分页")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-real-name:query')")
    public CommonResult<PageResult<MemberUserRealNameRespVO>> getMemberUserRealNamePage(@Valid MemberUserRealNamePageReqVO pageReqVO) {
        return success(memberUserRealNameService.getMemberUserRealNamePage(pageReqVO));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核实名认证")
    @PreAuthorize("@ss.hasPermission('linbang:member:real-name:audit')")
    public CommonResult<Boolean> auditMemberUserRealName(@Valid @RequestBody MemberUserRealNameAuditReqVO reqVO) {
        memberUserRealNameService.auditMemberUserRealName(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出实名认证表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:member-user-real-name:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMemberUserRealNameExcel(@Valid MemberUserRealNamePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MemberUserRealNameRespVO> list = memberUserRealNameService.getMemberUserRealNamePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "实名认证表.xls", "数据", MemberUserRealNameRespVO.class, list);
    }

}
