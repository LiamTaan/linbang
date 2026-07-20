package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangH5PaySubmitReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangH5PaySubmitRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangPayOrderRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppLinbangWechatMiniProgramPaySubmitRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositInfoRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppOrderDepositStatusRespVO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayOrderNotifyReqDTO;

import javax.validation.Valid;

public interface AppLinbangPayOrderService {

    Long createPayOrder(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO);

    AppLinbangH5PaySubmitRespVO submitH5Pay(Long authUserId, @Valid AppLinbangH5PaySubmitReqVO reqVO);

    AppLinbangWechatMiniProgramPaySubmitRespVO submitWechatMiniProgramPay(
            Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO);

    Long simulatePaySuccess(Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO);

    AppLinbangPayOrderRespVO getPayOrder(Long authUserId, Long id, Long orderId, Boolean sync);

    AppOrderDepositInfoRespVO getDepositInfo(Long authUserId, Long orderId);

    Long createDepositPayOrder(Long authUserId, Long orderId);

    AppLinbangH5PaySubmitRespVO submitDepositH5Pay(Long authUserId, @Valid AppLinbangH5PaySubmitReqVO reqVO);

    AppLinbangWechatMiniProgramPaySubmitRespVO submitDepositWechatMiniProgramPay(
            Long authUserId, @Valid AppLinbangPayOrderCreateReqVO reqVO);

    AppOrderDepositStatusRespVO getDepositStatus(Long authUserId, Long orderId, Boolean sync);

    void updatePaid(@Valid PayOrderNotifyReqDTO notifyReqDTO);

}
