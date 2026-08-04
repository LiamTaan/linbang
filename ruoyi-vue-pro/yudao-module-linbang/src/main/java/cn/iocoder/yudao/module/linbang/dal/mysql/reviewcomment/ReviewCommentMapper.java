package cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment;

import java.util.Collection;
import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.*;

/**
 * 评价 Mapper
 *
 * @author dawn
 */
@Mapper
public interface ReviewCommentMapper extends BaseMapperX<ReviewCommentDO> {

    default ReviewCommentDO selectByIdForUpdate(Long id) {
        return selectOneForUpdate(ReviewCommentDO::getId, id);
    }

    default ReviewCommentDO selectActiveByUnitIdAndFromUserIdForUpdate(Long unitId, Long fromUserId) {
        return selectOne(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getUnitId, unitId)
                .eq(ReviewCommentDO::getFromUserId, fromUserId)
                .eq(ReviewCommentDO::getStatus, "ENABLE")
                .last("LIMIT 1 FOR UPDATE"));
    }

    @Select({"<script>",
            "SELECT recent.to_user_id FROM (",
            "  SELECT r.to_user_id, r.star_level,",
            "         ROW_NUMBER() OVER (PARTITION BY r.to_user_id ORDER BY r.create_time DESC, r.id DESC) AS row_num",
            "  FROM lb_review r",
            "  WHERE r.tenant_id = #{tenantId} AND r.deleted = b'0' AND r.status = 'ENABLE' AND r.to_user_id IN",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'>#{userId}</foreach>",
            ") recent WHERE recent.row_num &lt;= 15",
            "GROUP BY recent.to_user_id",
            "HAVING COUNT(*) = 15 AND COUNT(recent.star_level) = 15 AND MIN(recent.star_level) &gt;= 4",
            "</script>"})
    List<Long> selectPriorityEligibleUserIds(@Param("tenantId") Long tenantId,
                                             @Param("userIds") Collection<Long> userIds);

    default PageResult<ReviewCommentDO> selectPage(ReviewCommentPageReqVO reqVO, List<Long> matchedOrderIds,
                                                   List<Long> matchedUnitIds, List<Long> fromUserIds,
                                                   List<Long> toUserIds) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ReviewCommentDO>()
                .inIfPresent(ReviewCommentDO::getOrderId, matchedOrderIds)
                .inIfPresent(ReviewCommentDO::getUnitId, matchedUnitIds)
                .inIfPresent(ReviewCommentDO::getFromUserId, fromUserIds)
                .inIfPresent(ReviewCommentDO::getToUserId, toUserIds)
                .eqIfPresent(ReviewCommentDO::getStarLevel, reqVO.getStarLevel())
                .eqIfPresent(ReviewCommentDO::getContent, reqVO.getContent())
                .eqIfPresent(ReviewCommentDO::getIsAutoReview, reqVO.getIsAutoReview())
                .eqIfPresent(ReviewCommentDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ReviewCommentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(ReviewCommentDO::getId));
    }

}
