package cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo;

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

import cn.iocoder.yudao.module.linbang.controller.admin.merchantinfo.vo.*;
import cn.iocoder.yudao.module.linbang.service.merchantinfo.MerchantInfoService;

@Tag(name = "管理后台 - 服务商信息表")
@RestController
@RequestMapping("/linbang/merchant-info")
@Validated
public class MerchantInfoController {

    @Resource
    private MerchantInfoService merchantInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建服务商信息表")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-info:create')")
    public CommonResult<Long> createMerchantInfo(@Valid @RequestBody MerchantInfoSaveReqVO createReqVO) {
        return success(merchantInfoService.createMerchantInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新服务商信息表")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-info:update')")
    public CommonResult<Boolean> updateMerchantInfo(@Valid @RequestBody MerchantInfoSaveReqVO updateReqVO) {
        merchantInfoService.updateMerchantInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除服务商信息表")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('linbang:merchant-info:delete')")
    public CommonResult<Boolean> deleteMerchantInfo(@RequestParam("id") Long id) {
        merchantInfoService.deleteMerchantInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除服务商信息表")
                @PreAuthorize("@ss.hasPermission('linbang:merchant-info:delete')")
    public CommonResult<Boolean> deleteMerchantInfoList(@RequestParam("ids") List<Long> ids) {
        merchantInfoService.deleteMerchantInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得服务商信息表")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-info:query')")
    public CommonResult<MerchantInfoDetailRespVO> getMerchantInfo(@RequestParam("id") Long id) {
        return success(merchantInfoService.getMerchantInfoDetail(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得服务商信息表分页")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-info:query')")
    public CommonResult<PageResult<MerchantInfoRespVO>> getMerchantInfoPage(@Valid MerchantInfoPageReqVO pageReqVO) {
        return success(merchantInfoService.getMerchantInfoPage(pageReqVO));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出服务商信息表 Excel")
    @PreAuthorize("@ss.hasPermission('linbang:merchant-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMerchantInfoExcel(@Valid MerchantInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MerchantInfoRespVO> list = merchantInfoService.getMerchantInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "服务商信息表.xls", "数据", MerchantInfoRespVO.class, list);
    }

}
