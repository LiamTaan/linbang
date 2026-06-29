package cn.iocoder.yudao.module.linbang.controller.admin.promotecontent;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentAuditReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentOfflineReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentPageReqVO;
import cn.iocoder.yudao.module.linbang.controller.admin.promotecontent.vo.PromoteContentRespVO;
import cn.iocoder.yudao.module.linbang.service.promotecontent.PromoteContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 推广内容审核")
@RestController
@RequestMapping("/promote/content")
@Validated
public class PromoteContentController {

    @Resource
    private PromoteContentService promoteContentService;

    @GetMapping("/page")
    @Operation(summary = "获取推广内容分页")
    @PreAuthorize("@ss.hasPermission('linbang:promote:content:query')")
    public CommonResult<PageResult<PromoteContentRespVO>> getPage(@Valid PromoteContentPageReqVO reqVO) {
        return success(promoteContentService.getAdminPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取推广内容详情")
    @Parameter(name = "id", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:promote:content:query')")
    public CommonResult<PromoteContentRespVO> get(@RequestParam("id") Long id) {
        return success(promoteContentService.getAdminContent(id));
    }

    @PostMapping("/audit")
    @Operation(summary = "人工审核推广内容")
    @PreAuthorize("@ss.hasPermission('linbang:promote:content:audit')")
    public CommonResult<Boolean> audit(@Valid @RequestBody PromoteContentAuditReqVO reqVO) {
        promoteContentService.auditContent(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }

    @PostMapping("/offline")
    @Operation(summary = "下架推广内容")
    @PreAuthorize("@ss.hasPermission('linbang:promote:content:offline')")
    public CommonResult<Boolean> offline(@Valid @RequestBody PromoteContentOfflineReqVO reqVO) {
        promoteContentService.offlineContent(getLoginUserId(), reqVO);
        return success(Boolean.TRUE);
    }
}
