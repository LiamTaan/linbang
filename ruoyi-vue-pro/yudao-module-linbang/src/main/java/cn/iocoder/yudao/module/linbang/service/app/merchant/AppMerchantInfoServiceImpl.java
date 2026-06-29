package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantAcceptStatusUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.info.vo.AppMerchantProfileUpdateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.dispatchsetting.vo.AppMerchantDispatchSettingRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.dispatchsetting.vo.AppMerchantDispatchSettingUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantdispatchsetting.MerchantDispatchSettingDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.ordermatchrecord.OrderMatchRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantservicepoint.MerchantServicePointDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantdispatchsetting.MerchantDispatchSettingMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantservicepoint.MerchantServicePointMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.ordermatchrecord.OrderMatchRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.service.match.MerchantDispatchSettingService;
import cn.iocoder.yudao.module.linbang.service.memberqualification.MemberQualificationExpiryService;
import cn.iocoder.yudao.module.linbang.service.match.PriorityPoolService;
import cn.iocoder.yudao.module.linbang.service.match.ShowcaseRewardService;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.MerchantReviewMetricsResp;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.ReviewCommentMetricsService;
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
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantServicePointMapper merchantServicePointMapper;
    @Resource
    private MerchantDispatchSettingService merchantDispatchSettingService;
    @Resource
    private MerchantDispatchSettingMapper merchantDispatchSettingMapper;
    @Resource
    private PriorityPoolService priorityPoolService;
    @Resource
    private ShowcaseRewardService showcaseRewardService;
    @Resource
    private OrderMatchRecordMapper orderMatchRecordMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private ReviewCommentMetricsService reviewCommentMetricsService;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;
    @Resource
    private MemberQualificationExpiryService memberQualificationExpiryService;
    @Resource
    private AppMerchantOperatorContextService merchantOperatorContextService;

    @Override
    public AppMerchantProfileRespVO getProfile(Long authUserId) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
        MerchantEntryDO latestEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getMerchantId, merchantInfo.getId())
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        List<MerchantCategoryRelDO> categoryRels = merchantCategoryRelMapper.selectListByMerchantId(merchantInfo.getId());
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(categoryRels);
        List<MerchantServicePointDO> servicePoints = merchantOperatorContextService.filterVisibleServicePoints(context,
                merchantServicePointMapper.selectList(new LambdaQueryWrapperX<MerchantServicePointDO>()
                .eq(MerchantServicePointDO::getMerchantId, merchantInfo.getId())
                .orderByDesc(MerchantServicePointDO::getId)));
        MerchantReviewMetricsResp metrics = reviewCommentMetricsService.calculateMerchantMetrics(merchantInfo.getId());

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
        respVO.setCompositeScore(metrics.getCompositeScore());
        respVO.setReviewCount(metrics.getReviewCount());
        respVO.setPositiveRate(metrics.getPositiveRate());
        respVO.setInPositivePriorityPool(metrics.getInPositivePriorityPool());
        respVO.setServiceScopeDesc(merchantInfo.getServiceScopeDesc());
        if (latestEntry != null) {
            respVO.setEntryId(latestEntry.getId());
            respVO.setEntryNo(latestEntry.getEntryNo());
            respVO.setRegionCode(latestEntry.getRegionCode());
            respVO.setEntryStatus(latestEntry.getStatus());
            respVO.setFirstAuditStatus(latestEntry.getFirstAuditStatus());
            respVO.setFinalAuditStatus(latestEntry.getFinalAuditStatus());
            respVO.setProgressStatus(latestEntry.getProgressStatus());
            respVO.setCurrentStageName(latestEntry.getCurrentStageName());
            respVO.setCurrentStageTime(latestEntry.getCurrentStageTime());
            respVO.setRejectReason(latestEntry.getRejectReason());
            respVO.setOnboardingBlockedReason(latestEntry.getOnboardingBlockedReason());
            respVO.setAcceptEnabled(latestEntry.getAcceptEnabled());
        }
        respVO.setMainAccountOperator(context.isMainAccount());
        respVO.setOperatorPermissionCodes(context.isMainAccount() ? Collections.emptyList() : context.getPermissionCodes().stream().sorted().collect(Collectors.toList()));
        respVO.setVisibleServicePointIds(context.getSafeVisibleServicePointIds());
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
            item.setManageable(context.isMainAccount() || context.getSafeVisibleServicePointIds().contains(point.getId()));
            return item;
        }).collect(Collectors.toList()));
        respVO.setUpdateTime(merchantInfo.getUpdateTime());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(Long authUserId, @Valid AppMerchantProfileUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredMerchantManageContext(authUserId);
        MerchantInfoDO merchantInfo = context.getMerchant();
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
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredContext(authUserId);
        MerchantInfoDO merchantInfo = getEnabledMerchantInfo(context);
        MerchantDispatchSettingDO setting = merchantDispatchSettingService.getOrCreate(merchantInfo.getId());
        AppMerchantAcceptStatusRespVO respVO = new AppMerchantAcceptStatusRespVO();
        respVO.setMerchantId(merchantInfo.getId());
        respVO.setMerchantStatus(merchantInfo.getStatus());
        respVO.setAcceptStatus(merchantInfo.getAcceptStatus());
        MerchantEntryDO latestEntry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getMerchantId, merchantInfo.getId())
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        boolean bankCardBound = hasEnabledBankCard(merchantInfo.getUserId());
        boolean qualificationValid = memberQualificationExpiryService.canMerchantAccept(merchantInfo.getUserId());
        boolean acceptEligible = "ENABLE".equals(merchantInfo.getStatus())
                && "ENABLE".equals(merchantInfo.getAcceptStatus())
                && latestEntry != null
                && Boolean.TRUE.equals(latestEntry.getAcceptEnabled())
                && bankCardBound
                && qualificationValid;
        respVO.setAcceptEligible(acceptEligible);
        if (latestEntry != null && "APPROVED_WAIT_BANK_CARD".equals(latestEntry.getProgressStatus())) {
            respVO.setBlockedReason("终审已通过，但尚未绑定有效银行卡");
            respVO.setNextAction("请先绑定有效银行卡");
        } else if (!qualificationValid) {
            respVO.setBlockedReason("必需证件已过期或待更新");
            respVO.setNextAction("请先更新已过期证件或等待豁免审核");
        } else if (latestEntry != null && latestEntry.getOnboardingBlockedReason() != null) {
            respVO.setBlockedReason(latestEntry.getOnboardingBlockedReason());
            respVO.setNextAction("请根据当前阻塞原因完成处理");
        }
        respVO.setDispatchEnabled(setting.getDispatchEnabled());
        respVO.setMaxAcceptRadiusKm(setting.getMaxAcceptRadiusKm());
        respVO.setVoiceRemindEnabled(setting.getVoiceRemindEnabled());
        respVO.setPriorityPoolFlag(priorityPoolService.isInPriorityPool(merchantInfo.getId()));
        respVO.setShowcaseRewardActive(showcaseRewardService.hasActiveReward(merchantInfo.getId()));
        respVO.setMainAccountOperator(context.isMainAccount());
        respVO.setOperatorPermissionCodes(context.isMainAccount() ? Collections.emptyList() : context.getPermissionCodes().stream().sorted().collect(Collectors.toList()));
        respVO.setVisibleServicePointIds(context.getSafeVisibleServicePointIds());
        List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectListByUserId(merchantInfo.getUserId());
        respVO.setPlatformClothingCertified(hasQualification(qualifications, "PLATFORM_CLOTHING"));
        respVO.setToolboxCertified(hasQualification(qualifications, "TOOLBOX"));
        respVO.setRecentPushes(orderMatchRecordMapper.selectActiveListByMerchantId(merchantInfo.getId(), java.time.LocalDateTime.now())
                .stream()
                .limit(5)
                .map(this::convertRecentPush)
                .collect(Collectors.toList()));
        return respVO;
    }

    @Override
    public void updateAcceptStatus(Long authUserId, @Valid AppMerchantAcceptStatusUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredOrderAcceptContext(authUserId);
        MerchantInfoDO merchantInfo = getEnabledMerchantInfo(context);
        merchantInfoMapper.updateById(MerchantInfoDO.builder()
                .id(merchantInfo.getId())
                .acceptStatus(reqVO.getAcceptStatus())
                .build());
        MerchantDispatchSettingDO setting = merchantDispatchSettingService.getOrCreate(merchantInfo.getId());
        if (reqVO.getDispatchEnabled() != null || reqVO.getMaxAcceptRadiusKm() != null || reqVO.getVoiceRemindEnabled() != null) {
            merchantDispatchSettingMapperUpdate(setting, reqVO);
        }
    }

    @Override
    public AppMerchantDispatchSettingRespVO getDispatchSetting(Long authUserId) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredContext(authUserId);
        MerchantInfoDO merchantInfo = getEnabledMerchantInfo(context);
        MerchantDispatchSettingDO setting = merchantDispatchSettingService.getOrCreate(merchantInfo.getId());
        AppMerchantDispatchSettingRespVO respVO = new AppMerchantDispatchSettingRespVO();
        respVO.setMerchantId(merchantInfo.getId());
        respVO.setDispatchEnabled(setting.getDispatchEnabled());
        respVO.setMaxAcceptRadiusKm(setting.getMaxAcceptRadiusKm());
        respVO.setVoiceRemindEnabled(setting.getVoiceRemindEnabled());
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDispatchSetting(Long authUserId, @Valid AppMerchantDispatchSettingUpdateReqVO reqVO) {
        AppMerchantOperatorContext context = merchantOperatorContextService.getRequiredOrderAcceptContext(authUserId);
        MerchantInfoDO merchantInfo = getEnabledMerchantInfo(context);
        merchantDispatchSettingService.updateSetting(MerchantDispatchSettingDO.builder()
                .merchantId(merchantInfo.getId())
                .dispatchEnabled(reqVO.getDispatchEnabled())
                .maxAcceptRadiusKm(reqVO.getMaxAcceptRadiusKm())
                .voiceRemindEnabled(reqVO.getVoiceRemindEnabled())
                .build());
    }

    private MerchantInfoDO getEnabledMerchantInfo(AppMerchantOperatorContext context) {
        MerchantInfoDO merchantInfo = context.getMerchant();
        if (merchantInfo == null) {
            throw exception(MERCHANT_INFO_NOT_EXISTS);
        }
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

    private boolean hasQualification(List<MemberUserQualificationDO> qualifications, String type) {
        return qualifications.stream().anyMatch(item -> Objects.equals(item.getQualificationType(), type)
                && Objects.equals(item.getAuditStatus(), "APPROVED")
                && Boolean.TRUE.equals(item.getPriorityEnabled()));
    }

    private boolean hasEnabledBankCard(Long userId) {
        return walletBankCardMapper.selectOne(new LambdaQueryWrapperX<cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO>()
                .eq(cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO::getUserId, userId)
                .eq(cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO::getStatus, "ENABLE")
                .last("LIMIT 1")) != null;
    }

    private AppMerchantAcceptStatusRespVO.RecentPushItem convertRecentPush(OrderMatchRecordDO record) {
        AppMerchantAcceptStatusRespVO.RecentPushItem item = new AppMerchantAcceptStatusRespVO.RecentPushItem();
        item.setUnitId(record.getUnitId());
        item.setStageNo(record.getStageNo());
        item.setPushBatchNo(record.getPushBatchNo());
        item.setPushTime(record.getPushTime());
        item.setCountdownSeconds(record.getExpiredTime() == null ? null
                : Math.max(0, (int) java.time.Duration.between(java.time.LocalDateTime.now(), record.getExpiredTime()).getSeconds()));
        return item;
    }

    private void merchantDispatchSettingMapperUpdate(MerchantDispatchSettingDO setting, AppMerchantAcceptStatusUpdateReqVO reqVO) {
        MerchantDispatchSettingDO updateObj = MerchantDispatchSettingDO.builder()
                .id(setting.getId())
                .dispatchEnabled(reqVO.getDispatchEnabled() != null ? reqVO.getDispatchEnabled() : setting.getDispatchEnabled())
                .maxAcceptRadiusKm(reqVO.getMaxAcceptRadiusKm() != null ? reqVO.getMaxAcceptRadiusKm() : setting.getMaxAcceptRadiusKm())
                .voiceRemindEnabled(reqVO.getVoiceRemindEnabled() != null ? reqVO.getVoiceRemindEnabled() : setting.getVoiceRemindEnabled())
                .build();
        merchantDispatchSettingMapper.updateById(updateObj);
    }

}
