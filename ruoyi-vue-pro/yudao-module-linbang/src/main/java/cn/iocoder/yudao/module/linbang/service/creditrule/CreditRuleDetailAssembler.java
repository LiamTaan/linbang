package cn.iocoder.yudao.module.linbang.service.creditrule;

import cn.iocoder.yudao.module.linbang.controller.admin.creditrule.vo.CreditRuleDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrule.CreditRuleDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class CreditRuleDetailAssembler {

    private CreditRuleDetailAssembler() {
    }

    static List<CreditRuleDetailRespVO.RelatedRuleRespVO> buildRelatedRules(List<CreditRuleDO> relatedRules) {
        if (relatedRules == null || relatedRules.isEmpty()) {
            return Collections.emptyList();
        }
        return relatedRules.stream().map(rule -> {
            CreditRuleDetailRespVO.RelatedRuleRespVO respVO = new CreditRuleDetailRespVO.RelatedRuleRespVO();
            respVO.setId(rule.getId());
            respVO.setRuleCode(rule.getRuleCode());
            respVO.setRuleName(rule.getRuleName());
            respVO.setScoreChange(rule.getScoreChange());
            respVO.setTriggerType(rule.getTriggerType());
            respVO.setStatus(rule.getStatus());
            respVO.setCreateTime(rule.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }
}
