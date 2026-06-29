package cn.iocoder.yudao.module.linbang.service.creditrecord;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrecord.vo.CreditRecordRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderabnormal.OrderAbnormalDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.walletwithdraw.WalletWithdrawDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrule.CreditRuleMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderabnormal.OrderAbnormalMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.walletwithdraw.WalletWithdrawMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CREDIT_RECORD_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.REVIEW_ACCESS_DENIED;

@Service
@Validated
public class CreditRecordServiceImpl implements CreditRecordService {

    @Resource
    private CreditRecordMapper creditRecordMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private CreditRuleMapper creditRuleMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderAbnormalMapper orderAbnormalMapper;
    @Resource
    private ComplaintMapper complaintMapper;
    @Resource
    private AppealMapper appealMapper;
    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private WalletWithdrawMapper walletWithdrawMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long applyCreditRule(Long userId, Long merchantId, String ruleCode, String bizType, Long bizId, String remark) {
        if (userId == null || ruleCode == null || ruleCode.isEmpty()) {
            return null;
        }
        CreditRuleDO rule = creditRuleMapper.selectOne(new LambdaQueryWrapperX<CreditRuleDO>()
                .eq(CreditRuleDO::getRuleCode, ruleCode)
                .eq(CreditRuleDO::getStatus, "ENABLE")
                .last("LIMIT 1"));
        if (rule == null || rule.getScoreChange() == null || rule.getScoreChange() == 0) {
            return null;
        }

        MerchantInfoDO merchant = resolveMerchant(userId, merchantId);
        Integer beforeScore = merchant != null && merchant.getCreditScore() != null ? merchant.getCreditScore() : 100;
        Integer afterScore = Math.max(0, beforeScore + rule.getScoreChange());
        String creditLevel = CreditLevelResolver.resolve(afterScore);

        if (merchant != null) {
            merchantInfoMapper.updateById(MerchantInfoDO.builder()
                    .id(merchant.getId())
                    .creditScore(afterScore)
                    .creditLevel(creditLevel)
                    .build());
            merchantId = merchant.getId();
        }

        CreditRecordDO record = CreditRecordDO.builder()
                .userId(userId)
                .merchantId(merchantId)
                .ruleId(rule.getId())
                .ruleCode(rule.getRuleCode())
                .ruleName(rule.getRuleName())
                .scoreChange(rule.getScoreChange())
                .beforeScore(beforeScore)
                .afterScore(afterScore)
                .triggerType(rule.getTriggerType())
                .bizType(bizType)
                .bizId(bizId)
                .remark(remark)
                .build();
        creditRecordMapper.insert(record);
        return record.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollbackBizCreditRecords(Long userId, String bizType, Long bizId) {
        if (userId == null || StrUtil.isBlank(bizType) || bizId == null) {
            return;
        }
        creditRecordMapper.delete(new LambdaQueryWrapperX<CreditRecordDO>()
                .eq(CreditRecordDO::getUserId, userId)
                .eq(CreditRecordDO::getBizType, bizType)
                .eq(CreditRecordDO::getBizId, bizId));
        rebuildUserCreditRecords(userId);
    }

    @Override
    public PageResult<CreditRecordRespVO> getCreditRecordPage(CreditRecordPageReqVO pageReqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(pageReqVO.getUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<CreditRecordDO> pageResult = creditRecordMapper.selectPage(pageReqVO, matchedUserIds);
        List<CreditRecordRespVO> list = BeanUtils.toBean(pageResult.getList(), CreditRecordRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public PageResult<AppCreditRecordRespVO> getAppCreditRecordPage(Long userId, AppCreditRecordPageReqVO pageReqVO) {
        PageResult<CreditRecordDO> pageResult = creditRecordMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<CreditRecordDO>()
                        .eq(CreditRecordDO::getUserId, userId)
                        .eqIfPresent(CreditRecordDO::getRuleCode, pageReqVO.getRuleCode())
                        .eqIfPresent(CreditRecordDO::getTriggerType, pageReqVO.getTriggerType())
                        .eqIfPresent(CreditRecordDO::getBizType, pageReqVO.getBizType())
                        .betweenIfPresent(CreditRecordDO::getCreateTime, pageReqVO.getCreateTime())
                        .orderByDesc(CreditRecordDO::getId));
        return new PageResult<>(BeanUtils.toBean(pageResult.getList(), AppCreditRecordRespVO.class), pageResult.getTotal());
    }

    @Override
    public CreditRecordDetailRespVO getCreditRecordDetail(Long id) {
        CreditRecordDO record = creditRecordMapper.selectById(id);
        if (record == null) {
            throw exception(CREDIT_RECORD_NOT_EXISTS);
        }

        MemberUserDO user = record.getUserId() == null ? null : memberUserMapper.selectById(record.getUserId());
        MerchantInfoDO merchant = record.getMerchantId() == null ? null : merchantInfoMapper.selectById(record.getMerchantId());
        CreditRuleDO rule = record.getRuleId() == null ? null : creditRuleMapper.selectById(record.getRuleId());

        Long sameUserRecordCount = record.getUserId() == null ? 0L : creditRecordMapper.selectCount(
                new LambdaQueryWrapperX<CreditRecordDO>().eq(CreditRecordDO::getUserId, record.getUserId()));
        Long sameRuleRecordCount = record.getRuleId() == null ? 0L : creditRecordMapper.selectCount(
                new LambdaQueryWrapperX<CreditRecordDO>().eq(CreditRecordDO::getRuleId, record.getRuleId()));

        List<CreditRecordDO> userRecords = record.getUserId() == null ? Collections.emptyList() : creditRecordMapper.selectList(
                new LambdaQueryWrapperX<CreditRecordDO>()
                        .eq(CreditRecordDO::getUserId, record.getUserId())
                        .orderByDesc(CreditRecordDO::getId));
        int positiveRecordCount = 0;
        int negativeRecordCount = 0;
        for (CreditRecordDO userRecord : userRecords) {
            if (userRecord.getScoreChange() == null) {
                continue;
            }
            if (userRecord.getScoreChange() >= 0) {
                positiveRecordCount++;
            } else {
                negativeRecordCount++;
            }
        }

        CreditRecordDetailRespVO respVO = BeanUtils.toBean(record, CreditRecordDetailRespVO.class);
        respVO.setSameUserRecordCount(sameUserRecordCount.intValue());
        respVO.setSameRuleRecordCount(sameRuleRecordCount.intValue());
        respVO.setPositiveRecordCount(positiveRecordCount);
        respVO.setNegativeRecordCount(negativeRecordCount);
        if (merchant != null) {
            respVO.setCurrentScore(merchant.getCreditScore());
            respVO.setCreditLevel(merchant.getCreditLevel());
            respVO.setMerchant(BeanUtils.toBean(merchant, CreditRecordDetailRespVO.MerchantRespVO.class));
        } else {
            respVO.setCurrentScore(record.getAfterScore());
        }
        if (user != null) {
            respVO.setUser(BeanUtils.toBean(user, CreditRecordDetailRespVO.UserRespVO.class));
        }
        if (rule != null) {
            respVO.setRule(BeanUtils.toBean(rule, CreditRecordDetailRespVO.RuleRespVO.class));
        }
        respVO.setBizDisplay(buildBizDisplay(record.getBizType(), record.getBizId()));
        return respVO;
    }

    @Override
    public AppCreditRecordDetailRespVO getAppCreditRecordDetail(Long userId, Long id) {
        CreditRecordDO record = creditRecordMapper.selectById(id);
        if (record == null) {
            throw exception(CREDIT_RECORD_NOT_EXISTS);
        }
        if (!Objects.equals(record.getUserId(), userId)) {
            throw exception(REVIEW_ACCESS_DENIED);
        }
        MerchantInfoDO merchant = record.getMerchantId() == null ? null : merchantInfoMapper.selectById(record.getMerchantId());
        AppCreditRecordDetailRespVO respVO = BeanUtils.toBean(record, AppCreditRecordDetailRespVO.class);
        respVO.setCurrentScore(merchant != null ? merchant.getCreditScore() : record.getAfterScore());
        respVO.setCreditLevel(merchant != null ? merchant.getCreditLevel() : CreditLevelResolver.resolve(record.getAfterScore()));
        return respVO;
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillDisplayInfo(List<CreditRecordRespVO> list) {
        Set<Long> merchantIds = convertSet(list, CreditRecordRespVO::getMerchantId,
                item -> item.getMerchantId() != null);
        Map<Long, MerchantInfoDO> merchantMap = merchantIds.isEmpty() ? Collections.emptyMap()
                : convertMap(merchantInfoMapper.selectBatchIds(merchantIds), MerchantInfoDO::getId);
        Set<Long> userIds = convertSet(list, CreditRecordRespVO::getUserId,
                item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MerchantInfoDO merchant = merchantMap.get(item.getMerchantId());
            if (merchant != null) {
                item.setMerchantName(merchant.getMerchantName());
                item.setMerchantContactName(merchant.getContactName());
                item.setMerchantContactMobile(merchant.getContactMobile());
            }
            MemberUserDO user = userMap.get(item.getUserId());
            if (user != null) {
                item.setUserNo(user.getUserNo());
                item.setUserNickname(user.getNickname());
                item.setUserMobile(user.getMobile());
            }
            item.setBizDisplay(buildBizDisplay(item.getBizType(), item.getBizId()));
        });
    }

    private String buildBizDisplay(String bizType, Long bizId) {
        if (bizId == null) {
            return "-";
        }
        String normalizedBizType = StrUtil.trimToEmpty(bizType).toUpperCase();
        switch (normalizedBizType) {
            case "ORDER":
            case "ORDER_INFO":
                OrderInfoDO order = orderInfoMapper.selectById(bizId);
                return order != null ? StrUtil.blankToDefault(order.getOrderNo(), "订单ID：" + bizId) : "订单ID：" + bizId;
            case "ORDER_UNIT":
            case "UNIT":
                OrderUnitDO unit = orderUnitMapper.selectById(bizId);
                return unit != null ? StrUtil.blankToDefault(unit.getUnitNo(), "单元ID：" + bizId) : "单元ID：" + bizId;
            case "ORDER_ABNORMAL":
            case "ABNORMAL":
                OrderAbnormalDO abnormal = orderAbnormalMapper.selectById(bizId);
                return abnormal != null ? StrUtil.blankToDefault(abnormal.getAbnormalNo(), "异常单ID：" + bizId) : "异常单ID：" + bizId;
            case "COMPLAINT":
                ComplaintDO complaint = complaintMapper.selectById(bizId);
                return complaint != null ? StrUtil.blankToDefault(complaint.getComplaintNo(), "投诉ID：" + bizId) : "投诉ID：" + bizId;
            case "APPEAL":
                AppealDO appeal = appealMapper.selectById(bizId);
                return appeal != null ? StrUtil.blankToDefault(appeal.getAppealNo(), "申诉ID：" + bizId) : "申诉ID：" + bizId;
            case "REVIEW":
                ReviewCommentDO review = reviewCommentMapper.selectById(bizId);
                return review != null ? "评价ID：" + review.getId() + " / " + StrUtil.blankToDefault(review.getContent(), "无评价内容") : "评价ID：" + bizId;
            case "WITHDRAW":
            case "WITHDRAW_APPLY":
                WalletWithdrawDO withdraw = walletWithdrawMapper.selectById(bizId);
                return withdraw != null ? StrUtil.blankToDefault(withdraw.getWithdrawNo(), "提现ID：" + bizId) : "提现ID：" + bizId;
            case "USER":
                MemberUserDO user = memberUserMapper.selectById(bizId);
                if (user == null) {
                    return "用户ID：" + bizId;
                }
                String summary = String.join(" / ", buildNonBlankParts(user.getNickname(), user.getMobile(), user.getUserNo()));
                return StrUtil.blankToDefault(summary, "用户ID：" + bizId);
            default:
                return NumberUtil.isLong(String.valueOf(bizId)) ? "业务ID：" + bizId : String.valueOf(bizId);
        }
    }

    private List<String> buildNonBlankParts(String... values) {
        List<String> parts = new java.util.ArrayList<>();
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                parts.add(value);
            }
        }
        return parts;
    }

    private MerchantInfoDO resolveMerchant(Long userId, Long merchantId) {
        if (merchantId != null) {
            return merchantInfoMapper.selectById(merchantId);
        }
        return merchantInfoMapper.selectOne(new LambdaQueryWrapperX<MerchantInfoDO>()
                .eq(MerchantInfoDO::getUserId, userId)
                .last("LIMIT 1"));
    }

    private void rebuildUserCreditRecords(Long userId) {
        List<CreditRecordDO> records = creditRecordMapper.selectList(new LambdaQueryWrapperX<CreditRecordDO>()
                .eq(CreditRecordDO::getUserId, userId)
                .orderByAsc(CreditRecordDO::getCreateTime, CreditRecordDO::getId));
        if (records.isEmpty()) {
            MerchantInfoDO merchant = resolveMerchant(userId, null);
            if (merchant != null) {
                merchantInfoMapper.updateById(MerchantInfoDO.builder()
                        .id(merchant.getId())
                        .creditScore(100)
                        .creditLevel(CreditLevelResolver.resolve(100))
                        .build());
            }
            return;
        }
        int score = 100;
        List<CreditRecordDO> updates = new ArrayList<>(records.size());
        for (CreditRecordDO record : records) {
            Integer scoreChange = record.getScoreChange() == null ? 0 : record.getScoreChange();
            int beforeScore = score;
            score = Math.max(0, score + scoreChange);
            updates.add(CreditRecordDO.builder()
                    .id(record.getId())
                    .beforeScore(beforeScore)
                    .afterScore(score)
                    .build());
        }
        creditRecordMapper.updateBatch(updates);
        MerchantInfoDO merchant = resolveMerchant(userId, records.get(records.size() - 1).getMerchantId());
        if (merchant != null) {
            merchantInfoMapper.updateById(MerchantInfoDO.builder()
                    .id(merchant.getId())
                    .creditScore(score)
                    .creditLevel(CreditLevelResolver.resolve(score))
                    .build());
        }
    }
}
