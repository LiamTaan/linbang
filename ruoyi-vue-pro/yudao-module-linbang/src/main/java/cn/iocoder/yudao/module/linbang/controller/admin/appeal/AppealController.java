package cn.iocoder.yudao.module.linbang.controller.admin.appeal;

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

import cn.iocoder.yudao.module.linbang.controller.admin.appeal.vo.*;
import cn.iocoder.yudao.module.linbang.service.appeal.AppealService;

@Tag(name = "管理后台 - 申诉")
@RestController
@RequestMapping("/review/appeal")
@Validated
public class AppealController {

    @Resource
    private AppealService appealService;

    @GetMapping("/get")
    @Operation(summary = "获得申诉")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:review:appeal:query')")
    public CommonResult<AppealDetailRespVO> getAppeal(@RequestParam("id") Long id) {
        return success(appealService.getAppealDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得申诉分页")
    @PreAuthorize("@ss.hasPermission('linbang:review:appeal:query')")
    public CommonResult<PageResult<AppealRespVO>> getAppealPage(@Valid AppealPageReqVO pageReqVO) {
        return success(appealService.getAppealPage(pageReqVO));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核申诉")
    @PreAuthorize("@ss.hasPermission('linbang:review:appeal:audit')")
    public CommonResult<Boolean> auditAppeal(@Valid @RequestBody AppealAuditReqVO reqVO) {
        appealService.auditAppeal(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出申诉 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:review:appeal:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAppealExcel(@Valid AppealPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<AppealRespVO> list = appealService.getAppealPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "申诉.xls", "数据", AppealRespVO.class, list);
    }

}
