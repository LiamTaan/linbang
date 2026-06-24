package cn.iocoder.yudao.module.linbang.dal.mysql.memberroleapply;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.memberroleapply.vo.MemberRoleApplyPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberroleapply.MemberRoleApplyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface MemberRoleApplyMapper extends BaseMapperX<MemberRoleApplyDO> {

    default PageResult<MemberRoleApplyDO> selectPage(MemberRoleApplyPageReqVO reqVO, Collection<Long> matchedUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberRoleApplyDO>()
                .inIfPresent(MemberRoleApplyDO::getUserId, matchedUserIds)
                .eqIfPresent(MemberRoleApplyDO::getApplyRoleCode, reqVO.getApplyRoleCode())
                .eqIfPresent(MemberRoleApplyDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(MemberRoleApplyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberRoleApplyDO::getId));
    }
}
