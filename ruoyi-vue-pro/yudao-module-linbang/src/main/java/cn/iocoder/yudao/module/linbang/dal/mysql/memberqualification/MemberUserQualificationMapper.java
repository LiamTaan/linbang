package cn.iocoder.yudao.module.linbang.dal.mysql.memberqualification;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.controller.admin.memberqualification.vo.MemberQualificationPageReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberqualification.MemberUserQualificationDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Mapper
public interface MemberUserQualificationMapper extends BaseMapperX<MemberUserQualificationDO> {

    default MemberUserQualificationDO selectByIdForUpdate(Long id) {
        return selectOneForUpdate(MemberUserQualificationDO::getId, id);
    }

    default PageResult<MemberUserQualificationDO> selectPage(MemberQualificationPageReqVO reqVO, Collection<Long> userIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eqIfPresent(MemberUserQualificationDO::getUserId, reqVO.getUserId())
                .inIfPresent(MemberUserQualificationDO::getUserId, userIds)
                .eqIfPresent(MemberUserQualificationDO::getQualificationType, reqVO.getQualificationType())
                .likeIfPresent(MemberUserQualificationDO::getQualificationName, reqVO.getQualificationName())
                .eqIfPresent(MemberUserQualificationDO::getAuditStatus, reqVO.getAuditStatus())
                .betweenIfPresent(MemberUserQualificationDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(MemberUserQualificationDO::getId));
    }

    default List<MemberUserQualificationDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, userId)
                .orderByDesc(MemberUserQualificationDO::getId));
    }

    default List<MemberUserQualificationDO> selectListByUserIdAndIds(Long userId, Collection<Long> ids) {
        return selectList(new LambdaQueryWrapperX<MemberUserQualificationDO>()
                .eq(MemberUserQualificationDO::getUserId, userId)
                .in(MemberUserQualificationDO::getId, ids)
                .orderByDesc(MemberUserQualificationDO::getId));
    }

    @Select("SELECT f.id FROM infra_file f "
            + "WHERE f.deleted = b'0' AND f.size >= 0 AND f.path LIKE CONCAT(#{pathPrefix}, '%') "
            + "AND f.create_time <= #{expiredBefore} AND f.id > #{afterId} "
            + "AND NOT EXISTS (SELECT 1 FROM lb_member_user_qualification q "
            + "WHERE q.file_id = f.id AND q.deleted = b'0') "
            + "ORDER BY f.id ASC LIMIT #{limit}")
    List<Long> selectUnreferencedFileIds(@Param("pathPrefix") String pathPrefix,
                                         @Param("expiredBefore") LocalDateTime expiredBefore,
                                         @Param("afterId") Long afterId,
                                         @Param("limit") int limit);

}
