package cn.iocoder.yudao.module.linbang.service.creditrule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.CreditRuleDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.CreditRulePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.CreditRuleSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrule.CreditRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CREDIT_RULE_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CREDIT_RULE_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CREDIT_RULE_CODE_DUPLICATE;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CREDIT_RULE_IN_USE;

/**
 * 信用分规则 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class CreditRuleServiceImpl implements CreditRuleService {

    private static final Set<String> TRIGGER_TYPES = new HashSet<>(Arrays.asList("AUTO", "MANUAL"));
    private static final Set<String> RULE_STATUSES = new HashSet<>(Arrays.asList("ENABLE", "DISABLE"));

    @Resource
    private CreditRuleMapper creditRuleMapper;
    @Resource
    private CreditRecordMapper creditRecordMapper;

    @Override
    public Long createCreditRule(CreditRuleSaveReqVO createReqVO) {
        validateCreditRule(createReqVO);
        CreditRuleDO creditRule = BeanUtils.toBean(createReqVO, CreditRuleDO.class);
        normalizeRule(creditRule);
        creditRuleMapper.insert(creditRule);

        // 返回
        return creditRule.getId();
    }

    @Override
    public void updateCreditRule(CreditRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateCreditRuleExists(updateReqVO.getId());
        validateCreditRule(updateReqVO);
        CreditRuleDO updateObj = BeanUtils.toBean(updateReqVO, CreditRuleDO.class);
        normalizeRule(updateObj);
        creditRuleMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCreditRule(Long id) {
        validateCreditRuleExists(id);
        if (creditRecordMapper.selectCount(CreditRecordDO::getRuleId, id) > 0) {
            throw exception(CREDIT_RULE_IN_USE);
        }
        creditRuleMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCreditRuleListByIds(List<Long> ids) {
        for (Long id : ids) {
            deleteCreditRule(id);
        }
    }


    private void validateCreditRuleExists(Long id) {
        if (creditRuleMapper.selectById(id) == null) {
            throw exception(CREDIT_RULE_NOT_EXISTS);
        }
    }

    private void validateCreditRule(CreditRuleSaveReqVO reqVO) {
        String ruleCode = normalize(reqVO.getRuleCode());
        LambdaQueryWrapperX<CreditRuleDO> duplicateQuery = new LambdaQueryWrapperX<CreditRuleDO>()
                .eq(CreditRuleDO::getRuleCode, ruleCode);
        if (reqVO.getId() != null) {
            duplicateQuery.ne(CreditRuleDO::getId, reqVO.getId());
        }
        if (creditRuleMapper.selectCount(duplicateQuery) > 0) {
            throw exception(CREDIT_RULE_CODE_DUPLICATE);
        }
        if (reqVO.getScoreChange() == null || reqVO.getScoreChange() < -1000 || reqVO.getScoreChange() > 1000) {
            throw exception(CREDIT_RULE_INVALID, "分值变动必须在 -1000 到 1000 之间");
        }
        if (!TRIGGER_TYPES.contains(normalize(reqVO.getTriggerType()))) {
            throw exception(CREDIT_RULE_INVALID, "触发类型仅支持 AUTO 或 MANUAL");
        }
        if (!RULE_STATUSES.contains(normalize(reqVO.getStatus()))) {
            throw exception(CREDIT_RULE_INVALID, "状态仅支持 ENABLE 或 DISABLE");
        }
    }

    private void normalizeRule(CreditRuleDO rule) {
        rule.setRuleCode(normalize(rule.getRuleCode()));
        rule.setTriggerType(normalize(rule.getTriggerType()));
        rule.setStatus(normalize(rule.getStatus()));
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    @Override
    public CreditRuleDO getCreditRule(Long id) {
        return creditRuleMapper.selectById(id);
    }

    @Override
    public CreditRuleDetailRespVO getCreditRuleDetail(Long id) {
        CreditRuleDO creditRule = creditRuleMapper.selectById(id);
        if (creditRule == null) {
            throw exception(CREDIT_RULE_NOT_EXISTS);
        }

        List<CreditRuleDO> sameTriggerRules = creditRuleMapper.selectList(new LambdaQueryWrapperX<CreditRuleDO>()
                .eq(CreditRuleDO::getTriggerType, creditRule.getTriggerType())
                .ne(CreditRuleDO::getId, creditRule.getId())
                .orderByDesc(CreditRuleDO::getStatus)
                .orderByDesc(CreditRuleDO::getId)
                .last("LIMIT 10"));
        long sameTriggerRuleCount = creditRuleMapper.selectCount(new LambdaQueryWrapperX<CreditRuleDO>()
                .eq(CreditRuleDO::getTriggerType, creditRule.getTriggerType())
                .ne(CreditRuleDO::getId, creditRule.getId()));
        long positiveRuleCount = creditRuleMapper.selectCount(new LambdaQueryWrapperX<CreditRuleDO>()
                .ge(CreditRuleDO::getScoreChange, 0));
        long negativeRuleCount = creditRuleMapper.selectCount(new LambdaQueryWrapperX<CreditRuleDO>()
                .lt(CreditRuleDO::getScoreChange, 0));

        CreditRuleDetailRespVO respVO = BeanUtils.toBean(creditRule, CreditRuleDetailRespVO.class);
        respVO.setSameTriggerRuleCount((int) Math.min(sameTriggerRuleCount, Integer.MAX_VALUE));
        respVO.setPositiveRuleCount((int) Math.min(positiveRuleCount, Integer.MAX_VALUE));
        respVO.setNegativeRuleCount((int) Math.min(negativeRuleCount, Integer.MAX_VALUE));
        respVO.setPositiveRule(creditRule.getScoreChange() != null && creditRule.getScoreChange() >= 0);
        respVO.setRelatedRules(sameTriggerRules.isEmpty()
                ? Collections.emptyList()
                : CreditRuleDetailAssembler.buildRelatedRules(sameTriggerRules));
        return respVO;
    }

    @Override
    public PageResult<CreditRuleDO> getCreditRulePage(CreditRulePageReqVO pageReqVO) {
        return creditRuleMapper.selectPage(pageReqVO);
    }

}
