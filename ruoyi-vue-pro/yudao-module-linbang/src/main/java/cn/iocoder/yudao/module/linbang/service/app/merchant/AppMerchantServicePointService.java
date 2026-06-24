package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.servicepoint.vo.AppMerchantServicePointUpdateReqVO;

import javax.validation.Valid;

public interface AppMerchantServicePointService {

    Long createServicePoint(Long authUserId, @Valid AppMerchantServicePointCreateReqVO reqVO);

    PageResult<AppMerchantServicePointRespVO> getServicePointPage(Long authUserId, AppMerchantServicePointPageReqVO reqVO);

    AppMerchantServicePointRespVO getServicePoint(Long authUserId, Long id);

    void updateServicePoint(Long authUserId, @Valid AppMerchantServicePointUpdateReqVO reqVO);

    void updateServicePointStatus(Long authUserId, @Valid AppMerchantServicePointStatusUpdateReqVO reqVO);

    void deleteServicePoint(Long authUserId, Long id);

}
