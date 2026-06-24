package cn.iocoder.yudao.module.linbang.service.app.merchant;

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
import java.util.List;
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
    public List<AppMerchantServiceCategoryRespVO> getCategoryList() {
        return merchantServiceCategoryMapper.selectList(new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                        .eq(MerchantServiceCategoryDO::getStatus, "ENABLE")
                        .orderByAsc(MerchantServiceCategoryDO::getParentId)
                        .orderByAsc(MerchantServiceCategoryDO::getSortNo)
                        .orderByAsc(MerchantServiceCategoryDO::getId))
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
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
        respVO.setSupportSplit(category.getSupportSplit());
        respVO.setSupportInvoice(category.getSupportInvoice());
        return respVO;
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

}
