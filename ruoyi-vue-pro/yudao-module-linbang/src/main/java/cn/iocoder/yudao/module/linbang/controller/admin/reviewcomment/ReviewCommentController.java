package cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.linbang.controller.admin.reviewcomment.vo.*;
import cn.iocoder.yudao.module.linbang.service.reviewcomment.ReviewCommentService;

@Tag(name = "管理后台 - 评价")
@RestController
@RequestMapping("/review/comment")
@Validated
public class ReviewCommentController {

    @Resource
    private ReviewCommentService reviewCommentService;

    @PostMapping("/create")
    @Operation(summary = "创建评价")
    @PreAuthorize("@ss.hasPermission('linbang:review:comment:create')")
    public CommonResult<Long> createReviewComment(@Valid @RequestBody ReviewCommentSaveReqVO createReqVO) {
        return success(reviewCommentService.createReviewComment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新评价")
    @PreAuthorize("@ss.hasPermission('linbang:review:comment:update')")
    public CommonResult<Boolean> updateReviewComment(@Valid @RequestBody ReviewCommentSaveReqVO updateReqVO) {
        reviewCommentService.updateReviewComment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除评价")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:review:comment:delete')")
    public CommonResult<Boolean> deleteReviewComment(@RequestParam("id") Long id) {
        reviewCommentService.deleteReviewComment(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除评价")
                @PreAuthorize("@ss.hasPermission('linbang:review:comment:delete')")
    public CommonResult<Boolean> deleteReviewCommentList(@RequestParam("ids") List<Long> ids) {
        reviewCommentService.deleteReviewCommentListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得评价")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:review:comment:query')")
    public CommonResult<ReviewCommentDetailRespVO> getReviewComment(@RequestParam("id") Long id) {
        return success(reviewCommentService.getReviewCommentDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得评价分页")
    @PreAuthorize("@ss.hasPermission('linbang:review:comment:query')")
    public CommonResult<PageResult<ReviewCommentRespVO>> getReviewCommentPage(@Valid ReviewCommentPageReqVO pageReqVO) {
        return success(reviewCommentService.getReviewCommentPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出评价 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:review:comment:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportReviewCommentExcel(@Valid ReviewCommentPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ReviewCommentRespVO> list = reviewCommentService.getReviewCommentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "评价.xls", "数据", ReviewCommentRespVO.class, list);
    }

}
