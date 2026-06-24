package cn.iocoder.yudao.module.linbang.dal.mysql.memberrealname;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberrealname.MemberUserRealNameDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.memberrealname.vo.*;

/**
 * 实名认证表 Mapper
 *
 * @author dawn
 */
@Mapper
public interface MemberUserRealNameMapper extends BaseMapperX<MemberUserRealNameDO> {

    default MemberUserRealNameDO selectByUserId(Long userId) {
        return selectOne(MemberUserRealNameDO::getUserId, userId);
    }

    default PageResult<MemberUserRealNameDO> selectPage(MemberUserRealNamePageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberUserRealNameDO>()
                .eqIfPresent(MemberUserRealNameDO::getUserId, reqVO.getUserId())
                .inIfPresent(MemberUserRealNameDO::getUserId, userIds)
                .likeIfPresent(MemberUserRealNameDO::getRealName, reqVO.getRealName())
                .eqIfPresent(MemberUserRealNameDO::getIdCardNo, reqVO.getIdCardNo())
                .eqIfPresent(MemberUserRealNameDO::getIdCardFrontFileId, reqVO.getIdCardFrontFileId())
                .eqIfPresent(MemberUserRealNameDO::getIdCardBackFileId, reqVO.getIdCardBackFileId())
                .eqIfPresent(MemberUserRealNameDO::getHoldCardFileId, reqVO.getHoldCardFileId())
                .eqIfPresent(MemberUserRealNameDO::getLivenessResult, reqVO.getLivenessResult())
                .eqIfPresent(MemberUserRealNameDO::getFaceVerifyResult, reqVO.getFaceVerifyResult())
                .eqIfPresent(MemberUserRealNameDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(MemberUserRealNameDO::getAuditRemark, reqVO.getAuditRemark())
                .eqIfPresent(MemberUserRealNameDO::getAuditBy, reqVO.getAuditBy())
                .betweenIfPresent(MemberUserRealNameDO::getAuditTime, reqVO.getAuditTime())
                .eqIfPresent(MemberUserRealNameDO::getRejectReason, reqVO.getRejectReason())
                .betweenIfPresent(MemberUserRealNameDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberUserRealNameDO::getId));
    }

}
