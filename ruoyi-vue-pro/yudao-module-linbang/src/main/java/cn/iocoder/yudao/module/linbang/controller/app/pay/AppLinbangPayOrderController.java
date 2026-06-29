package cn.iocoder.yudao.module.linbang.controller.app.pay;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositInfoRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositStatusRespVO;
import cn.iocoder.yudao.module.linbang.service.app.pay.AppLinbangPayOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 邻里支付订单")
@RestController
@RequestMapping("/linbang/pay/order")
@Validated
public class AppLinbangPayOrderController {

    @Resource
    private AppLinbangPayOrderService appLinbangPayOrderService;

    @PostMapping("/create")
    @Operation(summary = "创建支付订单")
    public CommonResult<Long> createPayOrder(@Valid @RequestBody AppLinbangPayOrderCreateReqVO reqVO) {
        return success(appLinbangPayOrderService.createPayOrder(getLoginUserId(), reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取支付订单")
    @Parameters({
            @Parameter(name = "id", description = "支付订单 ID"),
            @Parameter(name = "orderId", description = "业务订单 ID"),
            @Parameter(name = "sync", description = "是否同步支付状态")
    })
    public CommonResult<AppLinbangPayOrderRespVO> getPayOrder(@RequestParam(value = "id", required = false) Long id,
                                                              @RequestParam(value = "orderId", required = false) Long orderId,
                                                              @RequestParam(value = "sync", required = false) Boolean sync) {
        return success(appLinbangPayOrderService.getPayOrder(getLoginUserId(), id, orderId, sync));
    }

    @GetMapping("/deposit/info")
    @Operation(summary = "获取订单保证金确认信息")
    @Parameter(name = "orderId", required = true, description = "业务订单 ID")
    public CommonResult<AppOrderDepositInfoRespVO> getDepositInfo(@RequestParam("orderId") Long orderId) {
        return success(appLinbangPayOrderService.getDepositInfo(getLoginUserId(), orderId));
    }

    @PostMapping("/deposit/create")
    @Operation(summary = "创建订单保证金支付单")
    @Parameter(name = "orderId", required = true, description = "业务订单 ID")
    public CommonResult<Long> createDepositPayOrder(@RequestParam("orderId") Long orderId) {
        return success(appLinbangPayOrderService.createDepositPayOrder(getLoginUserId(), orderId));
    }

    @GetMapping("/deposit/status")
    @Operation(summary = "查询订单保证金支付状态")
    @Parameters({
            @Parameter(name = "orderId", required = true, description = "业务订单 ID"),
            @Parameter(name = "sync", description = "是否同步支付状态")
    })
    public CommonResult<AppOrderDepositStatusRespVO> getDepositStatus(@RequestParam("orderId") Long orderId,
                                                                      @RequestParam(value = "sync", required = false) Boolean sync) {
        return success(appLinbangPayOrderService.getDepositStatus(getLoginUserId(), orderId, sync));
    }
}
