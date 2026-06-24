package cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord;

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

import cn.iocoder.yudao.module.linbang.controller.admin.ordermatchrecord.vo.*;
import cn.iocoder.yudao.module.linbang.service.ordermatchrecord.OrderMatchRecordService;

@Tag(name = "管理后台 - 订单匹配记录")
@RestController
@RequestMapping("/order/match-record")
@Validated
public class OrderMatchRecordController {

    @Resource
    private OrderMatchRecordService orderMatchRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建订单匹配记录")
    @PreAuthorize("@ss.hasPermission('linbang:order:match-record:create')")
    public CommonResult<Long> createOrderMatchRecord(@Valid @RequestBody OrderMatchRecordSaveReqVO createReqVO) {
        return success(orderMatchRecordService.createOrderMatchRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新订单匹配记录")
    @PreAuthorize("@ss.hasPermission('linbang:order:match-record:update')")
    public CommonResult<Boolean> updateOrderMatchRecord(@Valid @RequestBody OrderMatchRecordSaveReqVO updateReqVO) {
        orderMatchRecordService.updateOrderMatchRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除订单匹配记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:order:match-record:delete')")
    public CommonResult<Boolean> deleteOrderMatchRecord(@RequestParam("id") Long id) {
        orderMatchRecordService.deleteOrderMatchRecord(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除订单匹配记录")
                @PreAuthorize("@ss.hasPermission('linbang:order:match-record:delete')")
    public CommonResult<Boolean> deleteOrderMatchRecordList(@RequestParam("ids") List<Long> ids) {
        orderMatchRecordService.deleteOrderMatchRecordListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单匹配记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:order:match-record:query')")
    public CommonResult<OrderMatchRecordDetailRespVO> getOrderMatchRecord(@RequestParam("id") Long id) {
        return success(orderMatchRecordService.getOrderMatchRecordDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单匹配记录分页")
    @PreAuthorize("@ss.hasPermission('linbang:order:match-record:query')")
    public CommonResult<PageResult<OrderMatchRecordRespVO>> getOrderMatchRecordPage(@Valid OrderMatchRecordPageReqVO pageReqVO) {
        return success(orderMatchRecordService.getOrderMatchRecordPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出订单匹配记录 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:order:match-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderMatchRecordExcel(@Valid OrderMatchRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OrderMatchRecordRespVO> list = orderMatchRecordService.getOrderMatchRecordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "订单匹配记录.xls", "数据", OrderMatchRecordRespVO.class, list);
    }

}
