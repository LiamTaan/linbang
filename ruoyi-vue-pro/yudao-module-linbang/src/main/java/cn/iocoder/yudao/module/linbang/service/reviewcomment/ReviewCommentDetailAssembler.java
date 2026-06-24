package cn.iocoder.yudao.module.linbang.service.reviewcomment;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.ReviewCommentDetailRespVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

final class ReviewCommentDetailAssembler {

    private ReviewCommentDetailAssembler() {
    }

    static ReviewCommentDetailRespVO build(ReviewCommentDO review, OrderInfoDO order, OrderUnitDO unit,
                                           MemberUserDO fromUser, MemberUserDO toUser, MerchantInfoDO toMerchant,
                                           List<ReviewCommentDO> relatedReviews, List<CreditRecordDO> creditRecords) {
        ReviewCommentDetailRespVO respVO = BeanUtils.toBean(review, ReviewCommentDetailRespVO.class);
        if (order != null) {
            respVO.setOrder(BeanUtils.toBean(order, ReviewCommentDetailRespVO.OrderRespVO.class));
        }
        if (unit != null) {
            respVO.setUnit(BeanUtils.toBean(unit, ReviewCommentDetailRespVO.UnitRespVO.class));
        }
        if (fromUser != null) {
            respVO.setFromUser(BeanUtils.toBean(fromUser, ReviewCommentDetailRespVO.UserRespVO.class));
        }
        if (toUser != null) {
            respVO.setToUser(BeanUtils.toBean(toUser, ReviewCommentDetailRespVO.UserRespVO.class));
        }
        if (toMerchant != null) {
            respVO.setToMerchant(BeanUtils.toBean(toMerchant, ReviewCommentDetailRespVO.MerchantRespVO.class));
        }
        respVO.setSummary(buildSummary(relatedReviews, creditRecords));
        respVO.setRelatedReviews(buildRelatedReviews(relatedReviews, review.getId()));
        respVO.setCreditRecords(buildCreditRecords(creditRecords));
        return respVO;
    }

    private static ReviewCommentDetailRespVO.SummaryRespVO buildSummary(List<ReviewCommentDO> relatedReviews,
                                                                        List<CreditRecordDO> creditRecords) {
        List<ReviewCommentDO> reviewSource = relatedReviews == null ? Collections.emptyList() : relatedReviews;
        List<CreditRecordDO> creditSource = creditRecords == null ? Collections.emptyList() : creditRecords;
        ReviewCommentDetailRespVO.SummaryRespVO summary = new ReviewCommentDetailRespVO.SummaryRespVO();
        summary.setSameOrderReviewCount(reviewSource.size());
        summary.setSameTargetReviewCount((int) reviewSource.stream()
                .map(ReviewCommentDO::getToUserId)
                .filter(item -> item != null)
                .distinct()
                .count());
        summary.setPositiveReviewCount((int) reviewSource.stream()
                .filter(item -> item.getStarLevel() != null && item.getStarLevel() >= 4)
                .count());
        summary.setNeutralReviewCount((int) reviewSource.stream()
                .filter(item -> item.getStarLevel() != null && item.getStarLevel() == 3)
                .count());
        summary.setNegativeReviewCount((int) reviewSource.stream()
                .filter(item -> item.getStarLevel() != null && item.getStarLevel() <= 2)
                .count());
        summary.setAutoReviewCount((int) reviewSource.stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsAutoReview()))
                .count());
        summary.setCreditRecordCount(creditSource.size());
        return summary;
    }

    private static List<ReviewCommentDetailRespVO.RelatedReviewRespVO> buildRelatedReviews(List<ReviewCommentDO> relatedReviews,
                                                                                            Long currentId) {
        if (relatedReviews == null || relatedReviews.isEmpty()) {
            return Collections.emptyList();
        }
        return relatedReviews.stream()
                .filter(item -> currentId == null || !currentId.equals(item.getId()))
                .limit(10)
                .map(item -> BeanUtils.toBean(item, ReviewCommentDetailRespVO.RelatedReviewRespVO.class))
                .collect(Collectors.toList());
    }

    private static List<ReviewCommentDetailRespVO.CreditRecordRespVO> buildCreditRecords(List<CreditRecordDO> creditRecords) {
        if (creditRecords == null || creditRecords.isEmpty()) {
            return Collections.emptyList();
        }
        return creditRecords.stream()
                .limit(10)
                .map(item -> BeanUtils.toBean(item, ReviewCommentDetailRespVO.CreditRecordRespVO.class))
                .collect(Collectors.toList());
    }
}
