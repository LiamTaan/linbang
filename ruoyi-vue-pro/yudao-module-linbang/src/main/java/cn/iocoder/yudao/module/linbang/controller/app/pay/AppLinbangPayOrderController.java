package cn.iocoder.yudao.module.linbang.controller.app.pay;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangH5PaySubmitReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangH5PaySubmitRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangWechatMiniProgramPaySubmitRespVO;
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

    @PostMapping("/h5/submit")
    @Operation(summary = "提交聚合支付 H5 支付", description = "前端传入 WECHAT_H5、ALIPAY_H5、UNIONPAY_WAP 三种支付入口之一，后端统一提交到 aggregate 聚合支付通道，返回 H5 收银台跳转地址")
    public CommonResult<AppLinbangH5PaySubmitRespVO> submitH5Pay(@Valid @RequestBody AppLinbangH5PaySubmitReqVO reqVO) {
        return success(appLinbangPayOrderService.submitH5Pay(getLoginUserId(), reqVO));
    }

    @PostMapping("/wechat-mini-program/submit")
    @Operation(summary = "提交微信小程序支付",
            description = "当前首期正式支付入口。后端使用已绑定到登录用户的微信小程序 openid，"
                    + "通过 wx_lite 渠道创建 JSAPI 预支付单，返回 wx.requestPayment 所需参数。")
    public CommonResult<AppLinbangWechatMiniProgramPaySubmitRespVO> submitWechatMiniProgramPay(
            @Valid @RequestBody AppLinbangPayOrderCreateReqVO reqVO) {
        return success(appLinbangPayOrderService.submitWechatMiniProgramPay(getLoginUserId(), reqVO));
    }

    @PostMapping("/simulate-success")
    @Operation(summary = "模拟支付成功", description = "仅开发联调 mock 模式可用，用于跑通发单支付后派送流程。")
    public CommonResult<Long> simulatePaySuccess(@Valid @RequestBody AppLinbangPayOrderCreateReqVO reqVO) {
        return success(appLinbangPayOrderService.simulatePaySuccess(getLoginUserId(), reqVO));
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

    @PostMapping("/deposit/h5/submit")
    @Operation(summary = "提交订单保证金聚合支付 H5 支付", description = "保证金支付与普通支付共用三种前端支付入口：WECHAT_H5、ALIPAY_H5、UNIONPAY_WAP；后端统一提交到 aggregate 聚合支付通道")
    public CommonResult<AppLinbangH5PaySubmitRespVO> submitDepositH5Pay(@Valid @RequestBody AppLinbangH5PaySubmitReqVO reqVO) {
        return success(appLinbangPayOrderService.submitDepositH5Pay(getLoginUserId(), reqVO));
    }

    @PostMapping("/deposit/wechat-mini-program/submit")
    @Operation(summary = "提交订单保证金微信小程序支付",
            description = "使用 wx_lite 渠道返回 wx.requestPayment 所需参数；保证金支付成功后按现有订单资金流程处理。")
    public CommonResult<AppLinbangWechatMiniProgramPaySubmitRespVO> submitDepositWechatMiniProgramPay(
            @Valid @RequestBody AppLinbangPayOrderCreateReqVO reqVO) {
        return success(appLinbangPayOrderService.submitDepositWechatMiniProgramPay(getLoginUserId(), reqVO));
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
