package cn.iocoder.yudao.module.linbang.controller.admin.merchantentry;

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

import cn.iocoder.yudao.module.linbang.controller.admin.merchantentry.vo.*;
import cn.iocoder.yudao.module.linbang.service.merchantentry.MerchantEntryService;

@Tag(name = "管理后台 - 服务商入驻申请表")
@RestController
@RequestMapping("/merchant/entry")
@Validated
public class MerchantEntryController {

    @Resource
    private MerchantEntryService merchantEntryService;

    @PostMapping("/create")
    @Operation(summary = "创建服务商入驻申请表")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-entry:create')")
    public CommonResult<Long> createMerchantEntry(@Valid @RequestBody MerchantEntrySaveReqVO createReqVO) {
        return success(merchantEntryService.createMerchantEntry(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新服务商入驻申请表")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-entry:update')")
    public CommonResult<Boolean> updateMerchantEntry(@Valid @RequestBody MerchantEntrySaveReqVO updateReqVO) {
        merchantEntryService.updateMerchantEntry(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除服务商入驻申请表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:merchant-entry:delete')")
    public CommonResult<Boolean> deleteMerchantEntry(@RequestParam("id") Long id) {
        merchantEntryService.deleteMerchantEntry(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除服务商入驻申请表")
                @PreAuthorize("@ss.hasPermission('linbang:merchant-entry:delete')")
    public CommonResult<Boolean> deleteMerchantEntryList(@RequestParam("ids") List<Long> ids) {
        merchantEntryService.deleteMerchantEntryListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得服务商入驻申请表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-entry:query')")
    public CommonResult<MerchantEntryDetailRespVO> getMerchantEntry(@RequestParam("id") Long id) {
        return success(merchantEntryService.getMerchantEntryDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得服务商入驻申请表分页")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-entry:query')")
    public CommonResult<PageResult<MerchantEntryRespVO>> getMerchantEntryPage(@Valid MerchantEntryPageReqVO pageReqVO) {
        return success(merchantEntryService.getMerchantEntryPage(pageReqVO));
    }

    @PostMapping("/audit")
    @Operation(summary = "审核服务商入驻申请")
    @PreAuthorize("@ss.hasPermission('linbang:merchant:entry:audit')")
    public CommonResult<Boolean> auditMerchantEntry(@Valid @RequestBody MerchantEntryAuditReqVO reqVO) {
        merchantEntryService.auditMerchantEntry(reqVO);
        return success(true);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出服务商入驻申请表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-entry:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMerchantEntryExcel(@Valid MerchantEntryPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MerchantEntryRespVO> list = merchantEntryService.getMerchantEntryPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "服务商入驻申请表.xls", "数据", MerchantEntryRespVO.class, list);
    }

}
