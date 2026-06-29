package cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRulePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRuleRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.ordersplitrule.vo.OrderSplitRuleSaveReqVO;
import cn.iocoder.yudao.module.linbang.service.ordersplitrule.OrderSplitRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 订单拆单规则")
@RestController
@RequestMapping("/order/split-rule")
@Validated
public class OrderSplitRuleController {

    @Resource
    private OrderSplitRuleService orderSplitRuleService;

    @PostMapping("/create")
    @Operation(summary = "创建订单拆单规则")
    @PreAuthorize("@ss.hasPermission('linbang:order:split-rule:create')")
    public CommonResult<Long> createOrderSplitRule(@Valid @RequestBody OrderSplitRuleSaveReqVO createReqVO) {
        return success(orderSplitRuleService.createOrderSplitRule(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新订单拆单规则")
    @PreAuthorize("@ss.hasPermission('linbang:order:split-rule:update')")
    public CommonResult<Boolean> updateOrderSplitRule(@Valid @RequestBody OrderSplitRuleSaveReqVO updateReqVO) {
        orderSplitRuleService.updateOrderSplitRule(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除订单拆单规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('linbang:order:split-rule:delete')")
    public CommonResult<Boolean> deleteOrderSplitRule(@RequestParam("id") Long id) {
        orderSplitRuleService.deleteOrderSplitRule(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得订单拆单规则")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('linbang:order:split-rule:query')")
    public CommonResult<OrderSplitRuleRespVO> getOrderSplitRule(@RequestParam("id") Long id) {
        return success(orderSplitRuleService.getOrderSplitRule(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得订单拆单规则分页")
    @PreAuthorize("@ss.hasPermission('linbang:order:split-rule:query')")
    public CommonResult<PageResult<OrderSplitRuleRespVO>> getOrderSplitRulePage(@Valid OrderSplitRulePageReqVO pageReqVO) {
        return success(orderSplitRuleService.getOrderSplitRulePage(pageReqVO));
    }

}
