package cn.iocoder.yudao.module.linbang.service.reviewcomment;

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

@Service
public class ReviewAutoEvaluateService {

    private static final int SCAN_BATCH_SIZE = 200;

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
        long lastId = 0L;
        while (true) {
            List<OrderUnitDO> finishedUnits = orderUnitMapper.selectList(new LambdaQueryWrapperX<OrderUnitDO>()
                    .gt(OrderUnitDO::getId, lastId)
                    .eq(OrderUnitDO::getStatus, "FINISHED")
                    .isNotNull(OrderUnitDO::getFinishTime)
                    .le(OrderUnitDO::getFinishTime, deadline)
                    .notExists("SELECT 1 FROM lb_review r INNER JOIN lb_order_info o ON o.id = lb_order_unit.order_id "
                            + "WHERE r.unit_id = lb_order_unit.id AND r.from_user_id = o.user_id "
                            + "AND r.tenant_id = lb_order_unit.tenant_id AND r.deleted = b'0'")
                    .orderByAsc(OrderUnitDO::getId)
                    .last("LIMIT " + SCAN_BATCH_SIZE));
            if (finishedUnits.isEmpty()) {
                return;
            }
            for (OrderUnitDO unit : finishedUnits) {
                autoEvaluateUnit(unit);
            }
            lastId = finishedUnits.get(finishedUnits.size() - 1).getId();
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
        long reviewCount = reviewCommentMapper.selectCount(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getUnitId, unit.getId())
                .eq(ReviewCommentDO::getFromUserId, order.getUserId())
                .eq(ReviewCommentDO::getStatus, "ENABLE"));
        if (reviewCount == 0) {
            appReviewService.createAutoReview(unit, order.getUserId(), merchantUserId);
        }
    }
}
