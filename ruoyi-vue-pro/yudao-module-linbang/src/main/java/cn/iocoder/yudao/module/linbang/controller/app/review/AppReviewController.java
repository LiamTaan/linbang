package cn.iocoder.yudao.module.linbang.controller.app.review;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
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
import cn.iocoder.yudao.module.linbang.service.app.review.AppReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 邻里售后与评价")
@RestController
@RequestMapping("/review")
@Validated
public class AppReviewController {

    @Resource
    private AppReviewService appReviewService;

    @PostMapping("/complaint/create")
    @Operation(summary = "提交投诉")
    public CommonResult<Long> createComplaint(@Valid @RequestBody AppComplaintCreateReqVO reqVO) {
        return success(appReviewService.createComplaint(getLoginUserId(), reqVO));
    }

    @GetMapping("/complaint/page")
    @Operation(summary = "获取投诉分页")
    public CommonResult<PageResult<AppComplaintRespVO>> getComplaintPage(@Valid AppComplaintPageReqVO reqVO) {
        return success(appReviewService.getComplaintPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/complaint/get")
    @Operation(summary = "获取投诉详情")
    public CommonResult<AppComplaintRespVO> getComplaint(@RequestParam("id") Long id) {
        return success(appReviewService.getComplaint(getLoginUserId(), id));
    }

    @PostMapping("/appeal/create")
    @Operation(summary = "提交申诉")
    public CommonResult<Long> createAppeal(@Valid @RequestBody AppAppealCreateReqVO reqVO) {
        return success(appReviewService.createAppeal(getLoginUserId(), reqVO));
    }

    @GetMapping("/appeal/page")
    @Operation(summary = "获取申诉分页")
    public CommonResult<PageResult<AppAppealRespVO>> getAppealPage(@Valid AppAppealPageReqVO reqVO) {
        return success(appReviewService.getAppealPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/appeal/get")
    @Operation(summary = "获取申诉详情")
    public CommonResult<AppAppealRespVO> getAppeal(@RequestParam("id") Long id) {
        return success(appReviewService.getAppeal(getLoginUserId(), id));
    }

    @PostMapping("/comment/create")
    @Operation(summary = "提交评价")
    public CommonResult<Long> createReview(@Valid @RequestBody AppReviewCreateReqVO reqVO) {
        return success(appReviewService.createReview(getLoginUserId(), reqVO));
    }

    @GetMapping("/comment/page")
    @Operation(summary = "获取评价分页")
    public CommonResult<PageResult<AppReviewRespVO>> getReviewPage(@Valid AppReviewPageReqVO reqVO) {
        return success(appReviewService.getReviewPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/comment/get")
    @Operation(summary = "获取评价详情")
    public CommonResult<AppReviewRespVO> getReview(@RequestParam("id") Long id) {
        return success(appReviewService.getReview(getLoginUserId(), id));
    }

    @GetMapping("/credit/get")
    @Operation(summary = "获取信用信息")
    public CommonResult<AppReviewCreditRespVO> getCredit() {
        return success(appReviewService.getCredit(getLoginUserId()));
    }

    @GetMapping("/credit-record/page")
    @Operation(summary = "获取信用记录分页")
    public CommonResult<PageResult<AppCreditRecordRespVO>> getCreditRecordPage(@Valid AppCreditRecordPageReqVO reqVO) {
        return success(appReviewService.getCreditRecordPage(getLoginUserId(), reqVO));
    }

    @GetMapping("/credit-record/get")
    @Operation(summary = "获取信用记录详情")
    public CommonResult<AppCreditRecordDetailRespVO> getCreditRecord(@RequestParam("id") Long id) {
        return success(appReviewService.getCreditRecord(getLoginUserId(), id));
    }
}
