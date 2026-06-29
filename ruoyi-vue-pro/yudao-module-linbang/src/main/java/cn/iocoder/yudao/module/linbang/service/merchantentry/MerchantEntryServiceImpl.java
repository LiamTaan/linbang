package cn.iocoder.yudao.module.linbang.service.merchantentry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo.*;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategory.MerchantServiceCategoryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantcategoryrel.MerchantCategoryRelDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantentry.MerchantEntryDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategory.MerchantServiceCategoryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantcategoryrel.MerchantCategoryRelMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantentry.MerchantEntryMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletbankcard.WalletBankCardMapper;
import cn.iocoder.yudao.module.linbang.service.creditrecord.CreditRecordService;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 服务商入驻申请表 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class MerchantEntryServiceImpl implements MerchantEntryService {

    @Resource
    private MerchantEntryMapper merchantEntryMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private MerchantCategoryRelMapper merchantCategoryRelMapper;
    @Resource
    private MerchantServiceCategoryMapper merchantServiceCategoryMapper;
    @Resource
    private MemberUserService memberUserService;
    @Resource
    private CreditRecordService creditRecordService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;
    @Resource
    private WalletBankCardMapper walletBankCardMapper;

    @Override
    public Long createMerchantEntry(MerchantEntrySaveReqVO createReqVO) {
        // 插入
        MerchantEntryDO merchantEntry = BeanUtils.toBean(createReqVO, MerchantEntryDO.class);
        merchantEntryMapper.insert(merchantEntry);

        // 返回
        return merchantEntry.getId();
    }

    @Override
    public void updateMerchantEntry(MerchantEntrySaveReqVO updateReqVO) {
        // 校验存在
        validateMerchantEntryExists(updateReqVO.getId());
        // 更新
        MerchantEntryDO updateObj = BeanUtils.toBean(updateReqVO, MerchantEntryDO.class);
        merchantEntryMapper.updateById(updateObj);
    }

    @Override
    public void deleteMerchantEntry(Long id) {
        // 校验存在
        validateMerchantEntryExists(id);
        // 删除
        merchantEntryMapper.deleteById(id);
    }

    @Override
        public void deleteMerchantEntryListByIds(List<Long> ids) {
        // 删除
        merchantEntryMapper.deleteByIds(ids);
        }


    private void validateMerchantEntryExists(Long id) {
        if (merchantEntryMapper.selectById(id) == null) {
            throw exception(MERCHANT_ENTRY_NOT_EXISTS);
        }
    }

    @Override
    public MerchantEntryDO getMerchantEntry(Long id) {
        return merchantEntryMapper.selectById(id);
    }

    @Override
    public MerchantEntryDetailRespVO getMerchantEntryDetail(Long id) {
        MerchantEntryDO entry = merchantEntryMapper.selectById(id);
        if (entry == null) {
            throw exception(MERCHANT_ENTRY_NOT_EXISTS);
        }
        MemberUserDO applicant = entry.getUserId() == null ? null : memberUserMapper.selectById(entry.getUserId());
        MerchantInfoDO merchant = entry.getMerchantId() == null ? null : merchantInfoMapper.selectById(entry.getMerchantId());
        MemberUserRealNameDO realName = entry.getUserId() == null ? null : memberUserRealNameMapper.selectByUserId(entry.getUserId());
        List<MerchantCategoryRelDO> categoryRels = entry.getMerchantId() == null ? Collections.emptyList()
                : merchantCategoryRelMapper.selectListByMerchantId(entry.getMerchantId());
        Map<Long, MerchantServiceCategoryDO> categoryMap = buildCategoryMap(categoryRels);
        List<MemberUserQualificationDO> qualifications = entry.getUserId() == null ? Collections.emptyList()
                : memberUserQualificationMapper.selectListByUserId(entry.getUserId());
        List<MerchantEntryDO> historyEntries = entry.getUserId() == null ? Collections.emptyList()
                : merchantEntryMapper.selectList(new LambdaQueryWrapperX<MerchantEntryDO>()
                .eq(MerchantEntryDO::getUserId, entry.getUserId())
                .orderByDesc(MerchantEntryDO::getId));
        return MerchantEntryDetailAssembler.build(entry, applicant, merchant, realName, categoryRels, categoryMap,
                qualifications, historyEntries);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditMerchantEntry(MerchantEntryAuditReqVO reqVO) {
        MerchantEntryDO entry = merchantEntryMapper.selectById(reqVO.getId());
        if (entry == null) {
            throw exception(MERCHANT_ENTRY_NOT_EXISTS);
        }
        Long loginUserId = SecurityFrameworkUtils.getLoginUserId();
        LocalDateTime now = LocalDateTime.now();
        MerchantEntryDO updateObj = new MerchantEntryDO();
        updateObj.setId(entry.getId());
        updateObj.setRemark("REJECTED".equals(reqVO.getAuditStatus()) ? reqVO.getRejectReason() : reqVO.getAuditRemark());
        if ("FIRST_APPROVED".equals(reqVO.getAuditStatus())) {
            updateObj.setFirstAuditStatus("APPROVED");
            updateObj.setFirstAuditBy(loginUserId);
            updateObj.setFirstAuditTime(now);
            updateObj.setStatus("FIRST_APPROVED");
            updateObj.setProgressStatus("PENDING_FINAL_AUDIT");
            updateObj.setCurrentStageName("待平台终审");
            updateObj.setCurrentStageTime(now);
            updateObj.setRejectReason(null);
            updateObj.setOnboardingBlockedReason(null);
        } else if ("APPROVED".equals(reqVO.getAuditStatus())) {
            if (!"APPROVED".equals(entry.getFirstAuditStatus())) {
                updateObj.setFirstAuditStatus("APPROVED");
                updateObj.setFirstAuditBy(loginUserId);
                updateObj.setFirstAuditTime(now);
            }
            updateObj.setFinalAuditStatus("APPROVED");
            updateObj.setFinalAuditBy(loginUserId);
            updateObj.setFinalAuditTime(now);
            updateObj.setStatus("APPROVED");
            if (hasEnabledBankCard(entry.getUserId())) {
                updateObj.setProgressStatus("APPROVED_ENABLED");
                updateObj.setCurrentStageName("已开通接单权限");
                updateObj.setCurrentStageTime(now);
                updateObj.setAcceptEnabled(Boolean.TRUE);
                updateObj.setBankCardRequired(Boolean.TRUE);
                updateObj.setOnboardingBlockedReason(null);
                enableMerchant(entry, true);
                messagePushDispatchService.dispatchSingle("lb_merchant_accept_enabled", "接单权限开通提醒", "MERCHANT_ENTRY",
                        entry.getId(), entry.getUserId(), "终审通过且已绑卡，自动开通接单权限");
            } else {
                updateObj.setProgressStatus("APPROVED_WAIT_BANK_CARD");
                updateObj.setCurrentStageName("终审通过，待绑定银行卡");
                updateObj.setCurrentStageTime(now);
                updateObj.setAcceptEnabled(Boolean.FALSE);
                updateObj.setBankCardRequired(Boolean.TRUE);
                updateObj.setOnboardingBlockedReason("终审已通过，但尚未绑定有效银行卡");
                enableMerchant(entry, false);
                messagePushDispatchService.dispatchSingle("lb_bind_bank_card_required", "银行卡绑定提醒", "MERCHANT_ENTRY",
                        entry.getId(), entry.getUserId(), "终审通过后提醒先绑定银行卡");
            }
        } else if ("REJECTED".equals(reqVO.getAuditStatus())) {
            if ("APPROVED".equals(entry.getFirstAuditStatus()) || "FIRST_APPROVED".equals(entry.getStatus())) {
                updateObj.setFinalAuditStatus("REJECTED");
                updateObj.setFinalAuditBy(loginUserId);
                updateObj.setFinalAuditTime(now);
            } else {
                updateObj.setFirstAuditStatus("REJECTED");
                updateObj.setFirstAuditBy(loginUserId);
                updateObj.setFirstAuditTime(now);
            }
            updateObj.setStatus("REJECTED");
            updateObj.setProgressStatus("REJECTED");
            updateObj.setCurrentStageName("入驻审核已驳回");
            updateObj.setCurrentStageTime(now);
            updateObj.setRejectReason(reqVO.getRejectReason());
            updateObj.setAcceptEnabled(Boolean.FALSE);
            updateObj.setOnboardingBlockedReason(reqVO.getRejectReason());
        }
        merchantEntryMapper.updateById(updateObj);
        messagePushDispatchService.dispatchSingle("lb_merchant_entry_audited", "入驻审核结果通知", "MERCHANT_ENTRY",
                entry.getId(), entry.getUserId(), "管理员审核服务商入驻后自动通知申请人");
        if ("APPROVED".equals(reqVO.getAuditStatus())) {
            creditRecordService.applyCreditRule(entry.getUserId(), entry.getMerchantId(), "MERCHANT_ENTRY_APPROVED",
                    "MERCHANT_ENTRY", entry.getId(), "服务商入驻审核通过");
        }
    }

    private void enableMerchant(MerchantEntryDO entry, boolean acceptEnabled) {
        if (entry.getMerchantId() != null) {
            merchantInfoMapper.updateById(MerchantInfoDO.builder()
                    .id(entry.getMerchantId())
                    .status("ENABLE")
                    .acceptStatus(acceptEnabled ? "ENABLE" : "DISABLE")
                    .build());
        }
        memberUserService.updateMemberUserRole(entry.getUserId(), "MERCHANT");
    }

    private boolean hasEnabledBankCard(Long userId) {
        return walletBankCardMapper.selectOne(new LambdaQueryWrapperX<cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO>()
                .eq(cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO::getUserId, userId)
                .eq(cn.iocoder.yudao.module.linbang.dal.dataobject.walletbankcard.WalletBankCardDO::getStatus, "ENABLE")
                .last("LIMIT 1")) != null;
    }

    @Override
    public PageResult<MerchantEntryRespVO> getMerchantEntryPage(MerchantEntryPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<MerchantEntryDO> pageResult = merchantEntryMapper.selectPage(pageReqVO, matchedUserIds);
        List<MerchantEntryRespVO> list = BeanUtils.toBean(pageResult.getList(), MerchantEntryRespVO.class);
        fillUserDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserDisplayInfo(List<MerchantEntryRespVO> list) {
        Set<Long> userIds = convertSet(list, MerchantEntryRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        Set<Long> merchantIds = convertSet(list, MerchantEntryRespVO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
                if (merchant != null) {
                    item.setMerchantName(merchant.getMerchantName());
                    item.setMerchantContactName(merchant.getContactName());
                    item.setMerchantContactMobile(merchant.getContactMobile());
                }
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                item.setMerchantName(merchant.getMerchantName());
                item.setMerchantContactName(merchant.getContactName());
                item.setMerchantContactMobile(merchant.getContactMobile());
            }
        });
    }

    private Map<Long, MerchantServiceCategoryDO> buildCategoryMap(List<MerchantCategoryRelDO> categoryRels) {
        if (CollUtil.isEmpty(categoryRels)) {
            return Collections.emptyMap();
        }
        Set<Long> categoryIds = new HashSet<>();
        for (MerchantCategoryRelDO categoryRel : categoryRels) {
            if (categoryRel.getCategoryId() != null) {
                categoryIds.add(categoryRel.getCategoryId());
            }
        }
        if (CollUtil.isEmpty(categoryIds)) {
            return Collections.emptyMap();
        }
        Map<Long, MerchantServiceCategoryDO> categoryMap = new HashMap<>();
        for (MerchantServiceCategoryDO category : merchantServiceCategoryMapper.selectBatchIds(categoryIds)) {
            categoryMap.put(category.getId(), category);
        }
        return categoryMap;
    }

}
