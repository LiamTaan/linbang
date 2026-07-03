package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.referenceprice.vo.AppMerchantReferencePriceUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantreferenceprice.MerchantReferencePriceDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantreferenceprice.MerchantReferencePriceMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_REFERENCE_PRICE_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_REFERENCE_PRICE_RANGE_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_NOT_EXISTS;

@Service
@Validated
public class AppMerchantReferencePriceServiceImpl implements AppMerchantReferencePriceService {

    @Resource
    private AppMerchantOperatorContextService merchantOperatorContextService;
    @Resource
    private MerchantReferencePriceMapper merchantReferencePriceMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long authUserId, @Valid AppMerchantReferencePriceCreateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        validateMerchantCategory(merchant.getId(), reqVO.getCategoryId());
        validatePriceRange(reqVO.getReferencePriceMin(), reqVO.getReferencePriceMax());
        MerchantReferencePriceDO record = MerchantReferencePriceDO.builder()
                .merchantId(merchant.getId())
                .categoryId(reqVO.getCategoryId())
                .priceUnitLabel(reqVO.getPriceUnitLabel())
                .referencePriceMin(reqVO.getReferencePriceMin())
                .referencePriceMax(reqVO.getReferencePriceMax())
                .referencePriceDesc(reqVO.getReferencePriceDesc())
                .status("ENABLE")
                .build();
        merchantReferencePriceMapper.insert(record);
        return record.getId();
    }

    @Override
    public List<AppMerchantReferencePriceRespVO> getList(Long authUserId) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        List<MerchantReferencePriceDO> records = merchantReferencePriceMapper.selectListByMerchantId(merchant.getId());
        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> categoryIds = records.stream().map(MerchantReferencePriceDO::getCategoryId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, MerchantServiceCategoryDO> categoryMap = merchantServiceCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(MerchantServiceCategoryDO::getId, item -> item));
        return records.stream().map(item -> {
            AppMerchantReferencePriceRespVO respVO = new AppMerchantReferencePriceRespVO();
            respVO.setId(item.getId());
            respVO.setCategoryId(item.getCategoryId());
            respVO.setCategoryName(categoryMap.containsKey(item.getCategoryId()) ? categoryMap.get(item.getCategoryId()).getCategoryName() : null);
            respVO.setPriceUnitLabel(item.getPriceUnitLabel());
            respVO.setReferencePriceMin(item.getReferencePriceMin());
            respVO.setReferencePriceMax(item.getReferencePriceMax());
            respVO.setReferencePriceDesc(item.getReferencePriceDesc());
            respVO.setStatus(item.getStatus());
            respVO.setUpdateTime(item.getUpdateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long authUserId, @Valid AppMerchantReferencePriceUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchant = context.getMerchant();
        MerchantReferencePriceDO existed = validateAndGet(merchant.getId(), reqVO.getId());
        validateMerchantCategory(merchant.getId(), reqVO.getCategoryId());
        validatePriceRange(reqVO.getReferencePriceMin(), reqVO.getReferencePriceMax());
        merchantReferencePriceMapper.updateById(MerchantReferencePriceDO.builder()
                .id(existed.getId())
                .categoryId(reqVO.getCategoryId())
                .priceUnitLabel(reqVO.getPriceUnitLabel())
                .referencePriceMin(reqVO.getReferencePriceMin())
                .referencePriceMax(reqVO.getReferencePriceMax())
                .referencePriceDesc(reqVO.getReferencePriceDesc())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long authUserId, @Valid AppMerchantReferencePriceStatusUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantReferencePriceDO existed = validateAndGet(context.getMerchant().getId(), reqVO.getId());
        merchantReferencePriceMapper.updateById(MerchantReferencePriceDO.builder()
                .id(existed.getId())
                .status(reqVO.getStatus())
                .build());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long authUserId, Long id) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantReferencePriceDO existed = validateAndGet(context.getMerchant().getId(), id);
        merchantReferencePriceMapper.deleteById(existed.getId());
    }

    private MerchantReferencePriceDO validateAndGet(Long merchantId, Long id) {
        MerchantReferencePriceDO existed = merchantReferencePriceMapper.selectByIdAndMerchantId(id, merchantId);
        if (existed == null) {
            throw exception(MERCHANT_REFERENCE_PRICE_NOT_EXISTS);
        }
        return existed;
    }

    private void validateMerchantCategory(Long merchantId, Long categoryId) {
        MerchantCategoryRelDO categoryRel = merchantCategoryRelMapper.selectOne(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                .eq(MerchantCategoryRelDO::getMerchantId, merchantId)
                .eq(MerchantCategoryRelDO::getCategoryId, categoryId)
                .last("LIMIT 1"));
        MerchantServiceCategoryDO category = merchantServiceCategoryMapper.selectById(categoryId);
        if (categoryRel == null || category == null || !"ENABLE".equals(category.getStatus())) {
            throw exception(MERCHANT_SERVICE_CATEGORY_NOT_EXISTS);
        }
    }

    private void validatePriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice) {
        if (minPrice == null || maxPrice == null || minPrice.compareTo(maxPrice) > 0) {
            throw exception(MERCHANT_REFERENCE_PRICE_RANGE_INVALID);
        }
    }
}
