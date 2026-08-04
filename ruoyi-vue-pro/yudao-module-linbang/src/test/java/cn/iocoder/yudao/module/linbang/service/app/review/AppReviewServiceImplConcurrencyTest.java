package cn.iocoder.yudao.module.linbang.service.app.review;

import cn.iocoder.yudao.framework.test.core.ut.BaseMockitoUnitTest;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewUpdateReqVO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.appeal.AppealDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.complaint.ComplaintDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.memberuser.MemberUserDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.merchantinfo.MerchantInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.orderinfo.OrderInfoDO;
import cn.iocoder.yudao.module.linbang.dal.dataobject.reviewcomment.ReviewCommentDO;
import cn.iocoder.yudao.module.linbang.dal.mysql.appeal.AppealMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.complaint.ComplaintMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.merchantinfo.MerchantInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderinfo.OrderInfoMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.orderunit.OrderUnitMapper;
import cn.iocoder.yudao.module.linbang.dal.mysql.reviewcomment.ReviewCommentMapper;
import cn.iocoder.yudao.module.linbang.service.memberuser.MemberUserService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveContentDetectService;
import cn.iocoder.yudao.module.linbang.service.sensitiveword.SensitiveDetectResult;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppReviewServiceImplConcurrencyTest extends BaseMockitoUnitTest {

    @Mock
    private MemberUserService memberUserService;
    @Mock
    private OrderInfoMapper orderInfoMapper;
    @Mock
    private OrderUnitMapper orderUnitMapper;
    @Mock
    private MerchantInfoMapper merchantInfoMapper;
    @Mock
    private ComplaintMapper complaintMapper;
    @Mock
    private AppealMapper appealMapper;
    @Mock
    private ReviewCommentMapper reviewCommentMapper;
    @Mock
    private SensitiveContentDetectService sensitiveContentDetectService;

    @Test
    void createComplaint_returnsExistingActiveOrderLevelComplaint() {
        AppReviewServiceImpl service = buildService();
        mockLoginAndLockedOrder();
        when(merchantInfoMapper.selectById(3L)).thenReturn(MerchantInfoDO.builder().id(3L).userId(2L).build());
        when(complaintMapper.selectActiveForUpdate(10L, null, 1L, 2L, "SERVICE"))
                .thenReturn(ComplaintDO.builder().id(20L).build());
        AppComplaintCreateReqVO reqVO = new AppComplaintCreateReqVO();
        reqVO.setOrderId(10L);
        reqVO.setRespondentUserId(2L);
        reqVO.setComplaintType("SERVICE");
        reqVO.setContent("content");

        Long id = service.createComplaint(100L, reqVO);

        assertEquals(20L, id);
        verify(complaintMapper, never()).insert(any(ComplaintDO.class));
        verify(sensitiveContentDetectService, never()).detect(any(), any(), any(), any(), any());
    }

    @Test
    void createAppeal_returnsExistingActiveOrderLevelAppeal() {
        AppReviewServiceImpl service = buildService();
        mockLoginAndLockedOrder();
        when(appealMapper.selectActiveForUpdate(10L, null, 1L, "RESULT"))
                .thenReturn(AppealDO.builder().id(21L).build());
        AppAppealCreateReqVO reqVO = new AppAppealCreateReqVO();
        reqVO.setOrderId(10L);
        reqVO.setAppealType("RESULT");
        reqVO.setContent("content");

        Long id = service.createAppeal(100L, reqVO);

        assertEquals(21L, id);
        verify(appealMapper, never()).insert(any(AppealDO.class));
        verify(sensitiveContentDetectService, never()).detect(any(), any(), any(), any(), any());
    }

    @Test
    void updateReview_usesLockedReviewForEditLimitCheck() {
        AppReviewServiceImpl service = buildService();
        when(memberUserService.getOrCreateMemberUser(100L))
                .thenReturn(MemberUserDO.builder().id(1L).build());
        ReviewCommentDO review = ReviewCommentDO.builder()
                .id(30L)
                .orderId(10L)
                .fromUserId(1L)
                .toUserId(2L)
                .starLevel(4)
                .editCount(0)
                .editDeadlineTime(LocalDateTime.now().plusDays(1))
                .status("ENABLE")
                .build();
        when(reviewCommentMapper.selectByIdForUpdate(30L)).thenReturn(review);
        when(sensitiveContentDetectService.detect(any(), any(), any(), any(), any()))
                .thenReturn(SensitiveDetectResult.builder().processedContent("updated").build());
        AppReviewUpdateReqVO reqVO = new AppReviewUpdateReqVO();
        reqVO.setId(30L);
        reqVO.setStarLevel(4);
        reqVO.setContent("updated");

        service.updateReview(100L, reqVO);

        verify(reviewCommentMapper).selectByIdForUpdate(30L);
        verify(reviewCommentMapper).updateById(any(ReviewCommentDO.class));
    }

    private AppReviewServiceImpl buildService() {
        AppReviewServiceImpl service = new AppReviewServiceImpl();
        ReflectionTestUtils.setField(service, "memberUserService", memberUserService);
        ReflectionTestUtils.setField(service, "orderInfoMapper", orderInfoMapper);
        ReflectionTestUtils.setField(service, "orderUnitMapper", orderUnitMapper);
        ReflectionTestUtils.setField(service, "merchantInfoMapper", merchantInfoMapper);
        ReflectionTestUtils.setField(service, "complaintMapper", complaintMapper);
        ReflectionTestUtils.setField(service, "appealMapper", appealMapper);
        ReflectionTestUtils.setField(service, "reviewCommentMapper", reviewCommentMapper);
        ReflectionTestUtils.setField(service, "sensitiveContentDetectService", sensitiveContentDetectService);
        return service;
    }

    @SuppressWarnings("unchecked")
    private void mockLoginAndLockedOrder() {
        when(memberUserService.getOrCreateMemberUser(100L))
                .thenReturn(MemberUserDO.builder().id(1L).build());
        OrderInfoDO order = OrderInfoDO.builder().id(10L).userId(1L).merchantId(3L).status("FINISHED").build();
        doReturn(order).when(orderInfoMapper)
                .selectOneForUpdate(any(SFunction.class), eq(10L));
    }
}
