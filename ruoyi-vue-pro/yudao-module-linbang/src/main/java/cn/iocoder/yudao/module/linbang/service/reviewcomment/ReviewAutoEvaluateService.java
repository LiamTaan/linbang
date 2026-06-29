package cn.iocoder.yudao.module.linbang.service.reviewcomment;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.app.review.AppReviewServiceImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ReviewAutoEvaluateService {

    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private AppReviewServiceImpl appReviewService;

    @Scheduled(cron = "0 */30 * * * ?")
    public void scanAndAutoEvaluate() {
        LocalDateTime deadline = LocalDateTime.now().minusHours(24);
        List<OrderUnitDO> finishedUnits = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                .eq(OrderUnitDO::getStatus, "FINISHED")
                .isNotNull(OrderUnitDO::getFinishTime)
                .le(OrderUnitDO::getFinishTime, deadline)
                .orderByAsc(OrderUnitDO::getFinishTime, OrderUnitDO::getId));
        for (OrderUnitDO unit : finishedUnits) {
            autoEvaluateUnit(unit);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void autoEvaluateUnit(OrderUnitDO unit) {
        if (unit == null) {
            return;
        }
        OrderInfoDO order = orderInfoMapper.selectById(unit.getOrderId());
        if (order == null || unit.getMerchantId() == null) {
            return;
        }
        MerchantInfoDO merchant = merchantInfoMapper.selectById(unit.getMerchantId());
        Long merchantUserId = merchant != null ? merchant.getUserId() : null;
        if (merchantUserId == null || order.getUserId() == null) {
            return;
        }
        List<ReviewCommentDO> reviews = reviewCommentMapper.selectList(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getUnitId, unit.getId())
                .eq(ReviewCommentDO::getStatus, "ENABLE"));
        if (!hasReviewFrom(reviews, order.getUserId())) {
            appReviewService.createAutoReview(unit, order.getUserId(), merchantUserId);
        }
        if (!hasReviewFrom(reviews, merchantUserId)) {
            appReviewService.createAutoReview(unit, merchantUserId, order.getUserId());
        }
    }

    private boolean hasReviewFrom(List<ReviewCommentDO> reviews, Long fromUserId) {
        if (CollUtil.isEmpty(reviews) || fromUserId == null) {
            return false;
        }
        return reviews.stream().anyMatch(item -> Objects.equals(item.getFromUserId(), fromUserId));
    }
}
