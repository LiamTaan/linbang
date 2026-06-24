package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileUpdateReqVO;

import javax.validation.Valid;

public interface AppMerchantInfoService {

    AppMerchantProfileRespVO getProfile(Long authUserId);

    void updateProfile(Long authUserId, @Valid AppMerchantProfileUpdateReqVO reqVO);

    AppMerchantAcceptStatusRespVO getAcceptStatus(Long authUserId);

    void updateAcceptStatus(Long authUserId, @Valid AppMerchantAcceptStatusUpdateReqVO reqVO);

}
