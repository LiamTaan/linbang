package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundCreateReqVO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;

import javax.validation.Valid;

public interface AppPayRefundService {

    Long createRefund(Long authUserId, @Valid AppPayRefundCreateReqVO reqVO);

    void updateRefunded(@Valid PayRefundNotifyReqDTO notifyReqDTO);

}
