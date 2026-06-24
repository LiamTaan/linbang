package cn.iocoder.yudao.module.linbang.service.blacklist;

import cn.iocoder.yudao.module.linbang.controller.admin.blacklist.vo.BlacklistDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.riskevent.RiskEventDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class BlacklistDetailAssembler {

    private BlacklistDetailAssembler() {
    }

    static BlacklistDetailRespVO.MemberUserSimpleRespVO buildUser(MemberUserDO user) {
        if (user == null) {
            return null;
        }
        BlacklistDetailRespVO.MemberUserSimpleRespVO respVO = new BlacklistDetailRespVO.MemberUserSimpleRespVO();
        respVO.setId(user.getId());
        respVO.setUserNo(user.getUserNo());
        respVO.setMobile(user.getMobile());
        respVO.setNickname(user.getNickname());
        respVO.setCurrentRoleCode(user.getCurrentRoleCode());
        respVO.setStatus(user.getStatus());
        respVO.setLastLoginTime(user.getLastLoginTime());
        respVO.setLastLoginIp(user.getLastLoginIp());
        return respVO;
    }

    static List<BlacklistDetailRespVO.RiskEventSimpleRespVO> buildRiskEvents(List<RiskEventDO> riskEvents) {
        if (riskEvents == null || riskEvents.isEmpty()) {
            return Collections.emptyList();
        }
        return riskEvents.stream().map(riskEvent -> {
            BlacklistDetailRespVO.RiskEventSimpleRespVO respVO = new BlacklistDetailRespVO.RiskEventSimpleRespVO();
            respVO.setId(riskEvent.getId());
            respVO.setBizType(riskEvent.getBizType());
            respVO.setBizId(riskEvent.getBizId());
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

}
