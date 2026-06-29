package cn.iocoder.yudao.module.linbang.service.merchantcategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo.MerchantServiceCategoryListReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo.MerchantServiceCategoryPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantcategory.vo.MerchantServiceCategorySaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_HAS_CHILDREN;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_IN_USE;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_PARENT_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_PARENT_NOT_EXISTS;

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
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMerchantServiceCategory(MerchantServiceCategorySaveReqVO createReqVO) {
        MerchantServiceCategoryDO merchantServiceCategory = buildCategory(createReqVO, null);
        merchantServiceCategoryMapper.insert(merchantServiceCategory);
        return merchantServiceCategory.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMerchantServiceCategory(MerchantServiceCategorySaveReqVO updateReqVO) {
        MerchantServiceCategoryDO existingCategory = validateMerchantServiceCategoryExists(updateReqVO.getId());
        MerchantServiceCategoryDO updateObj = buildCategory(updateReqVO, updateReqVO.getId());
        merchantServiceCategoryMapper.updateById(updateObj);
        if (!Objects.equals(existingCategory.getCategoryLevel(), updateObj.getCategoryLevel())) {
            refreshDescendantLevels(updateObj.getId(), updateObj.getCategoryLevel());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMerchantServiceCategory(Long id) {
        validateMerchantServiceCategoryExists(id);
        validateMerchantServiceCategoryCanDelete(id);
        merchantServiceCategoryMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMerchantServiceCategoryListByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        for (Long id : ids) {
            deleteMerchantServiceCategory(id);
        }
    }

    private MerchantServiceCategoryDO validateMerchantServiceCategoryExists(Long id) {
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(id);
        if (category == null) {
            throw exception(MERCHANT_SERVICE_CATEGORY_NOT_EXISTS);
        }
        return category;
    }

    @Override
    public MerchantServiceCategoryDO getMerchantServiceCategory(Long id) {
        return merchantServiceCategoryMapper.selectById(id);
    }

    @Override
    public List<MerchantServiceCategoryDO> getMerchantServiceCategoryList(MerchantServiceCategoryListReqVO listReqVO) {
        return merchantServiceCategoryMapper.selectList(listReqVO);
    }

    @Override
    public PageResult<MerchantServiceCategoryDO> getMerchantServiceCategoryPage(MerchantServiceCategoryPageReqVO pageReqVO) {
        return merchantServiceCategoryMapper.selectPage(pageReqVO);
    }

    private MerchantServiceCategoryDO buildCategory(MerchantServiceCategorySaveReqVO reqVO, Long currentId) {
        MerchantServiceCategoryDO category = BeanUtils.toBean(reqVO, MerchantServiceCategoryDO.class);
        MerchantServiceCategoryDO parentCategory = validateParentCategory(currentId, reqVO.getParentId());
        category.setParentId(normalizeParentId(reqVO.getParentId()));
        category.setCategoryLevel(parentCategory == null ? 1 : parentCategory.getCategoryLevel() + 1);
        category.setSupportedPricingModes(JsonUtils.toJsonString(reqVO.getSupportedPricingModes()));
        return category;
    }

    private MerchantServiceCategoryDO validateParentCategory(Long currentId, Long parentId) {
        Long normalizedParentId = normalizeParentId(parentId);
        if (normalizedParentId <= 0) {
            return null;
        }
        if (currentId != null) {
            if (Objects.equals(currentId, normalizedParentId)) {
                throw exception(MERCHANT_SERVICE_CATEGORY_PARENT_INVALID);
            }
            validateParentNotInDescendants(currentId, normalizedParentId);
        }
        MerchantServiceCategoryDO parentCategory = merchantServiceCategoryMapper.selectById(normalizedParentId);
        if (parentCategory == null) {
            throw exception(MERCHANT_SERVICE_CATEGORY_PARENT_NOT_EXISTS);
        }
        return parentCategory;
    }

    private void validateParentNotInDescendants(Long currentId, Long parentId) {
        Set<Long> descendantIds = collectDescendantIds(currentId);
        if (descendantIds.contains(parentId)) {
            throw exception(MERCHANT_SERVICE_CATEGORY_PARENT_INVALID);
        }
    }

    private Set<Long> collectDescendantIds(Long rootId) {
        Map<Long, List<MerchantServiceCategoryDO>> childrenMap = merchantServiceCategoryMapper.selectList()
                .stream()
                .filter(category -> category.getParentId() != null && category.getParentId() > 0)
                .collect(Collectors.groupingBy(MerchantServiceCategoryDO::getParentId));
        Set<Long> descendantIds = new HashSet<>();
        collectDescendantIds(rootId, childrenMap, descendantIds);
        return descendantIds;
    }

    private void collectDescendantIds(Long parentId, Map<Long, List<MerchantServiceCategoryDO>> childrenMap,
                                      Set<Long> descendantIds) {
        List<MerchantServiceCategoryDO> children = childrenMap.getOrDefault(parentId, Collections.emptyList());
        for (MerchantServiceCategoryDO child : children) {
            if (descendantIds.add(child.getId())) {
                collectDescendantIds(child.getId(), childrenMap, descendantIds);
            }
        }
    }

    private void refreshDescendantLevels(Long rootId, Integer rootLevel) {
        Map<Long, List<MerchantServiceCategoryDO>> childrenMap = merchantServiceCategoryMapper.selectList()
                .stream()
                .filter(category -> category.getParentId() != null && category.getParentId() > 0)
                .collect(Collectors.groupingBy(MerchantServiceCategoryDO::getParentId));
        refreshDescendantLevels(rootId, rootLevel, childrenMap);
    }

    private void refreshDescendantLevels(Long parentId, Integer parentLevel,
                                         Map<Long, List<MerchantServiceCategoryDO>> childrenMap) {
        List<MerchantServiceCategoryDO> children = childrenMap.getOrDefault(parentId, Collections.emptyList());
        for (MerchantServiceCategoryDO child : children) {
            Integer targetLevel = parentLevel + 1;
            if (!Objects.equals(child.getCategoryLevel(), targetLevel)) {
                child.setCategoryLevel(targetLevel);
                merchantServiceCategoryMapper.updateById(child);
            }
            refreshDescendantLevels(child.getId(), targetLevel, childrenMap);
        }
    }

    private void validateMerchantServiceCategoryCanDelete(Long id) {
        long childCount = merchantServiceCategoryMapper.selectCount(new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                .eq(MerchantServiceCategoryDO::getParentId, id));
        if (childCount > 0) {
            throw exception(MERCHANT_SERVICE_CATEGORY_HAS_CHILDREN);
        }
        long relationCount = merchantCategoryRelMapper.selectCount(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                .eq(MerchantCategoryRelDO::getCategoryId, id));
        if (relationCount > 0) {
            throw exception(MERCHANT_SERVICE_CATEGORY_IN_USE);
        }
    }

    private Long normalizeParentId(Long parentId) {
        return parentId == null || parentId <= 0 ? 0L : parentId;
    }

}
