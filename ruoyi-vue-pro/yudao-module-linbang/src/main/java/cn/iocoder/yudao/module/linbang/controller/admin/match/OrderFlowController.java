package cn.iocoder.yudao.module.linbang.controller.admin.match;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.OrderFlowPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.OrderFlowRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.match.vo.OrderFlowRetryRefundReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.service.app.pay.AutoFlowRefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 邻里流单退款看板")
@RestController
@RequestMapping("/linbang/order/flow")
@Validated
public class OrderFlowController {

    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private AutoFlowRefundService autoFlowRefundService;

    @GetMapping("/page")
    @Operation(summary = "分页获取流单退款看板")
    @PreAuthorize("@ss.hasPermission('linbang:order:flow:query')")
    public CommonResult<PageResult<OrderFlowRespVO>> page(@Valid OrderFlowPageReqVO reqVO) {
        String dispatchStatus = reqVO.getDispatchStatus();
        LambdaQueryWrapperX<OrderUnitDO> queryWrapper = new LambdaQueryWrapperX<OrderUnitDO>();
        queryWrapper.eqIfPresent(OrderUnitDO::getOrderId, reqVO.getOrderId());
        queryWrapper.eqIfPresent(OrderUnitDO::getId, reqVO.getUnitId());
        queryWrapper.eq(OrderUnitDO::getDispatchStatus, dispatchStatus == null ? "FLOWED" : dispatchStatus);
        queryWrapper.eqIfPresent(OrderUnitDO::getAutoRefundStatus, reqVO.getAutoRefundStatus());
        queryWrapper.isNotNull(OrderUnitDO::getFlowTime);
        queryWrapper.orderByDesc(OrderUnitDO::getFlowTime);
        queryWrapper.orderByDesc(OrderUnitDO::getId);
        PageResult<OrderUnitDO> pageResult = orderUnitMapper.selectPage(reqVO, queryWrapper);
        return success(new PageResult<>(pageResult.getList().stream().map(this::convert).collect(Collectors.toList()),
                pageResult.getTotal()));
    }

    @PostMapping("/retry-refund")
    @Operation(summary = "重试流单自动退款")
    @PreAuthorize("@ss.hasPermission('linbang:order:flow:update')")
    public CommonResult<Boolean> retryRefund(@Valid @RequestBody OrderFlowRetryRefundReqVO reqVO) {
        OrderUnitDO unit = orderUnitMapper.selectById(reqVO.getUnitId());
        if (unit != null && unit.getFlowTime() != null && Objects.equals(unit.getDispatchStatus(), "FLOWED")) {
            autoFlowRefundService.createAutoRefund(unit.getOrderId(), unit.getId(), unit.getFlowTime());
        }
        return success(Boolean.TRUE);
    }

    private OrderFlowRespVO convert(OrderUnitDO unit) {
        OrderFlowRespVO respVO = new OrderFlowRespVO();
        respVO.setUnitId(unit.getId());
        respVO.setOrderId(unit.getOrderId());
        respVO.setUnitAmount(unit.getUnitAmount());
        respVO.setDispatchStatus(unit.getDispatchStatus());
        respVO.setCurrentBatchNo(unit.getCurrentBatchNo());
        respVO.setFlowTime(unit.getFlowTime());
        respVO.setFlowReason(unit.getFlowReason());
        respVO.setAutoRefundStatus(unit.getAutoRefundStatus());
        respVO.setAutoRefundId(unit.getAutoRefundId());
        return respVO;
    }
}
