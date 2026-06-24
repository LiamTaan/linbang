package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;

import javax.validation.Valid;

public interface AppLinbangPayOrderService {

    Long createPayOrder(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO);

    AppLinbangPayOrderRespVO getPayOrder(Long authUserId, Long id, Long orderId, Boolean sync);

    void updatePaid(@Valid PayOrderNotifyReqDTO notifyReqDTO);

}
