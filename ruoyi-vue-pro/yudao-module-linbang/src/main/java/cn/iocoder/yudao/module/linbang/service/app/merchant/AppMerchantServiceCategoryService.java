package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo.AppMerchantServiceCategoryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo.AppMerchantSelectedCategoryUpdateReqVO;

import javax.validation.Valid;
import java.util.List;

public interface AppMerchantServiceCategoryService {

    List<AppMerchantServiceCategoryRespVO> getCategoryList(String keyword);

    void updateSelectedCategories(Long authUserId, @Valid AppMerchantSelectedCategoryUpdateReqVO reqVO);

}
