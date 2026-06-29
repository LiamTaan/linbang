package cn.iocoder.yudao.module.linbang.service.merchantsubaccount;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountSaveReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountServicePointUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantsubaccount.vo.MerchantSubAccountStatusUpdateReqVO;

import javax.validation.Valid;

public interface MerchantSubAccountAdminService {

    Long create(@Valid MerchantSubAccountSaveReqVO reqVO);

    void update(@Valid MerchantSubAccountSaveReqVO reqVO);

    PageResult<MerchantSubAccountRespVO> getPage(MerchantSubAccountPageReqVO reqVO);

    MerchantSubAccountRespVO get(Long id);

    void updateStatus(@Valid MerchantSubAccountStatusUpdateReqVO reqVO);

    void updateServicePoints(@Valid MerchantSubAccountServicePointUpdateReqVO reqVO);
}
