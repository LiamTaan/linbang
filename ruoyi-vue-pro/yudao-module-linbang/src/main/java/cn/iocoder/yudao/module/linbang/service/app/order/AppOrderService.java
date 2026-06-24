package cn.iocoder.yudao.module.linbang.service.app.order;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.*;

import javax.validation.Valid;

public interface AppOrderService {

    Long createOrder(Long authUserId, @Valid AppOrderCreateReqVO reqVO);

    PageResult<AppOrderPageItemRespVO> getOrderPage(Long authUserId, AppOrderPageReqVO reqVO);

    AppOrderDetailRespVO getOrderDetail(Long authUserId, Long orderId);

    Boolean cancelOrder(Long authUserId, @Valid AppOrderCancelReqVO reqVO);

    PageResult<AppOrderUnitRespVO> getOrderUnitPage(Long authUserId, AppOrderUnitPageReqVO reqVO);

    AppOrderUnitRespVO getOrderUnit(Long authUserId, Long id);

    Boolean startOrderUnitService(Long authUserId, @Valid AppOrderUnitStartServiceReqVO reqVO);

    Boolean confirmOrderUnit(Long authUserId, @Valid AppOrderUnitConfirmReqVO reqVO);

    AppOrderAcceptRespVO acceptOrder(Long authUserId, @Valid AppOrderAcceptCreateReqVO reqVO);

    Boolean uploadDeliveryProof(Long authUserId, @Valid AppDeliveryProofUploadReqVO reqVO);

}
