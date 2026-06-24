package cn.iocoder.yudao.module.linbang.service.memberroleapply;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.promoter.PromoterDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification.MemberUserQualificationMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname.MemberUserRealNameMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply.MemberRoleApplyMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.promoter.PromoterMapper;
import cn.iocoder.yudao.module.linbang.service.messagepushtask.MessagePushDispatchService;
import cn.iocoder.yudao.module.linbang.service.promoter.PromoterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertSet;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_AUDIT_STATUS_INVALID;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_NOT_EXISTS;

@Service
@Validated
public class MemberRoleApplyServiceImpl implements MemberRoleApplyService {

    @Resource
    private MemberRoleApplyMapper memberRoleApplyMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MemberUserRealNameMapper memberUserRealNameMapper;
    @Resource
    private MemberUserQualificationMapper memberUserQualificationMapper;
    @Resource
    private PromoterMapper promoterMapper;
    @Resource
    private PromoterService promoterService;
    @Resource
    private MessagePushDispatchService messagePushDispatchService;

    @Override
    public PageResult<MemberRoleApplyRespVO> getRoleApplyPage(MemberRoleApplyPageReqVO reqVO) {
        List<Long> matchedUserIds = resolveMatchedUserIds(reqVO.getUserKeyword());
        if (StrUtil.isNotBlank(reqVO.getUserKeyword()) && CollUtil.isEmpty(matchedUserIds)) {
            return PageResult.empty();
        }
        PageResult<MemberRoleApplyDO> pageResult = memberRoleApplyMapper.selectPage(reqVO, matchedUserIds);
        List<MemberRoleApplyRespVO> list = BeanUtils.toBean(pageResult.getList(), MemberRoleApplyRespVO.class);
        fillUserInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    @Override
    public MemberRoleApplyDetailRespVO getRoleApplyDetail(Long id) {
        MemberRoleApplyDO apply = getRequiredApply(id);
        MemberUserDO user = apply.getUserId() == null ? null : memberUserMapper.selectById(apply.getUserId());
        MemberUserRealNameDO realName = apply.getUserId() == null ? null : memberUserRealNameMapper.selectByUserId(apply.getUserId());
        MemberUserQualificationDO qualification = apply.getUserId() == null ? null : memberUserQualificationMapper.selectOne(
                new LambdaQueryWrapperX<MemberUserQualificationDO>()
                        .eq(MemberUserQualificationDO::getUserId, apply.getUserId())
                        .orderByDesc(MemberUserQualificationDO::getValidEndDate, MemberUserQualificationDO::getId)
                        .last("LIMIT 1"));
        PromoterDO promoter = apply.getUserId() == null ? null : promoterMapper.selectByUserId(apply.getUserId());

        MemberRoleApplyDetailRespVO respVO = BeanUtils.toBean(apply, MemberRoleApplyDetailRespVO.class);
        if (user != null) {
            MemberRoleApplyDetailRespVO.UserSummary summary = new MemberRoleApplyDetailRespVO.UserSummary();
            summary.setId(user.getId());
            summary.setUserNo(user.getUserNo());
            summary.setNickname(user.getNickname());
            summary.setMobile(user.getMobile());
            summary.setCurrentRoleCode(user.getCurrentRoleCode());
            summary.setStatus(user.getStatus());
            respVO.setUser(summary);
        }
        if (realName != null) {
            MemberRoleApplyDetailRespVO.RealNameSummary summary = new MemberRoleApplyDetailRespVO.RealNameSummary();
            summary.setId(realName.getId());
            summary.setRealName(realName.getRealName());
            summary.setIdCardNo(realName.getIdCardNo());
            summary.setAuditStatus(realName.getAuditStatus());
            respVO.setRealName(summary);
        }
        if (qualification != null) {
            MemberRoleApplyDetailRespVO.QualificationSummary summary = new MemberRoleApplyDetailRespVO.QualificationSummary();
            summary.setId(qualification.getId());
            summary.setQualificationType(qualification.getQualificationType());
            summary.setQualificationName(qualification.getQualificationName());
            summary.setValidEndDate(qualification.getValidEndDate());
            summary.setAuditStatus(qualification.getAuditStatus());
            respVO.setLatestQualification(summary);
        }
        if (promoter != null) {
            MemberRoleApplyDetailRespVO.PromoterSummary summary = new MemberRoleApplyDetailRespVO.PromoterSummary();
            summary.setId(promoter.getId());
            summary.setInviteCode(promoter.getInviteCode());
            summary.setLevelCode(promoter.getLevelCode());
            summary.setStatus(promoter.getStatus());
            respVO.setPromoter(summary);
        }
        return respVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditRoleApply(MemberRoleApplyAuditReqVO reqVO) {
        MemberRoleApplyDO apply = getRequiredApply(reqVO.getId());
        if (!"PENDING".equals(apply.getAuditStatus())) {
            throw exception(MEMBER_ROLE_APPLY_AUDIT_STATUS_INVALID);
        }
        memberRoleApplyMapper.updateById(MemberRoleApplyDO.builder()
                .id(reqVO.getId())
                .auditStatus(reqVO.getAuditStatus())
                .auditRemark(reqVO.getAuditRemark())
                .rejectReason(reqVO.getRejectReason())
                .auditBy(SecurityFrameworkUtils.getLoginUserId())
                .auditTime(LocalDateTime.now())
                .build());
        messagePushDispatchService.dispatchSingle("lb_role_apply_audited", "身份申请审核结果通知", "ROLE_APPLY",
                apply.getId(), apply.getUserId(), "管理员审核身份申请后自动通知申请人");
        if (!"APPROVED".equals(reqVO.getAuditStatus())) {
            return;
        }
        memberUserMapper.updateById(MemberUserDO.builder()
                .id(apply.getUserId())
                .currentRoleCode(apply.getApplyRoleCode())
                .build());
        if ("PROMOTER".equals(apply.getApplyRoleCode())) {
            promoterService.getOrCreatePromoter(apply.getUserId());
        }
    }

    private MemberRoleApplyDO getRequiredApply(Long id) {
        MemberRoleApplyDO apply = memberRoleApplyMapper.selectById(id);
        if (apply == null) {
            throw exception(MEMBER_ROLE_APPLY_NOT_EXISTS);
        }
        return apply;
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private void fillUserInfo(List<MemberRoleApplyRespVO> list) {
        java.util.Set<Long> userIds = convertSet(list, MemberRoleApplyRespVO::getUserId, item -> item.getUserId() != null);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            MemberUserDO user = userMap.get(item.getUserId());
            if (user == null) {
                return;
            }
            item.setUserNo(user.getUserNo());
            item.setUserNickname(user.getNickname());
            item.setUserMobile(user.getMobile());
        });
    }
}
