package cn.iocoder.yudao.module.linbang.service.merchantservicepoint;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantservicepoint.vo.MerchantServicePointSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;

import javax.validation.Valid;
import java.util.List;

public interface MerchantServicePointService {

    Long createMerchantServicePoint(@Valid MerchantServicePointSaveReqVO createReqVO);

    void updateMerchantServicePoint(@Valid MerchantServicePointSaveReqVO updateReqVO);

    void deleteMerchantServicePoint(Long id);

    void deleteMerchantServicePointListByIds(List<Long> ids);

    MerchantServicePointDO getMerchantServicePoint(Long id);

    MerchantServicePointDetailRespVO getMerchantServicePointDetail(Long id);

    PageResult<MerchantServicePointRespVO> getMerchantServicePointPage(MerchantServicePointPageReqVO pageReqVO);
}
