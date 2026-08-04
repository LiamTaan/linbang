package cn.iocoder.yudao.module.linbang.controller.admin.complaint;

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

import cn.iocoder.yudao.module.linbang.controller.admin.complaint.vo.*;
import cn.iocoder.yudao.module.linbang.service.complaint.ComplaintService;

@Tag(name = "管理后台 - 投诉")
@RestController
@RequestMapping("/review/complaint")
@Validated
public class ComplaintController {

    @Resource
    private ComplaintService complaintService;

    @GetMapping("/get")
    @Operation(summary = "获得投诉")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:review:complaint:query')")
    public CommonResult<ComplaintDetailRespVO> getComplaint(@RequestParam("id") Long id) {
        return success(complaintService.getComplaintDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得投诉分页")
    @PreAuthorize("@ss.hasPermission('linbang:review:complaint:query')")
    public CommonResult<PageResult<ComplaintRespVO>> getComplaintPage(@Valid ComplaintPageReqVO pageReqVO) {
        return success(complaintService.getComplaintPage(pageReqVO));
    }

    @PostMapping("/process")
    @Operation(summary = "处理投诉")
    @PreAuthorize("@ss.hasPermission('linbang:review:complaint:process')")
    public CommonResult<Boolean> processComplaint(@Valid @RequestBody ComplaintProcessReqVO reqVO) {
        complaintService.processComplaint(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出投诉 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:review:complaint:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportComplaintExcel(@Valid ComplaintPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<ComplaintRespVO> list = complaintService.getComplaintPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "投诉.xls", "数据", ComplaintRespVO.class, list);
    }

}
