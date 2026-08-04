package cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.app.member.roleapply.vo.AppMemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Arrays;

@Mapper
public interface MemberRoleApplyMapper extends BaseMapperX<MemberRoleApplyDO> {

    default MemberRoleApplyDO selectByIdForUpdate(Long id) {
        return selectOneForUpdate(MemberRoleApplyDO::getId, id);
    }

    default MemberRoleApplyDO selectActiveByUserIdAndRoleCodeForUpdate(Long userId, String roleCode) {
        return selectOne(new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getUserId, userId)
                .eq(MemberRoleApplyDO::getApplyRoleCode, roleCode)
                .in(MemberRoleApplyDO::getAuditStatus, Arrays.asList("PENDING", "APPROVED"))
                .last("LIMIT 1 FOR UPDATE"));
    }

    default PageResult<MemberRoleApplyDO> selectPage(MemberRoleApplyPageReqVO reqVO, Collection<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .inIfPresent(MemberRoleApplyDO::getUserId, matchedUserIds)
                .eqIfPresent(MemberRoleApplyDO::getApplyRoleCode, reqVO.getApplyRoleCode())
                .eqIfPresent(MemberRoleApplyDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(MemberRoleApplyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberRoleApplyDO::getId));
    }

    default PageResult<MemberRoleApplyDO> selectAppPage(Long userId, AppMemberRoleApplyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .eq(MemberRoleApplyDO::getUserId, userId)
                .eqIfPresent(MemberRoleApplyDO::getApplyRoleCode, reqVO.getApplyRoleCode())
                .eqIfPresent(MemberRoleApplyDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(MemberRoleApplyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberRoleApplyDO::getId));
    }
}
