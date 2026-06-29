package cn.iocoder.yudao.module.linbang.service.reviewcomment;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ReviewCommentMetricsServiceImpl implements ReviewCommentMetricsService {

    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;

    @Override
    public MerchantReviewMetricsResp calculateMerchantMetrics(Long merchantId) {
        MerchantReviewMetricsResp resp = new MerchantReviewMetricsResp();
        resp.setMerchantId(merchantId);
        resp.setCompositeScore(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        resp.setReviewCount(0);
        resp.setPositiveReviewCount(0);
        resp.setNeutralReviewCount(0);
        resp.setNegativeReviewCount(0);
        resp.setPositiveRate(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        resp.setInPositivePriorityPool(Boolean.FALSE);
        if (merchantId == null) {
            return resp;
        }
        MerchantInfoDO merchant = merchantInfoMapper.selectById(merchantId);
        if (merchant == null) {
            return resp;
        }

        List<OrderUnitDO> merchantUnits = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getMerchantId, merchantId));
        if (merchantUnits.isEmpty()) {
            return resp;
        }
        Set<Long> unitIds = merchantUnits.stream().map(OrderUnitDO::getId).collect(Collectors.toSet());
        List<ReviewCommentDO> reviews = reviewCommentMapper.selectList(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getStatus, "ENABLE")
                .eq(ReviewCommentDO::getToUserId, merchant.getUserId())
                .in(ReviewCommentDO::getUnitId, unitIds)
                .orderByDesc(ReviewCommentDO::getCreateTime, ReviewCommentDO::getId));
        if (reviews.isEmpty()) {
            return resp;
        }

        java.util.Map<Long, OrderUnitDO> unitMap = merchantUnits.stream()
                .collect(Collectors.toMap(OrderUnitDO::getId, item -> item, (left, right) -> left));
        BigDecimal scoreSum = BigDecimal.ZERO;
        BigDecimal weightSum = BigDecimal.ZERO;
        int positive = 0;
        int neutral = 0;
        int negative = 0;
        for (ReviewCommentDO review : reviews) {
            OrderUnitDO unit = unitMap.get(review.getUnitId());
            BigDecimal weight = unit != null && unit.getUnitAmount() != null
                    ? unit.getUnitAmount() : BigDecimal.ONE;
            BigDecimal star = BigDecimal.valueOf(review.getStarLevel() == null ? 0 : review.getStarLevel());
            scoreSum = scoreSum.add(star.multiply(weight));
            weightSum = weightSum.add(weight);
            if (review.getStarLevel() != null && review.getStarLevel() >= 4) {
                positive++;
            } else if (Objects.equals(review.getStarLevel(), 3)) {
                neutral++;
            } else {
                negative++;
            }
        }
        BigDecimal compositeScore = weightSum.compareTo(BigDecimal.ZERO) > 0
                ? scoreSum.divide(weightSum, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal positiveRate = BigDecimal.valueOf(positive)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(reviews.size()), 2, RoundingMode.HALF_UP);

        resp.setCompositeScore(compositeScore);
        resp.setReviewCount(reviews.size());
        resp.setPositiveReviewCount(positive);
        resp.setNeutralReviewCount(neutral);
        resp.setNegativeReviewCount(negative);
        resp.setPositiveRate(positiveRate);
        resp.setInPositivePriorityPool(isInPositivePriorityPool(reviews));
        return resp;
    }

    private boolean isInPositivePriorityPool(List<ReviewCommentDO> reviews) {
        if (reviews == null || reviews.size() < 15) {
            return false;
        }
        List<ReviewCommentDO> latestFifteen = reviews.subList(0, 15);
        return latestFifteen.stream().allMatch(review -> review.getStarLevel() != null && review.getStarLevel() >= 4);
    }
}
