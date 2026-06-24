package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
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
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_AUTH_REQUIRED;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_INFO_NOT_EXISTS;

@Service
@Validated
public class AppMerchantInfoServiceImpl implements AppMerchantInfoService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;

    @Override
    public AppMerchantProfileRespVO getProfile(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchantInfo = getMerchantInfoByAuthUserId(authUserId);
        MerchantEntryDO latestEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, loginUser.getId())
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        List<MerchantCategoryRelDO> categoryRels = merchantCategoryRelMapper.selectListByMerchantId(merchantInfo.getId());
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(categoryRels);
        List<MerchantServicePointDO> servicePoints = merchantServicePointMapper.selectList(new LambdaQueryWrapperX<MerchantServicePointDO>()
                .eq(MerchantServicePointDO::getMerchantId, merchantInfo.getId())
                .orderByDesc(MerchantServicePointDO::getId));

        AppMerchantProfileRespVO respVO = new AppMerchantProfileRespVO();
        respVO.setMerchantId(merchantInfo.getId());
        respVO.setUserId(merchantInfo.getUserId());
        respVO.setMerchantName(merchantInfo.getMerchantName());
        respVO.setContactName(merchantInfo.getContactName());
        respVO.setContactMobile(merchantInfo.getContactMobile());
        respVO.setMerchantStatus(merchantInfo.getStatus());
        respVO.setAcceptStatus(merchantInfo.getAcceptStatus());
        respVO.setCreditScore(merchantInfo.getCreditScore());
        respVO.setCreditLevel(merchantInfo.getCreditLevel());
        respVO.setServiceScopeDesc(merchantInfo.getServiceScopeDesc());
        if (latestEntry != null) {
            respVO.setEntryId(latestEntry.getId());
            respVO.setEntryNo(latestEntry.getEntryNo());
            respVO.setRegionCode(latestEntry.getRegionCode());
            respVO.setEntryStatus(latestEntry.getStatus());
            respVO.setFirstAuditStatus(latestEntry.getFirstAuditStatus());
            respVO.setFinalAuditStatus(latestEntry.getFinalAuditStatus());
        }
        respVO.setServicePointCount(servicePoints.size());
        respVO.setEnabledServicePointCount((int) servicePoints.stream()
                .filter(point -> Objects.equals(point.getStatus(), "ENABLE"))
                .count());
        respVO.setCategories(categoryRels.stream().map(rel -> {
            MerchantServiceCategoryDO category = categoryMap.get(rel.getCategoryId());
            AppMerchantProfileRespVO.MerchantCategoryItem item = new AppMerchantProfileRespVO.MerchantCategoryItem();
            item.setCategoryId(rel.getCategoryId());
            if (category != null) {
                item.setCategoryName(category.getCategoryName());
                item.setParentId(category.getParentId());
                item.setCategoryLevel(category.getCategoryLevel());
                item.setDefaultPricingMode(category.getDefaultPricingMode());
                item.setSupportSplit(category.getSupportSplit());
                item.setSupportInvoice(category.getSupportInvoice());
            }
            return item;
        }).collect(Collectors.toList()));
        respVO.setServiceAreas(servicePoints.stream().map(point -> {
            AppMerchantProfileRespVO.ServiceAreaItem item = new AppMerchantProfileRespVO.ServiceAreaItem();
            item.setServicePointId(point.getId());
            item.setPointName(point.getPointName());
            item.setProvince(point.getProvince());
            item.setCity(point.getCity());
            item.setDistrict(point.getDistrict());
            item.setStreet(point.getStreet());
            item.setDetailAddress(point.getDetailAddress());
            item.setLongitude(point.getLongitude());
            item.setLatitude(point.getLatitude());
            item.setServiceRadiusKm(point.getServiceRadiusKm());
            item.setStatus(point.getStatus());
            return item;
        }).collect(Collectors.toList()));
        respVO.setUpdateTime(merchantInfo.getUpdateTime());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long authUserId, @Valid AppMerchantProfileUpdateReqVO reqVO) {
        MerchantInfoDO merchantInfo = getMerchantInfoByAuthUserId(authUserId);
        merchantInfoMapper.updateById(MerchantInfoDO.builder()
                .id(merchantInfo.getId())
                .merchantName(reqVO.getMerchantName())
                .contactName(reqVO.getContactName())
                .contactMobile(reqVO.getContactMobile())
                .serviceScopeDesc(reqVO.getServiceScopeDesc())
                .build());
    }

    @Override
    public AppMerchantAcceptStatusRespVO getAcceptStatus(Long authUserId) {
        MerchantInfoDO merchantInfo = getEnabledMerchantInfo(authUserId);
        AppMerchantAcceptStatusRespVO respVO = new AppMerchantAcceptStatusRespVO();
        respVO.setMerchantId(merchantInfo.getId());
        respVO.setMerchantStatus(merchantInfo.getStatus());
        respVO.setAcceptStatus(merchantInfo.getAcceptStatus());
        return respVO;
    }

    @Override
    public void updateAcceptStatus(Long authUserId, @Valid AppMerchantAcceptStatusUpdateReqVO reqVO) {
        MerchantInfoDO merchantInfo = getEnabledMerchantInfo(authUserId);
        merchantInfoMapper.updateById(MerchantInfoDO.builder()
                .id(merchantInfo.getId())
                .acceptStatus(reqVO.getAcceptStatus())
                .build());
    }

    private MerchantInfoDO getMerchantInfoByAuthUserId(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        if (merchantInfo == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }
        return merchantInfo;
    }

    private MerchantInfoDO getEnabledMerchantInfo(Long authUserId) {
        MerchantInfoDO merchantInfo = getMerchantInfoByAuthUserId(authUserId);
        if (!"ENABLE".equals(merchantInfo.getStatus())) {
            throw exception(MERCHANT_AUTH_REQUIRED);
        }
        return merchantInfo;
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<MerchantCategoryRelDO> categoryRels) {
        if (categoryRels == null || categoryRels.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> categoryIds = categoryRels.stream()
                .map(MerchantCategoryRelDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return merchantServiceCategoryMapper.selectBatchIds(categoryIds).stream()
                .collect(Collectors.toMap(MerchantServiceCategoryDO::getId, item -> item));
    }

}
