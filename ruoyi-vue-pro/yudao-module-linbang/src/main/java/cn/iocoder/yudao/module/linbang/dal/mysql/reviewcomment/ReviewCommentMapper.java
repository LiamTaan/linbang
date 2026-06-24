package cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment;

import java.util.List;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.*;

/**
 * 评价 Mapper
 *
 * @author dawn
 */
@Mapper
public interface ReviewCommentMapper extends BaseMapperX<ReviewCommentDO> {

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
