package cn.iocoder.yudao.module.linbang.service.reviewcomment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.ReviewCommentDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.ReviewCommentPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.ReviewCommentRespVO;
import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.ReviewCommentSaveReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.creditrecord.CreditRecordDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderunit.OrderUnitDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.creditrecord.CreditRecordMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.memberuser.MemberUserMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertMap;
import static cn.iocoder.yudao.module.linbang.enums.ErrorCodeConstants.*;

/**
 * 评价 Service 实现类
 *
 * @author dawn
 */
@Service
@Validated
public class ReviewCommentServiceImpl implements ReviewCommentService {

    @Resource
    private ReviewCommentMapper reviewCommentMapper;
    @Resource
    private OrderInfoMapper orderInfoMapper;
    @Resource
    private OrderUnitMapper orderUnitMapper;
    @Resource
    private MemberUserMapper memberUserMapper;
    @Resource
    private MerchantInfoMapper merchantInfoMapper;
    @Resource
    private CreditRecordMapper creditRecordMapper;

    @Override
    public Long createReviewComment(ReviewCommentSaveReqVO createReqVO) {
        // 插入
        ReviewCommentDO reviewComment = BeanUtils.toBean(createReqVO, ReviewCommentDO.class);
        reviewCommentMapper.insert(reviewComment);

        // 返回
        return reviewComment.getId();
    }

    @Override
    public void updateReviewComment(ReviewCommentSaveReqVO updateReqVO) {
        // 校验存在
        validateReviewCommentExists(updateReqVO.getId());
        // 更新
        ReviewCommentDO updateObj = BeanUtils.toBean(updateReqVO, ReviewCommentDO.class);
        reviewCommentMapper.updateById(updateObj);
    }

    @Override
    public void deleteReviewComment(Long id) {
        // 校验存在
        validateReviewCommentExists(id);
        // 删除
        reviewCommentMapper.deleteById(id);
    }

    @Override
        public void deleteReviewCommentListByIds(List<Long> ids) {
        // 删除
        reviewCommentMapper.deleteByIds(ids);
        }


    private void validateReviewCommentExists(Long id) {
        if (reviewCommentMapper.selectById(id) == null) {
            throw exception(REVIEW_COMMENT_NOT_EXISTS);
        }
    }

    @Override
    public ReviewCommentDO getReviewComment(Long id) {
        return reviewCommentMapper.selectById(id);
    }

    @Override
    public ReviewCommentDetailRespVO getReviewCommentDetail(Long id) {
        ReviewCommentDO reviewComment = reviewCommentMapper.selectById(id);
        if (reviewComment == null) {
            throw exception(REVIEW_COMMENT_NOT_EXISTS);
        }
        OrderInfoDO order = reviewComment.getOrderId() == null ? null : orderInfoMapper.selectById(reviewComment.getOrderId());
        OrderUnitDO unit = reviewComment.getUnitId() == null ? null : orderUnitMapper.selectById(reviewComment.getUnitId());
        MemberUserDO fromUser = reviewComment.getFromUserId() == null ? null : memberUserMapper.selectById(reviewComment.getFromUserId());
        MemberUserDO toUser = reviewComment.getToUserId() == null ? null : memberUserMapper.selectById(reviewComment.getToUserId());
        MerchantInfoDO toMerchant = reviewComment.getToUserId() == null ? null : merchantInfoMapper.selectOne(
                new LambdaQueryWrapperX<MerchantInfoDO>()
                        .eq(MerchantInfoDO::getUserId, reviewComment.getToUserId())
                        .last("LIMIT 1"));
        List<ReviewCommentDO> relatedReviews = reviewComment.getOrderId() == null ? Collections.emptyList()
                : reviewCommentMapper.selectList(new LambdaQueryWrapperX<ReviewCommentDO>()
                .eq(ReviewCommentDO::getOrderId, reviewComment.getOrderId())
                .orderByDesc(ReviewCommentDO::getCreateTime, ReviewCommentDO::getId));
        List<CreditRecordDO> creditRecords = reviewComment.getToUserId() == null ? Collections.emptyList()
                : creditRecordMapper.selectList(new LambdaQueryWrapperX<CreditRecordDO>()
                .eq(CreditRecordDO::getUserId, reviewComment.getToUserId())
                .eq(CreditRecordDO::getBizType, "REVIEW")
                .eq(CreditRecordDO::getBizId, reviewComment.getId())
                .orderByDesc(CreditRecordDO::getCreateTime, CreditRecordDO::getId));
        return ReviewCommentDetailAssembler.build(reviewComment, order, unit, fromUser, toUser, toMerchant, relatedReviews, creditRecords);
    }

