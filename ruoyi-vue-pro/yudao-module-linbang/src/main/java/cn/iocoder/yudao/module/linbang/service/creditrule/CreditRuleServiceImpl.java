package cn.iocoder.yudao.module.linbang.service.creditrule;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.CreditRuleDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.CreditRulePageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.CreditRuleSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrule.CreditRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.CREDIT_RULE_NOT_EXISTS;

/**
 * 信用分规则 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class CreditRuleServiceImpl implements CreditRuleService {

    @Resource
    private CreditRuleMapper creditRuleMapper;

    @Override
    public Long createCreditRule(CreditRuleSaveReqVO createReqVO) {
        // 插入
        CreditRuleDO creditRule = BeanUtils.toBean(createReqVO, CreditRuleDO.class);
        creditRuleMapper.insert(creditRule);

        // 返回
        return creditRule.getId();
    }

    @Override
    public void updateCreditRule(CreditRuleSaveReqVO updateReqVO) {
        // 校验存在
        validateCreditRuleExists(updateReqVO.getId());
        // 更新
        CreditRuleDO updateObj = BeanUtils.toBean(updateReqVO, CreditRuleDO.class);
        creditRuleMapper.updateById(updateObj);
    }

    @Override
    public void deleteCreditRule(Long id) {
        // 校验存在
        validateCreditRuleExists(id);
        // 删除
        creditRuleMapper.deleteById(id);
    }

    @Override
        public void deleteCreditRuleListByIds(List<Long> ids) {
        // 删除
        creditRuleMapper.deleteByIds(ids);
        }


    private void validateCreditRuleExists(Long id) {
        if (creditRuleMapper.selectById(id) == null) {
            throw exception(CREDIT_RULE_NOT_EXISTS);
        }
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
                .orderByDesc(CreditRuleDO::getId));
        List<CreditRuleDO> relatedRules = sameTriggerRules.size() > 10 ? sameTriggerRules.subList(0, 10) : sameTriggerRules;
        List<CreditRuleDO> allRules = creditRuleMapper.selectList(new LambdaQueryWrapperX<CreditRuleDO>()
                .orderByDesc(CreditRuleDO::getId));

        int positiveRuleCount = 0;
        int negativeRuleCount = 0;
        for (CreditRuleDO rule : allRules) {
            if (rule.getScoreChange() == null) {
                continue;
            }
            if (rule.getScoreChange() >= 0) {
                positiveRuleCount++;
            } else {
                negativeRuleCount++;
            }
        }

        CreditRuleDetailRespVO respVO = BeanUtils.toBean(creditRule, CreditRuleDetailRespVO.class);
        respVO.setSameTriggerRuleCount(sameTriggerRules.size());
        respVO.setPositiveRuleCount(positiveRuleCount);
        respVO.setNegativeRuleCount(negativeRuleCount);
        respVO.setPositiveRule(creditRule.getScoreChange() != null && creditRule.getScoreChange() >= 0);
        respVO.setRelatedRules(sameTriggerRules.isEmpty()
                ? Collections.emptyList()
                : CreditRuleDetailAssembler.buildRelatedRules(relatedRules));
        return respVO;
    }

    @Override
    public PageResult<CreditRuleDO> getCreditRulePage(CreditRulePageReqVO pageReqVO) {
        return creditRuleMapper.selectPage(pageReqVO);
    }

}
