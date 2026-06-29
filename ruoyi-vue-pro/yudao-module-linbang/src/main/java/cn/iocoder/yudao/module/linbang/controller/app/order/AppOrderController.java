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

    @PostMapping("/info/preview")
    @Operation(summary = "发单预览", description = "提交前校验类目、计价方式、开票、协议、拆单与内容安全规则，并返回预览快照令牌。")
    public CommonResult<AppOrderPreviewRespVO> previewOrder(@Valid @RequestBody AppOrderPreviewReqVO reqVO) {
        return success(appOrderService.previewOrder(getLoginUserId(), reqVO));
    }

    @PostMapping("/info/create")
    @Operation(
            summary = "创建订单",
            description = "首页发布需求接口。无需传 title、无需传 addressId，直接传省/市/区/街道/详细地址。"
                    + "requireDesc 即需求描述，也是订单标题来源。serviceDurationDesc 仅作为服务时长/工期展示文案保存。"
                    + "后端会按已启用的拆单规则自动判断是否拆单并生成订单单元，needSplit 仅作为兼容字段保留。"
                    + "attachmentFileIds 需先通过 /app-api/infra/file/presigned-url 获取直传地址，上传成功后再调用 "
                    + "/app-api/infra/file/create 换取 fileId。")
    public CommonResult<Long> createOrder(@Valid @RequestBody AppOrderCreateReqVO reqVO) {
        return success(appOrderService.createOrder(getLoginUserId(), reqVO));
    }

    @GetMapping("/split-rule/match")
    @Operation(summary = "预览命中的拆单规则和预计生成的单元")
    public CommonResult<AppOrderSplitRuleMatchRespVO> matchSplitRule(@Valid AppOrderSplitRuleMatchReqVO reqVO) {
        return success(appOrderService.matchSplitRule(reqVO));
    }

    @GetMapping("/guarantee-config")
    @Operation(summary = "获取交易保障配置", description = "返回交易保障协议版本、协议文案、防逃单提醒文案等发单/接单前展示信息。")
    public CommonResult<AppOrderGuaranteeConfigRespVO> getGuaranteeConfig() {
        return success(appOrderService.getGuaranteeConfig());
    }

    @GetMapping("/accept/page")
    @Operation(summary = "分页查询待接单需求", description = "服务商抢单大厅列表。支持顶部关键字搜索、类目筛选、距离排序、价格排序、发布时间排序。")
    public CommonResult<PageResult<AppOrderAcceptPageItemRespVO>> getAcceptOrderPage(@Valid AppOrderAcceptPageReqVO reqVO) {
        return success(appOrderService.getAcceptOrderPage(getLoginUserId(), reqVO));
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

    @PutMapping("/info/update")
    @Operation(summary = "修改订单", description = "仅待付款、待接单阶段允许修改地址、预约说明、需求描述和附件。")
    public CommonResult<Boolean> updateOrder(@Valid @RequestBody AppOrderUpdateReqVO reqVO) {
        return success(appOrderService.updateOrder(getLoginUserId(), reqVO));
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

    @GetMapping("/unit/verify-code")
    @Operation(summary = "获取订单单元核销码")
    @Parameter(name = "unitId", required = true, description = "订单单元 ID")
    public CommonResult<AppOrderVerifyCodeRespVO> getOrderUnitVerifyCode(@RequestParam("unitId") Long unitId) {
        return success(appOrderService.getOrderUnitVerifyCode(getLoginUserId(), unitId));
    }

    @PostMapping("/unit/verify")
    @Operation(summary = "执行订单单元核销")
    public CommonResult<Boolean> verifyOrderUnit(@Valid @RequestBody AppOrderVerifyReqVO reqVO) {
        return success(appOrderService.verifyOrderUnit(getLoginUserId(), reqVO));
    }

    @GetMapping("/unit/verify-log/list")
    @Operation(summary = "获取订单单元核销日志")
    @Parameter(name = "unitId", required = true, description = "订单单元 ID")
    public CommonResult<java.util.List<AppOrderVerifyLogRespVO>> getOrderUnitVerifyLogs(@RequestParam("unitId") Long unitId) {
        return success(appOrderService.getOrderUnitVerifyLogs(getLoginUserId(), unitId));
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

    @GetMapping("/appeal/progress")
    @Operation(summary = "获取申诉进度")
    @Parameter(name = "id", required = true, description = "申诉 ID")
    public CommonResult<AppOrderAppealProgressRespVO> getAppealProgress(@RequestParam("id") Long id) {
        return success(appOrderService.getAppealProgress(getLoginUserId(), id));
    }
}
