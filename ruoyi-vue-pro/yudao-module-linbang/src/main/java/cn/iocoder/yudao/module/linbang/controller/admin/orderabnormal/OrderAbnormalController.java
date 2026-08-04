package cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal;

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

import cn.iocoder.yudao.module.linbang.controller.admin.orderabnormal.vo.*;
import cn.iocoder.yudao.module.linbang.service.orderabnormal.OrderAbnormalService;

@Tag(name = "管理后台 - 异常订单")
@RestController
@RequestMapping("/order/abnormal")
@Validated
public class OrderAbnormalController {

    @Resource
    private OrderAbnormalService orderAbnormalService;

    @GetMapping("/get")
    @Operation(summary = "获得异常订单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:order:abnormal:query')")
    public CommonResult<OrderAbnormalDetailRespVO> getOrderAbnormal(@RequestParam("id") Long id) {
        return success(orderAbnormalService.getOrderAbnormalDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得异常订单分页")
    @PreAuthorize("@ss.hasPermission('linbang:order:abnormal:query')")
    public CommonResult<PageResult<OrderAbnormalRespVO>> getOrderAbnormalPage(@Valid OrderAbnormalPageReqVO pageReqVO) {
        return success(orderAbnormalService.getOrderAbnormalPage(pageReqVO));
    }

    @PostMapping("/final-audit")
    @Operation(summary = "异常订单终审")
    @PreAuthorize("@ss.hasPermission('linbang:order:abnormal:final-audit')")
    public CommonResult<Boolean> finalAuditOrderAbnormal(@Valid @RequestBody OrderAbnormalFinalAuditReqVO reqVO) {
        orderAbnormalService.finalAuditOrderAbnormal(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出异常订单 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:order:abnormal:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderAbnormalExcel(@Valid OrderAbnormalPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(MAX_EXPORT_ROWS);
        List<OrderAbnormalRespVO> list = orderAbnormalService.getOrderAbnormalPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "异常订单.xls", "数据", OrderAbnormalRespVO.class, list);
    }

}
