package cn.iocoder.yudao.module.linbang.service.merchantcategory;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 服务类目表 Service 接口
 *
 * @author dawn
 */
public interface MerchantServiceCategoryService {

    /**
     * 创建服务类目表
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMerchantServiceCategory(@Valid MerchantServiceCategorySaveReqVO createReqVO);

    /**
     * 更新服务类目表
     *
     * @param updateReqVO 更新信息
     */
    void updateMerchantServiceCategory(@Valid MerchantServiceCategorySaveReqVO updateReqVO);

    /**
     * 删除服务类目表
     *
     * @param id 编号
     */
    void deleteMerchantServiceCategory(Long id);

    /**
    * 批量删除服务类目表
    *
    * @param ids 编号
    */
    void deleteMerchantServiceCategoryListByIds(List<Long> ids);

    /**
     * 获得服务类目表列表
     *
     * @param listReqVO 列表查询
     * @return 服务类目表列表
     */
    List<MerchantServiceCategoryDO> getMerchantServiceCategoryList(MerchantServiceCategoryListReqVO listReqVO);

    /**
     * 获得服务类目表
     *
     * @param id 编号
     * @return 服务类目表
     */
    MerchantServiceCategoryDO getMerchantServiceCategory(Long id);

    /**
     * 获得服务类目表分页
     *
     * @param pageReqVO 分页查询
     * @return 服务类目表分页
     */
    PageResult<MerchantServiceCategoryDO> getMerchantServiceCategoryPage(MerchantServiceCategoryPageReqVO pageReqVO);

}
