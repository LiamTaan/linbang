package cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord;

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

import cn.iocoder.yudao.module.linbang.controller.admin.orderacceptrecord.vo.*;
import cn.iocoder.yudao.module.linbang.service.orderacceptrecord.OrderAcceptRecordService;

@Tag(name = "管理后台 - 抢单记录")
@RestController
@RequestMapping("/order/accept-record")
@Validated
public class OrderAcceptRecordController {

    @Resource
    private OrderAcceptRecordService orderAcceptRecordService;

    @PostMapping("/create")
    @Operation(summary = "创建抢单记录")
    @PreAuthorize("@ss.hasPermission('linbang:order:accept-record:create')")
    public CommonResult<Long> createOrderAcceptRecord(@Valid @RequestBody OrderAcceptRecordSaveReqVO createReqVO) {
        return success(orderAcceptRecordService.createOrderAcceptRecord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新抢单记录")
    @PreAuthorize("@ss.hasPermission('linbang:order:accept-record:update')")
    public CommonResult<Boolean> updateOrderAcceptRecord(@Valid @RequestBody OrderAcceptRecordSaveReqVO updateReqVO) {
        orderAcceptRecordService.updateOrderAcceptRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除抢单记录")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:order:accept-record:delete')")
    public CommonResult<Boolean> deleteOrderAcceptRecord(@RequestParam("id") Long id) {
        orderAcceptRecordService.deleteOrderAcceptRecord(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除抢单记录")
                @PreAuthorize("@ss.hasPermission('linbang:order:accept-record:delete')")
    public CommonResult<Boolean> deleteOrderAcceptRecordList(@RequestParam("ids") List<Long> ids) {
        orderAcceptRecordService.deleteOrderAcceptRecordListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得抢单记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:order:accept-record:query')")
    public CommonResult<OrderAcceptRecordDetailRespVO> getOrderAcceptRecord(@RequestParam("id") Long id) {
        return success(orderAcceptRecordService.getOrderAcceptRecordDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得抢单记录分页")
    @PreAuthorize("@ss.hasPermission('linbang:order:accept-record:query')")
    public CommonResult<PageResult<OrderAcceptRecordRespVO>> getOrderAcceptRecordPage(@Valid OrderAcceptRecordPageReqVO pageReqVO) {
        return success(orderAcceptRecordService.getOrderAcceptRecordPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出抢单记录 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:order:accept-record:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderAcceptRecordExcel(@Valid OrderAcceptRecordPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OrderAcceptRecordRespVO> list = orderAcceptRecordService.getOrderAcceptRecordPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "抢单记录.xls", "数据", OrderAcceptRecordRespVO.class, list);
    }

}
