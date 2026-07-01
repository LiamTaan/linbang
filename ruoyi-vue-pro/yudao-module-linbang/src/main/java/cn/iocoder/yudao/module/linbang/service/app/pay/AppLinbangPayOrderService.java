package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositInfoRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositStatusRespVO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;

import javax.validation.Valid;

public interface AppLinbangPayOrderService {

    Long createPayOrder(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO);

    Long simulatePaySuccess(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO);

    AppLinbangPayOrderRespVO getPayOrder(Long authUserId, Long id, Long orderId, Boolean sync);

    AppOrderDepositInfoRespVO getDepositInfo(Long authUserId, Long orderId);

    Long createDepositPayOrder(Long authUserId, Long orderId);

    AppOrderDepositStatusRespVO getDepositStatus(Long authUserId, Long orderId, Boolean sync);

    void updatePaid(@Valid PayOrderNotifyReqDTO notifyReqDTO);

}
