package cn.iocoder.yudao.module.linbang.service.app.order;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.order.vo.*;

import javax.validation.Valid;

public interface AppOrderService {

    AppOrderPreviewRespVO previewOrder(Long authUserId, @Valid AppOrderPreviewReqVO reqVO);

    Long createOrder(Long authUserId, @Valid AppOrderCreateReqVO reqVO);

    AppOrderSplitRuleMatchRespVO matchSplitRule(AppOrderSplitRuleMatchReqVO reqVO);

    PageResult<AppOrderAcceptPageItemRespVO> getAcceptOrderPage(Long authUserId, AppOrderAcceptPageReqVO reqVO);

    PageResult<AppOrderPageItemRespVO> getOrderPage(Long authUserId, AppOrderPageReqVO reqVO);

    AppOrderDetailRespVO getOrderDetail(Long authUserId, Long orderId);

    Boolean updateOrder(Long authUserId, @Valid AppOrderUpdateReqVO reqVO);

    Boolean cancelOrder(Long authUserId, @Valid AppOrderCancelReqVO reqVO);

    PageResult<AppOrderUnitRespVO> getOrderUnitPage(Long authUserId, AppOrderUnitPageReqVO reqVO);

    AppOrderUnitRespVO getOrderUnit(Long authUserId, Long id);

    Boolean startOrderUnitService(Long authUserId, @Valid AppOrderUnitStartServiceReqVO reqVO);

    Boolean confirmOrderUnit(Long authUserId, @Valid AppOrderUnitConfirmReqVO reqVO);

    AppOrderVerifyCodeRespVO getOrderUnitVerifyCode(Long authUserId, Long unitId);

    Boolean verifyOrderUnit(Long authUserId, @Valid AppOrderVerifyReqVO reqVO);

    java.util.List<AppOrderVerifyLogRespVO> getOrderUnitVerifyLogs(Long authUserId, Long unitId);

    AppOrderAcceptRespVO acceptOrder(Long authUserId, @Valid AppOrderAcceptCreateReqVO reqVO);

    Boolean uploadDeliveryProof(Long authUserId, @Valid AppDeliveryProofUploadReqVO reqVO);

    AppOrderAppealProgressRespVO getAppealProgress(Long authUserId, Long appealId);

    AppOrderGuaranteeConfigRespVO getGuaranteeConfig();

}
