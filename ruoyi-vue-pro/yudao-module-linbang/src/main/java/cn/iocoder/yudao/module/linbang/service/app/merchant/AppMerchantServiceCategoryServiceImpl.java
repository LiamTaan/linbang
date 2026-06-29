package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo.AppMerchantServiceCategoryRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.category.vo.AppMerchantSelectedCategoryUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_AUTH_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_NOT_EXISTS;

@Service
@Validated
public class AppMerchantServiceCategoryServiceImpl implements AppMerchantServiceCategoryService {

    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MemberUserService memberUserService;

    @Override
    public List<AppMerchantServiceCategoryRespVO> getCategoryList(String keyword) {
        List<AppMerchantServiceCategoryRespVO> categories = merchantServiceCategoryMapper.selectList(new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                        .eq(MerchantServiceCategoryDO::getStatus, "ENABLE")
                        .orderByAsc(MerchantServiceCategoryDO::getCategoryLevel)
                        .orderByAsc(MerchantServiceCategoryDO::getParentId)
                        .orderByAsc(MerchantServiceCategoryDO::getSortNo)
                        .orderByAsc(MerchantServiceCategoryDO::getId))
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
        if (StrUtil.isNotBlank(keyword)) {
            String normalizedKeyword = StrUtil.trim(keyword);
            return categories.stream()
                    .filter(category -> StrUtil.containsIgnoreCase(category.getCategoryName(), normalizedKeyword))
                    .collect(Collectors.toList());
        }
        return buildCategoryTree(categories);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSelectedCategories(Long authUserId, @Valid AppMerchantSelectedCategoryUpdateReqVO reqVO) {
        MerchantInfoDO merchantInfo = getCurrentMerchant(authUserId);
        List<Long> categoryIds = normalizeCategoryIds(reqVO.getServiceCategoryIds());
        validateCategories(categoryIds);
        merchantCategoryRelMapper.delete(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                .eq(MerchantCategoryRelDO::getMerchantId, merchantInfo.getId()));
        for (Long categoryId : categoryIds) {
            merchantCategoryRelMapper.insert(MerchantCategoryRelDO.builder()
                    .merchantId(merchantInfo.getId())
                    .categoryId(categoryId)
                    .status("ENABLE")
                    .build());
        }
    }

    private AppMerchantServiceCategoryRespVO convert(MerchantServiceCategoryDO category) {
        AppMerchantServiceCategoryRespVO respVO = new AppMerchantServiceCategoryRespVO();
        respVO.setId(category.getId());
        respVO.setParentId(category.getParentId());
        respVO.setCategoryName(category.getCategoryName());
        respVO.setCategoryLevel(category.getCategoryLevel());
        respVO.setDefaultPricingMode(category.getDefaultPricingMode());
        respVO.setSupportedPricingModes(parseSupportedPricingModes(category));
        respVO.setSupportSplit(category.getSupportSplit());
        respVO.setSupportInvoice(category.getSupportInvoice());
        respVO.setLaborCategoryFlag(category.getLaborCategoryFlag());
        respVO.setForceAgreementType(category.getForceAgreementType());
        respVO.setInvoiceRateReminderText(category.getInvoiceRateReminderText());
        return respVO;
    }

    private List<AppMerchantServiceCategoryRespVO> buildCategoryTree(List<AppMerchantServiceCategoryRespVO> categories) {
        Map<Long, AppMerchantServiceCategoryRespVO> categoryMap = new LinkedHashMap<>();
        List<AppMerchantServiceCategoryRespVO> roots = new ArrayList<>();
        categories.forEach(category -> categoryMap.put(category.getId(), category));
        for (AppMerchantServiceCategoryRespVO category : categories) {
            Long parentId = category.getParentId();
            if (parentId == null || parentId <= 0) {
                roots.add(category);
                continue;
            }
            AppMerchantServiceCategoryRespVO parent = categoryMap.get(parentId);
            if (parent == null) {
                roots.add(category);
                continue;
            }
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(category);
        }
        return roots;
    }

    private void validateCategories(List<Long> categoryIds) {
        List<MerchantServiceCategoryDO> categories = merchantServiceCategoryMapper.selectList(new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                .in(MerchantServiceCategoryDO::getId, categoryIds)
                .eq(MerchantServiceCategoryDO::getStatus, "ENABLE"));
        if (categoryIds.isEmpty() || categories.size() != categoryIds.size()) {
            throw exception(MERCHANT_SERVICE_CATEGORY_NOT_EXISTS);
        }
    }

    private List<Long> normalizeCategoryIds(List<Long> categoryIds) {
        return categoryIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private MerchantInfoDO getCurrentMerchant(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        if (merchantInfo == null) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return merchantInfo;
    }

    private List<String> parseSupportedPricingModes(MerchantServiceCategoryDO category) {
        if (StrUtil.isBlank(category.getSupportedPricingModes())) {
            return StrUtil.isBlank(category.getDefaultPricingMode())
                    ? Collections.emptyList()
                    : Collections.singletonList(category.getDefaultPricingMode());
        }
        List<String> modes = JsonUtils.parseArray(category.getSupportedPricingModes(), String.class);
        if (CollUtil.isNotEmpty(modes)) {
            return modes;
        }
        return StrUtil.isBlank(category.getDefaultPricingMode())
                ? Collections.emptyList()
                : Collections.singletonList(category.getDefaultPricingMode());
    }

}