    @Override
    public PageResult<ReviewCommentRespVO> getReviewCommentPage(ReviewCommentPageReqVO pageReqVO) {
        List<Long> matchedOrderIds = resolveMatchedOrderIds(pageReqVO.getOrderNo());
        if (StrUtil.isNotBlank(pageReqVO.getOrderNo()) && CollUtil.isEmpty(matchedOrderIds)) {
            return PageResult.empty();
        }
        List<Long> matchedUnitIds = resolveMatchedUnitIds(pageReqVO.getUnitNo());
        if (StrUtil.isNotBlank(pageReqVO.getUnitNo()) && CollUtil.isEmpty(matchedUnitIds)) {
            return PageResult.empty();
        }
        List<Long> fromUserIds = resolveMatchedUserIds(pageReqVO.getFromUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getFromUserKeyword()) && CollUtil.isEmpty(fromUserIds)) {
            return PageResult.empty();
        }
        List<Long> toUserIds = resolveMatchedUserIds(pageReqVO.getToUserKeyword());
        if (StrUtil.isNotBlank(pageReqVO.getToUserKeyword()) && CollUtil.isEmpty(toUserIds)) {
            return PageResult.empty();
        }
        PageResult<ReviewCommentDO> pageResult = reviewCommentMapper.selectPage(pageReqVO, matchedOrderIds,
                matchedUnitIds, fromUserIds, toUserIds);
        List<ReviewCommentRespVO> list = BeanUtils.toBean(pageResult.getList(), ReviewCommentRespVO.class);
        fillDisplayInfo(list);
        return new PageResult<>(list, pageResult.getTotal());
    }

    private List<Long> resolveMatchedUserIds(String userKeyword) {
        if (StrUtil.isBlank(userKeyword)) {
            return null;
        }
        return convertList(memberUserMapper.selectListByKeyword(userKeyword), MemberUserDO::getId);
    }

    private List<Long> resolveMatchedOrderIds(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            return null;
        }
        return convertList(orderInfoMapper.selectListByOrderNo(orderNo), OrderInfoDO::getId);
    }

    private List<Long> resolveMatchedUnitIds(String unitNo) {
        if (StrUtil.isBlank(unitNo)) {
            return null;
        }
        return convertList(orderUnitMapper.selectListByUnitNo(unitNo), OrderUnitDO::getId);
    }

    private void fillDisplayInfo(List<ReviewCommentRespVO> list) {
        Set<Long> userIds = new HashSet<>();
        Set<Long> orderIds = new HashSet<>();
        Set<Long> unitIds = new HashSet<>();
        list.forEach(item -> {
            if (item.getOrderId() != null) {
                orderIds.add(item.getOrderId());
            }
            if (item.getUnitId() != null) {
                unitIds.add(item.getUnitId());
            }
            if (item.getFromUserId() != null) {
                userIds.add(item.getFromUserId());
            }
            if (item.getToUserId() != null) {
                userIds.add(item.getToUserId());
            }
        });
        Map<Long, OrderInfoDO> orderMap = orderIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderInfoMapper.selectBatchIds(orderIds), OrderInfoDO::getId);
        Map<Long, OrderUnitDO> unitMap = unitIds.isEmpty() ? Collections.emptyMap()
                : convertMap(orderUnitMapper.selectBatchIds(unitIds), OrderUnitDO::getId);
        Map<Long, MemberUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : convertMap(memberUserMapper.selectListByIds(userIds), MemberUserDO::getId);
        list.forEach(item -> {
            OrderInfoDO order = orderMap.get(item.getOrderId());
            if (order != null) {
                item.setOrderNo(order.getOrderNo());
            }
            OrderUnitDO unit = unitMap.get(item.getUnitId());
            if (unit != null) {
                item.setUnitNo(unit.getUnitNo());
            }
            MemberUserDO fromUser = userMap.get(item.getFromUserId());
            if (fromUser != null) {
                item.setFromUserNo(fromUser.getUserNo());
                item.setFromUserNickname(fromUser.getNickname());
                item.setFromUserMobile(fromUser.getMobile());
            }
            MemberUserDO toUser = userMap.get(item.getToUserId());
            if (toUser != null) {
                item.setToUserNo(toUser.getUserNo());
                item.setToUserNickname(toUser.getNickname());
                item.setToUserMobile(toUser.getMobile());
            }
        });
    }

}
