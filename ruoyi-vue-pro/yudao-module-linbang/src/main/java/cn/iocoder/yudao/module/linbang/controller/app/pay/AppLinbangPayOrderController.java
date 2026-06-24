package cn.iocoder.yudao.module.linbang.controller.app.pay;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.linbang.service.app.pay.AppLinbangPayOrderService;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;
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
import javax.annotation.security.PermitAll;
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

    @PostMapping("/update-paid")
    @Operation(summary = "更新邻里订单为已支付")
    @PermitAll
    public CommonResult<Boolean> updatePaid(@Valid @RequestBody PayOrderNotifyReqDTO notifyReqDTO) {
        appLinbangPayOrderService.updatePaid(notifyReqDTO);
        return success(Boolean.TRUE);
    }

}
