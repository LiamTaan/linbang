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

import static cn.iocoder.yudao.module.linbang.constants.LinbangExportConstants.MAX_EXPORT_ROWS;
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
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<MemberUserRealNameRespVO> list = memberUserRealNameService.getMemberUserRealNamePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "实名认证表.xls", "数据", MemberUserRealNameRespVO.class, list);
    }

}
