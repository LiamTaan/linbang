package cn.iocoder.yudao.module.linbang.service.app.pay;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.pay.vo.AppPayRefundRespVO;
import cn.iocoder.yudao.module.pay.api.notify.dto.PayRefundNotifyReqDTO;

import javax.validation.Valid;

public interface AppPayRefundService {

    Long createRefund(Long authUserId, @Valid AppPayRefundCreateReqVO reqVO);

    PageResult<AppPayRefundRespVO> getRefundPage(Long authUserId, AppPayRefundPageReqVO reqVO);

    AppPayRefundRespVO getRefund(Long authUserId, Long id);

    void updateRefunded(@Valid PayRefundNotifyReqDTO notifyReqDTO);

}
