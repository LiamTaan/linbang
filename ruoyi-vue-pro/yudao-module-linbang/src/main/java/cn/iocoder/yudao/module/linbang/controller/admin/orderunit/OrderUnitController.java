package cn.iocoder.yudao.module.linbang.controller.admin.orderunit;

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

import cn.iocoder.yudao.module.linbang.controller.admin.orderunit.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.service.orderunit.OrderUnitService;

@Tag(name = "管理后台 - 拆分单元")
@RestController
@RequestMapping("/order/unit")
@Validated
public class OrderUnitController {

    @Resource
    private OrderUnitService orderUnitService;

    @PostMapping("/create")
    @Operation(summary = "创建拆分单元")
    @PreAuthorize("@ss.hasPermission('linbang:order:unit:create')")
    public CommonResult<Long> createOrderUnit(@Valid @RequestBody OrderUnitSaveReqVO createReqVO) {
        return success(orderUnitService.createOrderUnit(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新拆分单元")
    @PreAuthorize("@ss.hasPermission('linbang:order:unit:update')")
    public CommonResult<Boolean> updateOrderUnit(@Valid @RequestBody OrderUnitSaveReqVO updateReqVO) {
        orderUnitService.updateOrderUnit(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除拆分单元")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:order:unit:delete')")
    public CommonResult<Boolean> deleteOrderUnit(@RequestParam("id") Long id) {
        orderUnitService.deleteOrderUnit(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除拆分单元")
                @PreAuthorize("@ss.hasPermission('linbang:order:unit:delete')")
    public CommonResult<Boolean> deleteOrderUnitList(@RequestParam("ids") List<Long> ids) {
        orderUnitService.deleteOrderUnitListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得拆分单元")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:order:unit:query')")
    public CommonResult<OrderUnitDetailRespVO> getOrderUnit(@RequestParam("id") Long id) {
        return success(orderUnitService.getOrderUnitDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得拆分单元分页")
    @PreAuthorize("@ss.hasPermission('linbang:order:unit:query')")
    public CommonResult<PageResult<OrderUnitRespVO>> getOrderUnitPage(@Valid OrderUnitPageReqVO pageReqVO) {
        return success(orderUnitService.getOrderUnitPage(pageReqVO));
    }

    @PostMapping("/unlock")
    @Operation(summary = "人工解锁单元")
    @PreAuthorize("@ss.hasPermission('linbang:order:unit:unlock')")
    public CommonResult<Boolean> unlockOrderUnit(@Valid @RequestBody OrderUnitUnlockReqVO reqVO) {
        orderUnitService.unlockOrderUnit(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出拆分单元 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:order:unit:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportOrderUnitExcel(@Valid OrderUnitPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<OrderUnitRespVO> list = orderUnitService.getOrderUnitPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "拆分单元.xls", "数据", OrderUnitRespVO.class, list);
    }

}
