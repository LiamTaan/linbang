package cn.iocoder.yudao.module.linbang.controller.app.order;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.*;
import cn.iocoder.yudao.module.linbang.service.app.order.AppOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 邻里订单")
@RestController
@RequestMapping("/order")
@Validated
public class AppOrderController {

    @Resource
    private AppOrderService appOrderService;

    @PostMapping("/info/create")
    @Operation(summary = "创建订单")
    public CommonResult<Long> createOrder(@Valid @RequestBody AppOrderCreateReqVO reqVO) {
        return success(appOrderService.createOrder(getLoginUserId(), reqVO));
    }

    @GetMapping("/info/page")
    @Operation(summary = "分页查询订单")
    public CommonResult<PageResult<AppOrderPageItemRespVO>> getOrderPage(@Valid AppOrderPageReqVO reqVO) {
        return success(appOrderService.getOrderPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/info/get")
    @Operation(summary = "获取订单详情")
    @Parameter(name = "id", required = true, description = "订单 ID")
    public CommonResult<AppOrderDetailRespVO> getOrder(@RequestParam("id") Long id) {
        return success(appOrderService.getOrderDetail(getLoginUserId(), id));
    }

    @PostMapping("/info/cancel")
    @Operation(summary = "取消订单")
    public CommonResult<Boolean> cancelOrder(@Valid @RequestBody AppOrderCancelReqVO reqVO) {
        return success(appOrderService.cancelOrder(getLoginUserId(), reqVO));
    }

    @GetMapping("/unit/page")
    @Operation(summary = "分页查询订单单元")
    public CommonResult<PageResult<AppOrderUnitRespVO>> getOrderUnitPage(@Valid AppOrderUnitPageReqVO reqVO) {
        return success(appOrderService.getOrderUnitPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/unit/get")
    @Operation(summary = "获取订单单元详情")
    @Parameter(name = "id", required = true, description = "订单单元 ID")
    public CommonResult<AppOrderUnitRespVO> getOrderUnit(@RequestParam("id") Long id) {
        return success(appOrderService.getOrderUnit(getLoginUserId(), id));
    }

    @PostMapping("/unit/start-service")
    @Operation(summary = "开始订单单元服务")
    public CommonResult<Boolean> startOrderUnitService(@Valid @RequestBody AppOrderUnitStartServiceReqVO reqVO) {
        return success(appOrderService.startOrderUnitService(getLoginUserId(), reqVO));
    }

    @PostMapping("/unit/confirm")
    @Operation(summary = "确认订单单元完成")
    public CommonResult<Boolean> confirmOrderUnit(@Valid @RequestBody AppOrderUnitConfirmReqVO reqVO) {
        return success(appOrderService.confirmOrderUnit(getLoginUserId(), reqVO));
    }

    @PostMapping("/accept/create")
    @Operation(summary = "接单/抢单")
    public CommonResult<AppOrderAcceptRespVO> acceptOrder(@Valid @RequestBody AppOrderAcceptCreateReqVO reqVO) {
        return success(appOrderService.acceptOrder(getLoginUserId(), reqVO));
    }

    @PostMapping("/unit/upload-delivery-proof")
    @Operation(summary = "上传交付凭证")
    public CommonResult<Boolean> uploadDeliveryProof(@Valid @RequestBody AppDeliveryProofUploadReqVO reqVO) {
        return success(appOrderService.uploadDeliveryProof(getLoginUserId(), reqVO));
    }
}
