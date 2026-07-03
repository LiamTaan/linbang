package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceUpdateReqVO;

import javax.validation.Valid;
import java.util.List;

public interface AppMerchantReferencePriceService {

    Long create(Long authUserId, @Valid AppMerchantReferencePriceCreateReqVO reqVO);

    List<AppMerchantReferencePriceRespVO> getList(Long authUserId);

    void update(Long authUserId, @Valid AppMerchantReferencePriceUpdateReqVO reqVO);

    void updateStatus(Long authUserId, @Valid AppMerchantReferencePriceStatusUpdateReqVO reqVO);

    void delete(Long authUserId, Long id);
}

