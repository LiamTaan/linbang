package cn.iocoder.yudao.module.linbang.service.riskrule;

import cn.iocoder.yudao.module.linbang.controller.admin.riskrule.vo.RiskRuleDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.blacklist.BlacklistDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

final class RiskRuleDetailAssembler {

    private RiskRuleDetailAssembler() {
    }

    static List<RiskRuleDetailRespVO.RiskEventSimpleRespVO> buildRiskEvents(List<RiskEventDO> riskEvents,
                                                                            Map<Long, MemberUserDO> userMap) {
        if (riskEvents == null || riskEvents.isEmpty()) {
            return Collections.emptyList();
        }
        return riskEvents.stream().map(riskEvent -> {
            RiskRuleDetailRespVO.RiskEventSimpleRespVO respVO = new RiskRuleDetailRespVO.RiskEventSimpleRespVO();
            respVO.setId(riskEvent.getId());
            respVO.setBizType(riskEvent.getBizType());
            respVO.setBizId(riskEvent.getBizId());
            if ("USER".equalsIgnoreCase(riskEvent.getBizType()) && riskEvent.getBizId() != null) {
                respVO.setUser(buildUser(userMap == null ? null : userMap.get(riskEvent.getBizId())));
            }
            respVO.setRiskType(riskEvent.getRiskType());
            respVO.setRiskLevel(riskEvent.getRiskLevel());
            respVO.setHitRuleCode(riskEvent.getHitRuleCode());
            respVO.setStatus(riskEvent.getStatus());
            respVO.setHandleBy(riskEvent.getHandleBy());
            respVO.setHandleTime(riskEvent.getHandleTime());
            respVO.setRemark(riskEvent.getRemark());
            respVO.setCreateTime(riskEvent.getCreateTime());
            return respVO;
        }).collect(Collectors.toList());
    }

    static List<RiskRuleDetailRespVO.BlacklistSimpleRespVO> buildBlacklists(List<BlacklistDO> blacklists,
                                                                             Map<Long, MemberUserDO> userMap) {
        if (blacklists == null || blacklists.isEmpty()) {
            return Collections.emptyList();
        }
        return blacklists.stream().map(blacklist -> {
            RiskRuleDetailRespVO.BlacklistSimpleRespVO respVO = new RiskRuleDetailRespVO.BlacklistSimpleRespVO();
            respVO.setId(blacklist.getId());
            respVO.setUserId(blacklist.getUserId());
            respVO.setBlackType(blacklist.getBlackType());
            respVO.setReason(blacklist.getReason());
            respVO.setStatus(blacklist.getStatus());
            respVO.setStartTime(blacklist.getStartTime());
            respVO.setEndTime(blacklist.getEndTime());
            respVO.setCreateTime(blacklist.getCreateTime());
            respVO.setUser(buildUser(userMap == null ? null : userMap.get(blacklist.getUserId())));
            return respVO;
        }).collect(Collectors.toList());
    }

    private static RiskRuleDetailRespVO.MemberUserSimpleRespVO buildUser(MemberUserDO user) {
        if (user == null) {
            return null;
        }
        RiskRuleDetailRespVO.MemberUserSimpleRespVO respVO = new RiskRuleDetailRespVO.MemberUserSimpleRespVO();
        respVO.setId(user.getId());
        respVO.setUserNo(user.getUserNo());
        respVO.setMobile(user.getMobile());
        respVO.setNickname(user.getNickname());
        respVO.setCurrentRoleCode(user.getCurrentRoleCode());
        respVO.setStatus(user.getStatus());
        return respVO;
    }
}
