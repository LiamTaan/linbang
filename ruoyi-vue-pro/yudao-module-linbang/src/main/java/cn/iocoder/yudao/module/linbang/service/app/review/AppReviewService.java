package cn.iocoder.yudao.module.linbang.service.app.review;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppAppealRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordDetailRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppCreditRecordRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppComplaintRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewCreditRespVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewCreateReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.app.review.vo.AppReviewRespVO;

import javax.validation.Valid;

public interface AppReviewService {

    Long createComplaint(Long authUserId, @Valid AppComplaintCreateReqVO reqVO);

    PageResult<AppComplaintRespVO> getComplaintPage(Long authUserId, AppComplaintPageReqVO reqVO);

    AppComplaintRespVO getComplaint(Long authUserId, Long id);

    Long createAppeal(Long authUserId, @Valid AppAppealCreateReqVO reqVO);

    PageResult<AppAppealRespVO> getAppealPage(Long authUserId, AppAppealPageReqVO reqVO);

    AppAppealRespVO getAppeal(Long authUserId, Long id);

    Long createReview(Long authUserId, @Valid AppReviewCreateReqVO reqVO);

    PageResult<AppReviewRespVO> getReviewPage(Long authUserId, AppReviewPageReqVO reqVO);

    AppReviewRespVO getReview(Long authUserId, Long id);

    AppReviewCreditRespVO getCredit(Long authUserId);

    PageResult<AppCreditRecordRespVO> getCreditRecordPage(Long authUserId, AppCreditRecordPageReqVO reqVO);

    AppCreditRecordDetailRespVO getCreditRecord(Long authUserId, Long id);
}
