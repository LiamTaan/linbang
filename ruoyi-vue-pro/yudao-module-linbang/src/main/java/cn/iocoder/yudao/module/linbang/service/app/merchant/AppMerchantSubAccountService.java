package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.subaccount.vo.AppMerchantSubAccountUpdateReqVO;

import javax.validation.Valid;

public interface AppMerchantSubAccountService {

    Long create(Long authUserId, @Valid AppMerchantSubAccountCreateReqVO reqVO);

    void update(Long authUserId, @Valid AppMerchantSubAccountUpdateReqVO reqVO);

    PageResult<AppMerchantSubAccountRespVO> page(Long authUserId);

    void updateStatus(Long authUserId, @Valid AppMerchantSubAccountStatusUpdateReqVO reqVO);

    void updateServicePoints(Long authUserId, @Valid AppMerchantSubAccountServicePointUpdateReqVO reqVO);
}
