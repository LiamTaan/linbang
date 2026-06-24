package cn.iocoder.yudao.module.linbang.service.app.member;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.object.BeanUtils.toBean;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_NOT_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_PENDING_EXISTS;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.MEMBER_ROLE_APPLY_ROLE_CODE_INVALID;

@Service
@Validated
public class AppMemberRoleApplyServiceImpl implements AppMemberRoleApplyService {

    private static final Set<String> SUPPORTED_ROLE_CODES = new HashSet<>(Arrays.asList("PROMOTER", "PARTNER"));

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
                .auditStatus("PENDING")
                .build();
        memberRoleApplyMapper.insert(apply);
        return apply.getId();
    }

    @Override
    public PageResult<AppMemberRoleApplyRespVO> getRoleApplyPage(Long authUserId, AppMemberRoleApplyPageReqVO reqVO) {
        MemberUserDO loginUser = memberUserService.getOrCreateMemberUser(authUserId);
        PageResult<MemberRoleApplyDO> pageResult = memberRoleApplyMapper.selectAppPage(loginUser.getId(), reqVO);
        return new PageResult<>(toBean(pageResult.getList(), AppMemberRoleApplyRespVO.class), pageResult.getTotal());
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
        return toBean(apply, AppMemberRoleApplyRespVO.class);
    }

    private void validateApplyRoleCode(String applyRoleCode) {
        if (!SUPPORTED_ROLE_CODES.contains(applyRoleCode)) {
            throw exception(MEMBER_ROLE_APPLY_ROLE_CODE_INVALID);
        }
    }
}
