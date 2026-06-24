package cn.iocoder.yudao.module.linbang.service.app.merchant;

import cn.hutool.core.util.IdUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantEntryCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.merchant.entry.vo.AppMerchantEntryRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_ENTRY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MERCHANT_SERVICE_CATEGORY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_USER_QUALIFICATION_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.WALLET_BANK_CARD_INVALID;

@Service
@Validated
public class AppMerchantEntryServiceImpl implements AppMerchantEntryService {

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEntry(Long authUserId, @Valid AppMerchantEntryCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        validateCategories(reqVO.getServiceCategoryIds());
        validateBankCard(loginUser.getId(), reqVO.getBankCardId());
        validateQualifications(loginUser.getId(), reqVO.getQualificationIds());

        MerchantInfoDO merchantInfo = merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, loginUser.getId()));
        if (merchantInfo == null) {
            merchantInfo = MerchantInfoDO.builder()
                    .userId(loginUser.getId())
                    .merchantName(reqVO.getMerchantName())
                    .contactName(reqVO.getContactName())
                    .contactMobile(reqVO.getContactMobile())
                    .serviceScopeDesc(reqVO.getServiceScopeDesc())
                    .status("DISABLE")
                    .acceptStatus("DISABLE")
                    .creditScore(100)
                    .creditLevel("NORMAL")
                    .build();
            merchantInfoMapper.insert(merchantInfo);
        } else {
            merchantInfoMapper.updateById(MerchantInfoDO.builder()
                    .id(merchantInfo.getId())
                    .merchantName(reqVO.getMerchantName())
                    .contactName(reqVO.getContactName())
                    .contactMobile(reqVO.getContactMobile())
                    .serviceScopeDesc(reqVO.getServiceScopeDesc())
                    .build());
            merchantInfo = merchantInfoMapper.selectById(merchantInfo.getId());
        }

        merchantCategoryRelMapper.delete(new LambdaQueryWrapperX<MerchantCategoryRelDO>()
                .eq(MerchantCategoryRelDO::getMerchantId, merchantInfo.getId()));
        for (Long categoryId : reqVO.getServiceCategoryIds()) {
            merchantCategoryRelMapper.insert(MerchantCategoryRelDO.builder()
                    .merchantId(merchantInfo.getId())
                    .categoryId(categoryId)
                    .status("ENABLE")
                    .build());
        }

        MerchantEntryDO entry = MerchantEntryDO.builder()
                .merchantId(merchantInfo.getId())
                .userId(loginUser.getId())
                .entryNo("LBE" + IdUtil.getSnowflakeNextIdStr())
                .regionCode(reqVO.getRegionCode())
                .firstAuditStatus("PENDING")
                .finalAuditStatus("PENDING")
                .status("PENDING")
                .remark(null)
                .build();
        merchantEntryMapper.insert(entry);
        return entry.getId();
    }

    @Override
    public AppMerchantEntryRespVO getCurrentEntry(Long authUserId) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MerchantEntryDO entry = merchantEntryMapper.selectOne(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, loginUser.getId())
                .orderByDesc(MerchantEntryDO::getId)
                .last("LIMIT 1"));
        if (entry == null) {
            return null;
        }
        MerchantInfoDO merchantInfo = entry.getMerchantId() != null ? merchantInfoMapper.selectById(entry.getMerchantId()) : null;
        Long bankCardId = resolveDefaultBankCardId(loginUser.getId());
        List<Long> categoryIds = resolveCategoryIds(entry.getMerchantId());
        List<Long> qualificationIds = memberUserQualificationMapper.selectListByUserId(loginUser.getId()).stream()
                .map(MemberUserQualificationDO::getId)
                .collect(Collectors.toList());
        AppMerchantEntryRespVO respVO = new AppMerchantEntryRespVO();
        respVO.setId(entry.getId());
        respVO.setMerchantId(entry.getMerchantId());
        respVO.setEntryNo(entry.getEntryNo());
        respVO.setRegionCode(entry.getRegionCode());
        respVO.setMerchantName(merchantInfo != null ? merchantInfo.getMerchantName() : null);
        respVO.setContactName(merchantInfo != null ? merchantInfo.getContactName() : null);
        respVO.setContactMobile(merchantInfo != null ? merchantInfo.getContactMobile() : null);
        respVO.setServiceScopeDesc(merchantInfo != null ? merchantInfo.getServiceScopeDesc() : null);
        respVO.setBankCardId(bankCardId);
        respVO.setServiceCategoryIds(categoryIds);
        respVO.setQualificationIds(qualificationIds);
        respVO.setFirstAuditStatus(entry.getFirstAuditStatus());
        respVO.setFinalAuditStatus(entry.getFinalAuditStatus());
        respVO.setStatus(entry.getStatus());
        respVO.setAcceptStatus(merchantInfo != null ? merchantInfo.getAcceptStatus() : null);
        respVO.setRemark(entry.getRemark());
        respVO.setCreateTime(entry.getCreateTime());
        return respVO;
    }

    private void validateCategories(List<Long> categoryIds) {
        List<MerchantServiceCategoryDO> categories = merchantServiceCategoryMapper.selectList(new LambdaQueryWrapperX<MerchantServiceCategoryDO>()
                .in(MerchantServiceCategoryDO::getId, categoryIds)
                .eq(MerchantServiceCategoryDO::getStatus, "ENABLE"));
        if (categories.size() != categoryIds.size()) {
            throw exception(MERCHANT_SERVICE_CATEGORY_NOT_EXISTS);
        }
    }

    private void validateBankCard(Long userId, Long bankCardId) {
        WalletBankCardDO bankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                .eq(WalletBankCardDO::getId, bankCardId)
                .eq(WalletBankCardDO::getUserId, userId)
                .eq(WalletBankCardDO::getStatus, "ENABLE"));
        if (bankCard == null) {
            throw exception(WALLET_BANK_CARD_INVALID);
        }
    }

    private void validateQualifications(Long userId, List<Long> qualificationIds) {
        List<MemberUserQualificationDO> qualifications = memberUserQualificationMapper.selectListByUserIdAndIds(userId, qualificationIds);
        if (qualifications.size() != qualificationIds.size()) {
            throw exception(MEMBER_USER_QUALIFICATION_NOT_EXISTS);
        }
    }

    private Long resolveDefaultBankCardId(Long userId) {
        WalletBankCardDO bankCard = walletBankCardMapper.selectOne(new LambdaQueryWrapperX<WalletBankCardDO>()
                .eq(WalletBankCardDO::getUserId, userId)
                .eq(WalletBankCardDO::getStatus, "ENABLE")
                .orderByDesc(WalletBankCardDO::getIsDefault)
                .orderByDesc(WalletBankCardDO::getId)
                .last("LIMIT 1"));
        return bankCard != null ? bankCard.getId() : null;
    }

    private List<Long> resolveCategoryIds(Long merchantId) {
        if (merchantId == null) {
            return Collections.emptyList();
        }
        return merchantCategoryRelMapper.selectListByMerchantId(merchantId).stream()
                .map(MerchantCategoryRelDO::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
