package cn.iocoder.yudao.module.linbang.controller.admin.orderinfo;

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

import cn.iocoder.yudao.module.linbang.controller.admin.orderinfo.vo.*;
import cn.iocoder.yudao.module.linbang.service.orderabnormal.OrderAbnormalService;
import cn.iocoder.yudao.module.linbang.service.orderinfo.OrderInfoService;

@Tag(name = "管理后台 - 订单主")
@RestController
@RequestMapping("/order/info")
@Validated
public class OrderInfoController {

    @Resource
    private OrderInfoService orderInfoService;
    @Resource
    private OrderAbnormalService orderAbnormalService;

    @PostMapping("/create")
    @Operation(summary = "创建订单主")
    @PreAuthorize("@ss.hasPermission('linbang:order:info:create')")
    public CommonResult<Long> createOrderInfo(@Valid @RequestBody OrderInfoSaveReqVO createReqVO) {
        return success(orderInfoService.createOrderInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新订单主")
    @PreAuthorize("@ss.hasPermission('linbang:order:info:update')")
    public CommonResult<Boolean> updateOrderInfo(@Valid @RequestBody OrderInfoSaveReqVO updateReqVO) {
        orderInfoService.updateOrderInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除订单主")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:order:info:delete')")
    public CommonResult<Boolean> deleteOrderInfo(@RequestParam("id") Long id) {
        orderInfoService.deleteOrderInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除订单主")
                @PreAuthorize("@ss.hasPermission('linbang:order:info:delete')")
    public CommonResult<Boolean> deleteOrderInfoList(@RequestParam("ids") List<Long> ids) {
        orderInfoService.deleteOrderInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单主")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:order:info:query')")
    public CommonResult<OrderInfoDetailRespVO> getOrderInfo(@RequestParam("id") Long id) {
        return success(orderInfoService.getOrderInfoDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单主分页")
    @PreAuthorize("@ss.hasPermission('linbang:order:info:query')")
    public CommonResult<PageResult<OrderInfoRespVO>> getOrderInfoPage(@Valid OrderInfoPageReqVO pageReqVO) {
        return success(orderInfoService.getOrderInfoPage(pageReqVO));
    }

    @PostMapping("/mark-abnormal")
    @Operation(summary = "标记异常订单")
    @PreAuthorize("@ss.hasPermission('linbang:order:abnormal:create')")
    public CommonResult<Long> markOrderAbnormal(@Valid @RequestBody OrderMarkAbnormalReqVO reqVO) {
        return success(orderAbnormalService.markOrderAbnormal(reqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出订单主 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:order:info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderInfoExcel(@Valid OrderInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OrderInfoRespVO> list = orderInfoService.getOrderInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "订单主.xls", "数据", OrderInfoRespVO.class, list);
    }

}
