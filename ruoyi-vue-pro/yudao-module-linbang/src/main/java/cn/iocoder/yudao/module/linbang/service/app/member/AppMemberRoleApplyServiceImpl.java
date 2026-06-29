package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply.MemberRoleApplyMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_PENDING_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_ROLE_CODE_INVALID;

@Service
@Validated
public class AppMemberRoleApplyServiceImpl implements AppMemberRoleApplyService {

    private static final Set<String> SUPPORTED_ROLE_CODES = new HashSet<>(Arrays.asList(
            "PROMOTER", "PARTNER", "PLATFORM_OPERATOR"));

    @Resource
    private MemberUserService memberUserService;
    @Resource
    private MemberRoleApplyMapper memberRoleApplyMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRoleApply(Long authUserId, AppMemberRoleApplyCreateReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        validateApplyRoleCode(reqVO.getApplyRoleCode());
        MemberRoleApplyDO pendingApply = memberRoleApplyMapper.selectOne(new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getUserId, loginUser.getId())
                .eq(MemberRoleApplyDO::getApplyRoleCode, reqVO.getApplyRoleCode())
                .eq(MemberRoleApplyDO::getAuditStatus, "PENDING")
                .last("LIMIT 1"));
        if (pendingApply != null) {
            throw exception(MEMBER_ROLE_APPLY_PENDING_EXISTS);
        }
        MemberRoleApplyDO apply = MemberRoleApplyDO.builder()
                .userId(loginUser.getId())
                .applyRoleCode(reqVO.getApplyRoleCode())
                .applyReason(reqVO.getApplyReason())
                .resourceDesc(reqVO.getResourceDesc())
                .expectedConversionDesc(reqVO.getExpectedConversionDesc())
                .abilityDesc(reqVO.getAbilityDesc())
                .availableTimeDesc(reqVO.getAvailableTimeDesc())
                .auditStatus("PENDING")
                .build();
        memberRoleApplyMapper.insert(apply);
        return apply.getId();
    }

    @Override
    public PageResult<AppMemberRoleApplyRespVO> getRoleApplyPage(Long authUserId, AppMemberRoleApplyPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<MemberRoleApplyDO> pageResult = memberRoleApplyMapper.selectAppPage(loginUser.getId(), reqVO);
        return new PageResult<>(pageResult.getList().stream().map(this::buildRespVO).collect(java.util.stream.Collectors.toList()),
                pageResult.getTotal());
    }

    @Override
    public AppMemberRoleApplyRespVO getRoleApply(Long authUserId, Long id) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        MemberRoleApplyDO apply = memberRoleApplyMapper.selectOne(new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getId, id)
                .eq(MemberRoleApplyDO::getUserId, loginUser.getId())
                .last("LIMIT 1"));
        if (apply == null) {
            throw exception(MEMBER_ROLE_APPLY_NOT_EXISTS);
        }
        return buildRespVO(apply);
    }

    private void validateApplyRoleCode(String applyRoleCode) {
        if (!SUPPORTED_ROLE_CODES.contains(applyRoleCode)) {
            throw exception(MEMBER_ROLE_APPLY_ROLE_CODE_INVALID);
        }
    }

    private AppMemberRoleApplyRespVO buildRespVO(MemberRoleApplyDO apply) {
        AppMemberRoleApplyRespVO respVO = BeanUtils.toBean(apply, AppMemberRoleApplyRespVO.class);
        respVO.setApplyNo("RA" + apply.getId());
        respVO.setCurrentNodeName(resolveCurrentNodeName(apply));
        respVO.setProcessNodes(buildProcessNodes(apply));
        return respVO;
    }

    private String resolveCurrentNodeName(MemberRoleApplyDO apply) {
        if ("APPROVED".equalsIgnoreCase(apply.getAuditStatus())) {
            return "审核通过";
        }
        if ("REJECTED".equalsIgnoreCase(apply.getAuditStatus())) {
            return "审核驳回";
        }
        return "平台终审";
    }

    private List<AppMemberRoleApplyRespVO.ProcessNode> buildProcessNodes(MemberRoleApplyDO apply) {
        java.util.List<AppMemberRoleApplyRespVO.ProcessNode> nodes = new java.util.ArrayList<>();
        nodes.add(buildNode("SUBMIT", "提交申请", "DONE", apply.getCreateTime(), apply.getApplyReason()));
        if ("APPROVED".equalsIgnoreCase(apply.getAuditStatus())) {
            nodes.add(buildNode("PLATFORM_AUDIT", "平台终审", "DONE", apply.getAuditTime(), apply.getAuditRemark()));
            nodes.add(buildNode("ROLE_ENABLED", "角色开通", "DONE", apply.getAuditTime(), "角色已开通，可切换使用"));
            return nodes;
        }
        if ("REJECTED".equalsIgnoreCase(apply.getAuditStatus())) {
            nodes.add(buildNode("PLATFORM_AUDIT", "平台终审", "REJECTED", apply.getAuditTime(), apply.getRejectReason()));
            nodes.add(buildNode("ROLE_ENABLED", "角色开通", "PENDING", null, "当前申请未通过"));
            return nodes;
        }
        nodes.add(buildNode("PLATFORM_AUDIT", "平台终审", "CURRENT", null, "平台审核中"));
        nodes.add(buildNode("ROLE_ENABLED", "角色开通", "PENDING", null, "审核通过后自动开通"));
        return nodes;
    }

    private AppMemberRoleApplyRespVO.ProcessNode buildNode(String nodeCode, String nodeName, String nodeStatus,
                                                           LocalDateTime nodeTime, String nodeRemark) {
        AppMemberRoleApplyRespVO.ProcessNode node = new AppMemberRoleApplyRespVO.ProcessNode();
        node.setNodeCode(nodeCode);
        node.setNodeName(nodeName);
        node.setNodeStatus(nodeStatus);
        node.setNodeTime(nodeTime);
        node.setNodeRemark(nodeRemark);
        return node;
    }
}
