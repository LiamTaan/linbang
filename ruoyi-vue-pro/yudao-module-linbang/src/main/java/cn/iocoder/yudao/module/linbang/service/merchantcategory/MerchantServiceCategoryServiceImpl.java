package cn.iocoder.yudao.module.linbang.service.merchantcategory;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 服务类目表 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class MerchantServiceCategoryServiceImpl implements MerchantServiceCategoryService {

    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;

    @Override
    public Long createMerchantServiceCategory(MerchantServiceCategorySaveReqVO createReqVO) {
        // 插入
        MerchantServiceCategoryDO merchantServiceCategory = BeanUtils.toBean(createReqVO, MerchantServiceCategoryDO.class);
        merchantServiceCategoryMapper.insert(merchantServiceCategory);

        // 返回
        return merchantServiceCategory.getId();
    }

    @Override
    public void updateMerchantServiceCategory(MerchantServiceCategorySaveReqVO updateReqVO) {
        // 校验存在
        validateMerchantServiceCategoryExists(updateReqVO.getId());
        // 更新
        MerchantServiceCategoryDO updateObj = BeanUtils.toBean(updateReqVO, MerchantServiceCategoryDO.class);
        merchantServiceCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteMerchantServiceCategory(Long id) {
        // 校验存在
        validateMerchantServiceCategoryExists(id);
        // 删除
        merchantServiceCategoryMapper.deleteById(id);
    }

    @Override
        public void deleteMerchantServiceCategoryListByIds(List<Long> ids) {
        // 删除
        merchantServiceCategoryMapper.deleteByIds(ids);
        }


    private void validateMerchantServiceCategoryExists(Long id) {
        if (merchantServiceCategoryMapper.selectById(id) == null) {
            throw exception(MERCHANT_SERVICE_CATEGORY_NOT_EXISTS);
        }
    }

    @Override
    public MerchantServiceCategoryDO getMerchantServiceCategory(Long id) {
        return merchantServiceCategoryMapper.selectById(id);
    }

    @Override
    public PageResult<MerchantServiceCategoryDO> getMerchantServiceCategoryPage(MerchantServiceCategoryPageReqVO pageReqVO) {
        return merchantServiceCategoryMapper.selectPage(pageReqVO);
    }

}