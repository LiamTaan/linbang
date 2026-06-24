package cn.iocoder.yudao.module.linbang.controller.admin.commissionorder;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.commissionorder.vo.CommissionOrderRespVO;
import cn.iocoder.yudao.module.linbang.service.commissionorder.CommissionOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 佣金明细")
@RestController
@RequestMapping("/promote/commission")
@Validated
public class CommissionOrderController {

    @Resource
    private CommissionOrderService commissionOrderService;

    @GetMapping("/page")
    @Operation(summary = "获得佣金分页")
    @PreAuthorize("@ss.hasPermission('linbang:promote:commission:query')")
    public CommonResult<PageResult<CommissionOrderRespVO>> getCommissionOrderPage(@Valid CommissionOrderPageReqVO reqVO) {
        return success(commissionOrderService.getCommissionOrderPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得佣金详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:promote:commission:query')")
    public CommonResult<CommissionOrderDetailRespVO> getCommissionOrder(@RequestParam("id") Long id) {
        return success(commissionOrderService.getCommissionOrderDetail(id));
    }
}
